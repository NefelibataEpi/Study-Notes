package main.java.util;

import main.java.model.Movie;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class RecommendManager {
    private RecommendManager() {}

    // get up to n recommendations based on the rating
    public static List<Movie> getRatingMovies(ArrayList<Movie> movies, int number) {
        movies.sort(new Comparator<Movie>() {
            @Override
            public int compare(Movie o1, Movie o2) {
                double o1R = o1.getRating();
                double o2R = o2.getRating();

                if (o1R > o2R) return -1;
                else if (o1R < o2R) return 1;
                else return 0;
            }
        });

        ArrayList<Movie> result = new ArrayList<>();

        for (int i = 0; i < movies.size() && result.size() < number; i++) {
            result.add(movies.get(i));
        }

        return result;
    }

    // get decade
    public static int getDecade(String yearStr) {
        int year = Integer.parseInt(yearStr);
        return (year / 10) * 10;
    }
}
