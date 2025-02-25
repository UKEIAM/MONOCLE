import React, { useState } from "react"
import dayjs from "dayjs"
import utc from "dayjs/plugin/utc"
import { Button, Card, Grid } from "@mui/material"
import { Patient } from "gen/api"
import { useNavigate } from "react-router-dom"
import SendIcon from "@mui/icons-material/Send"
import { FormProvider, useForm } from "react-hook-form"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import { PatientForm } from "../../components/Patient/PatientForm"

dayjs.extend(utc)

type Props = {
  navigateTo: string
}

export function NewPatient({ navigateTo }: Props) {
  const { PatientApi, EpisodeApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const workflowId = 1
  const [warningModal, setWarningModal] = useState<boolean>(false)
  const navigate = useNavigate()
  const methods = useForm<Patient>({
    defaultValues: {
      soarianId: "",
      workflowId: workflowId,
      createdAt: undefined,
      updatedAt: undefined,
      deletedAt: undefined,
      firstName: "",
      surname: "",
      gender: undefined,
      dateOfBirth: undefined,
      dateOfDeath: undefined,
      municipalityKey: undefined,
      consent: false,
      healthInsurance: undefined,
    },
  })

  const handleSubmit = (patientData: Patient) => {
    if (patientData.consent) {
      // validate birthdate
      let isValid = true
      if (patientData.dateOfBirth === "Invalid Date" || patientData.dateOfBirth === undefined) {
        isValid = false
        methods.setError("dateOfBirth", { message: "Das Geburtsdatum ist nicht valide!" })
      } else if (dayjs(patientData.dateOfBirth).isAfter(dayjs(new Date()))) {
        isValid = false
        methods.setError("dateOfBirth", { message: "Das Geburtsdatum liegt in der Zukunft" })
      }
      if (!isValid) return

      patientData.workflowId = workflowId

      PatientApi.addPatient(patientData)
        .then(() => {
          navigate(navigateTo)
        })
        .catch((error) => {
          if (error.response && error.response.status === 409) {
            setWarningModal(true)
          } else {
            showErrorNotification("Der/Die Patient:in konnte nicht angelegt werden.")
          }
        })
    } else {
      showErrorNotification("Bitte kreuzen Sie das Einwilligungskästchen an")
    }
  }

  const handleNewEpisode = () => {
    const errorMessage =
      "Es konnte keine neue Behandlungsepisode für diese/n Patient:in angelegt werden. Bitte versuchen Sie es später erneut."

    PatientApi.getPatients() // this is overhead but the patient ID is not known (only soarian)
      .then(({ data: patients }) => {
        const patientSoarian = methods.getValues().soarianId
        const patient = patients.find((patientItem) => patientItem.soarianId === patientSoarian)
        const patientId = patient?.id

        if (patientId) {
          EpisodeApi.addEpisode({ patientId: patientId, workflowId: workflowId })
            .then(() => {
              showSuccessNotification(
                "Es wurde erfolgreich eine neue Behandlungsepisode für diese/n Patient:in angelegt.",
              )
              navigate(navigateTo)
            })
            .catch(() => showErrorNotification(errorMessage))

          setWarningModal(false)
        } else {
          showErrorNotification(errorMessage)
        }
      })
      .catch(() => showErrorNotification(errorMessage))
  }

  return (
    <FormProvider {...methods}>
      <Card>
        <form onSubmit={methods.handleSubmit(handleSubmit)}>
          <PatientForm
            errors={methods.formState.errors}
            onNewEpisode={handleNewEpisode}
            showWarningModal={warningModal}
            onWarningModalClose={() => setWarningModal(false)}
          />

          <Grid item xs={12} padding={"0 2rem 2rem 2rem"}>
            <Button fullWidth variant="contained" type="submit" endIcon={<SendIcon />}>
              Anlegen
            </Button>
          </Grid>
        </form>
      </Card>
    </FormProvider>
  )
}
