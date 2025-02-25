//package de.uke.iam.mtb.control.repository;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//import de.uke.iam.mtb.api.model.AddressbookEntryDto.AddressbookEntryTypeEnum;
//import de.uke.iam.mtb.control.models.AddressbookEntry;
//import java.util.List;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest(classes = TestApplicationConfiguration.class, properties = {"spring.profiles.active=test"})
//public class AddressbookRepositoryTest {
//
//  @Autowired
//  private AddressbookRepository addressbookRepository;
//
//  @Test
//  @Disabled
//  public void insertEntryTest() {
//    AddressbookEntry addressbookEntry = new AddressbookEntry();
//    addressbookEntry.setFirstname("Max");
//    addressbookEntry.setLastname("Mustermann");
//    addressbookEntry.setEmail("max@mustermann.nowhere");
//    addressbookEntry.setUKEinternal(true);
//    addressbookEntry.setAddressbookEntryType(AddressbookEntryTypeEnum.ZUWEISER);
//
//    AddressbookEntry savedEntry = addressbookRepository.save(addressbookEntry);
//    assertNotNull(savedEntry);
//  }
//
//  @Test
//  @Disabled
//  public void getAllEntries() {
//    List<AddressbookEntry> list = addressbookRepository.findAll().stream().toList();
//    for (AddressbookEntry auditTrailEntry : list) {
//      System.out.println(auditTrailEntry.getId());
//    }
//  }
//}
