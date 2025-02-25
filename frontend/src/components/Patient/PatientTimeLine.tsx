import DoneIcon from "@mui/icons-material/Done"
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline"
import { Grid, Step, StepLabel, Stepper, Tooltip, Typography } from "@mui/material"
import { Patient, Step as ApiStep, StepInfo } from "gen/api"
import React, { useEffect, useState } from "react"
import { useParams } from "react-router-dom"
import { useApi } from "hooks/useApi"

type Props = {
  patient?: Patient
  onStepClick: (step: ApiStep) => void
}

export function PatientTimeLine({ patient, onStepClick }: Props) {
  const { WorkflowApi } = useApi()
  const { stepId } = useParams<{ stepId: string }>()
  const [stepInfos, setStepInfos] = useState<StepInfo[]>()
  const [allSteps, setAllSteps] = useState<ApiStep[]>()
  const [isActive, setIsActive] = useState(-1)
  useEffect(() => {
    setIsActive(Number(stepId ?? -1))
  }, [stepId])

  useEffect(() => {
    WorkflowApi.getWorkflows().then(({ data }) => {
      const workflowOne = data.find((workflow) => workflow.id === 1)
      setAllSteps(workflowOne?.steps)
    })
  }, [])

  useEffect(() => {
    setStepInfos(patient?.episodes?.at(0)?.stepInfo)
  }, [patient])

  const getStatusIcon = (status?: string) => {
    switch (status) {
      case "COMPLETE":
        return () => (
          <Tooltip title={"Bearbeitung abgeschlossen"}>
            <DoneIcon />
          </Tooltip>
        )
      case "INCOMPLETE":
      default: // "PENDING"
        return () => (
          <Tooltip title={"Bearbeitung notwendig"}>
            <ErrorOutlineIcon />
          </Tooltip>
        )
    }
  }

  return (
    <Grid container style={{ display: "flex", justifyContent: "center" }}>
      <Grid item xs={12}>
        <Stepper alternativeLabel activeStep={isActive}>
          {allSteps?.map((step, index) => {
            const status = stepInfos?.find((stepInfo) => stepInfo.stepId === step.id)?.stepStatus
            return (
              <Step key={index} completed style={{ cursor: "pointer" }} disabled={false}>
                <StepLabel
                  onClick={() => onStepClick(step)}
                  StepIconComponent={getStatusIcon(status)}
                >
                  <Typography fontSize={14} fontWeight={index === isActive ? "bold" : undefined}>
                    {step.name}
                  </Typography>
                </StepLabel>
              </Step>
            )
          })}
        </Stepper>
      </Grid>
    </Grid>
  )
}
