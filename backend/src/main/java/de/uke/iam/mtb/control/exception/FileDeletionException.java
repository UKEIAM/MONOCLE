package de.uke.iam.mtb.control.exception;

public class FileDeletionException extends RuntimeException {

    public FileDeletionException(String message) {
        super(message);
    }

    public FileDeletionException(String message, Throwable cause) {
        super(message, cause);
    }

}
