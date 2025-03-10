package com.moviecatalogservice.config;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import trendingmoviesservice.TrendingMoviesServiceGrpc;

@Configuration
public class GrpcClientConfig {

    @Bean
    public TrendingMoviesServiceGrpc.TrendingMoviesServiceBlockingStub movieServiceStub(
            @GrpcClient("movieService") TrendingMoviesServiceGrpc.TrendingMoviesServiceBlockingStub stub) {
        return stub;
    }
}
