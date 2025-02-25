package de.uke.iam.mtb.control.client;

import com.fasterxml.jackson.databind.JsonNode;
import de.uke.iam.mtb.control.exception.BwhcException;
import de.uke.iam.mtb.control.exception.EnviromentVariablesException;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class BwHealthCloud {

    public WebClient webClient;
    String BWHC_BASE_URL;
    String BWHC_USERNAME;
    String BWHC_PASSWORD;
    String BWHC_LOGIN_URL;
    String BWHC_UPLOAD_URL;

    @Autowired
    public BwHealthCloud(@Value("${BWHC_BASE_URL}") String BWHC_BASE_URL, @Value("${BWHC_USERNAME}") String BWHC_USERNAME,
        @Value("${BWHC_PASSWORD}") String BWHC_PASSWORD) throws EnviromentVariablesException {
        if (BWHC_BASE_URL == null) {
            throw new EnviromentVariablesException(
                "Environment variables did not found : Please define the enviroment variables BWHC_BASE_URL"
                    + "in your System or in Env file");
        }
        this.BWHC_BASE_URL = BWHC_BASE_URL;
        this.BWHC_USERNAME = BWHC_USERNAME;
        this.BWHC_PASSWORD = BWHC_PASSWORD;
        this.BWHC_LOGIN_URL = BWHC_BASE_URL + "/user/api/login";
        this.BWHC_UPLOAD_URL = BWHC_BASE_URL + "/etl/api/data/upload";
        this.webClient = getWebClient();
    }

    public WebClient getWebClient() {
        return WebClient.builder()
            .baseUrl(BWHC_BASE_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public String getToken() throws BwhcException {
        try {
            return Objects.requireNonNull(webClient.post()
                .uri(BWHC_LOGIN_URL)
                .body(BodyInserters.fromFormData("username", BWHC_USERNAME)
                    .with("password", BWHC_PASSWORD))
                .retrieve()
                .bodyToMono(Map.class)
                .block()).get("access_token").toString();
        }
        //  it indicates a client-side request error (such as network connectivity or invalid request)
        catch (WebClientRequestException e) {
            throw new BwhcException(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
        } catch (WebClientResponseException e) {
            throw new BwhcException(e.getStatusCode(), e.getResponseBodyAs(Map.class), e.getMessage());
        }

    }


  public Map sendData(Map<String, Object> data) throws BwhcException {
    try {
      return webClient.post()
          .uri(BWHC_UPLOAD_URL)
          .bodyValue(data)
          .retrieve()
          .bodyToMono(Map.class)
          .block();
    }
    //  it indicates a client-side request error (such as network connectivity or invalid request)
    catch (WebClientRequestException e) {
      throw new BwhcException(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
    } catch (WebClientResponseException e) {
      throw new BwhcException(e.getStatusCode(), e.getResponseBodyAs(Map.class), e.getMessage());
    }
  }

    public String sendJsonData(JsonNode data) throws BwhcException {
        try {
            return webClient
                .post()
                .uri(BWHC_UPLOAD_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(data))
                .retrieve()
                .bodyToMono(String.class)
                .block();
        } catch (WebClientRequestException e) {
            throw new BwhcException(HttpStatus.INTERNAL_SERVER_ERROR, null, e.getMessage());
        } catch (WebClientResponseException e) {
            throw new BwhcException(e.getStatusCode(), e.getResponseBodyAs(Map.class), e.getMessage());
        }
    }
}
