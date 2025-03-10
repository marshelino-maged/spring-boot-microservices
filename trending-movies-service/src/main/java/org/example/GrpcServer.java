package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.example.eureka.EurekaClient;
import org.example.service.TrendingMoviesService;

import java.io.IOException;

public class GrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        EurekaClient eurekaClient = new EurekaClient();

        // Register with Eureka
        eurekaClient.register();

        // Start gRPC server
        Server server = ServerBuilder.forPort(8084)
                .addService(new TrendingMoviesService())
                .build()
                .start();

        System.out.println("gRPC Server started on port 8084...");

        // Start sending heartbeats to Eureka
        eurekaClient.sendHeartbeat();

        // Shutdown hook to deregister from Eureka
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC Server...");
            eurekaClient.deregister();
            server.shutdown();
            System.out.println("Server shut down successfully.");
        }));

        server.awaitTermination();
    }
}
