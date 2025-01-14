package ru.leo.models.grpc;

import static ru.leo.models.grpc.GrpcUtils.req;

import com.google.inject.Inject;
import io.grpc.stub.StreamObserver;
import java.util.List;
import ru.leo.models.model.ModelService;

public class ModelServiceGrpcImpl extends ModelsServiceGrpc.ModelsServiceImplBase {
    private final ModelService modelService;

    @Inject
    public ModelServiceGrpcImpl(ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public void loadModel(LoadModelRequest request, StreamObserver<LoadModelResponse> responseObserver) {
        req("loadModel", request, responseObserver,
            r -> {
                modelService.loadModel(r.getModelName());
                return LoadModelResponse.getDefaultInstance();
            }
        );
    }

    @Override
    public void modelList(ModelListRequest request, StreamObserver<ModelListResponse> responseObserver) {
        req("modelList", request, responseObserver,
            r -> ModelListResponse.newBuilder()
                .addAllModelNames(
                    modelService.getModels()
                ).build()
        );
    }

    @Override
    public void modelInference(ModelInferenceRequest request, StreamObserver<ModelInferenceResponse> responseObserver) {
        req("modelInference", request, responseObserver,
            r -> {
                var res = modelService.inference(r.getModelName(), toFeaturesArr(r.getFeatureVectorsList()));
                var builder = ModelInferenceResponse.newBuilder();
                for (var prediction : res) {
                    builder.addPredictions(prediction);
                }
                return builder.build();
            }
        );
    }

    private static float[][] toFeaturesArr(List<FeatureVector> featureVectors) {
        float[][] arr = new float[featureVectors.size()][featureVectors.getFirst().getFeaturesCount()];
        for (int i = 0; i < featureVectors.size(); i++) {
            var vector = featureVectors.get(i);
            for (int j = 0; j < vector.getFeaturesCount(); j++) {
                arr[i][j] = vector.getFeatures(j);
            }
        }
        return arr;
    }
}
