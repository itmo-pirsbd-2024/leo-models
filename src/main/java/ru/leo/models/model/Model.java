package ru.leo.models.model;

public interface Model extends AutoCloseable {
    double[] inference(float[][] features);
}
