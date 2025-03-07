package de.uke.iam.mtb.control.models.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.List;
import org.postgresql.util.PGobject;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, PGobject> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public PGobject convertToDatabaseColumn(List<String> stringList) {
    PGobject pgObject = new PGobject();
    try {
      String jsonString = objectMapper.writeValueAsString(stringList);
      JsonNode jsonNode = objectMapper.readTree(jsonString);
      pgObject.setValue(objectMapper.writeValueAsString(jsonNode));
    } catch (Exception e) {
      throw new RuntimeException("Error converting list to JSON: " + e.getMessage());
    }
    return pgObject;
  }

  @Override
  public List<String> convertToEntityAttribute(PGobject pgObject) {
    try {
      TypeReference<List<String>> typeReference = new TypeReference<List<String>>() {
      };
      return objectMapper.readValue(pgObject.getValue(), typeReference);
    } catch (IOException e) {
      throw new RuntimeException("Error converting JSON to list: " + e.getMessage());
    }
  }
}
