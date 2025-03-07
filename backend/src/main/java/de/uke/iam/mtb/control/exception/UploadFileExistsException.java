package de.uke.iam.mtb.control.exception;

public class UploadFileExistsException extends RuntimeException {
    public UploadFileExistsException(String message) {
        super(message);
    }
    public UploadFileExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
