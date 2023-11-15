package org.jetbrains.research.anticopypasterpython.metrics.features;

import java.util.ArrayList;
import java.util.List;

public class FeaturesVector {
    private final List<FeatureItem> features = new ArrayList<>();
    private final int dimension;

    /**
     * Creates a vector of size `dimension` and fills it with pairs (`feature`;zero).
     */
    public FeaturesVector(int dimension) {
        this.dimension = dimension;
        for (int i = 0; i < dimension; i++) {
            features.add(new FeatureItem(Feature.fromId(i), 0));
        }
    }

    public void setFeature(final FeatureItem item) {
        features.set(item.getId(), item);
    }

    public int getDimension() {
        return dimension;
    }

    public double getFeatureValue(Feature toSearch) {
        return features.get(toSearch.getId()).getValue();
    }

    public List<FeatureItem> getItems() {
        return features;
    }

    /**
     * Returns List of floats, corresponding to computed features.
     */
    public List<Float> buildList() {
        List<Float> result = new ArrayList<>();
        for (int i = 0; i < dimension; ++i) {
            result.add((float) features.get(i).getValue());
        }
        return result;
    }

    /**
     * Returns Array of floats, corresponding to computed features.
     */
    public float[] buildArray() {
        float[] floatArray = new float[dimension];

        for (int i = 0; i < dimension; i++) {
            float to_insert = (float) features.get(i).getValue();
            floatArray[i] = to_insert;
        }

        return floatArray;
    }

    /**
     * Returns List of floats, corresponding to computed features,
     * indices of which are passed in `indexList`.
     */
    public List<Float> buildCroppedVector(List<Integer> indexList) {
        List<Float> result = new ArrayList<>();
        for (int i : indexList) {
            result.add((float) features.get(i).getValue());
        }
        return result;
    }
}
