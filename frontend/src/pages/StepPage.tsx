import { Button, Checkbox, FormControlLabel, Grid, Typography } from "@mui/material"
import PersonIcon from "@mui/icons-material/Person"
import React, { useEffect, useState } from "react"
import { useNavigate, useParams } from "react-router-dom"
import { Patient, Step, StepInfo, StepStatus } from "gen/api"
import { useAuth } from "react-oidc-context"
import { CoreDataFormTabs } from "components/CoreData/CoreDataFormTabs"
import { GeneticDataFormTabs } from "components/CoreData/GeneticDataFormTabs"
import RequirementCard from "components/Requirement/RequirementCard"
import MTBReport from "pages/MTBReport"
import Transfer from "components/Transfer/Transfer"
import { PatientTimeLine } from "components/Patient/PatientTimeLine"
import Session from "hooks/Session"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"

export default function StepPage() {
  const { StepsinfoApi, PatientApi, WorkflowApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const { patientId, stepId } = useParams()
  const navigate = useNavigate()
  const auth = useAuth()
  const episodeId = Session.getEpisodeId()

  const [currentPatient, setCurrentPatient] = useState<Patient>()
  const [allSteps, setAllSteps] = useState<Step[]>()
  const [isStepCompleted, setIsStepCompleted] = useState<{ [stepId: number]: boolean }>({})

  const currentStep = allSteps?.find((step) => step.id.toString() === stepId)
  const lastStep = (allSteps?.length ?? 0) - 1

  // OnMount
  useEffect(() => {
    WorkflowApi.getWorkflows().then(({ data }) => {
      const workflowOne = data.find((workflow) => workflow.id === 1)
      setAllSteps(workflowOne?.steps)
    })
  }, [])

  useEffect(() => {
    if (auth.user?.access_token && patientId) {
      PatientApi.getPatient(patientId!).then(({ data }) => {
        setCurrentPatient(data)
      })
    }
  }, [patientId, auth, isStepCompleted])

  useEffect(() => {
    if (episodeId) {
      StepsinfoApi.getStepsInfo(episodeId).then(({ data }) => {
        const newState = data.reduce<{ [stepId: number]: boolean }>((acc, stepInfo) => {
          return stepInfo.stepId !== undefined // Be careful: without '!== undefined' the Step ID 0 will evaluate to false
            ? { ...acc, [stepInfo.stepId]: stepInfo.stepStatus === StepStatus.Complete }
            : acc
        }, {})
        setIsStepCompleted(newState)
      })
    }
  }, [episodeId, StepsinfoApi])

  const complete = () => {
    return updateStepInfo(StepStatus.Complete)
  }
  const uncomplete = () => {
    return updateStepInfo(StepStatus.Pending)
  }
  const updateStepInfo = (status: StepStatus) => {
    if (!episodeId || !stepId) return Promise.reject()
    const stepInfo: StepInfo = {
      episodeId: episodeId,
      stepId: Number(stepId),
      stepStatus: status,
    }
    return StepsinfoApi.updateStepsInfo(episodeId, [stepInfo])
      .then(() => {
        setIsStepCompleted((prevState) => {
          prevState[Number(stepId)] = stepInfo.stepStatus === StepStatus.Complete
          return prevState
        })
        showSuccessNotification("Die Änderungen wurden gespeichert.")
      })
      .catch((_) =>
        showErrorNotification(
          "Die Änderungen konnten nicht gespeichert werden. Bitte versuchen Sie es später erneut.",
        ),
      )
  }

  const getStepComponent: any = (stepName: string | undefined) => {
    if (!currentPatient) return null
    switch (stepName) {
      case "Klinische Daten":
        return <CoreDataFormTabs />
      case "Anforderung":
        return <RequirementCard />
      case "Genetische Daten":
        return <GeneticDataFormTabs />
      case "MTB-Beschluss und MTB-Report":
        return <MTBReport patient={currentPatient} />
      case "Übermittlung":
        return <Transfer />
      default:
        return <div>Für den Step konnte keine Seite gefunden werden.</div>
    }
  }

  return (
    <div>
      <Grid container>
        <Grid
          item
          style={{ display: "flex", alignItems: "center", justifyContent: "center", width: "100%" }}
        >
          <Button
            style={{ flexDirection: "column", display: "flex" }}
            onClick={() => navigate(`/patients/${currentPatient?.id}`)}
          >
            <PersonIcon />
            <Typography
              variant={"button"}
              maxWidth={"200px"}
              noWrap
              style={{ padding: "0 10px" }}
            >{`${currentPatient?.firstName} ${currentPatient?.surname}`}</Typography>
          </Button>

          <PatientTimeLine
            onStepClick={(step) => {
              navigate(`/patients/${Session.getPatientId()}/step/${step.id}`)
            }}
            patient={currentPatient}
          />
        </Grid>

        <Grid item xs={12} marginTop={"16px"}>
          {getStepComponent(currentStep?.name)}
        </Grid>

        <Grid item xs={12}>
          {currentStep && currentStep.id !== lastStep ? (
            <FormControlLabel
              control={
                <Checkbox
                  key={stepId}
                  checked={isStepCompleted[Number(stepId)]}
                  onChange={() => {
                    ;(!isStepCompleted[Number(stepId)] ? complete() : uncomplete()).then(() => {
                      navigate(`../${(currentStep.id ?? -1) + 1}`, { relative: "path" })
                    })
                  }}
                />
              }
              label={`Ich habe die Bearbeitung von ${currentStep?.name} abgeschlossen.`}
            />
          ) : undefined}
        </Grid>
      </Grid>
    </div>
  )
}
