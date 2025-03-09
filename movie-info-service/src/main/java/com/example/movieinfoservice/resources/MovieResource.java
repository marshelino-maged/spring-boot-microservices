package com.example.movieinfoservice.resources;

import com.example.movieinfoservice.models.Movie;
import com.example.movieinfoservice.models.MovieSummary;
// import com.example.movieinfoservice.utils.Logger;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/movies")
public class MovieResource {

    @Value("${api.key}")
    private String apiKey;

    private RestTemplate restTemplate;

    private MovieCache movieCache;

    @Autowired
    public MovieResource(RestTemplate restTemplate, MovieCache movieCache) {
        this.restTemplate = restTemplate;
        this.movieCache = movieCache;
    }

    @RequestMapping("/{movieId}")
    public Movie getMovieInfo(@PathVariable("movieId") String movieId) {
        // Check if the movie info is in the cache
        Optional<Movie> movie = movieCache.getMovie(movieId);
        if(movie.isPresent()) {
            // Logger.green("Movie info from cache");
            return movie.get();
        }
        // Get the movie info from TMDB
        // Logger.red("Movie info from TMDB");
        final String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey;
        MovieSummary movieSummary = restTemplate.getForObject(url, MovieSummary.class);
        // Cache the movie info
        Movie newMovie = new Movie(movieId, movieSummary.getTitle(), movieSummary.getOverview());
        movieCache.addMovie(newMovie);

        return newMovie;
    }
}
