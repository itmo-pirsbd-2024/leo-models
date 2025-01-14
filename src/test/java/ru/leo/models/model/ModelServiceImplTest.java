package ru.leo.models.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ModelServiceImplTest {
    @Test
    void testModelService() throws Exception {
        var modelMock = mock(Model.class);
        var expectedPrediction = new double[] {1, 1};
        when(modelMock.inference(any())).thenReturn(expectedPrediction);
        var modelLoader = mock(ModelLoader.class);
        when(modelLoader.loadModel(anyString())).thenReturn(modelMock);

        try (var modelService = new ModelServiceImpl(modelLoader)) {
            modelService.loadModel("smth");

            assertThat(modelService.getModels()).isEqualTo(Set.of("smth"));
            assertThat(modelService.inference("smth", new float[0][0])).isEqualTo(expectedPrediction);
            assertThatThrownBy(() -> modelService.inference("unknown", new float[0][0]))
                .isInstanceOf(NoSuchElementException.class);
        }
    }
}