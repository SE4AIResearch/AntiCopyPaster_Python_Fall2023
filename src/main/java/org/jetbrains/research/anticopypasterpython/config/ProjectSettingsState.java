package org.jetbrains.research.anticopypasterpython.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.research.anticopypaster.config.advanced.AdvancedProjectSettingsComponent.JavaKeywords;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

@Service(Service.Level.PROJECT)
@State(
        name = "org.jetbrains.research.anticopypaster.config.ProjectSettingsState",
        storages = @Storage("anticopypaster-plugin.xml")
)
public final class ProjectSettingsState implements PersistentStateComponent<ProjectSettingsState> {

    // PRIMARY SETTINGS STATES
    public int minimumDuplicateMethods = 2;
    public int timeBuffer = 10;
    public boolean keywordsEnabled = true, couplingEnabled = true, sizeEnabled = true, complexityEnabled = true,
            keywordsRequired = true, couplingRequired = true, sizeRequired = true, complexityRequired = true;
    public int keywordsSensitivity = 50, couplingSensitivity = 50, sizeSensitivity = 50, complexitySensitivity = 50;

    // ADVANCED SETTINGS STATES
    // Each boolean array of two elements follow this scheme {boolean submetric_enabled, boolean submetric_required}

    // Keyword Metric
    public Boolean[] measureKeywordsTotal = {false, false}, measureKeywordsDensity = {true, true};
    public EnumMap<JavaKeywords, Boolean> activeKeywords = new EnumMap<>(JavaKeywords.class);
    {
        for (JavaKeywords keyword : JavaKeywords.values()) {
            activeKeywords.put(keyword, true);
        }
    }

    // Coupling Metric
    public Boolean[] measureCouplingTotal = {false, false}, measureCouplingDensity = {true, true};
    public Boolean[] measureTotalConnectivity = {true, true}, measureFieldConnectivity = {false, false}, measureMethodConnectivity = {false, false};

    // Complexity Metric
    public Boolean[] measureComplexityTotal = {false, false}, measureComplexityDensity = {true, true},
            measureMethodDeclarationArea = {false, false}, measureMethodDeclarationDepthPerLine = {false, false};

    // Size Metric
    public Boolean[] measureSizeByLines = {true, true}, measureSizeBySymbols = {false, false}, measureSizeBySymbolsPerLine = {false, false};
    public Boolean[] measureTotalSize = {true, true}, measureMethodDeclarationSize = {false, false};

    // Statistics collection database credentials
    public String statisticsUsername = null;
    public boolean statisticsPasswordIsSet = false;

    public static ProjectSettingsState getInstance(Project project) {
        return project.getService(ProjectSettingsState.class);
    }

    @Nullable
    @Override
    public ProjectSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ProjectSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}