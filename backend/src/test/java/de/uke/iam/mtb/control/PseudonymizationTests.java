//package de.uke.iam.mtb.control;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import de.uke.iam.mtb.api.model.AddPatientMlDto;
//import de.uke.iam.mtb.api.model.DataOfAddPatientTokenMlDto;
//import de.uke.iam.mtb.api.model.DataOfEditPatientTokenMlDto;
//import de.uke.iam.mtb.api.model.DataOfReadPatientTokenMlDto;
//import de.uke.iam.mtb.api.model.PatientCRUDEnumsDto;
//import de.uke.iam.mtb.api.model.PatientIdMlDto;
//import de.uke.iam.mtb.api.model.PatientMlDto;
//import de.uke.iam.mtb.api.model.PatientTokenAckMlDto;
//import de.uke.iam.mtb.api.model.PatientTokenMlDto;
//import de.uke.iam.mtb.api.model.SessionMlDto;
//import de.uke.iam.mtb.control.client.Pseudonymization;
//import de.uke.iam.mtb.control.exception.EnviromentVariablesException;
//import de.uke.iam.mtb.control.exception.PseudonymizationException;
//import de.uke.iam.mtb.control.repository.TestApplicationConfiguration;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest(classes = TestApplicationConfiguration.class, properties = {"spring.profiles.active=test"})
//class PseudonymizationTests {
//
//  private Pseudonymization pseudonymization;
//
//  @BeforeEach
//  void setup() throws EnviromentVariablesException {
//    pseudonymization = new Pseudonymization("http://localhost:8090/", "pleaseChangeMeToo", "3.1");
//    pseudonymization.BASE_URL = "http://localhost:8090/";
//  }
//
//
//  @Disabled
//  @Test
//  public void createSession_shouldReturnValidSessionMlDto() {
//    try {
//      SessionMlDto session = pseudonymization.createSession();
//      // Assert
//      assertNotNull(session);
//      assertNotNull(session.getSessionId());
//    } catch (PseudonymizationException e) {
//      e.printStackTrace();
//    }
//  }
//
//  @Disabled
//  @Test
//  public void isSessionActive_withValidSessionId_shouldReturnTrue() {
//    try {
//      // Arrange
//      SessionMlDto session = pseudonymization.createSession();
//
//      // Act
//      boolean isActive = pseudonymization.isSessionActive(session.getSessionId());
//
//      // Assert
//      assertTrue(isActive);
//    } catch (PseudonymizationException e) {
//      e.printStackTrace();
//    }
//
//  }
//
//  @Disabled
//  @Test
//  void getAddPatientToken_shouldReturnValidUUID() {
//    try {
//      // Arrange
//      SessionMlDto session = pseudonymization.createSession();
//      DataOfAddPatientTokenMlDto data = new DataOfAddPatientTokenMlDto();
//      List<String> idtypes = new ArrayList<>();
//      idtypes.add("pid");
//      data.setIdTypes(idtypes);
//      // set PatientTokenMlDto values
//      PatientTokenMlDto patientTokenMlDto = new PatientTokenMlDto();
//      patientTokenMlDto.setType(PatientCRUDEnumsDto.ADDPATIENT);
//      patientTokenMlDto.setData(data);
//      patientTokenMlDto.setAllowedUses("100");
//      // Act
//      PatientTokenAckMlDto patientTokenResponse = pseudonymization.getPatientTokenResponse(session.getSessionId(), patientTokenMlDto);
//      UUID addPatientTokenId = patientTokenResponse.getId();
//
//      // Assert
//      assertNotNull(addPatientTokenId);
//    } catch (PseudonymizationException | JsonProcessingException e) {
//      e.printStackTrace();
//    }
//  }
//
//  @Disabled
//  @Test
//  public void addPatientTest() {
//    try {
//      SessionMlDto session = pseudonymization.createSession();
//      UUID sessionId = session.getSessionId();
//
//      DataOfAddPatientTokenMlDto data = new DataOfAddPatientTokenMlDto();
//      List<String> idtypes = new ArrayList<>();
//      idtypes.add("pid");
//      data.setIdTypes(idtypes);
//      // set PatientTokenMlDto values
//      PatientTokenMlDto patientTokenMlDto = new PatientTokenMlDto();
//      patientTokenMlDto.setType(PatientCRUDEnumsDto.ADDPATIENT);
//      patientTokenMlDto.setData(data);
//      patientTokenMlDto.setAllowedUses("100");
//
//      PatientTokenAckMlDto patientTokenResponse = pseudonymization.getPatientTokenResponse(sessionId, patientTokenMlDto);
//      UUID addPatientTokenId = patientTokenResponse.getId();
//
//      AddPatientMlDto newPatient = new AddPatientMlDto();
//      // the id which will be generated
//      List<String> idTypes = List.of("pid");
//      newPatient.setIdtypes(idTypes);
//      // add external id to the patient
//      Map<String, String> externalIds = new HashMap<>();
//      externalIds.put("extid", "336633");
//      newPatient.setIds(externalIds);
//      newPatient.setSureness(true);
//
//      List<PatientIdMlDto> patientIds = pseudonymization.addPatient(addPatientTokenId, newPatient);
//      patientIds.forEach(patient -> System.out.println(patient.getIdType() + " : " + patient.getIdString() + "\n"));
//    } catch (PseudonymizationException e) {
//      e.printStackTrace();
//    } catch (JsonProcessingException e) {
//      throw new RuntimeException(e);
//    }
//
//  }
//
//  @Disabled
//  @Test
//  void getEditPatientToken_shouldReturnValidUUID() {
//    try {
//      // Arrange
//      SessionMlDto session = pseudonymization.createSession();
//      DataOfEditPatientTokenMlDto data = new DataOfEditPatientTokenMlDto();
//      // set the id of the patient, which should be edited
//      PatientIdMlDto patientId = new PatientIdMlDto();
//      patientId.setIdType("pid");
//      patientId.setIdString("0003Y0WZ");
//      data.setPatientId(patientId);
//
//      // add all field (Identification data), which could be changed with this Token
//      List<String> fields = Arrays.asList("vorname", "nachname");
//      data.setFields(fields);
//
//      // add all ids, which could be changed with this Token
//      List<String> ids = List.of("extid");
//      data.setIds(ids);
//
//      // set PatientTokenMlDto values
//      PatientTokenMlDto patientTokenMlDto = new PatientTokenMlDto();
//      patientTokenMlDto.setType(PatientCRUDEnumsDto.EDITPATIENT);
//      patientTokenMlDto.setData(data);
//      patientTokenMlDto.setAllowedUses("100");
//      // Act
//      PatientTokenAckMlDto patientTokenResponse = pseudonymization.getPatientTokenResponse(session.getSessionId(), patientTokenMlDto);
//      UUID editPatientTokenId = patientTokenResponse.getId();
//
//      // Assert
//      assertNotNull(editPatientTokenId);
//    } catch (PseudonymizationException e) {
//      e.printStackTrace();
//    } catch (JsonProcessingException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  @Disabled
//  @Test
//  public void editPatientTest() {
//    try {
//      SessionMlDto session = pseudonymization.createSession();
//      UUID sessionId = session.getSessionId();
//
//      DataOfEditPatientTokenMlDto data = new DataOfEditPatientTokenMlDto();
//
//      // set the id of the patient, which should be edited
//      PatientIdMlDto patientId = new PatientIdMlDto();
//      patientId.setIdType("pid");
//      patientId.setIdString("0003Y0WZ");
//      data.setPatientId(patientId);
//
//      // add all field (Identification data), which could be changed with this Token
//      List<String> fields = Arrays.asList("vorname", "nachname");
//      data.setFields(fields);
//
//      // add all ids, which could be changed with this Token
//      List<String> ids = List.of("extid");
//      data.setIds(ids);
//
//      // set PatientTokenMlDto values
//      PatientTokenMlDto patientTokenMlDto = new PatientTokenMlDto();
//      patientTokenMlDto.setType(PatientCRUDEnumsDto.EDITPATIENT);
//      patientTokenMlDto.setData(data);
//      patientTokenMlDto.setAllowedUses("100");
//      // Act
//      PatientTokenAckMlDto patientTokenResponse = pseudonymization.getPatientTokenResponse(session.getSessionId(), patientTokenMlDto);
//      UUID editPatientTokenId = patientTokenResponse.getId();
//
//      Map<String, String> variablesToChange = new HashMap<>();
//      variablesToChange.put("extid", "336677");
//
//      pseudonymization.editPatient(editPatientTokenId, variablesToChange);
//    } catch (PseudonymizationException e) {
//      e.printStackTrace();
//    } catch (JsonProcessingException e) {
//      throw new RuntimeException(e);
//    }
//
//  }
//
//
//  @Disabled
//  @Test
//  void getReadPatientToken_shouldReturnValidUUID() {
//    try {
//      // Arrange
//      SessionMlDto session = pseudonymization.createSession();
//      DataOfReadPatientTokenMlDto data = new DataOfReadPatientTokenMlDto();
//      // create a patientId element to add it to the data of the token
//      List<PatientIdMlDto> searchIds = new ArrayList<>();
//      PatientIdMlDto patientId1 = new PatientIdMlDto();
//      patientId1.setIdType("pid");
//      patientId1.setIdString("0003Y0WZ");
//      PatientIdMlDto patientId2 = new PatientIdMlDto();
//      patientId2.setIdType("pid");
//      patientId2.setIdString("0007W0W9");
//      searchIds.add(patientId1);
//      searchIds.add(patientId2);
//
//      // add all field (Identification data), which could be changed with this Token
//      data.setSearchIds(searchIds);
//      List<String> resultIds = Arrays.asList("pid", "extid");
//      data.setResultIds(resultIds);
//
//      // set PatientTokenMlDto values
//      PatientTokenMlDto patientTokenMlDto = new PatientTokenMlDto();
//      patientTokenMlDto.setType(PatientCRUDEnumsDto.READPATIENTS);
//      patientTokenMlDto.setData(data);
//      patientTokenMlDto.setAllowedUses("100");
//      // Act
//      PatientTokenAckMlDto patientTokenResponse = pseudonymization.getPatientTokenResponse(session.getSessionId(), patientTokenMlDto);
//      UUID readPatientTokenId = patientTokenResponse.getId();
//
//      // Assert
//      assertNotNull(readPatientTokenId);
//    } catch (PseudonymizationException e) {
//      e.printStackTrace();
//    } catch (JsonProcessingException e) {
//      throw new RuntimeException(e);
//    }
//
//  }
//
//  @Disabled
//  @Test
//  public void readPatientsTest() {
//    try {
//      SessionMlDto session = pseudonymization.createSession();
//
//      DataOfReadPatientTokenMlDto data = new DataOfReadPatientTokenMlDto();
//      // create a patientId element to add it to the data of the token
//      List<PatientIdMlDto> patients = new ArrayList<>();
//      PatientIdMlDto patientId1 = new PatientIdMlDto();
//      patientId1.setIdType("pid");
//      patientId1.setIdString("0003Y0WZ");
//      PatientIdMlDto patientId2 = new PatientIdMlDto();
//      patientId2.setIdType("pid");
//      patientId2.setIdString("0007W0W9");
//      patients.add(patientId1);
//      patients.add(patientId2);
//
//      // add all field (Identification data), which could be changed with this Token
//      data.setSearchIds(patients);
//      List<String> resultIds = Arrays.asList("pid", "extid");
//      data.setResultIds(resultIds);
//
//      // set PatientTokenMlDto values
//      PatientTokenMlDto patientTokenMlDto = new PatientTokenMlDto();
//      patientTokenMlDto.setType(PatientCRUDEnumsDto.READPATIENTS);
//      patientTokenMlDto.setData(data);
//      patientTokenMlDto.setAllowedUses("100");
//      // Act
//      PatientTokenAckMlDto patientTokenResponse = pseudonymization.getPatientTokenResponse(session.getSessionId(), patientTokenMlDto);
//      UUID readPatientTokenId = patientTokenResponse.getId();
//
//      List<PatientMlDto> patientsResponse = pseudonymization.getPatients(readPatientTokenId);
//      patientsResponse.forEach(
//          patient -> System.out.println(" Patient Id(0) : " + patient.getIds().get(0) + " Patient Id(1) : " + patient.getIds().get(1)));
//    } catch (PseudonymizationException e) {
//      e.printStackTrace();
//    } catch (JsonProcessingException e) {
//      throw new RuntimeException(e);
//    }
//
//  }
//
//  @Disabled
//  @Test
//  public void pseudonomizationTest() {
//
//  }
//
//
//}
