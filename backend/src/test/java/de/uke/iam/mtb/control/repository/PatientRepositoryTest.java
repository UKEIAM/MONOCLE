//package de.uke.iam.mtb.control.repository;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//import de.uke.iam.mtb.api.model.GenderTypeDto;
//import de.uke.iam.mtb.control.models.CoreData;
//import de.uke.iam.mtb.control.models.Patient;
//import de.uke.iam.mtb.control.models.Status;
//import de.uke.iam.mtb.control.models.Step;
//import de.uke.iam.mtb.control.models.StepInfo;
//import de.uke.iam.mtb.control.models.Workflow;
//import java.time.Instant;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//// TODO use @DataMongoTest for a Test MongoDB instance, when we do not need this test to insert data anymore?
//@SpringBootTest(classes = TestApplicationConfiguration.class, properties = {
//    "spring.profiles.active=test"})
//public class PatientRepositoryTest {
//
//  @Autowired
//  private PatientRepository patientRepository;
//
//  @Autowired
//  private WorkflowSchemaRepository workflowSchemaRepository;
//
//  @Autowired
//  private CoreDataRepository coreDataRepository;
//
//  @Autowired
//  private StepRepository stepRepository;
//
//  @Test
//  @Disabled
//  public void insertPatientTest() {
//    //Todo: check the steps of the workflow
//    Step entryDate = new Step(
//        UUID.randomUUID(),
//        "Aufnahmedatum",
//        false,
//        null
//    );
//
//    Step preTherapyAndPreDiagnosis = new Step(
//        UUID.randomUUID(),
//        "Vortherapie und Vordiagnose",
//        false,
//        null
//    );
//
//    Step savedEntryDate = stepRepository.save(entryDate);
//    Step savedPreTherapyAndPreDiagnosis = stepRepository.save(preTherapyAndPreDiagnosis);
//
//    Step clinicalDataset = new Step(
//        UUID.randomUUID(),
//        "klinischer Kerndatensatz",
//        false,
//        List.of(
//            savedEntryDate,
//            savedPreTherapyAndPreDiagnosis
//        )
//    );
//
//    Step geneticDataset = new Step(
//        UUID.randomUUID(),
//        "genetischer Kerndatensatz",
//        false,
//        null
//    );
//
//    Step savedClinicalDataset = stepRepository.save(clinicalDataset);
//    Step savedGeneticDataset = stepRepository.save(geneticDataset);
//
//    Step coreDataSet = new Step(
//        UUID.randomUUID(),
//        "Kerndatensatz",
//        false,
//        List.of(
//            savedClinicalDataset,
//            savedGeneticDataset
//        )
//    );
//
//    Step requirementsAndReferrers = new Step(
//        UUID.randomUUID(),
//        "Anforderungen und Zuweiser",
//        false,
//        null
//    );
//
//    Step molecularDiagnostics = new Step(
//        UUID.randomUUID(),
//        "molekulare Diagnostik",
//        false,
//        null
//    );
//
//    Step therapyRecommendationAndMtbReport = new Step(
//        UUID.randomUUID(),
//        "MTB-Beschluss und MTB-Report (Freitext)",
//        false,
//        null
//    );
//
//    Step transmissionToKIS = new Step(
//        UUID.randomUUID(),
//        "Übermittlung an KIS für MTB-Konferenz",
//        false,
//        null
//    );
//
//    Step communicationWithDKTK = new Step(
//        UUID.randomUUID(),
//        "Übermittlung und Kommunikation mit DKTK-Brückenkopf und DNPM-Knotenpunkt",
//        false,
//        null
//    );
//
//    Step savedTransmissionToKIS = stepRepository.save(transmissionToKIS);
//    Step savedCommunicationWithDKTK = stepRepository.save(communicationWithDKTK);
//
//    Step transmission = new Step(
//        UUID.randomUUID(),
//        "Übermittlung",
//        false,
//        List.of(
//            savedTransmissionToKIS,
//            savedCommunicationWithDKTK
//        )
//    );
//
//    Step savedCoreDataSet = stepRepository.save(coreDataSet);
//    Step savedRequirementsAndReferrers = stepRepository.save(requirementsAndReferrers);
//    Step savedMolecularDiagnostics = stepRepository.save(molecularDiagnostics);
//    Step savedTherapyRecommendationAndMtbReport = stepRepository.save(
//        therapyRecommendationAndMtbReport);
//    Step savedTransmission = stepRepository.save(transmission);
//
//    Workflow workflow = new Workflow(
//        UUID.randomUUID(),
//        "1.0",
//        List.of(
//            savedCoreDataSet,
//            savedRequirementsAndReferrers,
//            savedMolecularDiagnostics,
//            savedTherapyRecommendationAndMtbReport,
//            savedTransmission
//        )
//    );
//
//    Workflow savedWorkflow = workflowSchemaRepository.save(workflow);
//
//    CoreData coreData = coreDataRepository.save(
//        new CoreData(UUID.randomUUID(), Map.of("patient", Map.of(
//            "id", "someId", "sex", "male")), Instant.now(), Instant.now()));
//
//    // GIVEN
//    Patient patient = new Patient(
//        UUID.randomUUID(),
//        "Test ID",
//        "Test",
//        "Patient",
//        GenderTypeDto.MALE,
//        LocalDate.of(1990, 3, 17),
//        true,
//        "1234",
//        "Test Therapy Recommendation",
//        Instant.now(),
//        Instant.now(),
//        savedWorkflow,
//        List.of(
//            new StepInfo(
//                savedWorkflow.getSteps().get(0),
//                Status.PENDING
//            ),
//            new StepInfo(
//                savedWorkflow.getSteps().get(1),
//                Status.COMPLETE
//            )
//        ),
//        "TK"
//    );
//    patient.setCoreData(coreData);
//
//    // WHEN
//    Patient savedPatient = patientRepository.save(patient);
//
//    assertNotNull(savedPatient.getSoarianId());
//  }
//
//  @Test
//  @Disabled
//  public void getAllPatientsTest() {
//    List<Patient> list = patientRepository.findAll().stream().toList();
//    for (Patient patient : list) {
//      System.out.println(patient.getId().toString());
//    }
//  }
//}
