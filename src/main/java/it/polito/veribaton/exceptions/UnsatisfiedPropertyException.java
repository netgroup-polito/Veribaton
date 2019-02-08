package it.polito.veribaton.exceptions;

public class UnsatisfiedPropertyException extends Exception {
    public UnsatisfiedPropertyException() {
    }

    public UnsatisfiedPropertyException(String message) {
        super(message);
    }

    public UnsatisfiedPropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsatisfiedPropertyException(Throwable cause) {
        super(cause);
    }
}
