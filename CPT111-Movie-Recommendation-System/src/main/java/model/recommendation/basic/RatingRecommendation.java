package main.java.model.recommendation.basic;

import main.java.model.Movie;
import main.java.model.user.User;
import main.java.model.recommendation.RecommendationStrategy;
import main.java.util.DataManager;
import main.java.util.RecommendManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RatingRecommendation extends RecommendationStrategy {
    @Override
    public List<Movie> recommend(User user, int number) {
        ArrayList<Movie> candidates = getMovies(user);

        return RecommendManager.getRatingMovies(candidates, number);
    }

    private static ArrayList<Movie> getMovies(User user) {
        ArrayList<Movie> candidates = new ArrayList<>();

        List<Movie> allMovies = DataManager.getAllMovies();

        Map<Movie, List<String>> history = user.getHistory();
        List<Movie> watchlist = user.getWatchlist();

        for (Movie movie : allMovies) {
            if (movie == null) continue;

            if (history.containsKey(movie) || watchlist.contains(movie)) {
                continue;
            }

            candidates.add(movie);
        }
        return candidates;
    }
}
