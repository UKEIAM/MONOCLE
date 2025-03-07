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
import { HistologyReevaluationRequest } from "gen/api"
import Session from "hooks/Session"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import FormDatePicker from "components/FormFields/FormDatePicker"
import FormSelect from "components/FormFields/FormSelect"

type Props = {
  open: boolean
  onClose: () => void
  editElement?: HistologyReevaluationRequest
  specimenMap: { [key: string]: string }
}

export default function HistologyReevaluationRequestsDialog({
  open,
  onClose,
  editElement,
  specimenMap,
}: Props) {
  const { HistologyReevaluationRequestApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const methods = useForm<HistologyReevaluationRequest>()
  const episodeId = Session.getEpisodeId()
  const [specimensOptions, setSpecimensOptions] = useState<{ label: string; value: string }[]>([])

  useEffect(() => {
    // Trigger if open === true (Dialog opens)
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

  const onSubmit = (formData: HistologyReevaluationRequest) => {
    const addOrUpdatePromise =
      editElement === undefined || formData.id === undefined
        ? HistologyReevaluationRequestApi.addHistologyReevaluationRequest(episodeId, formData)
        : HistologyReevaluationRequestApi.updateHistologyReevaluationRequest(
            episodeId,
            formData.id!,
            formData,
          )

    addOrUpdatePromise
      .then(() => {
        showSuccessNotification("Der Histologie-Reevaluations-Auftrag wurde erfolgreich geändert")
        onClose()
      })
      .catch(() => {
        showErrorNotification(
          "Beim Speichern des Histologie-Reevaluations-Auftrags ist ein Fehler aufgetreten.",
        )
      })
    onClose()
  }

  return (
    <>
      <Dialog disableEnforceFocus open={open}>
        <FormProvider {...methods}>
          <form onSubmit={methods.handleSubmit(onSubmit)}>
            <DialogTitle>Histologie-Reevaluations-Auftrag</DialogTitle>
            <DialogContent>
              <DialogContentText>
                <Grid container spacing={2} sx={{ marginTop: 2 }}>
                  {/*Tumorproben*/}
                  <Grid item xs={12}>
                    <FormSelect
                      name={"specimen"}
                      label={"Tumorproben"}
                      validationRules={{ required: true }}
                      options={specimensOptions}
                    />
                  </Grid>

                  {/*Erstellungsdatum*/}
                  <Grid item xs={12}>
                    <FormDatePicker
                      name={"issuedOn"}
                      label={"Erstellungsdatum"}
                      maxdate={dayjs()}
                    />
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
