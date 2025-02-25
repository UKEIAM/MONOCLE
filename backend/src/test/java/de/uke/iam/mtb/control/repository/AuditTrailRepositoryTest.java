//package de.uke.iam.mtb.control.repository;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//import de.uke.iam.mtb.control.models.AuditTrailEntry;
//import java.util.List;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest(classes = TestApplicationConfiguration.class, properties = {"spring.profiles.active=test"})
//public class AuditTrailRepositoryTest {
//
//  @Autowired
//  private AuditTrailRepository auditTrailRepository;
//
//  @Test
//  @Disabled
//  public void insertEntryTest() {
//    AuditTrailEntry auditTrailEntry = new AuditTrailEntry("dummyuser", "An entry");
//
//    AuditTrailEntry savedEntry = auditTrailRepository.save(auditTrailEntry);
//    assertNotNull(savedEntry);
//  }
//
//  @Test
//  @Disabled
//  public void getAllEntries() {
//    List<AuditTrailEntry> list = auditTrailRepository.findAll().stream().toList();
//    for (AuditTrailEntry auditTrailEntry : list) {
//      System.out.println(auditTrailEntry.getId());
//    }
//  }
//}
