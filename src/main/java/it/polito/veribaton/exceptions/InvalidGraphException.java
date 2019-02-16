package it.polito.veribaton.exceptions;

/**
 * InvalidGraphException is thrown when an invalid graph is sent as input
 */
public class InvalidGraphException extends Exception{


    /**
     * Constructs a new InvalidGraphException with null as its detail message.
     */
    public InvalidGraphException() {
    }

    /**
     * Constructs a new InvalidGraphException with specified detail message.
     *
     * @param message the detail message
     */
    public InvalidGraphException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidGraphException with specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause, which can be null if non existent or unknown
     */
    public InvalidGraphException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidGraphException with the specified cause and a detail message of null or the cause string.
     * Useful when constructing a wrapper for another exception.
     *
     * @param cause the exception cause
     */
    public InvalidGraphException(Throwable cause) {
        super(cause);
    }
}
