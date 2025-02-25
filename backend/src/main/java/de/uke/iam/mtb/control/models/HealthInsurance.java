package de.uke.iam.mtb.control.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "health_insurance")
public class HealthInsurance {

  @Digits(integer = 9, fraction = 0)
  @Id
  @Column(nullable = false)
  private Long IK;
  private String Datum;
  private String Antragsschluessel;
  private String Anrede;
  private String Namenszeile_1, Namenszeile_2, Namenszeile_3, Namenszeile_4;
  private String H_Strasse;
  private String H_LKZ;
  private String H_PLZ;
  private String H_Ort;
  private String P_LKZ;
  private String P_PLZ;
  private String P_Ort;
  private String Postfach;
  private String TelVorwahl;
  private String TelRufNummer;
  private String FaxVorwahl;
  private String FaxNummer;

}
