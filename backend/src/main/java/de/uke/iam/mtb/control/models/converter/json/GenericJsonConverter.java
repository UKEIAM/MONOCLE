package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;

/**
 * A generic JSON converter that implements JPA's AttributeConverter interface. This class can convert between Java objects/collections and
 * their JSON string representations.
 *
 * @param <T> The type of object to convert (can be a single object or List of objects)
 */
@Converter
public class GenericJsonConverter<T> implements AttributeConverter<T, String> {

    private final ObjectMapper objectMapper;
    private final TypeReference<T> typeReference;

    /**
     * Constructor that initializes the converter with the TypeReference.
     *
     * @param typeReference The TypeReference for handling generic types
     */
    public GenericJsonConverter(TypeReference<T> typeReference) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.typeReference = typeReference;
    }

    /**
     * Converts a Java object to its JSON string representation for database storage.
     *
     * @param attribute The object to convert
     * @return String JSON representation of the object
     * @throws RuntimeException if JSON conversion fails
     */
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

    /**
     * Converts a JSON string from the database back to its Java object representation.
     *
     * @param dbData The JSON string from the database
     * @return T The converted Java object
     * @throws RuntimeException if JSON parsing fails
     */
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