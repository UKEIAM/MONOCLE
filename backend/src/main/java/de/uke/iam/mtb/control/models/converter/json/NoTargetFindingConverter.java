package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;
import de.uke.iam.mtb.control.models.coredata.NoTargetFinding;

public class NoTargetFindingConverter extends GenericJsonConverter<NoTargetFinding> {

    public NoTargetFindingConverter() {
        super(new TypeReference<NoTargetFinding>() {
        });
    }

}
