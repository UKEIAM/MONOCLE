//package de.uke.iam.mtb.control;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//import de.uke.iam.mtb.control.models.Step;
//import de.uke.iam.mtb.control.models.Workflow;
//import de.uke.iam.mtb.control.repository.StepRepository;
//import de.uke.iam.mtb.control.repository.TestApplicationConfiguration;
//import de.uke.iam.mtb.control.repository.WorkflowSchemaRepository;
//import java.util.List;
//import java.util.UUID;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest(classes = TestApplicationConfiguration.class, properties = {"spring.profiles.active=test"})
//public class WorkflowSchemaTest {
//
//  @Autowired
//  private WorkflowSchemaRepository workflowSchemaRepository;
//
//  @Autowired
//  private StepRepository stepRepository;
//
//  @Test
//  @Disabled
//  public void insertWorkflowTest() {
//    // Insert the first version of Workflow (Kerndatensatz 1)
//    Step clinicalDataset = new Step(
//        UUID.fromString("e74c20e6-cc82-4d03-9663-c79bed446180"),
//        "klinischer Kerndatensatz",
//        false,
//        null
//    );
//
//    Step geneticDataset = new Step(
//        UUID.fromString("927928dc-18cf-4e7c-bbfb-ce09f5ec7b31"),
//        "genetischer Kerndatensatz",
//        false,
//        null
//    );
//
//    Step requirements = new Step(
//        UUID.fromString("a788b4de-fa2c-46e0-962a-99ecd12ed16d"),
//        "Anforderungen",
//        false,
//        null);
//
//    Step geneticData = new Step(
//        UUID.fromString("f382d08a-34f4-4bff-9778-26636b092552"),
//        "Genetische Daten",
//        false,
//        null);
//
//    Step report = new Step(
//        UUID.fromString("203078cc-9fee-4f66-93c5-a2ac19e09abe"),
//        "MTB-Beschluss und MTB-Report (Freitext)",
//        false,
//        null);
//
//    Step transmissionToKIS = new Step(
//        UUID.fromString("58271d9b-97e0-4242-bb53-aa5ef1dca6cf"),
//        "Übermittlung an KIS für MTB-Konferenz",
//        false,
//        null
//    );
//
//    Step communicationWithDKTK = new Step(
//        UUID.fromString("db53fab3-78c2-4117-abc5-fcf2205098ed"),
//        "Übermittlung und Kommunikation mit bwHealthCloud und DNPM-Knotenpunkt",
//        false,
//        null
//    );
//
//    Step transmission = new Step(
//        UUID.fromString("1e2e8e15-7a2b-4b27-b5a0-42bf9ed632c5"),
//        "Übermittlung",
//        false,
//        List.of(
//            transmissionToKIS,
//            communicationWithDKTK
//        )
//    );
//
//    Step coreDataSet = new Step(
//        UUID.fromString("268fb842-1760-4983-9177-e1b5977b73d7"),
//        "Kerndatensatz",
//        false,
//        List.of(
//            clinicalDataset,
//            geneticDataset
//        )
//    );
//    // save all steps of this Workflow in the Database
//    Step savedClinicalDataset = stepRepository.save(clinicalDataset);
//    Step savedGeneticDataset = stepRepository.save(geneticDataset);
//    Step savedRequirements = stepRepository.save(requirements);
//    Step savedGeneticData = stepRepository.save(geneticData);
//    Step savedReport = stepRepository.save(report);
//    Step savedTransmissionToKIS = stepRepository.save(transmissionToKIS);
//    Step savedCommunicationWithDKTK = stepRepository.save(communicationWithDKTK);
//    Step savedCoreDataSet = stepRepository.save(coreDataSet);
//    Step savedTransmission = stepRepository.save(transmission);
//
//    Workflow workflow = new Workflow(
//        UUID.fromString("98c02c20-fdfa-419b-bde3-b60d2a767b6d"),
//        "1",
//        List.of(
//            coreDataSet,
//            requirements,
//            geneticData,
//            report,
//            transmission
//        )
//    );
//    // save workflow in the database
//    Workflow savedWorkflow = workflowSchemaRepository.save(workflow);
//
//    // THEN
//    assertNotNull(savedWorkflow.getId());
//  }
//}
