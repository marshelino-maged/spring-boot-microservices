package com.example.movieinfoservice.resources;

import com.example.movieinfoservice.models.Movie;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MovieCache {

    private MongoCollection<Document> collection;
    private MongoDatabase database;
    private String uri = "mongodb://localhost:27017/cache";
    private int maxCacheSize = 1000000;
    private int currentCacheSize = 0;

    public MovieCache() {
        // System.out.println("Creating cache");
        MongoClient mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("cache");
        collection = database.getCollection("movies");
        // System.out.println("Cache created");
    }

    public void addMovie(Movie movie) {
        if (currentCacheSize >= maxCacheSize) {
            List<Document> oldestMovies = collection.find()
                    .sort(Sorts.ascending("cachedAt"))
                    .limit(1000)
                    .into(new java.util.ArrayList<>());

            for (Document oldMovie : oldestMovies) {
                collection.deleteOne(Filters.eq("_id", oldMovie.get("_id")));
            }
            currentCacheSize -= oldestMovies.size();
        }
        
        currentCacheSize++;
        Document document = new Document("_id", movie.getMovieId())
                .append("name", movie.getName()).append("description", movie.getDescription())
                .append("cachedAt", new java.util.Date());
        collection.insertOne(document);
    }

    public Optional<Movie> getMovie(String movieId) {
        Document document = collection.find(new Document("_id", movieId)).first();
        if (document == null) {
            return Optional.empty();
        }
        return Optional.of(
                new Movie(document.getString("_id"), document.getString("name"), document.getString("description")));
    }

}
