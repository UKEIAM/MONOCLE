package de.uke.iam.mtb.control.models.converter.json;

import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import de.uke.iam.mtb.control.models.coredata.ProteinExpression;

public class ProteinExpressionListConverter extends GenericJsonConverter<List<ProteinExpression>> {
    public ProteinExpressionListConverter() {
        super(new TypeReference<List<ProteinExpression>>() {});
    }
}
