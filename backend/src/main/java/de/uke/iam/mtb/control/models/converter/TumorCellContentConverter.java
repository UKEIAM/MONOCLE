package de.uke.iam.mtb.control.models.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.uke.iam.mtb.control.models.coredata.TumorCellContent;
import jakarta.persistence.AttributeConverter;
import java.io.IOException;
import org.postgresql.util.PGobject;

public class TumorCellContentConverter implements AttributeConverter<TumorCellContent, PGobject> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public PGobject convertToDatabaseColumn(TumorCellContent tumorCellContent) {
    PGobject pgObject = new PGobject();
    pgObject.setType("jsonb");
    try {
      objectMapper.registerModule(new JavaTimeModule());
      String jsonString = objectMapper.writeValueAsString(tumorCellContent);
      JsonNode jsonNode = objectMapper.readTree(jsonString);
      pgObject.setValue(objectMapper.writeValueAsString(jsonNode));
    } catch (Exception e) {
      throw new RuntimeException("Error converting list to JSON: " + e.getMessage());
    }
    return pgObject;
  }

  @Override
  public TumorCellContent convertToEntityAttribute(PGobject pgObject) {
    try {
      objectMapper.registerModule(new JavaTimeModule());
      return objectMapper.readValue(pgObject.getValue(), TumorCellContent.class);
    } catch (IOException e) {
      throw new RuntimeException("Error converting JSON to list: " + e.getMessage());
    }
  }

}
