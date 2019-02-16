package it.polito.veribaton.exceptions;

/**
 * UnsatisfiedPropertyException is thrown when a property defined in verifoo request is not satisfiable
 */
public class UnsatisfiedPropertyException extends Exception {

    /**
     * Constructs a new UnsatisfiedPropertyException with null as its detail message.
     */
    public UnsatisfiedPropertyException() {
    }

    /**
     * Constructs a new UnsatisfiedPropertyException with specified detail message.
     *
     * @param message the detail message
     */
    public UnsatisfiedPropertyException(String message) {
        super(message);
    }

    /**
     * Constructs a new UnsatisfiedPropertyException with specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause, which can be null if non existent or unknown
     */
    public UnsatisfiedPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new UnsatisfiedPropertyException with the specified cause and a detail message of null or the cause string.
     * Useful when constructing a wrapper for another exception.
     *
     * @param cause the exception cause
     */
    public UnsatisfiedPropertyException(Throwable cause) {
        super(cause);
    }
}
