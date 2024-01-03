package org.jetbrains.research.anticopypasterpython.ide;

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RawText;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiFile;
//import com.intellij.psi.PsiMethod;

import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyFile;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.research.anticopypasterpython.checkers.FragmentCorrectnessChecker;
import org.jetbrains.research.anticopypasterpython.config.ProjectSettingsState;
import org.jetbrains.research.anticopypasterpython.statistics.AntiCopyPasterUsageStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;

import static org.jetbrains.research.anticopypasterpython.utils.PsiUtil.findMethodByOffset;
import static org.jetbrains.research.anticopypasterpython.utils.PsiUtil.getCountOfCodeLines;

/**
 * Handles any copy-paste action and checks if the pasted code fragment could be extracted into a separate method.
 */
public class AntiCopyPastePreProcessor implements CopyPastePreProcessor {
    private final DuplicatesInspection inspection = new DuplicatesInspection();
    private final Timer timer = new Timer(true);
    private final ArrayList<RefactoringNotificationTask> refactoringNotificationTask = new ArrayList<>();
    private int test = 0;
    private static final Logger LOG = Logger.getInstance(AntiCopyPastePreProcessor.class);

    /**
     * Triggers on each copy action.
     */
    @Nullable
    @Override
    public String preprocessOnCopy(PsiFile file, int[] startOffsets, int[] endOffsets, String text) {
        AntiCopyPasterUsageStatistics.getInstance(file.getProject()).onCopy();
        return null;
    }

    /**
     * Triggers on each paste action to search for duplicates and check the Extract Method refactoring opportunities
     * for a copied-pasted code fragment.
     */
    @NotNull
    @Override
    public String preprocessOnPaste(Project project, PsiFile file, Editor editor, String text, RawText rawText) {
        HashSet<String> variablesInCodeFragment = new HashSet<>();
        HashMap<String, Integer> variablesCountsInCodeFragment = new HashMap<>();
        //System.out.println("preprocessonpaste Text: " +  text); // Text of copy and paste
        //System.out.println("Triggers: " +  test);
        test++;
        //System.out.println(rawText);
        RefactoringNotificationTask rnt = getRefactoringTask(project);

        if (rnt == null) {
            rnt = new RefactoringNotificationTask(inspection, timer, project);
            refactoringNotificationTask.add(rnt);
            setCheckingForRefactoringOpportunities(rnt, project);
        }

        AntiCopyPasterUsageStatistics.getInstance(project).onPaste();

        if (editor == null || file == null || !FragmentCorrectnessChecker.isCorrect(project, file,
                text,
                variablesInCodeFragment,
                variablesCountsInCodeFragment)) {
            return text;
        }

        DataContext dataContext = DataManager.getInstance().getDataContext(editor.getContentComponent());
        @Nullable Caret caret = CommonDataKeys.CARET.getData(dataContext);
        int offset = caret == null ? 0 : caret.getOffset();

        // The issue here was that whitespaces do not have PyFunction parents, they go straight to main.
        // To solve this, we simply revert the offset from the caret back into actual text.
        while (file.findElementAt(offset).toString() == "PsiWhiteSpace")
            offset--;

        PyFunction destinationMethod = findMethodByOffset((PyFile) file, offset);

        // find number of code fragments considered as duplicated
        DuplicatesInspection.InspectionResult result = inspection.resolve((PyFile) file, text);
        if (result.getDuplicatesCount() == 0) {
            return text;
        }

        //number of lines in fragment
        int linesOfCode = getCountOfCodeLines(text);
        System.out.println("destinationMethod right before it is added to rnt:"+destinationMethod);
        rnt.addEvent(
                new RefactoringEvent((PyFile) file, destinationMethod, text, result.getDuplicatesCount(),
                        project, editor, linesOfCode));

        return text;
    }

    /**
     * Finds the RefactoringNotificationTask in the refactoringNotificationTask ArrayList that is associated with the
     * given project. Returns the RefactoringNotificationTask if it exists, and null if it does not.
     * */
     private RefactoringNotificationTask getRefactoringTask(Project project) {
        for (RefactoringNotificationTask t:refactoringNotificationTask) {
            if (t.getProject() == project) {
                return t;
            }
        }
        return null;
    }

    /**
     * Sets the regular checking for Extract Method refactoring opportunities.
     */
    private void setCheckingForRefactoringOpportunities(RefactoringNotificationTask task, Project project) {
        ProjectSettingsState settings = ProjectSettingsState.getInstance(project);
        int scheduleDelayInMs = settings.timeBuffer * 1000;

        try {
            timer.schedule(task, scheduleDelayInMs, scheduleDelayInMs);
        } catch (Exception ex) {
            LOG.error("[ACP] Failed to schedule the checking for refactorings.", ex.getMessage());
        }
    }
}