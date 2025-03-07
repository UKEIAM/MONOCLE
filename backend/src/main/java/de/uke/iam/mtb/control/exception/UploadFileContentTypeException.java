package de.uke.iam.mtb.control.exception;

public class UploadFileContentTypeException extends RuntimeException {
    public UploadFileContentTypeException(String message) {
        super(message);
    }
    public UploadFileContentTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
