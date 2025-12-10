package main.java.model.user;

import main.java.model.Movie;

import java.util.List;
import java.util.Map;

public class PremiumUser extends User {
    // constructor
    public PremiumUser(String username, String password, String watchlist, String history) {
        super(username, password, watchlist, history);
    }
    public PremiumUser(String username, String password) {
        super(username, password);
    }
    public PremiumUser(String username, String password, List<Movie> watchlist, Map<Movie, List<String>> history) {
        super(username, password, watchlist, history);
    }
}
