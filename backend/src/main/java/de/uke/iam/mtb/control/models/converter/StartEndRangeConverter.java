package de.uke.iam.mtb.control.models.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uke.iam.mtb.control.models.coredata.ngsreport.StartEndRange;
import jakarta.persistence.AttributeConverter;
import java.io.IOException;
import org.postgresql.util.PGobject;

public class StartEndRangeConverter implements AttributeConverter<StartEndRange, PGobject> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public PGobject convertToDatabaseColumn(StartEndRange startEndRange) {
    PGobject pgObject = new PGobject();
    try {
      pgObject.setValue(objectMapper.writeValueAsString(startEndRange));
    } catch (Exception e) {
      throw new RuntimeException("Error converting StartEndRange to JSON: " + e.getMessage());
    }
    return pgObject;
  }

  @Override
  public StartEndRange convertToEntityAttribute(PGobject pgObject) {
    try {
      return objectMapper.readValue(pgObject.getValue(), StartEndRange.class);
    } catch (IOException e) {
      throw new RuntimeException("Error converting JSON to StartEndRange: " + e.getMessage());
    }
  }

}
