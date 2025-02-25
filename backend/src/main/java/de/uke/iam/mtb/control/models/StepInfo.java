package de.uke.iam.mtb.control.models;

import de.uke.iam.mtb.control.models.enums.StepStatus;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "step_info")
public class StepInfo {

  @EmbeddedId
  private StepInfoId id;

  @ManyToOne
  @JoinColumn(name = "step_id", referencedColumnName = "id")
  @MapsId("step_id")
  private Step step;
  @ManyToOne
  @JoinColumn(name = "episode_id", referencedColumnName = "id")
  @MapsId("episode_id")
  private Episode episode;

  private StepStatus stepStatus;
}
