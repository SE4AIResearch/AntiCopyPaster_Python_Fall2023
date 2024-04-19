package org.jetbrains.research.anticopypasterpython.ide;

import com.intellij.CommonBundle;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.highlighting.actions.HighlightUsagesAction;
import com.intellij.codeInsight.intention.impl.preview.IntentionPreviewDiffResult;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseEventArea;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.ex.RangeHighlighterEx;
import com.intellij.openapi.editor.markup.*;
import com.intellij.ui.HighlightedText;
import com.intellij.vcs.log.ui.actions.HighlightersActionGroup;
import com.intellij.codeInsight.highlighting.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.scope.DelegatingScopeProcessor;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.codeInsight.codeFragment.PyCodeFragment;
import com.jetbrains.python.codeInsight.codeFragment.PyCodeFragmentUtil;
import com.jetbrains.python.codeInsight.controlflow.ScopeOwner;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyElement;
import com.jetbrains.python.psi.PyFile;

import java.util.concurrent.TimeUnit;

import com.jetbrains.python.refactoring.extractmethod.*;

import org.jetbrains.research.anticopypasterpython.AntiCopyPasterPythonBundle;
import org.jetbrains.research.anticopypasterpython.checkers.FragmentCorrectnessChecker;
import org.jetbrains.research.anticopypasterpython.config.ProjectSettingsState;
import org.jetbrains.research.anticopypasterpython.models.PredictionModel;
import org.jetbrains.research.anticopypasterpython.models.TensorflowModel;
import org.jetbrains.research.anticopypasterpython.models.UserSettingsModel;
import org.jetbrains.research.anticopypasterpython.statistics.AntiCopyPasterUsageStatistics;
import org.jetbrains.research.anticopypasterpython.utils.MetricsGatherer;
import org.jetbrains.research.anticopypasterpython.metrics.MetricCalculator;
import org.jetbrains.research.anticopypasterpython.metrics.features.FeaturesVector;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.jetbrains.python.refactoring.extractmethod.PyExtractMethodHandler.*;
import static org.jetbrains.research.anticopypasterpython.utils.PsiUtil.*;

import org.jetbrains.research.anticopypasterpython.utils.PyExtractMethodHandler;
import com.intellij.openapi.editor.colors.EditorColors;

/**
 * Shows a notification about discovered Extract Method refactoring opportunity.
 */
public class RefactoringNotificationTask extends TimerTask {
    private static final Logger LOG = Logger.getInstance(RefactoringNotificationTask.class);
    private static float tensorflowPredictionThreshold = 0.01f; // certainty threshold for TensorFlow model
    private static float usersettingsPredictionThreshold = 0.5f; // certainty threshold for UserSettings model
    private static float predictionThreshold = usersettingsPredictionThreshold; // Prediction threshold at runtime.
    private final DuplicatesInspection inspection;
    private final ConcurrentLinkedQueue<RefactoringEvent> eventsQueue = new ConcurrentLinkedQueue<>();
    private final NotificationGroup notificationGroup = NotificationGroupManager.getInstance()
            .getNotificationGroup("Extract Method suggestion");
    private final Timer timer;
    private PredictionModel model;
    private final boolean debugMetrics = true;
    private String logFilePath;
    private Project p;

    public boolean didAlreadyHighlight;

    public Editor editor;

    Collection<RangeHighlighter> collection = new ArrayList<>();


    public RefactoringNotificationTask(DuplicatesInspection inspection, Timer timer, Project p) {
        this.inspection = inspection;
        this.timer = timer;
        this.p = p;
        this.logFilePath = p.getBasePath() + "/.idea/anticopypaster-refactoringSuggestionsLog.log";
        didAlreadyHighlight=false;
    }

    private PredictionModel getOrInitModel() {
        ProjectSettingsState settings = ProjectSettingsState.getInstance(this.p);
        boolean tensorFlowModel = settings.tensorFlowEnabled;
        PredictionModel model = this.model;
        System.out.println(model);

        // It makes sense to decouple thresholds between the settings model and tensor model.
        if (tensorFlowModel) {
            predictionThreshold = tensorflowPredictionThreshold;

            if (model != null && model instanceof UserSettingsModel)
                model = null;

            if (model == null) {
                this.model = new TensorflowModel();
                model = this.model;
            }
        }
        else {
            predictionThreshold = usersettingsPredictionThreshold;

            if (model != null && model instanceof TensorflowModel)
                model = null;

            if (model == null) {
                this.model = new UserSettingsModel(new MetricsGatherer(p), p);
                model = this.model;
                if(debugMetrics){
                    UserSettingsModel settingsModel = (UserSettingsModel) model;
                    try(FileWriter fr = new FileWriter(logFilePath, true)){
                        String timestamp =
                                new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                        fr.write("\n-----------------------\nInitial Metric Thresholds: " +
                                timestamp + "\n");
                    } catch(IOException ioe) { ioe.printStackTrace(); }
                    System.out.println("InitModelError");
                    settingsModel.logThresholds(logFilePath);
                }
            }
        }

        System.out.println("Model is tensorflow: " + (model instanceof TensorflowModel));
        System.out.println(model);
        return model;
    }

