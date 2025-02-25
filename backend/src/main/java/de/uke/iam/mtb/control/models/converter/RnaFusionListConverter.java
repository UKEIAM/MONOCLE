package de.uke.iam.mtb.control.models.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.uke.iam.mtb.control.models.coredata.ngsreport.RnaFusion;
import jakarta.persistence.AttributeConverter;
import java.io.IOException;
import java.util.List;
import org.postgresql.util.PGobject;

public class RnaFusionListConverter implements AttributeConverter<List<RnaFusion>, PGobject> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PGobject convertToDatabaseColumn(List<RnaFusion> rnaFusionList) {
        PGobject pgObject = new PGobject();
        try {
            objectMapper.registerModule(new JavaTimeModule());
            String jsonString = objectMapper.writeValueAsString(rnaFusionList);
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            pgObject.setValue(objectMapper.writeValueAsString(jsonNode));
        } catch (Exception e) {
            throw new RuntimeException("Error converting list to JSON: " + e.getMessage());
        }
        return pgObject;
    }

    @Override
    public List<RnaFusion> convertToEntityAttribute(PGobject pgObject) {
        try {
            objectMapper.registerModule(new JavaTimeModule());
            TypeReference<List<RnaFusion>> typeReference = new TypeReference<List<RnaFusion>>() {
            };
            return objectMapper.readValue(pgObject.getValue(), typeReference);
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to list: " + e.getMessage());
        }
    }

}
