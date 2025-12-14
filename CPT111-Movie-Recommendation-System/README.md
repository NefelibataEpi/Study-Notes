# CPT111 - Movie Recommendation System

This project is a Java-based movie recommendation system developed for the CPT111 (Java Programming) module.

---

## 1.0 Introduction

This project implements a Movie Recommendation and Tracker System in Java. The system supports the complete user workflow required in the coursework: authentication, browsing a movie library, maintaining a watchlist, recording viewing history, and generating recommendations based on user preference signals. The implementation is organised to keep the program modular and maintainable by separating the user interface layer from core business logic and file-based persistence.

A key feature of our solution is providing two independent entry points: a fully functional command-line interface (CLI) that meets the baseline requirements, and a JavaFX GUI as an advanced feature. Although the two interfaces differ in interaction style, they operate on the same domain objects and data files to ensure consistent behavior across modes. In the GUI version, layouts are defined with FXML designed in Scene Builder, and controllers handle event-driven operations such as browsing movies, watchlist/history actions, and requesting recommendations (including premium-only advanced options).

The recommendation module supports multiple strategies through a parent class `RecommendationStrategy` and shared helper logic in `RecommendationManager`, including a Top-N rating sorter and decade conversion utility. This design keeps recommendation classes concise while ensuring consistent selection rules (e.g., producing exactly N results when possible).

The following chapters describe the system design, implementation details, and evidence from testing and robustness checks.

---

## 2.0 Overall System Design

The system separates UI, core logic, and CSV persistence to keep the codebase modular and easy to maintain.

A key design decision of this project is to support two independent program entry points: a command-line interface (CLI), which satisfies the basic coursework requirements, and a graphical user interface (GUI) implemented using JavaFX as an advanced feature. Although the two interfaces differ in presentation, they share the same underlying logic and data handling mechanisms, ensuring consistent behaviour across different modes of use.

### 2.1 Project Structure
The system follows a layered architecture, with packages organized according to their responsibilities. The overall project structure is shown in Figure 1. The source code starts from `src/main`, where the application is divided into several main packages, including application entry points, controllers, core models, utility classes, and data resources.

<img src="E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\Main Structure.png" alt="Main Structure" style="zoom:50%;" />

At the top level, the `app` package contains the two program entry points: `CommandApp` for the command-line version and `GUIApp` for the JavaFX-based graphical version. This design allows the system to support both interfaces without duplicating business logic. Each entry point is responsible only for initializing the program and directing execution to the appropriate controllers.

The core logic of the system is organized into packages such as `model`, `recommendation`, and `util`. These packages are shared by both the CLI and GUI versions. For example, user data manipulation, movie information handling, and recommendation algorithms are all implemented independently of the user interface. As a result, changes to recommendation logic or data processing do not require modifications to either interface, which improves maintainability.

User interface–specific logic is isolated within the `command` and `controller` packages. The `command` package supports menu-driven interaction in the CLI version, while the `controller` package contains JavaFX controllers that are linked to FXML views in the GUI version. This separation ensures that interface-related code does not interfere with the core system logic.

### 2.2 Relationships Between CLI and GUI Implementations
Both the CLI and the JavaFX GUI provide the same user workflow (login, browsing, watchlist/history management, and recommendations). To avoid duplicating codes, we implement logic once in model and utilities, and the GUI controller achieves the user's actions by calling the same operations that already exist in the CLI. As a result, validation and state updates behave consistently across the two entry points, while the GUI layer focuses only on presentation (scene switching, displaying data, and user feedback).

---

## 3.0 Object-Oriented Design

This chapter explains the most important classes that represent data in the system and keep the program state consistent across both the CLI and JavaFX GUI versions. The focus is on how `Movie` and `User` objects are modelled in memory, and how persistent user data is loaded from and saved back to CSV files through `DataManager`.

