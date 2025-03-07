package de.uke.iam.mtb.control.models.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uke.iam.mtb.control.models.coredata.ngsreport.GeneCoding;
import jakarta.persistence.AttributeConverter;
import java.io.IOException;
import java.util.List;
import org.postgresql.util.PGobject;

public class GeneCodingListConverter implements AttributeConverter<List<GeneCoding>, PGobject> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public PGobject convertToDatabaseColumn(List<GeneCoding> geneCodingList) {
    PGobject pgObject = new PGobject();
    try {
      String jsonString = objectMapper.writeValueAsString(geneCodingList);
      JsonNode jsonNode = objectMapper.readTree(jsonString);
      pgObject.setValue(objectMapper.writeValueAsString(jsonNode));
    } catch (Exception e) {
      throw new RuntimeException("Error converting GeneCoding to JSON: " + e.getMessage());
    }
    return pgObject;
  }

  @Override
  public List<GeneCoding> convertToEntityAttribute(PGobject pgObject) {
    try {
      TypeReference<List<GeneCoding>> typeReference = new TypeReference<List<GeneCoding>>() {
      };
      return objectMapper.readValue(pgObject.getValue(), typeReference);
    } catch (IOException e) {
      throw new RuntimeException("Error converting JSON to GeneCoding: " + e.getMessage());
    }
  }

}
