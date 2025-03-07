package de.uke.iam.mtb.control.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class StepInfoId implements Serializable {

  @Column(name = "step_id")
  private Integer stepId;

  @Column(name = "episode_id")
  private UUID episodeId;

}