### 3.1 Movie Object Design
`Movie` is designed as a lightweight object that holds movie information in memory. It contains only the essential fields required for system features (e.g., ID, title, genre, year, rating) and provides basic setters and getters. It acts as the target object created by the data layer when movie records are read from `movies.csv`.

### 3.2 User Object Design
The design of the `User` class is based directly on the storage format in `users.csv`. We observed that each line in the CSV represents one user record, which can be split by commas into four fields: `username`, `password`, `watchlist`, and `history`. These four fields are therefore mapped to the four core attributes in the `User` class. For the `watchlist` field, movies are separated using semicolons in the file, so we implemented `watchlist` as a `List<Movie>` to store the user’s saved movies. For `history`, entries are also separated by semicolons, but each entry additionally uses the `@` symbol to separate the movie identifier from the watching time. To support multiple watch times for the same movie and keep the structure easy to read/write, we designed `history` as a `Map<Movie, List<String>>`, where the map key represents the movie and the value is a list of time strings. This structure avoids repeatedly storing the full movie information and makes it straightforward to retrieve both the movie details and all recorded watch times (Listing 1).

```java
private List<Movie> watchlist;
private Map<Movie, List<String>> history;
```

For object construction, listing 2 shows that in addition to the default constructor and the full-parameter constructor, we implemented a constructor that takes all fields as strings so that raw CSV data can be parsed into a `User` object directly. We also provided a partial constructor with only username and password to support account creation during registration. In the getter/setter design, one deliberate choice is that we do not provide setters for `watchlist` and `history`. First, the current program logic does not require replacing these collections as a whole; second, restricting direct replacement helps protect data integrity by ensuring that updates are performed through controlled methods rather than arbitrary external assignments. We also override `toString()` to support debugging in the CLI version.

```java
public User() {}
public User(String username, String password, String watchlistStr, String historyStr) {...}
public User(String username, String password) {...}
public User(String username, String password, List<Movie> watchlist, Map<Movie, List<String>> history) {...}
```

In terms of behavior, the `User` class provides four core methods to update `watchlist` and `history`: adding to the watchlist, removing from the watchlist, adding a history record, and checking whether a movie is already in the watchlist. These operations are implemented using movie IDs, because in the CLI workflow the movie ID is the primary identifier that users interact with and it is the most convenient input format. When adding a history record, we use `LocalDate` to obtain the current date as the watching time.

To support advanced features, we define two user types: Basic and Premium. Premium users can access advanced recommendation options and specify the number of recommendations, while Basic users are limited to three standard recommendation types and a fixed recommendation count of five. During registration, a new user can choose whether to become a Premium user, and an existing Basic user can also upgrade to Premium from the main menu. In our implementation, `BasicUser` and `PremiumUser` both extend the parent class `User`. These subclasses mainly provide constructors rather than unique behavior methods; instead, the system distinguishes user capabilities through polymorphism in the program flow and menu/GUI option control (Listing 3).

```java
switch (choice) {
    ...
    case PREMIUM_RECOMMENDATION_OR_UPGRADE -> {
        if (user instanceof PremiumUser) {
            premiumRecommendation((PremiumUser) user, sc);
        } else {
            user = upgradeToVIP(user, allUsers);
            DataManager.refreshUserCSV(allUsers);
        }
    }
    ...
}
```

### 3.3 Core Data Utility and Persistence
`DataManager` is the core utility class responsible for transforming CSV data into objects and writing updated user state back to storage. In our program, direct CSV reading and writing is restricted so that only `DataManager` is allowed to perform file I/O. The class therefore focuses on two responsibilities: loading data and refreshing persistent user data.

For data loading, `DataManager` provides `getAllMovies()` and `getAllUsers()`, which read `movies.csv` and `users.csv` and return lists of `Movie` and `User` objects. To prevent problems caused by repeated reads (such as duplicated lists or inconsistent updates across different parts of the program), we implemented a simple cache: the CSV files are read only once, and when the cache is not empty the method returns the existing cached list directly. When parsing lines from the CSV files, we use `split(",", -1)` to split each row into an array by commas, where each segment corresponds to a specific attribute in the movie/user object. The `limit` parameter is set to `-1` so that trailing empty fields are preserved. This avoids missing-field issues when a user’s watchlist or history is empty and would otherwise result in fewer columns after splitting. In addition, we added an extra column in `users.csv` to store the user type, and `DataManager` creates different user objects based on this type when parsing user records (Listing 4).

