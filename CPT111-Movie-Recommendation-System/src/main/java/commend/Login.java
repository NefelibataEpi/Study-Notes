package main.java.commend;

import main.java.exception.InvalidCharacterException;
import main.java.exception.LengthException;
import main.java.exception.UsernameFormatException;
import main.java.model.user.BasicUser;
import main.java.model.user.PremiumUser;
import main.java.model.user.User;
import main.java.util.DataManager;

import java.util.*;

public class Login {
    // constant
    private static final String REGISTER = "1";
    private static final String LOGIN = "2";
    private static final String FORGET_PASSWORD = "3";
    private static final String EXIT = "4";

    // start the login system
    public static void startLogin() {
        List<User> allUsers = DataManager.getAllUsers();
        Scanner sc = new Scanner(System.in);

        while (true) {
            printMenu();

            System.out.print("Please select you choice: ");
            String choose = sc.nextLine();

            switch (choose) {
                case REGISTER -> register(allUsers, sc);
                case LOGIN -> login(allUsers, sc);
                case FORGET_PASSWORD -> forgetPassword(allUsers);
                case EXIT -> {
                    System.out.println("Thank you for your use.");
                    System.out.println("Wish you all the best!");
                    System.exit(0);
                }
                default -> System.out.println("Wrong choice!");
            }
        }
    }

    // Method
    // 1. register
    public static void register(List<User> allUsers, Scanner sc) {
        // state
        String username;
        String password;

        // 1. enter the username
        while (true) {
            System.out.print("Please enter your username: ");
            username = sc.nextLine();

            try {
                // verify whether the format is correct
                checkUsername(username);

                // determine whether the username is unique
                boolean containFlag = containUsername(allUsers, username);
                if (containFlag) {
                    // the username already exists
                    System.out.println("The username " + username + " already exists.");
                    System.out.println("Please re-enter your username.");
                } else {
                    System.out.println("The username " + username + " is available");
                    break;
                }

            } catch (LengthException | InvalidCharacterException | UsernameFormatException e) {
                System.out.println("Error:" + e.getMessage());
            }
        }

        // 2. enter the password
        // only when there are two consistent entries can you register
        while (true) {
            System.out.print("Please enter the password you want to register: ");
            password = sc.nextLine();

            try {
                mainCheck(password);
                System.out.print("Please re-enter your password: ");
                String againPassword = sc.nextLine();

                if (!password.equals(againPassword)) {
                    System.out.println("The two passwords you entered do not match.");
                    System.out.println("Please re-enter your password.");
                } else {
                    System.out.println("The password is available");
                    break;
                }

            } catch (LengthException | InvalidCharacterException e){
                System.out.println("Error:" + e.getMessage());
            }
        }

        // create user
        User user;
        Loop: while (true) {
            System.out.println("Do you want to become a VIP user?");
            System.out.println("1. Yes(Premium)");
            System.out.println("2. No(Basic)");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> {
                    user = new PremiumUser(username, password);
                    break Loop;
                }
                case "2" -> {
                    user = new BasicUser(username, password);
                    break Loop;
                }
                default -> System.out.println("Wrong choice!");
            }
        }

