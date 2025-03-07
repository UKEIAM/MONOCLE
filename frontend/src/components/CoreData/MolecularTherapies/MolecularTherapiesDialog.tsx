import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  Typography,
} from "@mui/material"
import DatePicker from "components/FormFields/DatePicker"
import Select from "components/FormFields/Select"
import { TextField } from "components/FormFields/TextField"
import { FormProvider, useForm } from "react-hook-form"
import {
  dosageOptions,
  notDoneReasonOptions,
  realisationOptions,
  reasonStoppedOptions,
  statusOptions,
} from "./MolecularTherapiesTypes"
import dayjs from "dayjs"
import React, { useEffect, useState } from "react"
import { MedicationComponent } from "components/FormFields/MedicationComponent"
import { MolecularTherapy } from "gen/api"
import Session from "hooks/Session"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

type PropType = {
  open: boolean
  onClose: () => void
  editElement?: MolecularTherapy
  recommendationMap: { [key: string]: string }
}

export default function MolecularTherapiesDialog({
  open,
  onClose,
  editElement,
  recommendationMap,
}: PropType) {
  const episodeId = Session.getEpisodeId()
  const { MolecularTherapyApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const methods = useForm<MolecularTherapy>()
  const [startDate, setStartDate] = useState(dayjs(new Date("01.01.1970")))
  const [endDate, setEndDate] = useState(dayjs(new Date("01.01.1970")))

  const [recommendationOptions, setRecommendationOptions] = useState<
    { label: string; value: string }[]
  >([])
  const molecularTherapyStatus = methods.watch("status")

  useEffect(() => {
    methods.reset({
      id: editElement?.id,
      episodeId: episodeId,
      medication: editElement?.medication ?? [],
      // FIXME? How does BWHC reacto to code null but system set?
      reasonStopped: {
        code: editElement?.reasonStopped?.code ?? undefined,
        system: "MTB-CDS:MolecularTherapy:StopReason",
      },
      ...editElement,
    })
  }, [open])

  useEffect(() => {
    // Trigger if specimenMap was changed from parent
    setRecommendationOptions(
      Object.entries(recommendationMap).map(([uuid, readableName]) => ({
        label: readableName,
        value: uuid,
      })),
    )
  }, [recommendationMap])

  const onSubmit = (formData: MolecularTherapy) => {
    if (formData.dosage === "") delete formData.dosage

    let start = formData.period?.start
    let end = formData.period?.end

    let isValid = true
    if (start === "") {
      isValid = false
      methods.setError("period.start", { message: "Das Datum darf nicht leer sein." })
    } else if (start === "Invalid Date") {
      isValid = false
      methods.setError("period.start", { message: "Das Datum ist nicht valide!" })
    } else if (end === "Invalid Date") {
      isValid = false
      methods.setError("period.end", { message: "Das Datum ist nicht valide!" })
    } else if (dayjs(start).isAfter(dayjs(end))) {
      isValid = false
      methods.setError("period.start", { message: "Das Startdatum liegt nach den Enddatum." })
    } else if (dayjs(start).isAfter(dayjs(new Date()))) {
      isValid = false
      methods.setError("period.start", { message: "Das Datum liegt in der Zukunft" })
    } else if (dayjs(end).isAfter(dayjs(new Date()))) {
      isValid = false
      methods.setError("period.end", { message: "Das Datum liegt in der Zukunft" })
    }

    if (formData.status !== "not-done" && formData.medication?.length! <= 0) {
      isValid = false
      methods.setError("medication.0.code", {
        message: "Es muss mindestens ein Wirkstoff vorhanden sein.",
      })
    }

    if (isValid) {
      if (editElement === undefined || formData.id === undefined) {
        // Add new
        MolecularTherapyApi.addMolecularTherapy(episodeId, formData)
          .then(() => {
            showSuccessNotification("Die Systemische Therapie wurden erfolgreich gespeichert")
            onClose()
          })
          .catch((err) => {
            console.log(err)
            showErrorNotification(
              "Beim Speichern der Systemischen Therapie ist ein Fehler aufgetreten.",
            )
          })
      } else {
        // Update
        MolecularTherapyApi.updateMolecularTherapy(episodeId, formData.id, formData)
          .then(() => {
            showSuccessNotification("Die Systemische Therapie wurden erfolgreich geändert")
            onClose()
          })
          .catch((err) => {
            console.log(err)
            showErrorNotification(
              "Beim Speichern der Systemischen Therapie ist ein Fehler aufgetreten.",
            )
          })
      }
      handleClose()
    }
  }

  const handleClose = () => {
    setStartDate(dayjs(new Date("01.01.1970")))
    setEndDate(dayjs(new Date("01.01.1970")))
    onClose()
  }

  return (
    <Dialog open={open}>
      <FormProvider {...methods}>
        <form onSubmit={methods.handleSubmit(onSubmit)}>
          <DialogTitle>Systemische Therapie</DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ marginTop: 2 }}>
              <Grid item xs={12}>
                <DatePicker
                  name={`recordedOn`}
                  label={"Erfassungsdatum"}
                  isRequired={true}
                  maxdate={dayjs()}
                />
              </Grid>
              <Grid item xs={12}>
                <Select
                  name={`basedOn`}
                  label={"Therapieempfehlung"}
                  isRequired={true}
                  options={recommendationOptions}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField name={`note`} label={"Bemerkung"} multiline={true} />
              </Grid>
              <Grid item xs={12}>
                <Select
                  name={`status`}
                  label={"Status"}
                  isRequired={true}
                  options={statusOptions}
                />
              </Grid>
              <Grid item xs={12}>
                <Select
                  name={`notDoneReason.code`}
                  label={"Grund für Nicht-Umsetzung"}
                  options={notDoneReasonOptions}
                />
              </Grid>
              <Grid item xs={12}>
                {/*<DatePicker name={`period.start`} label={"Anfang"} isRequired={methods.getValues("status") !== "not-done"}*/}
                <DatePicker
                  name={`period.start`}
                  label={"Anfang"}
                  isRequired={molecularTherapyStatus !== "not-done"}
                  maxdate={
                    dayjs(endDate).isSame(dayjs(new Date("01.01.1970")))
                      ? dayjs(new Date())
                      : endDate
                  }
                  addHandleOnChange={(date) => {
                    if (date) setStartDate(date)
                  }}
                />
                <span style={{ color: "red" }}>
                  {methods.formState.errors?.period?.start?.message}
                </span>
              </Grid>
              <Grid item xs={12}>
                <DatePicker
                  name={`period.end`}
                  label={"Ende"}
                  mindate={startDate}
                  maxdate={dayjs(new Date())}
                  addHandleOnChange={(date) => {
                    if (date) setEndDate(date)
                  }}
                />
                <span style={{ color: "red" }}>
                  {methods.formState.errors?.period?.end?.message}
                </span>
              </Grid>
              <Grid item xs={12}>
                <MedicationComponent
                  fieldName={"medication"}
                  isRequired={molecularTherapyStatus !== "not-done"}
                ></MedicationComponent>
                <span style={{ color: "red" }}>
                  {methods.formState.errors?.medication?.[0]?.code?.message}
                </span>
              </Grid>
              <Grid item xs={12}>
                <Select name={`dosage`} label={"Dosisdichte"} options={dosageOptions} />
              </Grid>
              <Grid item xs={12}>
                <Select
                  name={`reasonStopped.code`}
                  label={"Grund für Therapieende"}
                  options={reasonStoppedOptions}
                />
              </Grid>
              <Grid item xs={12}>
                <Select
                  name={`realisation`}
                  label={"Umsetzung der Therapieempfehlung"}
                  options={realisationOptions}
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
            <Button variant="contained" onClick={handleClose}>
              abbrechen
            </Button>
          </DialogActions>
        </form>
      </FormProvider>
    </Dialog>
  )
}
