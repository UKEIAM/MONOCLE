package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;

import de.uke.iam.mtb.control.models.coredata.EvidenceLevel;

public class LevelOfEvidenceConverter extends GenericJsonConverter<EvidenceLevel> {

    public LevelOfEvidenceConverter() {
        super(new TypeReference<EvidenceLevel>() {
        });
    }

}
