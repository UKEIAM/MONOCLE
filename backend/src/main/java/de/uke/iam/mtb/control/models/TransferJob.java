package de.uke.iam.mtb.control.models;

import de.uke.iam.mtb.control.models.enums.JobStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
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
@Table(name = "bwhctransfer")
public class TransferJob {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private UUID patientId;
    private UUID episodeId;
    private JobStatus status;

}
