package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.example.service.TrendingMoviesService;

import java.io.IOException;

public class GrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8084)
                .addService(new TrendingMoviesService())
                .build()
                .start();

        System.out.println("gRPC Server started on port 8084...");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC Server...");
            server.shutdown();
            System.out.println("Server shut down successfully.");
        }));

        server.awaitTermination();
    }
}
