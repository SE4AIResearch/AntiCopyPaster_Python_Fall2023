package org.jetbrains.research.anticopypasterpython.models;

import org.jetbrains.research.anticopypaster.metrics.features.FeaturesVector;

public abstract class PredictionModel {
    public abstract float predict(FeaturesVector featuresVector);
}
