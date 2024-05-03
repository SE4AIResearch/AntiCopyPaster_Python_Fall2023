package org.jetbrains.research.anticopypasterpython.utils;

import com.intellij.openapi.project.Project;
import org.jetbrains.research.anticopypasterpython.config.ProjectSettingsState;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class providing a mocked Pycharm project whose only functionality is returning a provided
 * {@code ProjectSettingsState}. More lightweight than simulating a full Pycharm project.
 */
public class ProjectMock {

    @Mock
    private Project mockProject;

    public ProjectMock(ProjectSettingsState settings) {
        mockProject = mock(Project.class);
        // Required for ProjectSettingsState.getInstance to work
        when(mockProject.getService(eq(ProjectSettingsState.class))).thenReturn(settings);
    }

    public Project getMock() {
        return mockProject;
    }
}
