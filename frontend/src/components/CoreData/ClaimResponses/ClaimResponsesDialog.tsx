import React, { useEffect, useState } from "react"
import { FormProvider, useForm } from "react-hook-form"
import Session from "hooks/Session"
import { ClaimResponse } from "gen/api"
import { reasonOptions, statusOptions } from "./ClaimResponsesTypes"
import dayjs from "dayjs"
import FormSelect from "components/FormFields/FormSelect"
import FormDatePicker from "components/FormFields/FormDatePicker"
import { OptionType } from "components/FormFields/types/FormTypes"
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  Typography,
} from "@mui/material"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"

type Props = {
  open: boolean
  onClose: () => void
  editElement?: ClaimResponse
  claimsMap: Map<string, string>
}

export default function ClaimResponsesDialog({ open, onClose, editElement, claimsMap }: Props) {
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const { ClaimResponseApi } = useApi()
  const episodeId = Session.getEpisodeId()

  const methods = useForm<ClaimResponse>()
  useEffect(() => {
    if (!open) return
    methods.reset({
      episodeId,
      ...editElement,
    })
  }, [editElement, episodeId, methods, open])

  const [claimOptions, setClaimOptions] = useState<OptionType[]>([])
  useEffect(() => {
    const newClaimOptions: OptionType[] = []
    claimsMap.forEach((value, key) => {
      newClaimOptions.push({ label: value, value: key })
    })
    setClaimOptions(newClaimOptions)
  }, [claimsMap])

  const onSubmit = (formData: ClaimResponse) => {
    let addOrUpdatePromise =
      editElement === undefined
        ? ClaimResponseApi.addClaimResponse(episodeId, formData) // add care plan response
        : ClaimResponseApi.updateClaimResponse(episodeId, formData.id!, formData) // update care plan response

    addOrUpdatePromise
      .then(() => {
        showSuccessNotification("Die Kostenübernahme Antwort wurde erfolgreich gespeichert")
        onClose()
      })
      .catch(() => {
        showErrorNotification(
          "Beim Speichern der Kostenübernahme Antwort ist ein Fehler aufgetreten.",
        )
      })
  }

  return (
    <Dialog disableEnforceFocus open={open}>
      <FormProvider {...methods}>
        <form onSubmit={methods.handleSubmit(onSubmit)}>
          <DialogTitle>Kostenübernahme Antwort</DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ marginTop: 2 }}>
              <Grid item xs={12}>
                <FormSelect
                  name={"claim"}
                  label={"Antrag"}
                  validationRules={{ required: true }}
                  options={claimOptions}
                />
              </Grid>
              <Grid item xs={12}>
                <FormDatePicker
                  name={"issuedOn"}
                  label={"Antwortdatum"}
                  validationRules={{ required: true }}
                  maxdate={dayjs()}
                />
              </Grid>
              <Grid item xs={12}>
                <FormSelect
                  name={"status"}
                  label={"Status"}
                  validationRules={{ required: true }}
                  options={statusOptions}
                />
              </Grid>
              <Grid item xs={12}>
                <FormSelect
                  name={"reason"}
                  label={"Grund"}
                  validationRules={{ required: "Grund ist erforderlich" }}
                  options={reasonOptions}
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
  )
}
