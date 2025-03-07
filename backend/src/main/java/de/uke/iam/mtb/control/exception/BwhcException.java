package de.uke.iam.mtb.control.exception;

import java.util.Map;
import org.springframework.http.HttpStatusCode;

public class BwhcException extends Exception {

  HttpStatusCode statusCode;
  Map responseBody;

  public BwhcException(HttpStatusCode statusCode, Map responseBody, String message) {
    super(message);
    this.statusCode = statusCode;
    this.responseBody = responseBody;
  }

  public HttpStatusCode getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(HttpStatusCode statusCode) {
    this.statusCode = statusCode;
  }

  public Map getResponseBody() {
    return responseBody;
  }

  public void setResponseBody(Map responseBody) {
    this.responseBody = responseBody;
  }
}
