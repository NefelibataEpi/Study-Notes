package main.java.model.recommendation.advanced;

import main.java.model.Movie;
import main.java.model.user.User;
import main.java.model.recommendation.RecommendationStrategy;
import main.java.model.recommendation.basic.GenreRecommendation;
import main.java.util.DataManager;
import main.java.util.RecommendManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenreRating extends RecommendationStrategy {
    @Override
    public List<Movie> recommend(User user, int number) {
        ArrayList<Movie> candidates = new ArrayList<>();

        List<Movie> allMovies = DataManager.getAllMovies();

        Map<Movie, List<String>> history = user.getHistory();
        List<Movie> watchlist = user.getWatchlist();

        GenreRecommendation genreRecommendation = new GenreRecommendation();
        ArrayList<Map.Entry<String, Integer>> genreList = genreRecommendation.getFavour(user);

        for (Map.Entry<String, Integer> genreFavour : genreList) {
            String genre = genreFavour.getKey();

            for (Movie movie : allMovies) {
                if (movie == null) continue;

                if ((!movie.getGenre().equals(genre)) || (history.containsKey(movie)) || (watchlist.contains(movie))) {
                    continue;
                }

                candidates.add(movie);
            }
        }

        return RecommendManager.getRatingMovies(candidates, number);
    }
}
