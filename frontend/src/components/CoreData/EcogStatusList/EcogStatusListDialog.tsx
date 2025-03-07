import { FormProvider, useForm } from "react-hook-form"
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  Typography,
} from "@mui/material"
import dayjs from "dayjs"
import React, { useEffect } from "react"
import FormDatePicker from "components/FormFields/FormDatePicker"
import FormSelect from "components/FormFields/FormSelect"
import { EcogStatus } from "gen/api"
import Session from "hooks/Session"
import { ecogCodes } from "./EcogStatusListTypes"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

type EcogProps = {
  open: boolean
  onClose: () => void
  editElement?: EcogStatus
}

export function EcogStatusListDialog({ open, onClose, editElement }: EcogProps) {
  const { EcogStatusApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()

  const methods = useForm<EcogStatus>()
  useEffect(() => {
    methods.reset({
      episodeId: episodeId,
      ...editElement,
      value: {
        system: "ECOG-Performance-Status",
        ...editElement?.value,
      },
    })
  }, [open])

  const handleSubmit = (formData: EcogStatus) => {
    // delete all unnecessary elements if values are empty, so bwhc has no error message
    if (formData.value?.code === "") delete formData.value.code

    const addOrUpdatePromise =
      editElement === undefined || formData.id === undefined
        ? EcogStatusApi.addEcogStatus(episodeId, formData)
        : EcogStatusApi.updateEcogStatus(episodeId, formData.id!, formData)

    addOrUpdatePromise
      .then(() => {
        showSuccessNotification("Der ECOG Performance Status Befund wurde erfolgreich gespeichert")
        onClose()
      })
      .catch(() => {
        showErrorNotification(
          "Beim Speichern des ECOG Performance Status Befund ist ein Fehler aufgetreten.",
        )
      })
  }

  return (
    <>
      <Dialog open={open}>
        <FormProvider {...methods}>
          <form onSubmit={methods.handleSubmit(handleSubmit)}>
            <DialogTitle>ECOG-Performance-Status-Befund</DialogTitle>
            <DialogContent>
              <Grid container spacing={2} sx={{ marginTop: 2 }}>
                <Grid item xs={12}>
                  <FormDatePicker
                    name={"effectiveDate"}
                    label={"Zeitpunkt"}
                    maxdate={dayjs()}
                    validationRules={{ required: true }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormSelect
                    name={"value.code"}
                    label={"ECOG-Performance-Status"}
                    options={ecogCodes}
                    validationRules={{ required: true }}
                  />
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
    </>
  )
}
