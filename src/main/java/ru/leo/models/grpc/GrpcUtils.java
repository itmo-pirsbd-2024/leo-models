package ru.leo.models.grpc;

import com.google.protobuf.GeneratedMessage;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcUtils {
    private static final Logger log = LoggerFactory.getLogger(GrpcUtils.class);

    public static <T extends GeneratedMessage, V extends GeneratedMessage> void req(
        String method,
        T request,
        StreamObserver<V> responseObserver,
        Function<T, V> responseFunction
    ) {
        log.info("{} request: {}", method, request);

        V response;
        try {
            response = responseFunction.apply(request);
        } catch (Exception e) {
            log.error("Error during method {}", method, e);
            responseObserver.onError(Status.INTERNAL.withCause(e)
                .withDescription(e.getMessage())
                .asRuntimeException());
            return;
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
