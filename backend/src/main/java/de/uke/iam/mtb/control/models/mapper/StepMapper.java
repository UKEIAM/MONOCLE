package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.StepDto;
import de.uke.iam.mtb.api.model.WorkflowDto;
import de.uke.iam.mtb.control.models.Step;
import de.uke.iam.mtb.control.models.Workflow;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StepMapper {

  StepDto toDto(Step step);

  Step toEntity(StepDto stepDto);

  WorkflowDto toDto(Workflow workflow);

  Workflow toEntity(WorkflowDto workflowDto);
}
