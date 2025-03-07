package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;

import de.uke.iam.mtb.control.models.coredata.EvidenceLevel;

public class EvidenceLevelConverter extends GenericJsonConverter<EvidenceLevel> {

    public EvidenceLevelConverter() {
        super(new TypeReference<EvidenceLevel>() {
        });
    }

}
