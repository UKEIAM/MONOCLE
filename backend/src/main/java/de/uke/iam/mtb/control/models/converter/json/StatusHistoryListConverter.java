package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;
import de.uke.iam.mtb.control.models.coredata.StatusHistory;
import java.util.List;

public class StatusHistoryListConverter extends GenericJsonConverter<List<StatusHistory>> {

    public StatusHistoryListConverter() {
        super(new TypeReference<List<StatusHistory>>() {
        });
    }

}
