package ru.leo.models.model;

import java.util.Collection;

public interface ModelService extends AutoCloseable {
    void loadModel(String modelName);

    Collection<String> getModels();

    double[] inference(String modelName, float[][] features);
}