    public void highlight(Project project, RefactoringEvent event, Runnable callback){
        //if(!didAlreadyHighlight){     //prevents us from adding multiple highlights?????
            HighlightManager hm = HighlightManager.getInstance(project);
            int startOffset = event.getDestinationMethod().getTextRange().getStartOffset();
            int endOffset = event.getDestinationMethod().getTextRange().getEndOffset();

            int startOffset2 = getStartOffset(event.getEditor(),event.getFile(), event.getText());
            if(startOffset2 < 0){
                startOffset2 = event.getText().length();
            }
            int endOffset2 = startOffset2+event.getText().length();
            TextAttributesKey betterColor = EditorColors. INJECTED_LANGUAGE_FRAGMENT;
//            System.out.println("Event text: "+event.getText());
//          collection.clear();
            hm.addOccurrenceHighlight(event.getEditor(), startOffset2, endOffset2, betterColor, 001, collection);
            final Notification notification = notificationGroup.createNotification( AntiCopyPasterPythonBundle.message(
                    "extract.method.refactoring.is.available"), NotificationType.INFORMATION);
            notification.addAction(NotificationAction.createSimple(
                    AntiCopyPasterPythonBundle.message("anticopypasterpython.recommendation.notification.action"),
                    callback));
            notification.notify(project);
            AntiCopyPasterUsageStatistics.getInstance(project).notificationShown();
//            EditorMouseListener mouseListener = new EditorMouseListener() {
//                @Override
//                public void mouseClicked(@NotNull EditorMouseEvent event) {
//                    if (event.getMouseEvent().getClickCount() == 1 & event.getOffset() >= startOffset && event.getOffset() <= endOffset) {
//                        System.out.println("Mouse clicked once");
//
//                        for (RangeHighlighter highlighter : collection) {
//                            hm.removeSegmentHighlighter(event.getEditor(), highlighter);
//                        }
//                    }
//                }
//            };
//            event.getEditor().addEditorMouseListener(mouseListener);
//            System.out.println("Added mouse listener");
//            didAlreadyHighlight=true;
//        }
//        else{
//            System.out.println("Already highlighted");
//            didAlreadyHighlight=false;
//        }
        ProjectSettingsState settings = ProjectSettingsState.getInstance(this.p);
        if(settings.highlightTimer != 0) {
            scheduleRemoveHighlight(project, collection, event, settings.highlightTimer);
        }
    }

    public boolean canBeExtracted(RefactoringEvent event) {
        Project project = event.getProject();
        Editor editor = event.getEditor();
        PyFile file = event.getFile();

        return PyExtractMethodHandler.checkExtraction(project, editor, file);
    }

    private Runnable getRunnableToShowSuggestionDialog(RefactoringEvent event) {
        return () -> {
            System.out.println("showing popup for refactoring task");
            String message = event.getReasonToExtract();
            if (message.isEmpty()) {
//                message = AntiCopyPasterPythonBundle.message("extract.method.to.simplify.logic.of.enclosing.method");
            }

            int startOffset = getStartOffset(event.getEditor(), event.getFile(), event.getText());
            if(startOffset==-1){    //fixes the -1 startoffset error
                startOffset=event.getText().length();
            }
            event.getEditor().getSelectionModel().setSelection(startOffset, startOffset + event.getText().length());

            int result =
                    Messages.showOkCancelDialog(message,
                            AntiCopyPasterPythonBundle.message("anticopypasterpython.recommendation.dialog.name"),
                            CommonBundle.getOkButtonText(),
                            CommonBundle.getCancelButtonText(),
                            Messages.getInformationIcon());

            //result is equal to 0 if a user accepted the suggestion and clicked on OK button, 1 otherwise
            if (result == 0) {
                scheduleExtraction(event.getProject(),
                        event.getFile(),
                        event.getEditor(),
                        event.getText());

                AntiCopyPasterUsageStatistics.getInstance(event.getProject()).extractMethodApplied();
            } else {
                AntiCopyPasterUsageStatistics.getInstance(event.getProject()).extractMethodRejected();
            }
            HighlightManager hm = HighlightManager.getInstance(event.getProject());
            for (RangeHighlighter highlighter : collection) {
                hm.removeSegmentHighlighter(event.getEditor(), highlighter);
            }
        };
    }

    public void notify(Project project, String content, Runnable callback) {
        final Notification notification = notificationGroup.createNotification(content, NotificationType.INFORMATION);
        notification.addAction(NotificationAction.createSimple(
                AntiCopyPasterPythonBundle.message("anticopypasterpython.recommendation.notification.action"),
                callback));
        notification.notify(project);
        AntiCopyPasterUsageStatistics.getInstance(project).notificationShown();
    }

