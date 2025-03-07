import { FormProvider, useForm } from "react-hook-form"
import { FamilyMemberDiagnosis } from "gen/api"
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  Typography,
} from "@mui/material"
import FormSelect from "components/FormFields/FormSelect"
import FormTextField from "components/FormFields/FormTextField"
import React, { useEffect } from "react"
import Session from "hooks/Session"
import { relationshipCodes } from "./FamilyMemberDiagnosesTypes"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

type Props = {
  open: boolean
  onClose: () => void
  editElement?: FamilyMemberDiagnosis
}

export function FamilyMemberDiagnosesDialog({ open, onClose, editElement }: Props) {
  const { FamilyMemberDiagnosisApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const methods = useForm<FamilyMemberDiagnosis>()
  useEffect(() => {
    methods.reset({
      episodeId,
      ...editElement,
      relationship: {
        system: "http://terminology.hl7.org/ValueSet/v3-FamilyMember",
        ...editElement?.relationship,
      },
    })
  }, [open]) // eslint-disable-line react-hooks/exhaustive-deps -- FIXME potential misuse of useEffect to initialize?

  const episodeId = Session.getEpisodeId()

  // handlers
  const handleSubmit = (formData: FamilyMemberDiagnosis) => {
    // delete all unnecessary elements if values are empty, so bwhc has no error message
    if (formData.relationship?.code === "") delete formData.relationship.code
    if (formData.details === "") delete formData.details

    // no validation required

    // push changes

    const addOrUpdatePromise =
      editElement === undefined || formData.id === undefined
        ? FamilyMemberDiagnosisApi.addFamilyMemberDiagnosis(episodeId, formData)
        : FamilyMemberDiagnosisApi.updateFamilyMemberDiagnosis(episodeId, formData.id!, formData)

    addOrUpdatePromise
      .then(() => {
        showSuccessNotification("Die Familienanamnese wurde erfolgreich gespeichert")
        onClose()
      })
      .catch(() => {
        showErrorNotification("Beim Speichern der Familienanamnese ist ein Fehler aufgetreten.")
      })
  }

  return (
    <Dialog disableEnforceFocus open={open}>
      {" "}
      {/* FIXME disableEnforceFocus docu sais: Generally this should never be set to true as it makes the modal less accessible to assistive technologies, like screen readers. */}
      <FormProvider {...methods}>
        <form onSubmit={methods.handleSubmit(handleSubmit)}>
          <DialogTitle>Familienanamnese</DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ marginTop: 2 }}>
              <Grid item xs={12}>
                <FormSelect
                  label={"Verwandschaftsgrad"}
                  name={"relationship.code"}
                  validationRules={{ required: true }}
                  options={relationshipCodes}
                />
              </Grid>
              <Grid item xs={12}>
                <FormTextField name={"details"} label={"Details"} />
              </Grid>
              <Grid item xs={12}>
                <Typography>* Pflichtfelder</Typography>
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions>
            <Button variant="contained" type={"submit"}>
              {editElement !== undefined ? "aktualisieren" : "hinzufügen"}
            </Button>
            <Button variant="contained" onClick={onClose}>
              abbrechen
            </Button>
          </DialogActions>
        </form>
      </FormProvider>
    </Dialog>
  )
}
