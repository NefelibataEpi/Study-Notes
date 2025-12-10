package main.java.exception;

public class UsernameFormatException extends RuntimeException {
    public UsernameFormatException(String message) {
        super(message);
    }
}
