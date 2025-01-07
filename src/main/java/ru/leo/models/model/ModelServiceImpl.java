package ru.leo.models.model;

import com.google.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class ModelServiceImpl implements ModelService {
    private final Map<String, Model> models = new ConcurrentHashMap<>();
    private final ModelLoader loader;

    @Inject
    public ModelServiceImpl(ModelLoader loader) {
        this.loader = loader;
    }

    @Override
    public void loadModel(String modelName) {
        models.put(modelName, loader.loadModel(modelName));
    }

    @Override
    public Collection<String> getModels() {
        return models.keySet();
    }

    @Override
    public double[] inference(String modelName, float[][] features) {
        var model = models.get(modelName);
        if (model == null) {
            throw new NoSuchElementException("No model with name " + modelName);
        }
        return model.inference(features);
    }

    @Override
    public void close() throws Exception {
        for (var m : models.values()) {
            m.close();
        }
    }
}
