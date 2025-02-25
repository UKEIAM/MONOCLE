package de.uke.iam.mtb.control.repository;

import de.uke.iam.mtb.control.models.StepInfo;
import de.uke.iam.mtb.control.models.StepInfoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepInfoRepository extends JpaRepository<StepInfo, StepInfoId> {

}
