package main.java.model.recommendation.basic;

import main.java.model.Movie;
import main.java.model.user.User;
import main.java.model.recommendation.RecommendationStrategy;
import main.java.util.DataManager;
import main.java.util.RecommendManager;

import java.util.*;

public class DecadeRecommendation extends RecommendationStrategy {
    @Override
    public List<Movie> recommend(User user, int number) {
        ArrayList<Movie> result = new ArrayList<>();

        ArrayList<Map.Entry<Integer, Integer>> yearList = getFavour(user);

        List<Movie> allMovies = DataManager.getAllMovies();

        Map<Movie, List<String>> history = user.getHistory();
        List<Movie> watchlist = user.getWatchlist();

        for (int i = 0; i < yearList.size(); i++) {
            int decade = yearList.get(i).getKey();

            for (int j = 0; j < allMovies.size(); j++) {
                Movie movie = allMovies.get(j);
                int movieDecade = RecommendManager.getDecade(movie.getYear());

                if (movieDecade != decade) {
                    continue;
                }

                if (history.containsKey(movie) || watchlist.contains(movie)) {
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

    public ArrayList<Map.Entry<Integer, Integer>> getFavour(User user) {
        HashMap<Integer, Integer> favour = new HashMap<>();

        Map<Movie, List<String>> history = user.getHistory();
        List<Movie> watchlist = user.getWatchlist();

        Set<Map.Entry<Movie, List<String>>> entries = history.entrySet();
        for (Map.Entry<Movie, List<String>> entry : entries) {
            Movie movie = entry.getKey();
            List<String> movieTimes = entry.getValue();

            int decade = RecommendManager.getDecade(movie.getYear());
            int times = movieTimes.size();

            if (favour.containsKey(decade)) {
                favour.put(decade, favour.get(decade) + times);
            } else {
                favour.put(decade, times);
            }
        }

        for (Movie movie : watchlist) {
            if (movie == null) continue;

            int decade = RecommendManager.getDecade(movie.getYear());

            if (favour.containsKey(decade)) {
                favour.put(decade, favour.get(decade) + 1);
            } else {
                favour.put(decade, 1);
            }
        }

        // sort
        ArrayList<Map.Entry<Integer, Integer>> yearList = new ArrayList<>(favour.entrySet());
        yearList.sort(new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o1.getValue() - o2.getValue();
            }
        });

        return yearList;
    }
}
