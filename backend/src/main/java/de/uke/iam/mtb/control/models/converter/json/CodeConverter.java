package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;
import de.uke.iam.mtb.control.models.coredata.Code;

public class CodeConverter extends GenericJsonConverter<Code> {

    public CodeConverter() {
        super(new TypeReference<Code>() {
        });
    }

}
