package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.api.model.WorkflowDto;
import de.uke.iam.mtb.control.models.Step;
import de.uke.iam.mtb.control.models.Workflow;
import de.uke.iam.mtb.control.models.mapper.WorkflowMapper;
import de.uke.iam.mtb.control.repository.StepRepository;
import de.uke.iam.mtb.control.repository.WorkflowRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

  private final WorkflowRepository workflowRepository;
  private final StepRepository stepRepository;
  private final WorkflowMapper workflowMapper;

  public WorkflowService(WorkflowRepository workflowRepository, StepRepository stepRepository, WorkflowMapper workflowMapper) {
    this.workflowRepository = workflowRepository;
    this.stepRepository = stepRepository;
    this.workflowMapper = workflowMapper;
  }

  public List<WorkflowDto> getAllWorkflows() {
    return workflowRepository.findAllWorkflowsWithFilteredAndSortedSteps().stream().map(workflowMapper::toDto).toList();
  }

  public List<Step> extractStepsFromWorkflow(Workflow workflow) {
    List<Step> allSteps = new ArrayList<>();
    workflow.getSteps().forEach(step -> {
      // add primary steps
      allSteps.add(step);
      if (step.getSteps() != null) {
        // Set parent step
        step.getSteps().stream().forEach(substep -> {
          substep.setParentStep(step);
          allSteps.add(substep);
        });
      }
    });
    return allSteps;
  }

  public void addWorkflow(WorkflowDto workflowDto) {
    Workflow workflow = workflowMapper.toEntity(workflowDto);
    List<Step> allSteps = extractStepsFromWorkflow(workflow);
    stepRepository.saveAll(allSteps);
    workflow.setSteps(allSteps);
    workflowRepository.save(workflow);
  }
}
