package main.java.model.recommendation.basic;

import main.java.model.Movie;
import main.java.model.user.User;
import main.java.model.recommendation.RecommendationStrategy;
import main.java.util.DataManager;

import java.util.*;

public class GenreRecommendation extends RecommendationStrategy {
    @Override
    public List<Movie> recommend(User user, int number) {
        ArrayList<Movie> result = new ArrayList<>();

        ArrayList<Map.Entry<String, Integer>> genreList = getFavour(user);

        List<Movie> allMovies = DataManager.getAllMovies();

        Map<Movie, List<String>> history = user.getHistory();
        List<Movie> watchlist = user.getWatchlist();

        for (int i = 0; i < genreList.size(); i++) {
            String genre = genreList.get(i).getKey();

            for (int j = 0; j < allMovies.size(); j++) {
                Movie movie = allMovies.get(j);

                if ((!movie.getGenre().equals(genre)) || (history.containsKey(movie)) || (watchlist.contains(movie))) {
                    continue;
                }

                result.add(movie);

                if (result.size() >= number) {
                    return result;
                }
            }
        }

        return result;
    }

    // analyze user preferences based on types
    public ArrayList<Map.Entry<String, Integer>> getFavour(User user) {
        HashMap<String, Integer> favour = new HashMap<>();

        Map<Movie, List<String>> history = user.getHistory();
        List<Movie> watchlist = user.getWatchlist();

        Set<Map.Entry<Movie, List<String>>> entries = history.entrySet();
        for (Map.Entry<Movie, List<String>> entry : entries) {
            Movie movie = entry.getKey();
            List<String> movieTimes = entry.getValue();

            String genre = movie.getGenre();
            int times = movieTimes.size();

            if (favour.containsKey(genre)) {
                favour.put(genre, favour.get(genre) + times);
            } else {
                favour.put(genre, times);
            }
        }

        for (Movie movie : watchlist) {
            if (movie == null) continue;

            String genre = movie.getGenre();
            if (favour.containsKey(genre)) {
                favour.put(genre, favour.get(genre) + 1);
            } else {
                favour.put(genre, 0);
            }
        }

        // sort
        ArrayList<Map.Entry<String, Integer>> genreList = new ArrayList<>(favour.entrySet());
        genreList.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });

        return genreList;
    }
}
