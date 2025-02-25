package de.uke.iam.mtb.control.models.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.uke.iam.mtb.control.models.coredata.SpecimenCollection;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import org.postgresql.util.PGobject;

@Converter
public class SpecimenCollectionConverter implements AttributeConverter<SpecimenCollection, PGobject> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public PGobject convertToDatabaseColumn(SpecimenCollection specimenCollection) {
    PGobject pgObject = new PGobject();
    pgObject.setType("jsonb");
    try {
      objectMapper.registerModule(new JavaTimeModule());
      pgObject.setValue(objectMapper.writeValueAsString(specimenCollection));
    } catch (Exception e) {
      throw new RuntimeException("Error converting SpecimenCollection to JSON: " + e.getMessage());
    }
    return pgObject;
  }

  @Override
  public SpecimenCollection convertToEntityAttribute(PGobject pgObject) {
    try {
      objectMapper.registerModule(new JavaTimeModule());
      return objectMapper.readValue(pgObject.getValue(), SpecimenCollection.class);
    } catch (IOException e) {
      throw new RuntimeException("Error converting JSON to SpecimenCollection: " + e.getMessage());
    }
  }

}