```java
private static List<User> allUsersCache = null;

public static List<User> getAllUsers() {
    if (allUsersCache != null) {
        return allUsersCache;
    }
    ...
    User user;
    if (type.equals("premium")) {
        user = new PremiumUser(username, password, watchlist, history);
    } else {
        user = new BasicUser(username, password, watchlist, history);
    }
    ...
}
```

For persistence, we observed that only user information needs to be refreshed at runtime, so we designed a dedicated method `refreshCSV`. The implementation uses `BufferedWriter`, taking advantage of the fact that opening a writer in overwrite mode clears the file before writing. The method first reads the header row using `BufferedReader`, and then writes all current user records back to the file in CSV format. To support this, we implemented `explainUser`, which converts a `User` object into a correctly formatted CSV line. By combining these two methods and calling refresh whenever user state changes, the program can persist user operations in real time and ensure that updates remain consistent across program restarts.

---

## 4.0 Program Entry Points and User Interfaces

This chapter describes how the system is executed and interacted with through two different user interfaces: a command-line interface (CLI) and a graphical user interface (GUI). Both interfaces provide access to the same core features, but differ in how user input is collected and how results are presented. The chapter focuses on the program flow and interaction logic rather than visual appearance.

### 4.1 Command-Line Program Flow
The CLI version is launched from the entry point located in the `app` package. Specifically, the `main` method in `CommandApp` starts the command-line program by calling `Login.startLogin()`. This call is the beginning of the CLI workflow and leads the user into the login phase before any other functions become available.

After the program enters the login phase, the interface is fully menu-driven. The program prints a set of numbered options and waits for the user to select an action. This same pattern continues after successful authentication: the user is taken to the main menu, where each option corresponds to a feature (movie browsing, watchlist operations, history operations, recommendations, and account-related actions). The menu structure wrapped in a `while(true)` loop is intentionally repeated after each operation so that the program remains easy to navigate and the user can perform multiple actions in one run without restarting the application.

A key implementation focus in the CLI version is robustness against user mistakes. Because menu-driven systems depend heavily on user input, the CLI logic includes checks that prevent invalid operations from breaking program flow. Examples include handling incorrect menu choices, rejecting invalid movie IDs, and preventing inconsistent states such as adding duplicates into a watchlist. When a user marks a movie as watched, the program applies a consistency rule by recording the movie into history and removing it from the watchlist when needed, which keeps the two collections aligned with the user’s real intent.

### 4.2 GUI Program Flow
The GUI version provides a full interactive workflow for the system. Users can log in with an existing account, create a new account through registration, and reset a password through the “forgot password” page. After entering the main interface, users can browse the complete movie library, add selected movies to their watchlist, remove items from the watchlist, mark movies as watched (which updates viewing history), and view their watch history with additional details. The GUI also supports requesting recommendations, where standard users access the normal recommendation options and premium users can access the advanced options. Finally, users can log out and exit the application with confirmation.

The interface is implemented using JavaFX with multiple FXML views, and all FXML layouts were designed using Scene Builder. The application starts from GUIApp and loads the initial login view.

After successful login, the application switches to the main interface using `FXMLLoader`. At this transition point, a key design decision is applied: the logged-in user and shared user data are injected into the `MainController`, which acts as a central session holder. By storing user state in `MainController`, the program ensures that user information is preserved when navigating between different pages inside the main interface. Without this step, user data would be lost whenever a new view controller is created.

