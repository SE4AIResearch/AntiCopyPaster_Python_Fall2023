package org.jetbrains.research.anticopypasterpython.metrics.features;

public class FeatureItem {
    private final Feature type;
    private double value;

    public FeatureItem(Feature type, double value) {
        this.type = type;
        this.value = value;
    }

    public int getId() {
        return type.getId();
    }

    public String getName() {
        return type.getName();
    }

    public double getValue() {
        return value;
    }

    public void setValue(double newValue) {
        this.value = newValue;
    }
}