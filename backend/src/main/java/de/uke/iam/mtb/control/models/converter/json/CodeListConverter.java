package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;
import de.uke.iam.mtb.control.models.coredata.Code;
import java.util.List;

public class CodeListConverter extends GenericJsonConverter<List<Code>> {

    public CodeListConverter() {
        super(new TypeReference<List<Code>>() {
        });
    }

}