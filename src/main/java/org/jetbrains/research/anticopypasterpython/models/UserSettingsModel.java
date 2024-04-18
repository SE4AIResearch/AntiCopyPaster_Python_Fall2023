package org.jetbrains.research.anticopypasterpython.models;

import com.intellij.openapi.project.Project;
import org.jetbrains.research.anticopypasterpython.config.ProjectSettingsState;
import org.jetbrains.research.anticopypasterpython.metrics.features.FeaturesVector;
import org.jetbrains.research.anticopypasterpython.utils.*;

import java.util.List;


public class UserSettingsModel extends PredictionModel{

    private Flag keywordsMetrics;
    private Flag sizeMetrics;
    private Flag complexityMetrics;
    private Flag couplingMetrics;
    private Project project;

    public UserSettingsModel(MetricsGatherer mg, Project project) {
        this.project = project;
        initMetricsGathererAndMetricsFlags(mg);
    }

    /**
     * Helper initialization method for the metrics gatherer.
     * This is a separate method so that if we ever wanted to have the metrics
     * gatherer regather metrics and update the values in the sensitivity
     * thresholds
     */
    public void initMetricsGathererAndMetricsFlags(MetricsGatherer mg) {
        mg.setProject(project);

        List<FeaturesVector> methodMetrics = mg.getMethodsMetrics();
        this.keywordsMetrics = new KeywordsMetrics(methodMetrics, project);
        this.complexityMetrics = new ComplexityMetrics(methodMetrics, project);
        this.sizeMetrics = new SizeMetrics(methodMetrics, project);
        this.couplingMetrics = new CouplingMetrics(methodMetrics, project);
    }

    /**
     * Returns a value higher than 0.5 if the task satisfied the requirements
     * to be extracted, lower than 0.5 means the notification will not appear.
     * This is currently hardcoded to return 1 until the metrics category logic
     * has been implemented.
     */
    @Override
    public float predict(FeaturesVector featuresVector) {
        if (sizeMetrics == null || complexityMetrics == null || keywordsMetrics == null || couplingMetrics == null)
            return 0;

        ProjectSettingsState settings = ProjectSettingsState.getInstance(project);

        System.out.println("Enables:\n");
        System.out.println(settings.sizeEnabled);
        System.out.println(settings.complexityEnabled);
        System.out.println(settings.keywordsEnabled);
        System.out.println(settings.couplingEnabled);
        System.out.println("");
        boolean sizeTriggered = settings.sizeEnabled && sizeMetrics.isFlagTriggered(featuresVector);
        boolean complexityTriggered = settings.complexityEnabled && complexityMetrics.isFlagTriggered(featuresVector);
        boolean keywordsTriggered = settings.keywordsEnabled && keywordsMetrics.isFlagTriggered(featuresVector);
        boolean couplingTriggered = settings.couplingEnabled && couplingMetrics.isFlagTriggered(featuresVector);
        System.out.println("Triggers:\n");
        System.out.println(sizeTriggered);
        System.out.println(complexityTriggered);
        System.out.println(keywordsTriggered);
        System.out.println(couplingTriggered);
        System.out.println("");
        System.out.println("Requires:\n");
        System.out.println(settings.sizeRequired);
        System.out.println(settings.complexityEnabled);
        System.out.println(settings.keywordsRequired);
        System.out.println(settings.couplingRequired);
        System.out.println("");

        boolean shouldNotify = sizeTriggered || complexityTriggered || keywordsTriggered || couplingTriggered;
        System.out.println("Shouldnotify:\n");
        System.out.println(shouldNotify);
        if (shouldNotify) {
            if (!sizeTriggered && settings.sizeRequired)
                shouldNotify = false;
            else if (!complexityTriggered && settings.complexityRequired)
                shouldNotify = false;
            else if (!keywordsTriggered && settings.keywordsRequired)
                shouldNotify = false;
            else if (!couplingTriggered && settings.couplingRequired)
                shouldNotify = false;
        }
        System.out.println(shouldNotify);

        return shouldNotify ? 1 : 0;
    }

    /**
     * This function logs all the pertinent metrics info for
     * a copy/paste event
     * @param filepath the filepath to the log file
     */
    public void logMetrics(String filepath) {
        this.complexityMetrics.logMetric(filepath);
        this.keywordsMetrics.logMetric(filepath);
        this.sizeMetrics.logMetric(filepath);
        this.couplingMetrics.logMetric(filepath);
    }

    /**
     * This function logs all the metrics thresholds
     * @param filepath the filepath to the log file
     */
    public void logThresholds(String filepath) {
        this.complexityMetrics.logThresholds(filepath);
        this.keywordsMetrics.logThresholds(filepath);
        this.sizeMetrics.logThresholds(filepath);
        this.couplingMetrics.logMetric(filepath);
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
