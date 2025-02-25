package de.uke.iam.mtb.control.models.converter.json;

import com.fasterxml.jackson.core.type.TypeReference;
import de.uke.iam.mtb.control.models.coredata.ngsreport.MicroSatelliteInstabilities;

public class MicroSatelliteInstabilitiesConverter extends GenericJsonConverter<MicroSatelliteInstabilities> {

    public MicroSatelliteInstabilitiesConverter() {
        super(new TypeReference<MicroSatelliteInstabilities>() {
        });
    }
}