        allUsers.add(user);
        DataManager.refreshUserCSV(allUsers); // refresh CSV
        System.out.println("Register successfully!");
    }

    // 2. login
    public static void login(List<User> allUsers, Scanner sc) {
        for (int i = 0; i < 3; i++) {
            // enter the username
            System.out.print("Please enter your username: ");
            String username = sc.nextLine();

            // determine whether the username exists
            boolean containFlag = containUsername(allUsers, username);
            if (!containFlag) {
                System.out.println("The username " + username + " is not registered.");
                System.out.println("Please register before your login.");
                return;
            }

            // enter the password
            System.out.print("Please enter your password: ");
            String password = sc.nextLine();

            // verification code
            while (true) {
                String rightCode = getCode();
                System.out.println("The current verification code is " + rightCode);
                System.out.print("Please enter the verification code: ");
                String code = sc.nextLine();
                if (code.equalsIgnoreCase(rightCode)) {
                    System.out.println("Correct!");
                    break;
                } else {
                    System.out.println("Wrong!");
                }
            }

            // verify whether the username and password are correct
            User userInfo = new User(username, password);
            User user = findUser(allUsers, userInfo);
            if (user != null) {
                System.out.println("Login successful!");

                // launch tha main procedure
                Main.startMain(user, allUsers);

                break;
            } else {
                System.out.println("Login failed!");

                if (i == 2) {
                    System.out.println("The current account " + username + " is locked");
                    return;
                } else {
                    System.out.println("The username or password is incorrect.");
                    System.out.println("You have " + (2-i) + " attempts left.");
                }
            }
        }
    }

    // 3. forget password
    public static void forgetPassword(List<User> allUsers) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Please enter the username: ");
        String username = sc.nextLine();
        boolean containFlag = containUsername(allUsers, username);
        if (!containFlag) {
            System.out.println("The username " + username + " is not registered.");
            System.out.println("Please register before your login.");
            return;
        }

        int index = findIndex(allUsers, username);
        User user = allUsers.get(index);

        String password;
        while (true) {
            System.out.print("Please enter a new password: ");
            password = sc.nextLine();

            try {
                mainCheck(password);

                System.out.print("Please re-enter your new password: ");
                String againPassword = sc.nextLine();
                if (password.equals(againPassword)) {
                    System.out.println("The password is now available");
                    break;
                } else {
                    System.out.println("The two passwords you entered do not match.");
                    System.out.println("Please re-enter your password.");
                }
            } catch (LengthException | InvalidCharacterException e){
                System.out.println("Error:" + e.getMessage());
            }
        }

        // 直接修改
        user.setPassword(password);
        DataManager.refreshUserCSV(allUsers); // refresh CSV
        System.out.println("Password reset successfully!");
        startLogin();
    }

    // Plugin
    // 1. print the menu
    private static void printMenu() {
        System.out.println("===== Login System =====");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Forget Password");
        System.out.println("4. Logout");
    }

    // 2. the main checks are on format (length + character combination)
    public static void mainCheck(String str) throws InvalidCharacterException, LengthException {
        // length is between 3-20 characters
        int len = str.length();
        if (len < 3 || len > 20) {
            throw new LengthException("Length must be between 3 and 20 characters.");
        }

        // combinations of numbers and letters
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (! ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))) {
                throw new InvalidCharacterException("Only letters and digits are allowed.");
            }
        }
    }

    // 3. check if the username format is correct
    public static void checkUsername(String username) throws InvalidCharacterException, LengthException, UsernameFormatException {
        mainCheck(username);

        // cannot be pure numbers
        boolean hasLetter = false;
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                hasLetter = true;
                break;
            }
        }

        if (!hasLetter) {
            throw new UsernameFormatException("Username cannot be pure numbers.");
        }
    }

    // 4. check if the username is unique
    public static boolean containUsername(List<User> allUsers, String username) {
        for (User user : allUsers) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    // 5. generate verification code
    public static String getCode() {
        // create a set to store all uppercase and lowercase letters
        ArrayList<Character> list = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            list.add((char) (i + 'a'));
            list.add((char) (i + 'A'));
        }

        // randomly select 4 characters
        Random r = new Random();
        String str = "";

        for (int i = 0; i < 4; i++) {
            int index = r.nextInt(list.size());
            char c = list.get(index);
            str += c;
        }

        int number = r.nextInt(10);
        str += number;

        char[] arr = str.toCharArray();

        int randomIndex = r.nextInt(arr.length);
        char temp = arr[randomIndex];
        arr[randomIndex] = arr[arr.length - 1];
        arr[arr.length - 1] = temp;

        return new String(arr);
    }

    // 6. concentrate on verifying whether the username and password are correct
    //    obtain the User object through the username and password
    public static User findUser(List<User> allUsers, User userInfo) {
        for (User user : allUsers) {
            if (user.getUsername().equals(userInfo.getUsername()) && user.getPassword().equals(userInfo.getPassword())) {
                return user;
            }
        }

        return null;
    }

    // 7. query user information index
    public static int findIndex(List<User> allUsers, String username) {
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getUsername().equals(username)) {
                return i;
            }
        }

        return -1;
    }
}
