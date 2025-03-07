package de.uke.iam.mtb.control.models.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uke.iam.mtb.control.models.coredata.ngsreport.GeneCoding;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import org.postgresql.util.PGobject;

@Converter
public class GeneCodingConverter implements AttributeConverter<GeneCoding, PGobject> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PGobject convertToDatabaseColumn(GeneCoding geneCoding) {
        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        try {
            pgObject.setValue(objectMapper.writeValueAsString(geneCoding));
        } catch (Exception e) {
            throw new RuntimeException("Error converting GeneCoding to JSON: " + e.getMessage());
        }
        return pgObject;
    }

    @Override
    public GeneCoding convertToEntityAttribute(PGobject pgObject) {
        try {
            return objectMapper.readValue(pgObject.getValue(), GeneCoding.class);
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to GeneCoding: " + e.getMessage());
        }
    }

}
