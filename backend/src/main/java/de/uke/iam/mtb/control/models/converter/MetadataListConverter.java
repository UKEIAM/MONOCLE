package de.uke.iam.mtb.control.models.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uke.iam.mtb.control.models.coredata.ngsreport.Metadata;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.List;
import org.postgresql.util.PGobject;

@Converter
public class MetadataListConverter implements AttributeConverter<List<Metadata>, PGobject> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public PGobject convertToDatabaseColumn(List<Metadata> metadataList) {
    PGobject pgObject = new PGobject();
    pgObject.setType("jsonb");
    try {
      String jsonString = objectMapper.writeValueAsString(metadataList);
      JsonNode jsonNode = objectMapper.readTree(jsonString);
      pgObject.setValue(objectMapper.writeValueAsString(jsonNode));
    } catch (Exception e) {
      throw new RuntimeException("Error converting MetaDataList to JSON: " + e.getMessage());
    }
    return pgObject;
  }

  @Override
  public List<Metadata> convertToEntityAttribute(PGobject pgObject) {
    try {
      TypeReference<List<Metadata>> typeReference = new TypeReference<List<Metadata>>() {
      };
      return objectMapper.readValue(pgObject.getValue(), typeReference);
    } catch (IOException e) {
      throw new RuntimeException("Error converting JSON to MetaData: " + e.getMessage());
    }
  }

}
