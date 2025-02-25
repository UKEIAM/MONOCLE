package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;
import de.uke.iam.mtb.control.models.coredata.TumorMorphology;

public class TumorMorphologyConverter extends GenericJsonConverter<TumorMorphology> {

    public TumorMorphologyConverter() {
        super(new TypeReference<TumorMorphology>() {
        });
    }
}