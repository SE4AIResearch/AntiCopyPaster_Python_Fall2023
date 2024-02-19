package org.jetbrains.research.anticopypasterpython.ide;

import com.intellij.CommonBundle;
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

//import com.intellij.refactoring.extractMethod.ExtractMethodProcessor;
//import com.intellij.refactoring.extractMethod.PrepareFailedException;

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

//import static com.intellij.refactoring.extractMethod.ExtractMethodHandler.getProcessor;
import static com.jetbrains.python.refactoring.extractmethod.PyExtractMethodHandler.*;
import static org.jetbrains.research.anticopypasterpython.utils.PsiUtil.*;

import org.jetbrains.research.anticopypasterpython.utils.PyExtractMethodHandler;

/**
 * Shows a notification about discovered Extract Method refactoring opportunity.
 */
public class RefactoringNotificationTask extends TimerTask {
    private static final Logger LOG = Logger.getInstance(RefactoringNotificationTask.class);
    private static float tensorflowPredictionThreshold = 0.00005f; // certainty threshold for TensorFlow model
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


    public RefactoringNotificationTask(DuplicatesInspection inspection, Timer timer, Project p) {
        this.inspection = inspection;
        this.timer = timer;
        this.p = p;
        this.logFilePath = p.getBasePath() + "/.idea/anticopypaster-refactoringSuggestionsLog.log";
    }

    private PredictionModel getOrInitModel() {
        ProjectSettingsState settings = ProjectSettingsState.getInstance(this.p);
        boolean tensorFlowModel = settings.tensorFlowEnabled;
        PredictionModel model = this.model;

        // It makes sense to decouple thresholds between the settings model and tensor model.
        if (tensorFlowModel) {
            predictionThreshold = tensorflowPredictionThreshold;

            if (model != null && model instanceof UserSettingsModel)
                model = null;

            if (model == null)
                model = this.model = new TensorflowModel();
        }
        else {
            predictionThreshold = usersettingsPredictionThreshold;

            if (model != null && model instanceof TensorflowModel)
                model = null;

            if (model == null) {
                model = this.model = new UserSettingsModel(new MetricsGatherer(p), p);
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

    public boolean canBeExtracted(RefactoringEvent event) {
        Project project = event.getProject();
        Editor editor = event.getEditor();
        PyFile file = event.getFile();

        return PyExtractMethodHandler.checkExtraction(project, editor, file);
    }

    private Runnable getRunnableToShowSuggestionDialog(RefactoringEvent event) {
        return () -> {
            String message = event.getReasonToExtract();
            if (message.isEmpty()) {
//                message = AntiCopyPasterPythonBundle.message("extract.method.to.simplify.logic.of.enclosing.method");
            }

            int startOffset = getStartOffset(event.getEditor(), event.getFile(), event.getText());
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
        int eventBeginLine = getNumberOfLine(file,
                methodAfterPasting.getTextRange().getStartOffset());
        int eventEndLine = getNumberOfLine(file,
                methodAfterPasting.getTextRange().getEndOffset());
        MetricCalculator metricCalculator =
                new MetricCalculator(methodAfterPasting, event.getText(),
                        eventBeginLine, eventEndLine);

        return metricCalculator.getFeaturesVector();
    }

    public void setProject(Project p) {
        this.p = p;
    }
    public Project getProject() {
        return p;
    }

    @Override
    public void run() {
        // System.out.println("87");
        while (!eventsQueue.isEmpty()) {
            // System.out.println("89");
            final PredictionModel model = getOrInitModel();
            //System.out.println("Line 92");
            try {
                final RefactoringEvent event = eventsQueue.poll();
                ApplicationManager.getApplication().runReadAction(() -> {
                    DuplicatesInspection.InspectionResult result = inspection.resolve(event.getFile(), event.getText());
                    // This only triggers if there are duplicates found in at least as many
                    // methods as specified by the user in configurations.

                    ProjectSettingsState settings = ProjectSettingsState.getInstance(event.getProject());

                    if (!settings.tensorFlowEnabled && result.getDuplicatesCount() < settings.minimumDuplicateMethods) {
                        return;
                    }
                    HashSet<String> variablesInCodeFragment = new HashSet<>();
                    HashMap<String, Integer> variablesCountsInCodeFragment = new HashMap<>();
                    System.out.println("Notification task 101");
                    if (!FragmentCorrectnessChecker.isCorrect(event.getProject(), event.getFile(),
                            event.getText(),
                            variablesInCodeFragment,
                            variablesCountsInCodeFragment)) {
                        System.out.println("Notification task 110");
                        return;
                    }
                    //System.out.println("event in RefactoringNotificationTask from eventsQueue:" + event.getText());
                    FeaturesVector featuresVector = calculateFeatures(event);
                    //System.out.println("Features vector: " + featuresVector.toString());

                    float prediction = model.predict(featuresVector);
                    System.out.println("Prediction: " + prediction);
                    System.out.println("Threshold: " + predictionThreshold);
                    //float prediction = 999999999;
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
                    event.setReasonToExtract(AntiCopyPasterPythonBundle.message(
                            "extract.method.to.simplify.logic.of.enclosing.method")); // dummy

//                    if ((event.isForceExtraction() || prediction > predictionThreshold) && canBeExtracted(event)) {
//                        //System.out.println("Notification task 138");
//                        if (true) {
//                            notify(event.getProject(),
//                                    AntiCopyPasterPythonBundle.message(
//                                            "extract.method.refactoring.is.available"),
//                                    getRunnableToShowSuggestionDialog(event)
//                            );
//                        }
//                    }
                    if ((event.isForceExtraction() || prediction > predictionThreshold) &&
                            canBeExtracted(event)) {
                        System.out.println("EXTRACTION");
                        notify(event.getProject(),
                                AntiCopyPasterPythonBundle.message(
                                        "extract.method.refactoring.is.available"),
                                getRunnableToShowSuggestionDialog(event)
                        );
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