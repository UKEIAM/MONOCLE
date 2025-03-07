package de.uke.iam.mtb.control.models.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.uke.iam.mtb.control.models.coredata.ngsreport.CopyNumberVariant;
import jakarta.persistence.AttributeConverter;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.util.List;

public class CopyNumberVariantListConverter implements AttributeConverter<List<CopyNumberVariant>, PGobject> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PGobject convertToDatabaseColumn(List<CopyNumberVariant> copyNumberVariantList) {
        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        try {
            objectMapper.registerModule(new JavaTimeModule());
            String jsonString = objectMapper.writeValueAsString(copyNumberVariantList);
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            pgObject.setValue(objectMapper.writeValueAsString(jsonNode));
        } catch (Exception e) {
            throw new RuntimeException("Error converting list to JSON: " + e.getMessage());
        }
        return pgObject;
    }

    @Override
    public List<CopyNumberVariant> convertToEntityAttribute(PGobject pgObject) {
        try {
            objectMapper.registerModule(new JavaTimeModule());
            TypeReference<List<CopyNumberVariant>> typeReference = new TypeReference<List<CopyNumberVariant>>() {
            };
            return objectMapper.readValue(pgObject.getValue(), typeReference);
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to list: " + e.getMessage());
        }
    }

}
