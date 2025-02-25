package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;
import de.uke.iam.mtb.control.models.coredata.ValueSet;

public class ValueSetConverter extends GenericJsonConverter<ValueSet> {

    public ValueSetConverter() {
        super(new TypeReference<ValueSet>() {
        });
    }

}
