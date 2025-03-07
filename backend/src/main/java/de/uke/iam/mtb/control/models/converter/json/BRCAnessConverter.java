package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;
import de.uke.iam.mtb.control.models.coredata.ngsreport.BRCAness;

public class BRCAnessConverter extends GenericJsonConverter<BRCAness> {

    public BRCAnessConverter() {
        super(new TypeReference<>() {
        });
    }

}
