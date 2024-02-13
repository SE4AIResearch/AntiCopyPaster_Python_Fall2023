package org.jetbrains.research.anticopypasterpython.ide;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

//import com.intellij.psi.PsiMethod;

import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyFile;

/**
 * Contains information about the code fragment that is recommended for extraction into a separate method.
 */
public class RefactoringEvent {
    private final PyFile file;
    private final PyFunction destinationMethod;
    private final String text;
    private final int matches;
    private final Project project;
    private final Editor editor;
    private final int linesOfCode;
    private boolean forceExtraction = false;
    private String reasonToExtract;

    public RefactoringEvent(PyFile file, PyFunction destinationMethod, String text, int matches,
                            Project project,
                            Editor editor,
                            int linesOfCode
    ) {
        this.file = file;
        this.destinationMethod = destinationMethod;
        this.text = text;
        this.matches = matches;
        this.project = project;
        this.editor = editor;
        this.linesOfCode = linesOfCode;
       // System.out.println("destinationMethod in RefactoringEvent constructor: "+destinationMethod);
    }

    public void setForceExtraction(boolean forceExtraction) {
        this.forceExtraction = forceExtraction;
    }

    public void setReasonToExtract(String message) {
        this.reasonToExtract = message;
    }

    public boolean isForceExtraction() {
        return forceExtraction;
    }

    public String getReasonToExtract() {
        return reasonToExtract;
    }

    public PyFile getFile() {
        return file;
    }

    public PyFunction getDestinationMethod() {
        return destinationMethod;
    }

    public String getText() {
        return text;
    }

    public int getMatches() {
        return matches;
    }

    public Project getProject() {
        return project;
    }

    public Editor getEditor() {
        return editor;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }
}