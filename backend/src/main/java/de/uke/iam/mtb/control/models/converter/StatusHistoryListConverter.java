package de.uke.iam.mtb.control.models.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.uke.iam.mtb.control.models.coredata.StatusHistory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.List;
import org.postgresql.util.PGobject;

@Converter
public class StatusHistoryListConverter implements AttributeConverter<List<StatusHistory>, PGobject> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public PGobject convertToDatabaseColumn(List<StatusHistory> statusHistoryList) {
    PGobject pgObject = new PGobject();
    pgObject.setType("jsonb");
    try {
      objectMapper.registerModule(new JavaTimeModule());
      String jsonString = objectMapper.writeValueAsString(statusHistoryList);
      JsonNode jsonNode = objectMapper.readTree(jsonString);
      pgObject.setValue(objectMapper.writeValueAsString(jsonNode));
    } catch (Exception e) {
      throw new RuntimeException("Error converting list to JSON: " + e.getMessage());
    }
    return pgObject;
  }

  @Override
  public List<StatusHistory> convertToEntityAttribute(PGobject pgObject) {
    try {
      objectMapper.registerModule(new JavaTimeModule());
      TypeReference<List<StatusHistory>> typeReference = new TypeReference<List<StatusHistory>>() {
      };
      return objectMapper.readValue(pgObject.getValue(), typeReference);
    } catch (IOException e) {
      throw new RuntimeException("Error converting JSON to list: " + e.getMessage());
    }
  }
}
