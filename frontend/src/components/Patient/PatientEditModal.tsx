import { Patient } from "gen/api"
import React from "react"
import { Button, Dialog, DialogActions, DialogContentText, DialogTitle } from "@mui/material"
import dayjs from "dayjs"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import { PatientForm } from "./PatientForm"
import { FormProvider, useForm } from "react-hook-form"

type propsType = {
  open: boolean
  patient: Patient
  onClose: () => void
}

export function PatientEditModal({ open, patient, onClose }: propsType) {
  const { PatientApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const methods = useForm<Patient>({
    defaultValues: patient,
  })

  const handleSubmit = (patientEdit: Patient) => {
    // validate birthdate
    let isValid = true
    if (patientEdit.dateOfBirth === "Invalid Date" || patientEdit.dateOfBirth === undefined) {
      isValid = false
      methods.setError("dateOfBirth", { message: "Das Geburtsdatum ist nicht valide!" })
    } else if (dayjs(patientEdit.dateOfBirth).isAfter(dayjs(new Date()))) {
      isValid = false
      methods.setError("dateOfBirth", { message: "Das Geburtsdatum liegt in der Zukunft" })
    }
    // Date of death
    if (dayjs(patientEdit.dateOfBirth).isAfter(patientEdit.dateOfDeath)) {
      isValid = false
      methods.setError("dateOfDeath", { message: "Das Todesdatum liegt vor dem Geburtsdatum" })
    }

    // This is not seen because the validation is already on the field
    if (patientEdit.municipalityKey?.length != 5) {
      isValid = false
      methods.setError("municipalityKey", {
        message: "Gemeindeschlüssel muss die richtige Länge haben",
      })
    }

    if (!isValid) return

    if (patientEdit && patientEdit.id && patientEdit.soarianId) {
      PatientApi.updatePatient(patientEdit.id, patientEdit)
        .then(() => {
          showSuccessNotification("Der/die Patient:in wurde erfolgreich aktualisiert.")
          onClose()
        })
        .catch((reason) => {
          if (reason.response.status === 409) {
            showErrorNotification(
              "Ein anderer Patient oder eine andere Patientin mit der PatID existiert bereits.",
            )
          } else {
            showErrorNotification(
              "Der Patient oder die Patientin konnte nicht aktualisiert werden.",
            )
          }
        })
    } else {
      showErrorNotification(
        "Die PatID ist leer. Der Patient oder die Patientin konnte nicht aktualisiert werden.",
      )
    }
  }

  return (
    <FormProvider {...methods}>
      <Dialog disableEnforceFocus open={open}>
        <form onSubmit={methods.handleSubmit(handleSubmit)}>
          <DialogTitle>Personendaten bearbeiten</DialogTitle>
          <DialogContentText>
            <PatientForm
              patientInsuranceId={methods.getValues("healthInsurance")}
              isEditMode={true}
              errors={methods.formState.errors}
            />
          </DialogContentText>
          <DialogActions>
            <Button variant={"contained"} type="submit">
              Änderungen speichern
            </Button>
            <Button variant={"contained"} onClick={onClose}>
              Abbrechen
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </FormProvider>
  )
}