Most main features are implemented through controller event methods connected to GUI components. Movie data is displayed using `TableView` by binding columns to movie properties and populating an observable list, allowing the interface to render `Movie` objects directly (Listing 5). History is stored with timestamps, so the GUI uses a selectable display and a listener (Listing 6) to show the corresponding movie details when an entry is clicked. In addition, the program intercepts window close requests using `setOnCloseRequest` and consumes the event to show a confirmation dialogue before exiting, reducing accidental termination (Listing 7).

```java
// table
@FXML private TableView<Movie> movieTableView;
@FXML private TableColumn<Movie, String> colID;
...
@FXML
public void initialize(){
    List<Movie> allMovies = DataManager.getAllMovies();
    colID.setCellValueFactory(new PropertyValueFactory<>("id"));
    ...
    ObservableList<Movie> movieList = FXCollections.observableList(allMovies);
    movieTableView.setItems(movieList);
```

```java
movieTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Movie>() {
    @Override
    public void changed(ObservableValue<? extends Movie> observableValue, Movie movie, Movie t1) {
        if (t1 != null) {
            showMovieDetails(t1);
        }
    }
});
```

```java
stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
    @Override
    public void handle(WindowEvent event) {
        event.consume();
        logout(stage);
    }
});
```

---

## 5.0 Recommendation Algorithm

The core structure is simple: `RecommendationStrategy` is used as a normal parent class to keep all recommendation classes consistent in how they are called. Each concrete recommendation class implements its own `recommend(User user, int number)` method, and the caller only needs to create the chosen recommendation object and invoke this method. A small utility class `RecommendationManager` is used to centralise reusable helper logic that multiple algorithms depend on, rather than duplicating the same sorting or conversion code across different recommendation classes.

### 5.1 Shared Helper Logic
A key helper is `getRatingMovies(ArrayList<Movie> movies, int number)` (Listing 8). This method takes a candidate movie list, sorts it by rating in descending order using `movies.sort(...)` with a custom comparator, and then collects only the first Top-N results. A small but important detail is that the loop stops once `result.size() == number`, so the program does not continue scanning the full list when the required number has already been collected. This keeps the method efficient and predictable.

```java
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
```

Another helper is `getDecade(String yearStr)`. Movie years are stored as strings in the raw data, so the method first converts the year into an integer and then maps it into a decade by removing the last digit. This ensures that decade grouping is based on numeric meaning rather than string matching, and avoids incorrect grouping caused by string formatting differences.

### 5.2 Basic Recommendation Classes
`GenreRecommendation` first analyses the user’s preferences by counting genres from the user’s existing data (watchlist/history). The implementation collects genre counts using a map, then converts `entrySet()` into a list of `Map.Entry<String, Integer>` and sorts it by count. After obtaining the most preferred genres, the algorithm builds a candidate list from the global movie library and returns recommendations from those preferred genres first. A practical detail here is that preference is derived from real user behaviour rather than from hardcoded genres, so different users will naturally receive different results.

`DecadeRecommendation` follows a similar structure but uses decade buckets instead of genres. It counts which decades appear most frequently in the user’s data and then recommends movies from those decades. The decade computation is kept consistent using the getDecade helper, which avoids repeated code and prevents decade calculation errors.

`RatingRecommendation` is the simplest: it treats the entire movie library as candidates and relies on the shared sorting helper to return top-rated movies. However, to avoid recommending movies the user already interacted with, the candidate list is filtered to exclude items that already exist in the watchlist or history, so the output is always “new to the user”.

### 5.3 Advanced Recommendation Classes
The advanced recommendations extend the basic logic by combining constraints (e.g., genre + rating and decade + rating) rather than introducing a completely different mechanism. In practice, the advanced classes still begin by identifying the user’s favourite category (genre or decade), but then apply rating prioritisation to improve recommendation quality. The typical flow is: build a candidate list that matches the preferred category, remove any movies already in watchlist/history, and finally use `getRatingMovies(...)` to output the highest-rated subset of the remaining candidates.

---

## 6.0 Testing and Robustness

