package de.uke.iam.mtb.control.models.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uke.iam.mtb.control.models.coredata.TumorMorphology;
import jakarta.persistence.AttributeConverter;
import java.io.IOException;
import org.postgresql.util.PGobject;

public class TumorMorphologyConverter implements AttributeConverter<TumorMorphology, PGobject> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public PGobject convertToDatabaseColumn(TumorMorphology tumorCellContent) {
    PGobject pgObject = new PGobject();
    pgObject.setType("jsonb");
    try {
      String jsonString = objectMapper.writeValueAsString(tumorCellContent);
      JsonNode jsonNode = objectMapper.readTree(jsonString);
      pgObject.setValue(objectMapper.writeValueAsString(jsonNode));
    } catch (Exception e) {
      throw new RuntimeException("Error converting list to JSON: " + e.getMessage());
    }
    return pgObject;
  }

  @Override
  public TumorMorphology convertToEntityAttribute(PGobject pgObject) {
    try {
      return objectMapper.readValue(pgObject.getValue(), TumorMorphology.class);
    } catch (IOException e) {
      throw new RuntimeException("Error converting JSON to list: " + e.getMessage());
    }
  }

}
