import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  Typography,
} from "@mui/material"
import React, { useEffect, useState } from "react"
import DatePicker from "components/FormFields/DatePicker"
import dayjs from "dayjs"
import Select from "components/FormFields/Select"
import { FormProvider, useForm } from "react-hook-form"
import { methodOptions, recistOptions } from "./TherapyResponsesTypes"
import { MolecularTherapyResponse } from "gen/api"
import Session from "hooks/Session"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import Autocomplete from "components/FormFields/AutocompleteFreeSolo"

type TherapyResponsesDialogProps = {
  open: boolean
  onClose: () => void
  editElement?: MolecularTherapyResponse
  molecularTherapiesMap: { [key: string]: string }
}

export default function TherapyResponsesDialog({
  open,
  onClose,
  editElement,
  molecularTherapiesMap,
}: TherapyResponsesDialogProps) {
  const { MolecularTherapyResponseApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const methods = useForm<MolecularTherapyResponse>()
  const episodeId = Session.getEpisodeId()
  const [molecularTherapiesOptions, setMolecularTherapiesOptions] = useState<
    {
      label: string
      value: string
    }[]
  >([])

  useEffect(() => {
    methods.reset({
      episodeId,
      ...editElement,
      value: {
        system: "RECIST",
        ...editElement?.value,
      },
      method: {
        // show label to user convert it back to code on submit
        code: methodOptions.find((elem) => elem.value == editElement?.method?.code)?.label,
      },
    })
  }, [open])

  useEffect(() => {
    // Trigger if molecularTherapiesMap was changed from parent
    setMolecularTherapiesOptions(
      Object.entries(molecularTherapiesMap).map(([uuid, readableName]) => ({
        label: readableName,
        value: uuid,
      })),
    )
  }, [molecularTherapiesMap])

  const handleSubmit = (formData: MolecularTherapyResponse) => {
    // update method.code if it matches an option
    const codeFromOption = methodOptions.find((elem) => elem.label == formData.method?.code)?.value
    if (codeFromOption !== undefined) {
      formData.method!.code = codeFromOption
    }
    let isValid = true

    // Validate effectiveDate
    if (formData.effectiveDate === "Invalid Date") {
      methods.setError("effectiveDate", { message: "Das Datum ist nicht valide!" })
      isValid = false
    } else if (dayjs(formData.effectiveDate).isAfter(dayjs())) {
      methods.setError("effectiveDate", { message: "Das Datum liegt in der Zukunft" })
      isValid = false
    }

    if (isValid) {
      if (editElement === undefined || formData.id === undefined) {
        // Add new
        MolecularTherapyResponseApi.addMolecularTherapyResponse(episodeId, formData)
          .then(() => {
            showSuccessNotification("Der Therapie Befund wurde erfolgreich gespeichert")
            onClose()
          })
          .catch((err) => {
            console.log(err)
            showErrorNotification("Beim Speichern des Therapie Befunds ist ein Fehler aufgetreten.")
          })
      } else {
        // Update
        MolecularTherapyResponseApi.updateMolecularTherapyResponse(episodeId, formData.id, formData)
          .then(() => {
            showSuccessNotification("Der Therapie Befund wurde erfolgreich geändert")
            onClose()
          })
          .catch((err) => {
            console.log(err)
            showErrorNotification("Beim Speichern des Therapie Befunds ist ein Fehler aufgetreten.")
          })
      }
    }
  }

  return (
    <FormProvider {...methods}>
      <Dialog open={open}>
        <form onSubmit={methods.handleSubmit(handleSubmit)}>
          <DialogTitle>Molekular-Therapie-Befund</DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ marginTop: 2 }}>
              <Grid item xs={12}>
                <Select
                  label={"Systemische Therapie"}
                  name={`therapy`}
                  isRequired={true}
                  options={molecularTherapiesOptions}
                />
              </Grid>
              <Grid item xs={12}>
                <DatePicker
                  label={"Zeitpunkt"}
                  name={`effectiveDate`}
                  isRequired={true}
                  maxdate={dayjs()}
                />
                <span style={{ color: "red" }}>
                  {methods.formState.errors.effectiveDate?.message}
                </span>
              </Grid>
              <Grid item xs={12}>
                <Select
                  label={"Wert"}
                  name={`value.code`}
                  isRequired={true}
                  options={recistOptions}
                />
              </Grid>
              <Grid item xs={12}>
                <Autocomplete
                  label="Methode"
                  name="method.code"
                  options={methodOptions.map((elem) => elem.label)}
                  loading={false}
                  isTextInputPossible
                  control={methods.control}
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
      </Dialog>
    </FormProvider>
  )
}