This chapter summarises our testing approach and the evidence collected for verification. We conducted scenario-based end-to-end tests on both entry points (CLI and JavaFX GUI) using the same CSV data files, focusing on required workflow correctness, persistence across runs, and robustness under invalid inputs. The results are supported by execution screenshots and a before and after comparison of `users.csv`.

### 6.1 Functional Tests
We verified the full CLI workflow from authentication to watchlist/history updates and recommendation output. The end-to-end run is shown in Figure 2, where the system remains interactive after each operation and returns to the main menu correctly.

<img src="E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\LoginTest.png" alt="LoginTest" style="zoom:33%;" /><img src="E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\AddWatchlistTest.png" alt="AddWatchlistTest" style="zoom:33%;" /><img src="E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\AddHistoryTest.png" alt="AddHistoryTest" style="zoom:33%;" /><img src="E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\HistoryView.png" alt="HistoryView" style="zoom: 25%;" /><img src="E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\Recommendation.png" alt="Recommendation" style="zoom:33%;" />

For GUI robustness, we tested invalid login inputs and verified that the program does not crash but displays clear feedback. Figure 3 demonstrates error handling on the login page and also shows a successful registration or password reset flow.

<img src="E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\WrongPassword.png" alt="WrongPassword" style="zoom: 50%;" /><img src="E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\WrongCode.png" alt="WrongCode" style="zoom:50%;" /><img src="E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\RegisterSuccess.png" alt="RegisterSuccess" style="zoom:50%;" /><img src="E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\ForgetPasswordSuccess.png" alt="ForgetPasswordSuccess" style="zoom:50%;" />

When the user requests a fixed number of recommendations, the system returns a Top-N list and avoids suggesting movies the user has already added to the watchlist or watched before. This behaviour is evidenced in Figure 4, where N = 8 produces exactly eight recommendations and none of the IDs overlap with the user’s existing watchlist/history.

<img src="E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\Watchlist.png" alt="Watchlist" style="zoom:33%;" /><img src="E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\RecommendationNumber.png" alt="RecommendationNumber" style="zoom: 33%;" /><img src="E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\History.png" alt="History" style="zoom:33%;" />

### 6.2 Persistence
Persistence was validated by comparing the same user record in users.csv before and after state-changing operations. Figure 5 shows the updated watchlist/history fields after marking a movie as watched, confirming that changes are written back and reloaded correctly across runs.

![Before](E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\Before.png)

![After](E:\GitProjects\Study-Notes\CPT111-Movie-Recommendation-System\Images\After.png)

### 6.3 Invalid Inputs and Exception
In the CLI version, robustness is achieved through defensive checks around menu selection and user-provided values. The program validates inputs before executing operations such as adding/removing watchlist items, marking movies as watched, and requesting recommendations. When an invalid option or value is entered, the program prints an error message and returns to the menu loop instead of terminating. This ensures that incorrect input becomes a recoverable event rather than a fatal error. For operations that depend on movie IDs, the program checks whether the entered ID exists and prevents duplicates (e.g., adding the same movie twice to the watchlist). For authentication-related actions, the program verifies username constraints and password correctness before changing state.

To keep validation logic readable and reusable, the project defines dedicated checks and uses custom exception types for common input problems (for example, illegal characters, invalid length, and username format violations, as it is shown in Listing 9). These exceptions represent “expected failures” caused by user behaviour, and they are caught at the interaction layer so that the system can display a human-friendly message. This approach avoids deeply nested conditional code and makes it clear which rule has been violated. The same validation rules are reused across different workflows such as registration and password reset, so behaviour remains consistent.

```java
public static void mainCheck(String str) throws InvalidCharacterException, LengthException {...}
```

In the GUI version, the same validation rules are enforced, but feedback is shown via an on-screen error label and alert dialogues (Listing 10). Each button handler validates user input first (e.g., empty fields, wrong code, invalid selection, non-numeric quantity) and only proceeds when safe, preventing crashes from parsing or illegal states.

