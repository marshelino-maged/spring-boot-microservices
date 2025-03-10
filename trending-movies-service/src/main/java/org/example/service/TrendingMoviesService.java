package org.example.service;

import trendingmoviesservice.Movie;
import trendingmoviesservice.TrendingMoviesResponse;
import trendingmoviesservice.TrendingMoviesServiceGrpc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TrendingMoviesService extends TrendingMoviesServiceGrpc.TrendingMoviesServiceImplBase {

    @Override
    public void getTrendingMovies(trendingmoviesservice.TrendingMoviesRequest request,
            io.grpc.stub.StreamObserver<trendingmoviesservice.TrendingMoviesResponse> responseObserver) {
        List<Movie> trendingMovies = Arrays.asList(
                Movie.newBuilder().setId("1").setName("Dune: Part Two").setDescription("Sci-fi epic continues.").build(),
                Movie.newBuilder().setId("2").setName("Oppenheimer").setDescription("The story of the atomic bomb.").build(),
                Movie.newBuilder().setId("3").setName("Barbie").setDescription("A fun and colorful adventure.").build(),
                Movie.newBuilder().setId("4").setName("Spider-Man: Across the Spider-Verse").setDescription("Multiverse saga continues.").build(),
                Movie.newBuilder().setId("5").setName("John Wick: Chapter 4").setDescription("More action, more revenge.").build()
        );

        int limit = Math.min(request.getLimit(), trendingMovies.size());

        TrendingMoviesResponse response = TrendingMoviesResponse.newBuilder()
                .addAllMovies(trendingMovies.stream().limit(limit).collect(Collectors.toList()))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}