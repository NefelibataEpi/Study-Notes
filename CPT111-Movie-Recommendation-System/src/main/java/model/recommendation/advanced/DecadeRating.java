package main.java.model.recommendation.advanced;

import main.java.model.Movie;
import main.java.model.user.User;
import main.java.model.recommendation.RecommendationStrategy;
import main.java.model.recommendation.basic.DecadeRecommendation;
import main.java.util.DataManager;
import main.java.util.RecommendManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DecadeRating extends RecommendationStrategy {
    @Override
    public List<Movie> recommend(User user, int number) {
        ArrayList<Movie> candidates = new ArrayList<>();

        List<Movie> allMovies = DataManager.getAllMovies();

        Map<Movie, List<String>> history = user.getHistory();
        List<Movie> watchlist = user.getWatchlist();

        DecadeRecommendation decadeRecommendation = new DecadeRecommendation();
        ArrayList<Map.Entry<Integer, Integer>> decadeList = decadeRecommendation.getFavour(user);

        for (Map.Entry<Integer, Integer> decadeFavour : decadeList) {
            int decade = decadeFavour.getKey();

            for (Movie movie : allMovies) {
                if (movie == null) continue;

                int movieDecade = RecommendManager.getDecade(movie.getYear());

                if ((movieDecade != decade) || (history.containsKey(movie)) || (watchlist.contains(movie))) {
                    continue;
                }

                candidates.add(movie);
            }
        }

        return RecommendManager.getRatingMovies(candidates, number);
    }
}