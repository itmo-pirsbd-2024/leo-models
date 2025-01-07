package ru.leo.models;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.protobuf.services.ProtoReflectionServiceV1;
import io.minio.MinioClient;
import ru.leo.models.grpc.ModelServiceGrpcImpl;
import ru.leo.models.model.ModelLoader;
import ru.leo.models.model.ModelLoaderImpl;
import ru.leo.models.model.ModelService;
import ru.leo.models.model.ModelServiceImpl;

public class ApplicationModule extends AbstractModule {
    private static final int SERVER_PORT = 8080;
    private static final String MINIO_ENDPOINT = "http://localhost:9000";
    private static final String MINIO_ACCESS_KEY = "admin";
    private static final String MINIO_SECRET_KEY = "password";

    @Override
    protected void configure() {
        bind(ModelLoader.class).to(ModelLoaderImpl.class).asEagerSingleton();
        bind(ModelService.class).to(ModelServiceImpl.class).asEagerSingleton();
        bind(ModelServiceGrpcImpl.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    Server modelServer(ModelServiceGrpcImpl modelServiceGrpc) {
        return ServerBuilder
            .forPort(SERVER_PORT)
            .addService(ProtoReflectionService.newInstance())
            .addService(modelServiceGrpc)
            .build();
    }

    @Provides
    @Singleton
    private static MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(MINIO_ENDPOINT)
            .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
            .build();
    }
}
