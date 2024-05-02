package org.jetbrains.research.anticopypasterpython.utils;

import com.intellij.openapi.project.Project;
import org.jetbrains.research.anticopypasterpython.config.ProjectSettingsState;
import org.jetbrains.research.anticopypasterpython.metrics.features.Feature;
import org.jetbrains.research.anticopypasterpython.metrics.features.FeaturesVector;

import java.util.List;
import org.jetbrains.research.anticopypasterpython.config.advanced.AdvancedProjectSettingsComponent.PythonKeywords;

public class KeywordsMetrics extends Flag {

    public KeywordsMetrics(List<FeaturesVector> featuresVectorList, Project project){
        super(featuresVectorList, project);
    }

    /**
     * Keywords uses features index 16 to 77, according to this scheme:
         * Even indices: Total keywords
         * (DEFAULT) Odd indices: Keyword density
         * Each even-odd pair of indices corresponds to one Java keyword.
     */
    protected void setSelectedMetrics(){
        ProjectSettingsState settings = ProjectSettingsState.getInstance(project);
        int metricNum = 16;
        for(PythonKeywords keyword : PythonKeywords.values()) {
            if (settings.activeKeywords.get(keyword)) {
                // Keyword is selected to be counted by the plugin.
                // Even indices indicate the total count of the current keyword.
                if (settings.measureKeywordsTotal[0]) {
                    selectedMetrics.add(Feature.fromId(metricNum));
                    if (settings.measureKeywordsTotal[1])
                        requiredMetrics.add(Feature.fromId(metricNum));
                }
                metricNum++;
                // Iterate metricNum by one to consider the density feature for the same keyword.
                if (settings.measureKeywordsDensity[0]) {
                    selectedMetrics.add(Feature.fromId(metricNum));
                    if (settings.measureKeywordsDensity[1])
                        requiredMetrics.add(Feature.fromId(metricNum));
                }
                metricNum++;
            } else metricNum += 2;
        }
        numFeatures = selectedMetrics.size();
    }


    /**
     * Required override function from Flag. Gets the sensitivity for this metric
     * by grabbing its appropriate settings from this project's ProjectSettingsState.
     */
    @Override
    protected int getSensitivity() {
        return ProjectSettingsState.getInstance(project).keywordsSensitivity;
    }

    /**
     * Easier to use logMetric
     * @param filepath path to the log file
     */
    @Override
    public void logMetric(String filepath){
        logMetric(filepath, "Keywords");
    }

    /**
     * Easier to use logThresholds
     * @param filepath path to the log file
     */
    @Override
    public void logThresholds(String filepath){
        logThresholds(filepath, "Keywords");
    }
}