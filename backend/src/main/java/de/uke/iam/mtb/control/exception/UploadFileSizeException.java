package de.uke.iam.mtb.control.exception;

public class UploadFileSizeException extends RuntimeException {
    public UploadFileSizeException(String message) {
        super(message);
    }
    public UploadFileSizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
