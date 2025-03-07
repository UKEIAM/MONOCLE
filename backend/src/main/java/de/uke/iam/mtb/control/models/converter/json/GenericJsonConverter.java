package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;

@Converter
public class GenericJsonConverter<T> implements AttributeConverter<T, String> {

    private final ObjectMapper objectMapper;
    private final TypeReference<T> typeReference;

    public GenericJsonConverter(TypeReference<T> typeReference) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.typeReference = typeReference;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        try {
            if (attribute == null) {
                return null;
            }
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) {
                return null;
            }
            return objectMapper.readValue(dbData, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to object: " + e.getMessage(), e);
        }
    }
}