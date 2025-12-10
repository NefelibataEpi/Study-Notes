package main.java.util;

import main.java.model.Movie;
import main.java.model.user.BasicUser;
import main.java.model.user.PremiumUser;
import main.java.model.user.User;

import java.io.*;
import java.util.*;

public final class DataManager {
    private DataManager() {}

    // get all Movies
    private static List<Movie> movieCache = null;

    public static List<Movie> getAllMovies() {
        if (movieCache != null) {
            return movieCache;
        }

        movieCache = new ArrayList<>();

        File file = new File("src\\main\\resources\\data\\movies.csv");

        try (Scanner sc = new Scanner(file)) {
            // skip the first line
            sc.nextLine();

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] arr = line.split(",", -1);

                // explain arr
                String id = arr[0];
                String title = arr[1];
                String genre = arr[2];
                String year = arr[3];
                double rating = Double.parseDouble(arr[4]);

                // create Movie object
                Movie movie = new Movie(id, title, genre, year, rating);
                movieCache.add(movie);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return movieCache;
    }

    // query movie by id
    public static Movie getMovie(String id) {
        List<Movie> allMovies = getAllMovies();

        for (Movie movie : allMovies) {
            if (movie.getId().equals(id)) {
                return movie;
            }
        }

        return null;
    }

    // get all Users
    private static List<User> allUsersCache = null;

    public static List<User> getAllUsers() {
        if (allUsersCache != null) {
            return allUsersCache;
        }

        allUsersCache = new ArrayList<>();

        File file = new File("src\\main\\resources\\data\\users.csv");

        try (Scanner sc = new Scanner(file)) {
            // skip the first line
            sc.nextLine();

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] arr = line.split(",", -1);

                // explain arr
                String username = arr[0];
                String password = arr[1];
                String watchlist = arr[2];
                String history = arr[3];
                String type = arr[4];

                // create User object
                User user;
                if (type.equals("premium")) {
                    user = new PremiumUser(username, password, watchlist, history);
                } else {
                    user = new BasicUser(username, password, watchlist, history);
                }
                allUsersCache.add(user);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return allUsersCache;
    }

    // Explain User
    public static String explainUser(User user) {
        // state
        String username = user.getUsername();
        String password = user.getPassword();
        String watchlist = "";
        String history = "";
        String type;

        // explain watchlist
        List<Movie> watchlistArr = user.getWatchlist();
        if (watchlistArr != null && !watchlistArr.isEmpty()) {
            for (int i = 0; i < watchlistArr.size(); i++) {
                Movie movie = watchlistArr.get(i);
                if (movie == null) continue;

                String id = movie.getId();

                if (i != watchlistArr.size() - 1) {
                    watchlist += id + ";";
                } else {
                    watchlist += id;
                }
            }
        }


        // explain history
        Map<Movie, List<String>> historyArr = user.getHistory();
        if (historyArr != null) {
            Set<Map.Entry<Movie, List<String>>> entries = historyArr.entrySet();
            for (Map.Entry<Movie, List<String>> entry : entries) {
                Movie movie = entry.getKey();
                List<String> historyTime = entry.getValue();

                String movieId = movie.getId();
                history += movieId + "@";

                for (int i = 0; i < historyTime.size(); i++) {
                    String time = historyTime.get(i);

                    if (i != historyTime.size() - 1) {
                        history += time + "@";
                    } else {
                        history += time;
                    }
                }

                history += ";";
            }
            if (!history.isEmpty()) {
                history = history.substring(0, history.length() - 1);
            }
        }

        // explain type
        if (user instanceof PremiumUser) {
            type = "premium";
        } else {
            type = "basic";
        }

        return username + "," + password + "," + watchlist + "," + history + "," + type;
    }

    // Refresh CSV file
    public static void refreshUserCSV(List<User> allUsers) {
        File file = new File("src\\main\\resources\\data\\users.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(file))){
            // read the title
            String title = br.readLine();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                // write
                bw.write(title);
                bw.newLine();
                for (int i = 0; i < allUsers.size(); i++) {
                    User user = allUsers.get(i);
                    String line = explainUser(user);
                    if (i != allUsers.size() - 1) {
                        bw.write(line);
                        bw.newLine();
                    } else {
                        bw.write(line);
                    }
                }
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
        } catch (IOException ioe){
            System.out.println(ioe.getMessage());
        }
    }
}
