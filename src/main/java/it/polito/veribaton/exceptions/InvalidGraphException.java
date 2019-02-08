package it.polito.veribaton.exceptions;

public class InvalidGraphException extends Exception{
    public InvalidGraphException() {
    }

    public InvalidGraphException(String message) {
        super(message);
    }

    public InvalidGraphException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidGraphException(Throwable cause) {
        super(cause);
    }
}
