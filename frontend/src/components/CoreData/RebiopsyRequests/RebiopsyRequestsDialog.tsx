import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Grid,
  Typography,
} from "@mui/material"
import dayjs from "dayjs"
import React, { useEffect, useState } from "react"
import { FormProvider, useForm } from "react-hook-form"
import DatePicker from "components/FormFields/DatePicker"
import Select from "components/FormFields/Select"
import { RebiopsyRequest } from "gen/api"
import Session from "hooks/Session"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"

type Props = {
  open: boolean
  onClose: () => void
  editElement?: RebiopsyRequest
  specimenMap: { [key: string]: string }
}

export default function RebiopsyRequestsDialog({ open, onClose, editElement, specimenMap }: Props) {
  const { RebiopsyRequestApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const methods = useForm<RebiopsyRequest>()
  const episodeId = Session.getEpisodeId()
  const [specimensOptions, setSpecimensOptions] = useState<{ label: string; value: string }[]>([])

  useEffect(() => {
    if (open) {
      methods.reset({
        id: editElement?.id,
        episodeId: episodeId,
        issuedOn: editElement?.issuedOn ?? undefined,
        specimen: editElement?.specimen ?? "",
      })
    }
  }, [open])

  useEffect(() => {
    // Trigger if specimenMap was changed from parent
    setSpecimensOptions(
      Object.entries(specimenMap).map(([uuid, readableName]) => ({
        label: readableName,
        value: uuid,
      })),
    )
  }, [specimenMap])

  const onSubmit = (formData: RebiopsyRequest) => {
    if (formData.issuedOn === "Invalid Date") {
      methods.setError("issuedOn", { message: "Das Datum ist nicht valide!" })
    } else if (dayjs(formData.issuedOn).isAfter(dayjs(new Date()))) {
      methods.setError("issuedOn", { message: "Das Datum liegt in der Zukunft" })
    } else {
      if (editElement && formData.id) {
        // update
        RebiopsyRequestApi.updateRebiopsyRequest(episodeId, formData.id, formData)
          .then(() => {
            showSuccessNotification("Der Rebiopsie-Auftrag wurde erfolgreich geändert.")
            onClose()
          })
          .catch(() => {
            showErrorNotification(
              "Beim Speichern des Rebiopsie-Auftrags ist ein Fehler aufgetreten.",
            )
          })
      } else {
        // add
        RebiopsyRequestApi.addRebiopsyRequest(episodeId, formData)
          .then(() => {
            showSuccessNotification("Der Rebiopsie-Auftrag wurde erfolgreich gespeichert")
            onClose()
          })
          .catch(() => {
            showErrorNotification(
              "Beim Speichern des Rebiopsie-Auftrags ist ein Fehler aufgetreten.",
            )
          })
      }
    }
  }

  return (
    <>
      <Dialog disableEnforceFocus open={open}>
        <FormProvider {...methods}>
          <form onSubmit={methods.handleSubmit(onSubmit)}>
            <DialogTitle>Rebiopsie-Auftrag</DialogTitle>
            <DialogContent>
              <DialogContentText>
                <Grid container spacing={2} sx={{ marginTop: 2 }}>
                  {/*Tumorproben*/}
                  <Grid item xs={12}>
                    <Select
                      name={"specimen"}
                      label={"Tumorproben"}
                      isRequired={true}
                      options={specimensOptions}
                    />
                  </Grid>

                  {/*Erstellungsdatum*/}
                  <Grid item xs={12}>
                    <DatePicker
                      sxStyle={{ marginTop: 2 }}
                      name={"issuedOn"}
                      label={"Erstellungsdatum"}
                      maxdate={dayjs()}
                    />
                    <span style={{ color: "red" }}>
                      {methods.formState.errors.issuedOn?.message}
                    </span>
                  </Grid>
                  <Grid item xs={12}>
                    <Typography>* Pflichtfelder</Typography>
                  </Grid>
                </Grid>
              </DialogContentText>
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
