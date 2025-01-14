package ru.leo.models;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.grpc.Server;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server;
        try {
            Injector injector = Guice.createInjector(new ApplicationModule());
            server = injector.getInstance(Server.class);
        } catch (Exception e) {
            log.error("Error during injection.", e);
            return;
        }

        server.start();
        log.info("Server started on port {}.", server.getPort());

        server.awaitTermination();
    }
}
