package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class StringListConverter extends GenericJsonConverter<List<String>> {

    public StringListConverter() {
        super(new TypeReference<List<String>>() {
        });
    }

}