    private void scheduleRemoveHighlight(Project project,Collection<RangeHighlighter> Collection,RefactoringEvent event, int timer1 ) {
        ProjectSettingsState settings = ProjectSettingsState.getInstance(this.p);
        timer.schedule(
                new HighlightTask(project, collection, event),
                timer1 * 1000
        );
    }


    private void scheduleExtraction(Project project, PsiFile file, Editor editor, String text) {
        timer.schedule(
                new ExtractionTask(editor, file, text, project),
                100
        );
    }

    public void addEvent(RefactoringEvent event) {
        this.eventsQueue.add(event);
    }

    /**
     * Calculates the metrics for the pasted code fragment and a method where the code fragment was pasted into.
     */
    private FeaturesVector calculateFeatures(RefactoringEvent event) {
        //here we know the event is not null; This means it is probably an issue where new RefactoringEvent() is being called
        PyFile file = event.getFile();
        PyFunction methodAfterPasting = event.getDestinationMethod();
        if(methodAfterPasting!=null){
            int eventBeginLine = getNumberOfLine(file, methodAfterPasting.getTextRange().getStartOffset());
            int eventEndLine = getNumberOfLine(file, methodAfterPasting.getTextRange().getEndOffset());
            MetricCalculator metricCalculator = new MetricCalculator(methodAfterPasting, event.getText(), eventBeginLine, eventEndLine);

            return metricCalculator.getFeaturesVector();
        }
        return null;
    }

    public void setProject(Project p) {
        this.p = p;
    }
    public Project getProject() {
        return p;
    }

    @Override
    public void run() {
        while (!eventsQueue.isEmpty()) {
            final PredictionModel model = getOrInitModel();
            try {
                final RefactoringEvent event = eventsQueue.poll();

                ProjectSettingsState settings = ProjectSettingsState.getInstance(event.getProject());
                ApplicationManager.getApplication().runReadAction(() -> {
                    DuplicatesInspection.InspectionResult result = inspection.resolve(event.getFile(), event.getText());
                    //System.out.println(settings.minimumDuplicateMethods);
                    //System.out.println(result.getDuplicatesCount());
                    System.out.println(result.getDuplicatesCount() < settings.minimumDuplicateMethods);
                    if (result.getDuplicatesCount() < settings.minimumDuplicateMethods) {
                        System.out.println("don't trigger");
                        return;
                    }
                    HashSet<String> variablesInCodeFragment = new HashSet<>();
                    HashMap<String, Integer> variablesCountsInCodeFragment = new HashMap<>();
                    if (!FragmentCorrectnessChecker.isCorrect(event.getProject(), event.getFile(),
                            event.getText(),
                            variablesInCodeFragment,
                            variablesCountsInCodeFragment)) {
                        return;
                    }
                    FeaturesVector featuresVector = calculateFeatures(event);
                    System.out.println(featuresVector);

                    float prediction = model.predict(featuresVector);
                    System.out.println("Prediction: " + prediction);
                    System.out.println("Threshold: " + predictionThreshold);
//                    prediction = 999999999;
//                    if (debugMetrics) {
//                        UserSettingsModel settingsModel = (UserSettingsModel) model;
//                        try (FileWriter fr = new FileWriter(logFilePath, true)) {
//                            String timestamp =
//                                    new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
//
//                            fr.write("\n-----------------------\nNEW COPY/PASTE EVENT: "
//                                    + timestamp + "\nPASTED CODE:\n"
//                                    + event.getText());
//
//                            if (prediction > predictionThreshold) {
//                                fr.write("\n\nSent Notification: True");
//                            } else {
//                                fr.write("\n\nSent Notification: False");
//                            }
//                            fr.write("\n\nSent Notification: True");
//                            fr.write("\nMETRICS\n");
//                        } catch (IOException ioe) {
//                            ioe.printStackTrace();
//                        }
//                        //settingsModel.logMetrics(logFilePath); BROKEN
//                    }

                    //System.out.println(settings.highlight);
                    if ((event.isForceExtraction() || prediction > predictionThreshold) &&
                            canBeExtracted(event)) {
                        try {
                            TimeUnit.SECONDS.sleep((int)settings.timeBuffer);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        if (settings.highlight) {
                            event.setReasonToExtract(AntiCopyPasterPythonBundle.message(
                                    "extract.method.to.simplify.logic.of.enclosing.method")); // dummy
                            highlight(event.getProject(), event,
                                    getRunnableToShowSuggestionDialog(event));
                        } else {
                            event.setReasonToExtract(AntiCopyPasterPythonBundle.message(
                                    "extract.method.to.simplify.logic.of.enclosing.method")); // dummy
                            notify(event.getProject(),
                                    AntiCopyPasterPythonBundle.message(
                                            "extract.method.refactoring.is.available"),
                                    getRunnableToShowSuggestionDialog(event)
                            );
                        }
                    }
                });
            }
            catch (Exception e) {
                LOG.error("[ACP] Can't process an event " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
