package org.example.eureka;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EurekaClient {

    private static final String EUREKA_SERVER = "http://localhost:8761/eureka";
    private static final String APP_NAME = "grpc-movie-service";
    private static final String INSTANCE_ID = "grpc-movie-service-8084";
    private static final String SERVICE_URL = "http://localhost:8084";  // gRPC uses raw TCP but we provide a URL for Eureka

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void register() throws IOException {
        Map<String, Object> instance = new HashMap<>();
        instance.put("hostName", "MSI:trending-movies-service:8084");
        instance.put("app", APP_NAME.toUpperCase());
        instance.put("ipAddr", "127.0.0.1");

        Map<String, Object> port = new HashMap<>();
        port.put("$", 8084);
        port.put("@enabled", true);
        instance.put("port", port);
        instance.put("status", "UP");
        instance.put("vipAddress", APP_NAME.toLowerCase());
        instance.put("secureVipAddress", APP_NAME.toLowerCase());
        instance.put("homePageUrl", SERVICE_URL);
        instance.put("statusPageUrl", SERVICE_URL + "/status");
        instance.put("healthCheckUrl", SERVICE_URL + "/health");
        instance.put("dataCenterInfo", Map.of("name", "MyOwn", "@class", "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo"));

        Map<String, Object> requestBody = Map.of("instance", instance);
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        Request request = new Request.Builder()
                .url(EUREKA_SERVER + "/apps/" + APP_NAME)
                .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Eureka Registration Response: " + response.code());
        response.close();
    }

    public void sendHeartbeat() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30_000); // Send heartbeat every 30 seconds
                    Request request = new Request.Builder()
                            .url(EUREKA_SERVER + "/apps/" + APP_NAME + "/" + INSTANCE_ID)
                            .put(RequestBody.create("", MediaType.get("application/json")))
                            .build();
                    Response response = client.newCall(request).execute();
                    response.close();
                } catch (Exception e) {
                    System.err.println("Failed to send heartbeat to Eureka: " + e.getMessage());
                }
            }
        }).start();
    }

    public void deregister() {
        try {
            Request request = new Request.Builder()
                    .url(EUREKA_SERVER + "/apps/" + APP_NAME + "/" + INSTANCE_ID)
                    .delete()
                    .build();
            Response response = client.newCall(request).execute();
            response.close();
            System.out.println("Service deregistered from Eureka.");
        } catch (Exception e) {
            System.err.println("Failed to deregister from Eureka: " + e.getMessage());
        }
    }
}
