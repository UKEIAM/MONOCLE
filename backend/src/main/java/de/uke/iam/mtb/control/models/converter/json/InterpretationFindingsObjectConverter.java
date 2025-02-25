package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;
import de.uke.iam.mtb.control.models.coredata.ngsreport.InterpretationFindingsObject;

public class InterpretationFindingsObjectConverter extends GenericJsonConverter<InterpretationFindingsObject> {

    public InterpretationFindingsObjectConverter() {
        super(new TypeReference<>() {
        });
    }
}