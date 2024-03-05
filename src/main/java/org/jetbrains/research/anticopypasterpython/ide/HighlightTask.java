package org.jetbrains.research.anticopypasterpython.ide;


import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;

import java.util.Collection;
import java.util.TimerTask;

public class HighlightTask extends TimerTask {

    public Project project;
    public Collection<RangeHighlighter> collection;
    public RefactoringEvent event;
    public HighlightTask(Project project, Collection<RangeHighlighter> collection, RefactoringEvent event) {
        this.project = project;
        this.collection = collection;
        this.event = event;
    }

    @Override
    public void run() {
        ApplicationManager.getApplication().invokeLater(() -> {
            HighlightManager hm = HighlightManager.getInstance(project);
            for (RangeHighlighter highlighter : collection) {
                hm.removeSegmentHighlighter(event.getEditor(), highlighter);
            }
        });
    }
}
