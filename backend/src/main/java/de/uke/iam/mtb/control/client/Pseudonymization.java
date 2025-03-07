package de.uke.iam.mtb.control.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uke.iam.mtb.api.model.AddPatientMlDto;
import de.uke.iam.mtb.api.model.PatientIdMlDto;
import de.uke.iam.mtb.api.model.PatientMlDto;
import de.uke.iam.mtb.api.model.PatientTokenAckMlDto;
import de.uke.iam.mtb.api.model.PatientTokenMlDto;
import de.uke.iam.mtb.api.model.SessionMlDto;
import de.uke.iam.mtb.control.exception.EnviromentVariablesException;
import de.uke.iam.mtb.control.exception.PseudonymizationException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Component
public class Pseudonymization {

  final String SESSION_API;
  final String TOKEN_API;
  // Url to get Pateints or add a new Patient
  final String PATIENT_API;
  final String EDIT_PATIENT_API;
  public String BASE_URL;
  public WebClient webClient;

  @Autowired
  public Pseudonymization(@Value("${ML_BASE_URL}") String ML_BASE_URL,
      @Value("${ML_API_KEY}") String ML_API_KEY,
      @Value("${ML_API_VERSION}") String ML_API_VERSION
  ) throws EnviromentVariablesException {
    if (ML_BASE_URL == null || ML_API_KEY == null || ML_API_VERSION == null) {
      throw new EnviromentVariablesException("Environment variables did not found : Please define the enviroment variables ML_BASE_URL, "
          + "ML_API_KEY and ML_API_VERSION in your System or in Env file");
    }
    this.BASE_URL = ML_BASE_URL;
    this.SESSION_API = BASE_URL + "/sessions";
    this.TOKEN_API = BASE_URL + "/sessions/%s/tokens";
    this.PATIENT_API = BASE_URL + "/patients?tokenId=%s";
    this.EDIT_PATIENT_API = BASE_URL + "/patients/tokenId/%s";
    this.webClient = getWebClient(ML_API_KEY, ML_API_VERSION);
  }

  public WebClient getWebClient(String mainzellisteApiKey, String mainzellisteApiVersion) {
    return WebClient.builder()
        .baseUrl(BASE_URL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("mainzellisteApiKey", mainzellisteApiKey)
        .defaultHeader("mainzellisteApiVersion", mainzellisteApiVersion)
        .build();
  }

  public String objectToStringWithoutNull(Object object) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return objectMapper.writeValueAsString(object);
  }

  public SessionMlDto createSession() throws PseudonymizationException {
    try {
      return webClient.post()
          .uri(SESSION_API)
          .retrieve()
          .bodyToMono(SessionMlDto.class)
          .block();
      //  it indicates a client-side request error (such as network connectivity or invalid request)
    } catch (WebClientRequestException e) {
      throw new PseudonymizationException(HttpStatusCode.valueOf(400), e.getMessage());
      //  it indicates a response error (such as a non-2xx status code)
    } catch (WebClientResponseException e) {
      throw new PseudonymizationException(e.getStatusCode(), e.getMessage());
    }
  }

  public boolean isSessionActive(UUID sesseionId) throws PseudonymizationException {
    String url = SESSION_API + "/" + sesseionId;
    try {
      this.webClient.get()
          .uri(url)
          .retrieve()
          .bodyToMono(SessionMlDto.class)
          .block();
      return true;
    } catch (WebClientRequestException e) {
      throw new PseudonymizationException(HttpStatusCode.valueOf(400), e.getMessage());
    } catch (WebClientResponseException e) {
      throw new PseudonymizationException(e.getStatusCode(), e.getMessage());
    }
  }

  public PatientTokenAckMlDto getPatientTokenResponse(UUID sessionId, PatientTokenMlDto patientToken)
      throws JsonProcessingException, PseudonymizationException {
    String url = String.format(TOKEN_API, sessionId);
    String patientTokenStr = objectToStringWithoutNull(patientToken);
    try {
      return this.webClient.post()
          .uri(url)
          .body(BodyInserters.fromValue(patientTokenStr))
          .retrieve()
          .bodyToMono(PatientTokenAckMlDto.class)
          .block();
    } catch (WebClientRequestException e) {
      throw new PseudonymizationException(HttpStatusCode.valueOf(400), e.getMessage());
    } catch (WebClientResponseException e) {
      throw new PseudonymizationException(e.getStatusCode(), e.getMessage());
    }
  }

  public List<PatientIdMlDto> addPatient(UUID tokenId, AddPatientMlDto newPatient)
      throws JsonProcessingException, PseudonymizationException {
    String url = String.format(PATIENT_API, tokenId);

    String newPatientStr = objectToStringWithoutNull(newPatient);
    try {
      return this.webClient.post()
          .uri(url)
          .body(BodyInserters.fromValue(newPatientStr))
          .retrieve()
          .bodyToFlux(PatientIdMlDto.class)
          .collectList()
          .block();
    } catch (WebClientRequestException e) {
      throw new PseudonymizationException(HttpStatusCode.valueOf(400), e.getMessage());
    } catch (WebClientResponseException e) {
      throw new PseudonymizationException(e.getStatusCode(), e.getMessage());
    }
  }

  public void editPatient(UUID tokenId, Map<String, String> variablesToChange) throws PseudonymizationException {
    String url = String.format(EDIT_PATIENT_API, tokenId);
    try {
      this.webClient.put()
          .uri(url)
          .body(BodyInserters.fromValue(variablesToChange.toString()))
          .retrieve()
          .toBodilessEntity()
          .block();
    } catch (WebClientRequestException e) {
      throw new PseudonymizationException(HttpStatusCode.valueOf(400), e.getMessage());
    } catch (WebClientResponseException e) {
      throw new PseudonymizationException(e.getStatusCode(), e.getMessage());
    }
  }

  public List<PatientMlDto> getPatients(UUID tokenId) throws PseudonymizationException {
    String url = String.format(PATIENT_API, tokenId);
    try {
      return this.webClient.get()
          .uri(url)
          .retrieve()
          .bodyToFlux(PatientMlDto.class)
          .collectList()
          .block();
    } catch (WebClientRequestException e) {
      throw new PseudonymizationException(HttpStatusCode.valueOf(400), e.getMessage());
    } catch (WebClientResponseException e) {
      throw new PseudonymizationException(e.getStatusCode(), e.getMessage());
    }
  }

}
