package main.java.commend;

import main.java.model.Movie;
import main.java.model.recommendation.RecommendationStrategy;
import main.java.model.recommendation.advanced.DecadeRating;
import main.java.model.recommendation.advanced.GenreRating;
import main.java.model.recommendation.basic.DecadeRecommendation;
import main.java.model.recommendation.basic.GenreRecommendation;
import main.java.model.recommendation.basic.RatingRecommendation;
import main.java.model.user.BasicUser;
import main.java.model.user.PremiumUser;
import main.java.model.user.User;
import main.java.util.DataManager;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {
    // constant
    private static final String BROWSE_MOVIES = "1";
    private static final String ADD_WATCHLIST = "2";
    private static final String REMOVE_WATCHLIST = "3";
    private static final String VIEW_WATCHLIST = "4";
    private static final String MARK_WATCHED = "5";
    private static final String VIEW_HISTORY = "6";
    private static final String NORMAL_RECOMMENDATION = "7";
    private static final String LOGOUT = "8";
    private static final String PREMIUM_RECOMMENDATION_OR_UPGRADE = "9";

    // Start Main Procedure
    public static void startMain(User user, List<User> allUsers) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            printMenu(user);

            System.out.print("Please select your choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case BROWSE_MOVIES -> browseMovies();
                case ADD_WATCHLIST -> addWatchlist(user, allUsers, sc);
                case REMOVE_WATCHLIST -> removeWatchlist(user, allUsers, sc);
                case VIEW_WATCHLIST -> viewWatchlist(user);
                case MARK_WATCHED -> markWatched(user, allUsers, sc);
                case VIEW_HISTORY -> viewHistory(user);
                case NORMAL_RECOMMENDATION -> normalRecommendation(user, sc);

                case PREMIUM_RECOMMENDATION_OR_UPGRADE -> {
                    if (user instanceof PremiumUser) {
                        premiumRecommendation((PremiumUser) user, sc);
                    } else {
                        user = upgradeToVIP(user, allUsers);
                        DataManager.refreshUserCSV(allUsers);
                    }
                }

                case LOGOUT -> {
                    System.out.println("Log out.");
                    Login.startLogin();
                }

                default -> System.out.println("Wrong choice!");
            }
        }
    }

    // Method
    // 1. Browse movies
    private static void browseMovies() {
        List<Movie> allMovies = DataManager.getAllMovies();

        System.out.println("There are a total of " + allMovies.size() + " movies: ");

        for (Movie movie : allMovies) {
            printMovie(movie);
        }
    }

    // 2. Add to watchlist
    private static void addWatchlist(User user, List<User> allUsers, Scanner sc) {
        System.out.print("Please enter the movie ID you want to add to your watchlist: ");
        String id = sc.nextLine();

        Movie movie = DataManager.getMovie(id);

        if (id == null || movie == null) {
            System.out.println("Invalid movie ID!");
            return;
        } else if (user.hasInWatchlist(id)) {
            System.out.println("This movie is already in watchlist!");
            return;
        }

        user.addWatchlist(id);
        System.out.println("The movie has been added successfully!");
        DataManager.refreshUserCSV(allUsers);
    }

    // 3. Remove from watchlist
    private static void removeWatchlist(User user, List<User> allUsers, Scanner sc) {
        System.out.print("Please enter the movie ID you want to remove from your watchlist: ");
        String id = sc.nextLine();

        // get the user's watchlist
        List<Movie> watchlist = user.getWatchlist();

        // get the current movie by ID
        Movie currentMovie = DataManager.getMovie(id);

        if (id == null || currentMovie == null) {
            System.out.println("Invalid movie ID!");
            return;
        } else if (!(user.hasInWatchlist(id))) {
            System.out.println("The movie ID " + id + " does not exist in your watchlist!");
            return;
        }

        user.removeWatchlist(id);
        System.out.println("The movie has been removed successfully!");
        DataManager.refreshUserCSV(allUsers);
    }

    // 4. View watchlist
    private static void viewWatchlist(User user) {
        // get the user's watchlist
        List<Movie> watchlist = user.getWatchlist();

        System.out.println("Your watchlist contains a total of " + watchlist.size() + " movies:");

        for (Movie movie : watchlist) {
            if (movie == null) continue;
            printMovie(movie);
        }
    }

    // 5. Mark movie as watched
    private static void markWatched(User user, List<User> allUsers, Scanner sc) {
        System.out.print("Please enter the movie ID you want to mark as watched: ");
        String id = sc.nextLine();

        // get the current movie by ID
        Movie currentMovie = DataManager.getMovie(id);

        if (id == null || currentMovie == null) {
            System.out.println("Invalid movie ID!");
            return;
        }

        user.addHistory(id);
        System.out.println("The movie has been added successfully!");
        DataManager.refreshUserCSV(allUsers);
    }

    // 6. View history
    private static void viewHistory(User user) {
        // get the user's history
        Map<Movie, List<String>> history = user.getHistory();

        System.out.println("Your history contains " + history.size() + " movies: ");

        Set<Map.Entry<Movie, List<String>>> entries = history.entrySet();
        for (Map.Entry<Movie, List<String>> entry : entries) {
            Movie movie = entry.getKey();
            List<String> movieTimes = entry.getValue();

            if (movie == null) continue;
            printMovie(movie);

            System.out.println("You have watched this film " + movieTimes.size() + " times.");
            for (int i = 0; i < movieTimes.size(); i++) {
                String time = movieTimes.get(i);
                System.out.println((i + 1) + ": " + time);
            }
        }
    }

    // 7. Get basic recommendation
    private static void normalRecommendation(User user, Scanner sc) {
        System.out.println("===== Normal Recommendation =====");
        System.out.println("1. Recommend by Genre");
        System.out.println("2. Recommend by Decade");
        System.out.println("3. Recommend by Rating");

        System.out.print("Please select your choice: ");
        String choice = sc.nextLine();
        RecommendationStrategy strategy = null;

        switch (choice) {
            case "1" -> {
                strategy = new GenreRecommendation();
                System.out.println("Current Strategy: Genre");
            }
            case "2" -> {
                strategy = new DecadeRecommendation();
                System.out.println("Current Strategy: Decade");
            }
            case "3" -> {
                strategy = new RatingRecommendation();
                System.out.println("Current Strategy: Rating");
            }
            default -> {
                System.out.println("Wrong choice!");
                return;
            }
        }

        List<Movie> result = strategy.recommend(user, 5);
        System.out.println("There are " + result.size() + " movies recommended!");
        for (Movie movie : result) {
            if (movie == null) continue;
            printMovie(movie);
        }
    }

    // 8. Get advanced recommendation
    private static void premiumRecommendation(PremiumUser user, Scanner sc) {
        System.out.println("===== Premium Recommendation =====");
        System.out.println("1. Genre + Rating");
        System.out.println("2. Decade + Rating");

        System.out.print("Please select your choice: ");
        String choice = sc.nextLine();
        RecommendationStrategy strategy = null;

        switch (choice) {
            case "1" -> {
                strategy = new GenreRating();
                System.out.println("Current Strategy: Genre + Rating");
            }
            case "2" -> {
                strategy = new DecadeRating();
                System.out.println("Current Strategy: Decade + Rating");
            }
            default -> {
                System.out.println("Wrong choice!");
                return;
            }
        }

        // determine whether the user input is a number;
        int number;
        while (true) {
            System.out.print("Please enter the number of recommended movies you would like to receive: ");
            String numberStr = sc.nextLine();

            if (numberStr.matches("[0-9]+")) {
                number = Integer.parseInt(numberStr);
                break;
            } else {
                System.out.println("You should only be able to enter numbers!");
            }
        }

        List<Movie> result = strategy.recommend(user, number);
        System.out.println("There are " + result.size() + " movies recommended!");
        for (Movie movie : result) {
            if (movie == null) continue;
            printMovie(movie);
        }
    }

    // 9. Upgrade to VIP
    public static PremiumUser upgradeToVIP(User user, List<User> allUsers) {
        int index = allUsers.indexOf(user);

        PremiumUser newUser = new PremiumUser(
                user.getUsername(),
                user.getPassword(),
                user.getWatchlist(),
                user.getHistory()
        );

        allUsers.set(index, newUser);

        System.out.println("Congratulations! You are now a Premium User!");

        return newUser;
    }

    // Plugin
    // 1. print the menu
    private static void printMenu(User user) {
        System.out.println("===== Movie Recommendation System =====");
        System.out.println("1. Browse movies");
        System.out.println("2. Add to watchlist");
        System.out.println("3. Remove from watchlist");
        System.out.println("4. View watchlist");
        System.out.println("5. Mark movie as watched");
        System.out.println("6. View History");
        System.out.println("7. Get recommendation (Basic)");
        System.out.println("8. Logout");

        if (user instanceof BasicUser) {
            System.out.println("9. Upgrade to Premium (VIP)");
        }

        if (user instanceof PremiumUser) {
            System.out.println("9. Get recommendation (Advanced | VIP only)");
        }
    }

    // 2. print the movie
    private static void printMovie(Movie movie) {
        String id = movie.getId();
        String title = movie.getTitle();
        String genre = movie.getGenre();
        String year = movie.getYear();
        double rating = movie.getRating();

        System.out.println(id + "[ Title: " + title + ", Genre: " + genre +
                ", Publish Year: " + year + ", Rating: " + rating + " ]");
    }
}
