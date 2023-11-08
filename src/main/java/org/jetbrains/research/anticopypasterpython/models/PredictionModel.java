package org.jetbrains.research.anticopypasterpython.models;

import org.jetbrains.research.anticopypasterpython.metrics.features.FeaturesVector;

public abstract class PredictionModel {
    public abstract float predict(FeaturesVector featuresVector);
}
