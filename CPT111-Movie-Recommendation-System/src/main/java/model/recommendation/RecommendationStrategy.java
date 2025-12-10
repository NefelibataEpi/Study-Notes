package main.java.model.recommendation;

import main.java.model.Movie;
import main.java.model.user.User;

import java.util.ArrayList;
import java.util.List;

public class RecommendationStrategy {
    public RecommendationStrategy() {}

    public List<Movie> recommend(User user, int number) {
        ArrayList<Movie> result = new ArrayList<>();
        return result;
    }
}
