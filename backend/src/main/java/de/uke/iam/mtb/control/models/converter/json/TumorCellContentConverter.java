package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;
import de.uke.iam.mtb.control.models.coredata.TumorCellContent;

public class TumorCellContentConverter extends GenericJsonConverter<TumorCellContent> {

    public TumorCellContentConverter() {
        super(new TypeReference<TumorCellContent>() {
        });
    }
}