package de.uke.iam.mtb.control.models.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.uke.iam.mtb.control.models.coredata.Code;
import jakarta.persistence.AttributeConverter;
import org.postgresql.util.PGobject;

import java.io.IOException;

public class CodeConverter implements AttributeConverter<Code, PGobject> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PGobject convertToDatabaseColumn(Code code) {
        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        try {
            pgObject.setValue(objectMapper.writeValueAsString(code));
        } catch (Exception e) {
            throw new RuntimeException("Error converting SpecimenCollection to JSON: " + e.getMessage());
        }
        return pgObject;
    }

    @Override
    public Code convertToEntityAttribute(PGobject pgObject) {
        try {
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(pgObject.getValue(), Code.class);
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to SpecimenCollection: " + e.getMessage());
        }
    }

}
