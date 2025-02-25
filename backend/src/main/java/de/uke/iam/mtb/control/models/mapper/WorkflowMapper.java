package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.StepDto;
import de.uke.iam.mtb.api.model.WorkflowDto;
import de.uke.iam.mtb.control.models.Step;
import de.uke.iam.mtb.control.models.Workflow;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring", uses = StepMapper.class)
public interface WorkflowMapper {

  @Mappings({
      @Mapping(target = "steps", source = "steps")
  })
  WorkflowDto toDto(Workflow workflow);

  @Mappings({
      @Mapping(target = "steps", source = "steps")
  })
  Workflow toEntity(WorkflowDto workflowDto);

  List<StepDto> stepsToStepDto(List<Step> steps);

  List<Step> stepsDtoToSteps(List<StepDto> stepDtos);

}
