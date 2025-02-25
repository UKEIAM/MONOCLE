//package de.uke.iam.mtb.control.repository;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import de.uke.iam.mtb.api.model.PatientDto;
//import de.uke.iam.mtb.control.models.mapper.CoreDataMapper;
//import de.uke.iam.mtb.control.models.mapper.PatientMapper;
//import de.uke.iam.mtb.control.models.mapper.StepInfoMapper;
//import de.uke.iam.mtb.control.models.mapper.WorkflowMapper;
//import de.uke.iam.mtb.control.service.CoreDataService;
//import de.uke.iam.mtb.control.service.PatientService;
//import java.io.File;
//import java.io.IOException;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest(classes = TestApplicationConfiguration.class, properties = {
//    "spring.profiles.active=test"})
//public class PatientServiceTest {
//
//  @Autowired
//  private PatientRepository patientRepository;
//
//  @Autowired
//  private CoreDataRepository coreDataRepository;
//
//  @Test
//  @Disabled
//  public void addPatientThroughServiceTest() throws IOException {
//    CoreDataMapper coreDataMapper = new CoreDataMapper();
//    CoreDataService coreDataService = new CoreDataService(coreDataRepository, coreDataMapper);
//    WorkflowMapper workflowMapper = new WorkflowMapper();
//    StepInfoMapper stepInfoMapper = new StepInfoMapper();
//    PatientMapper patientMapper = new PatientMapper(coreDataMapper, workflowMapper, stepInfoMapper);
//    PatientService patientService = new PatientService(patientRepository, patientMapper,
//        coreDataService, stepInfoMapper);
//
//    ClassLoader classLoader = getClass().getClassLoader();
//    File jsonFile = new File(classLoader.getResource("newpatient.json").getFile());
//
//    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
//    PatientDto patientDto = mapper.readValue(jsonFile, PatientDto.class);
//    try {
//      patientService.addPatient(patientDto);
//    } catch (Exception e) {
//      System.out.println(e.getMessage());
//    }
//
//  }
//
//}
