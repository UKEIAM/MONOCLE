package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;
import de.uke.iam.mtb.control.models.coredata.SpecimenCollection;

public class SpecimenCollectionConverter extends GenericJsonConverter<SpecimenCollection> {

    public SpecimenCollectionConverter() {
        super(new TypeReference<SpecimenCollection>() {
        });

    }
}
