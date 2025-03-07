package de.uke.iam.mtb.control.exception;

import org.springframework.http.HttpStatusCode;

/**
 * Exception thrown if something goes wrong when talking to Mainzelliste (Pseudonomization)
 */
public class PseudonymizationException extends Exception {

  HttpStatusCode statusCode;

  public PseudonymizationException(HttpStatusCode statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public HttpStatusCode getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(HttpStatusCode statusCode) {
    this.statusCode = statusCode;
  }
}
