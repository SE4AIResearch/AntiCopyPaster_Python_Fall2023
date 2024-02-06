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

/**
 * Shows a notification about discovered Extract Method refactoring opportunity.
 */
public class RefactoringNotificationTask extends TimerTask {
    private static final Logger LOG = Logger.getInstance(RefactoringNotificationTask.class);
    private static final float predictionThreshold = 0.5f; // certainty threshold for models
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
        PredictionModel model = this.model;
        //System.out.println("Line 67");
        System.out.println(model == null);
        System.out.println("WAKE ME UP INSIDE");
        if (model == null) {
            model = this.model = new UserSettingsModel(new MetricsGatherer(p), p);
            System.out.println("WAKE ME UP INSIDE2");
           if(debugMetrics){
               UserSettingsModel settingsModel = (UserSettingsModel) model;
              try(FileWriter fr = new FileWriter(logFilePath, true)){
                   String timestamp =
                            new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
                    fr.write("\n-----------------------\nInitial Metric Thresholds: " +
                            timestamp + "\n");
                   System.out.println("Line 76");
                } catch(IOException ioe) { ioe.printStackTrace(); }
                System.out.println("InitMOdelError");
                settingsModel.logThresholds(logFilePath);
           }
        }
        System.out.println(model == null);
        return model;
    }

    @Override
    public void run() {
        System.out.println("87");
        while (!eventsQueue.isEmpty()) {
            System.out.println("89");
            final PredictionModel model = getOrInitModel();
            System.out.println("Line 92");
            try {
                final RefactoringEvent event = eventsQueue.poll();
                ApplicationManager.getApplication().runReadAction(() -> {
                    System.out.println("94");
                    DuplicatesInspection.InspectionResult result = inspection.resolve( event.getFile(), event.getText());
                    // This only triggers if there are duplicates found in at least as many
                    // methods as specified by the user in configurations.

                    ProjectSettingsState settings = ProjectSettingsState.getInstance(event.getProject());

//                    if (result.getDuplicatesCount() < settings.minimumDuplicateMethods) {
//                        return;
//                    }
                    HashSet<String> variablesInCodeFragment = new HashSet<>();
                    HashMap<String, Integer> variablesCountsInCodeFragment = new HashMap<>();
                    System.out.println("Ntofication task 101");
                    if (!FragmentCorrectnessChecker.isCorrect(event.getProject(), event.getFile(),
                            event.getText(),
                            variablesInCodeFragment,
                            variablesCountsInCodeFragment)) {
                        System.out.println("Ntofication task 110");
                        return;
                    }
                    System.out.println("event in RefactoringNotificationTask from eventsQueue:"+event.getText());
                    FeaturesVector featuresVector = calculateFeatures(event);
                    System.out.println("Features vector: "+featuresVector.toString());

//                    float prediction = model.predict(featuresVector);
//                    if(debugMetrics){
//                        UserSettingsModel settingsModel = (UserSettingsModel) model;
//                        try(FileWriter fr = new FileWriter(logFilePath, true)){
//                            String timestamp =
//                                    new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
//
//                            fr.write("\n-----------------------\nNEW COPY/PASTE EVENT: "
//                                    + timestamp + "\nPASTED CODE:\n"
//                                    + event.getText());
//
////                            if(prediction > predictionThreshold){
////                                fr.write("\n\nSent Notification: True");
////                            }else{
////                                fr.write("\n\nSent Notification: False");
////                            }
//                            fr.write("\n\nSent Notification: True");
//                            fr.write("\nMETRICS\n");
//                        } catch(IOException ioe) { ioe.printStackTrace(); }
//                        settingsModel.logMetrics(logFilePath);
//                    }
                    event.setReasonToExtract(AntiCopyPasterPythonBundle.message(
                            "extract.method.to.simplify.logic.of.enclosing.method")); // dummy

                    //if ((event.isForceExtraction() || prediction > predictionThreshold) &&
                     //       canBeExtracted(event)) {
                    System.out.println("Ntofication task 138");
                    if(true){
                        notify(event.getProject(),
                                AntiCopyPasterPythonBundle.message(
                                        "extract.method.refactoring.is.available"),
                                getRunnableToShowSuggestionDialog(event)
                        );
                    }
                });
            } catch (Exception e) {
                LOG.error("[ACP] Can't process an event " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public boolean canBeExtracted(RefactoringEvent event) {
        boolean canBeExtracted;
        int startOffset = getStartOffset(event.getEditor(), event.getFile(), event.getText());
        PyElement[] elementsInCodeFragment = getElements(event.getProject(), event.getFile(),
                startOffset, startOffset + event.getText().length());
        PyExtractMethodUtil processor;// = getProcessor(event.getProject(), elementsInCodeFragment,
//                event.getFile(), false);
//        if (processor == null) return false;
        try {
//            canBeExtracted = processor.prepare(null);
//            processor.findOccurrences();
        } catch (/*PrepareFailedException*/ Exception e) {
            LOG.error("[ACP] Failed to check if a code fragment can be extracted.", e.getMessage());
//            return false;
        }

        return true;//canBeExtracted;
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
<<<<<<< Updated upstream
=======

    @Override
    public void run() {
        //System.out.println("87");
        while (!eventsQueue.isEmpty()) {
           //System.out.println("89");
            final PredictionModel model = getOrInitModel();
           // System.out.println("Line 92");
            try {
                final RefactoringEvent event = eventsQueue.poll();
                ApplicationManager.getApplication().runReadAction(() -> {
                  //  System.out.println("94");
                    DuplicatesInspection.InspectionResult result = inspection.resolve(event.getFile(), event.getText());
                    // This only triggers if there are duplicates found in at least as many
                    // methods as specified by the user in configurations.

                    ProjectSettingsState settings = ProjectSettingsState.getInstance(event.getProject());

                    if (result.getDuplicatesCount() < settings.minimumDuplicateMethods) {
                        return;
                    }
                    HashSet<String> variablesInCodeFragment = new HashSet<>();
                    HashMap<String, Integer> variablesCountsInCodeFragment = new HashMap<>();
                    //System.out.println("Notification task 101");
                    if (!FragmentCorrectnessChecker.isCorrect(event.getProject(), event.getFile(),
                            event.getText(),
                            variablesInCodeFragment,
                            variablesCountsInCodeFragment)) {
                      //  System.out.println("Notification task 110");
                        return;
                    }
                  //  System.out.println("event in RefactoringNotificationTask from eventsQueue:" + event.getText());
                    FeaturesVector featuresVector = calculateFeatures(event);
                  //  System.out.println("Features vector: " + featuresVector.toString());

                    float prediction = model.predict(featuresVector);
                    //float prediction = 999999999;
                    System.out.println(prediction);
                    if (debugMetrics) {
                        UserSettingsModel settingsModel = (UserSettingsModel) model;
                        try (FileWriter fr = new FileWriter(logFilePath, true)) {
                            String timestamp =
                                    new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());

                            fr.write("\n-----------------------\nNEW COPY/PASTE EVENT: "
                                    + timestamp + "\nPASTED CODE:\n"
                                    + event.getText());

                            if (prediction > predictionThreshold) {
                                fr.write("\n\nSent Notification: True");
                            } else {
                                fr.write("\n\nSent Notification: False");
                            }
                            fr.write("\n\nSent Notification: True");
                            fr.write("\nMETRICS\n");
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                        //settingsModel.logMetrics(logFilePath); BROKEN
                    }
                    event.setReasonToExtract(AntiCopyPasterPythonBundle.message(
                            "extract.method.to.simplify.logic.of.enclosing.method")); // dummy

                    if ((event.isForceExtraction() || prediction > predictionThreshold) && canBeExtracted(event)) {
                       // System.out.println("Notification task 138");
                        if (true) {
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
>>>>>>> Stashed changes
}
