package de.uke.iam.mtb.control.models.enums.coredata;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CopyNumberVariantType {
    @JsonProperty("low-level-gain") LOW_LEVEL_GAIN,
    @JsonProperty("high-level-gain") HIGH_LEVEL_GAIN,
    @JsonProperty("loss") LOSS

}