```java
public static void showError(Label errorLabel, String message) {
    errorLabel.setText(message);
    errorLabel.setVisible(true);
}

public static void showSuccessAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    ...
}
```

---

## 7.0 Team Practices

We organised the work around clear module ownership (object design, login/authentication, CLI core features and recommendation logic, and JavaFX GUI). The CLI was implemented first as a stable baseline, then the GUI was built on top by reusing the same underlying operations to keep behaviour consistent. Integration issues (e.g., navigation or state passing between scenes) were discussed and resolved through group messaging, and we reviewed the end-to-end workflow together before finalising the report.

---

## 8.0 Ethics

This section discusses two ethical risks in our system: (1) user data security and privacy in a CSV-based storage design, and (2) bias and potential abuse in a history-driven recommendation process. We also propose practical mitigation strategies that fit the scope of this coursework.

### 8.1 User Privacy and Data Security
At present, user credentials are stored in plaintext in the user CSV file, which creates a clear risk if the file is accessed by unauthorised parties. A straightforward improvement is to store hashed passwords rather than plaintext. In practice, this should be done with a salt and a one-way hash (e.g., storing `salt:hash` instead of the raw password), so that even if the CSV file is leaked, passwords are not directly exposed. As an additional safeguard, the system should minimise the exposure of the CSV file by limiting access permissions and avoiding unnecessary logging of sensitive values during execution.

Within the codebase, we also applied encapsulation to reduce the chance of incorrect or unintended state changes. In `User`, we intentionally do not expose setters that replace the entire watchlist/history structure (Listing 11}). Instead, updates are performed through controlled methods (e.g., adding/removing items and recording watched movies), which ensures that validation rules are consistently applied and helps maintain data integrity. However, it is important to note that this is not a full security guarantee: a user with direct access to the CSV file could still modify the stored data externally. Therefore, file-level protections and password hashing remain the primary privacy-related improvements.

```java
// setters
public void setUsername(String username) {this.username = username;}
public void setPassword(String password) {this.password = password;}
```

### 8.2 Recommendation Bias and Potential Abuse
Our recommendation logic relies mainly on implicit signals from watchlist and viewing history. A limitation of this approach is that it does not capture whether the user actually liked a movie. For example, if a user watches a movie from a certain genre but dislikes it, the system still increases the preference count for that genre, which can lead to over-recommending similar movies. This creates a form of “static preference bias” where early viewing behaviour can dominate later recommendations, reducing diversity and adaptability over time.

A practical improvement is to introduce an explicit feedback mechanism, such as allowing users to rate watched movies (e.g., 1–10) or mark items as “not interested”. Recommendations can then weigh positive feedback more strongly than mere viewing counts, and negative feedback can reduce repeated suggestions of unwanted genres/decades. In addition, the system can reduce over-specialisation by adding a small diversification rule (e.g., limiting consecutive recommendations from the same dominant genre/decade), so the output remains personalised but not repetitive.

---

## 9.0 Conclusion
In this coursework, we implemented a Movie Recommendation and Tracker System with two runnable entry points: a complete CLI version and a JavaFX GUI version. The system supports authentication, browsing the movie library, managing a watchlist, recording viewing history, and generating Top-N recommendations. User state is persisted through CSV files so that changes remain consistent across runs.

Several advanced elements were integrated beyond the baseline requirements, including a JavaFX GUI designed with Scene Builder, multiple switchable recommendation options, and differentiated user privileges (basic vs premium). Robustness was improved by validating user inputs in both interfaces and ensuring that invalid operations do not terminate execution.

Future work would focus on strengthening security and improving recommendation quality. The most important security improvement is hashing user passwords before storing them in the CSV file. For recommendation quality, adding explicit user feedback (ratings or “not interested”) would reduce bias from purely history-driven signals and allow the system to adapt to changing preferences. Additional improvements could include more systematic testing, further refactoring to reduce duplication between CLI and GUI interaction layers, and enhanced GUI usability features.