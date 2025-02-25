package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;
import de.uke.iam.mtb.control.models.coredata.Study;
import java.util.List;

public class StudyListConverter extends GenericJsonConverter<List<Study>> {

    public StudyListConverter() {
        super(new TypeReference<List<Study>>() {
        });
    }

}
