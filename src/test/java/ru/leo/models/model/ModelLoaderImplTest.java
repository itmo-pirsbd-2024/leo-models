package ru.leo.models.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ModelLoaderImplTest {
    private static final Path TEST_PATH = Paths.get("src", "test", "resources", "models");
    private static final Path MODEL_PATH = TEST_PATH.resolve("model.cbm");
    private static final Path FEATURES_PATH = TEST_PATH.resolve("features.csv");
    private static final Path EXPECTED_PREDICTIONS_PATH = TEST_PATH.resolve("expected_predictions.csv");

    private InputStream model;
    private ModelLoader modelLoader;

    @BeforeEach
    void beforeEach() throws Exception {
        model = Files.newInputStream(MODEL_PATH);
        MinioClient minioClient = mock(MinioClient.class);
        when(minioClient.getObject(any()))
            .thenReturn(new GetObjectResponse(null, null, null, null, model));
        modelLoader = new ModelLoaderImpl(minioClient);
    }

    @Test
    void testPredictions() throws Exception {
        var features = TestUtil.readNumeric(FEATURES_PATH);
        var expected = TestUtil.readPredictions(EXPECTED_PREDICTIONS_PATH);
        try (var catboost = modelLoader.loadModel("smth")) {
            var result = catboost.inference(features);
            assertThat(result).isEqualTo(expected);
        }
    }

    @Test
    void testBadFeatures() throws Exception {
        try (var catboost = modelLoader.loadModel("smth")) {
            assertThatThrownBy(() -> catboost.inference(null))
                .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> catboost.inference(new float[0][0]))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @AfterEach
    void afterEach() throws IOException {
        model.close();
    }
}