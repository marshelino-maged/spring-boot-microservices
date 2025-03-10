package com.moviecatalogservice.services;

import com.moviecatalogservice.models.Movie;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import trendingmoviesservice.TrendingMoviesRequest;
import trendingmoviesservice.TrendingMoviesResponse;
import trendingmoviesservice.TrendingMoviesServiceGrpc;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopMoviesService {

    @GrpcClient("trending-movies-service")
    private TrendingMoviesServiceGrpc.TrendingMoviesServiceBlockingStub trendingMoviesStub;

    public List<Movie> getTrendingMovies(int limit) {
        TrendingMoviesRequest request = TrendingMoviesRequest.newBuilder().setLimit(limit).build();

        TrendingMoviesResponse response = trendingMoviesStub.getTrendingMovies(request);

        return response.getMoviesList().stream()
                .map(movie -> new Movie(movie.getId(), movie.getName(), movie.getDescription()))
                .collect(Collectors.toList());
    }
}