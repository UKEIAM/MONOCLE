package de.uke.iam.mtb.control.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "step")
public class Step {

  @Id
  private Integer id;
  private String name;
  private Boolean skippable;
  @ManyToOne
  @JoinColumn(name = "parent_step_id", referencedColumnName = "id")
  private Step parentStep;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentStep")
  private List<Step> steps;
}
