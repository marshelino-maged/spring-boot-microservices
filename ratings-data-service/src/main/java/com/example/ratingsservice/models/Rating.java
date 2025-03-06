package com.example.ratingsservice.models;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "userrating")
public class Rating {

    @Embeddable
    public static class RatingId implements Serializable {

        @Column(name = "user_id")
        private Integer userId;

        @Column(name = "movie_id")
        private Integer movieId;

        public RatingId() {}

        public RatingId(Integer userId, Integer movieId) {
            this.userId = userId;
            this.movieId = movieId;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getMovieId() {
            return movieId;
        }

        public void setMovieId(Integer movieId) {
            this.movieId = movieId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RatingId ratingId = (RatingId) o;
            return Objects.equals(userId, ratingId.userId) &&
                    Objects.equals(movieId, ratingId.movieId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, movieId);
        }
    }

    @EmbeddedId
    private RatingId id;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    public Rating() {}

    public Rating(Integer userId, Integer movieId, Integer rating) {
        this.id = new RatingId(userId, movieId);
        this.rating = rating;
    }

    public RatingId getId() {
        return id;
    }

    public void setId(RatingId id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
