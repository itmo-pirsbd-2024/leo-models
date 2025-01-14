package ru.leo.models.model;

import ai.catboost.CatBoostError;
import ai.catboost.CatBoostModel;
import com.google.inject.Inject;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelLoaderImpl implements ModelLoader {
    private static final String BUCKET = "leo-models";
    private static final Logger log = LoggerFactory.getLogger(ModelLoaderImpl.class);

    private final MinioClient minioClient;

    @Inject
    public ModelLoaderImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
        initBucket();
    }

    private void initBucket() {
        boolean bucketExists;
        try {
            bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET).build());
        } catch (Exception e) {
            log.error("Can't check if {} bucket exists in minio.", BUCKET, e);
            throw new RuntimeException(e);
        }
        if (!bucketExists) {
            try {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());
            } catch (Exception e) {
                log.error("Cant make bucket.", e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Model loadModel(String modelName) {
        var catBoostModel = loadCatboost(modelName);

        return new Model() {
            @Override
            public double[] inference(float[][] features) {
                if (features == null) {
                    throw new IllegalArgumentException("Null in features");
                }
                if (features.length == 0) {
                    throw new IllegalArgumentException("Empty features array");
                }
                if (catBoostModel.getUsedNumericFeatureCount() > features[0].length) {
                    log.error("Catboost features size is bigger to given feature vector size. {} > {}",
                        catBoostModel.getUsedNumericFeatureCount(), features[0].length);
                    throw new IllegalArgumentException("Catboost features size is not equal to given feature vector size");
                }
                try {
                    return catBoostModel.predict(features, (String[][]) null).copyRowMajorPredictions();
                } catch (CatBoostError e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void close() throws Exception {
                catBoostModel.close();
            }
        };
    }

    private CatBoostModel loadCatboost(String modelName) {
        GetObjectResponse cbm;
        try {
            cbm = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(BUCKET)
                    .object(modelName)
                    .build()
            );
        } catch (Exception e) {
            log.error("Error downloading model {} from minio", modelName, e);
            throw new IllegalArgumentException("Error downloading model from minio");
        }

        CatBoostModel catBoostModel;
        try {
            catBoostModel = CatBoostModel.loadModel(cbm);
        } catch (CatBoostError | IOException e) {
            log.error("Error with creating catboost model {}", modelName);
            throw new IllegalStateException("Error with creating catboost model");
        }
        return catBoostModel;
    }
}
