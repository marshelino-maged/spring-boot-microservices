package org.example.service;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.model.Rating;
import trendingmoviesservice.Movie;
import trendingmoviesservice.TrendingMoviesRequest;
import trendingmoviesservice.TrendingMoviesResponse;
import trendingmoviesservice.TrendingMoviesServiceGrpc;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class TrendingMoviesService extends TrendingMoviesServiceGrpc.TrendingMoviesServiceImplBase {

    private final OkHttpClient client = new OkHttpClient();
    private static final String RATINGS_API_URL = "http://localhost:8083/ratings/all";
    private static final String MOVIE_INFO_API_URL = "http://localhost:8082/movies/";

    @Override
    public void getTrendingMovies(TrendingMoviesRequest request,
                                  io.grpc.stub.StreamObserver<TrendingMoviesResponse> responseObserver) {
        List<Rating> ratings = fetchMovieRatings();

        Map<String, Integer> movieRatings = new HashMap<>();
        ratings.forEach(rating ->
                movieRatings.merge(rating.getMovieId(), rating.getRating(), Integer::sum)
        );

        LinkedHashMap<String, Integer> topMovies = movieRatings.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        List<Movie> trendingMovies = topMovies.keySet().stream()
                .map(movieId -> Movie.newBuilder()
                        .setId(movieId)
                        .setName(getMovieDetails(movieId).getName())
                        .setDescription(getMovieDetails(movieId).getDescription())
                        .setRating(topMovies.get(movieId))
                        .build())
                .collect(Collectors.toList());

        TrendingMoviesResponse response = TrendingMoviesResponse.newBuilder()
                .addAllMovies(trendingMovies)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private List<Rating> fetchMovieRatings() {
        Request request = new Request.Builder().url(RATINGS_API_URL).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return Arrays.asList(new Gson().fromJson(response.body().string(), Rating[].class));
            }
        } catch (IOException e) {
            System.err.println("Failed to fetch ratings: " + e.getMessage());
        }

        return List.of(new Rating("0", 0));
    }

    public Movie getMovieDetails(String movieId) {
        Request request = new Request.Builder().url(MOVIE_INFO_API_URL + movieId).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String json = response.body().string();
                System.out.println("Raw API Response: " + json); // Debugging

                JSONObject jsonObject = new JSONObject(json);
                String id = jsonObject.optString("movieId", "0");
                String name = jsonObject.optString("name", "Unknown Movie");
                String description = jsonObject.optString("description", "No description available.");

                return Movie.newBuilder()
                        .setId(id)
                        .setName(name)
                        .setDescription(description)
                        .build();
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch movie details: " + e.getMessage());
        }

        return Movie.newBuilder().setId("0").setName("Movie 0").build();
    }
}
