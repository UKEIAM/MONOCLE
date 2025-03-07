import { Claim } from "gen/api"
import { FormProvider, useForm } from "react-hook-form"
import Session from "hooks/Session"
import React, { useEffect, useState } from "react"
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
import FormDatePicker from "components/FormFields/FormDatePicker"
import FormCheckbox from "../../FormFields/FormCheckbox"
import dayjs from "dayjs"
import { stage } from "./ClaimsTypes"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"

type Props = {
  open: boolean
  onClose: () => void
  editElement?: Claim
  therapyRecommendationsMap: Map<string, string>
}

export function ClaimsDialog({ open, onClose, editElement, therapyRecommendationsMap }: Props) {
  const { showErrorNotification, showSuccessNotification } = useNotification()
  const { ClaimApi } = useApi()
  const episodeId = Session.getEpisodeId()

  const methods = useForm<Claim>()
  useEffect(() => {
    if (!open) return
    methods.reset({
      episodeId,
      stage: {
        code: editElement?.stage?.code,
        display: editElement?.stage?.display,
        system: "dnpm-dip/mtb/claim/stage",
      },
      ...editElement,
    })
  }, [editElement, episodeId, methods, open])

  const [therapyRecommendationOptions, setTherapyRecommendationOptions] = useState<
    { label: string; value: string }[]
  >([])
  useEffect(() => {
    const newTherapyRecommendationOptions: { label: string; value: string }[] = []
    therapyRecommendationsMap.forEach((value, key) => {
      newTherapyRecommendationOptions.push({ label: value, value: key })
    })
    setTherapyRecommendationOptions(newTherapyRecommendationOptions)
  }, [therapyRecommendationsMap])

  const onSubmit = (formData: Claim) => {
    if (formData.stage?.code) {
      formData.stage.display = stage.find((s) => s.value === formData?.stage?.code)?.label
    }

    const addOrUpdatePromise = formData.id
      ? ClaimApi.updateClaim(episodeId, formData.id!, formData)
      : ClaimApi.addClaim(episodeId, formData)

    addOrUpdatePromise
      .then(() => {
        showSuccessNotification(
          `Der Kostenübernahme Antrag wurde erfolgreich ${formData.id ? "geändert" : "gespeichert"}.`,
        )
        onClose()
      })
      .catch(() => {
        showErrorNotification(
          "Beim Speichern des Kostenübernahme Antrags ist ein Fehler aufgetreten.",
        )
      })
  }

  return (
    <Dialog disableEnforceFocus open={open}>
      <FormProvider {...methods}>
        <form onSubmit={methods.handleSubmit(onSubmit)}>
          <DialogTitle>Kostenübernahme Antrag</DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ marginTop: 2 }}>
              <Grid item xs={12}>
                <FormSelect
                  name={"therapy"}
                  label={"Therapie-Empfehlung"}
                  validationRules={{ required: "Therapie-Empfehlung ist erforderlich" }}
                  options={therapyRecommendationOptions}
                />
              </Grid>
              <Grid item xs={12}>
                <FormDatePicker
                  name={"issuedOn"}
                  label={"Antragsdatum"}
                  validationRules={{ required: "Antragsdatum ist erforderlich" }}
                  maxdate={dayjs()}
                />
                <span style={{ color: "red" }}>{methods.formState.errors.issuedOn?.message}</span>
              </Grid>
              <Grid item xs={12}>
                <FormSelect
                  name={"stage.code"}
                  label={"Antragsstadium"}
                  validationRules={{ required: "Antragsstadium ist erforderlich" }}
                  options={stage}
                />
              </Grid>
              <Grid item xs={12}>
                <FormCheckbox
                  name={"isClaimViaZpmOffice"}
                  label={"Antragstellung über ZPM-Geschäftsstelle"}
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
