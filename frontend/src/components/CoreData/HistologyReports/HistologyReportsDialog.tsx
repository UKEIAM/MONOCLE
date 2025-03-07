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
import React, { useEffect, useState } from "react"
import { FormProvider, useForm } from "react-hook-form"
import dayjs from "dayjs"
import FormSelect from "components/FormFields/FormSelect"
import FormDatePicker from "components/FormFields/FormDatePicker"
import FormTextField from "components/FormFields/FormTextField"
import { HistologyReport, TumorCellContentMethodEnum } from "gen/api"
import Session from "hooks/Session"
import { TUMOR_MORPHOLOGY_VALID_VERSIONS } from "utils/Versions"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import { differentiationDegreeList } from "./HistologyReportsTypes"
import FormCheckbox from "../../FormFields/FormCheckbox"
import { NumberField } from "../../FormFields/NumberField"
import FormNumberField from "../../FormFields/FormNumberField"

export type PropType = {
  open: boolean
  onClose: () => void
  editElement?: HistologyReport
  specimenMap: { [key: string]: string }
}

export function HistologyReportsDialog({ open, onClose, editElement, specimenMap }: PropType) {
  const { HistologyReportApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const methods = useForm<HistologyReport>()

  const [specimensOptions, setSpecimensOptions] = useState<{ label: string; value: string }[]>([])
  const [differentiationDegreeOptions, setDifferentiationDegreeOptions] = useState<
    { label: string; value: string }[]
  >([])

  useEffect(() => {
    methods.reset({
      id: editElement?.id,
      episodeId: episodeId,
      specimen: editElement?.specimen ?? "",
      issuedOn: editElement?.issuedOn ?? undefined,
      tumorMorphology: {
        id: editElement?.tumorMorphology?.id,
        episodeId: episodeId,
        specimen: editElement?.specimen ?? "",
        value: {
          code: editElement?.tumorMorphology?.value?.code,
          display: editElement?.tumorMorphology?.value?.display,
          version: editElement?.tumorMorphology?.value?.version,
          system: editElement?.tumorMorphology?.value?.system ?? "ICD-O-3-M",
        },
        note: editElement?.tumorMorphology?.note ?? "",
      },
      tumorCellContent: {
        id: editElement?.tumorCellContent?.id,
        specimen: editElement?.specimen ?? "",
        method: editElement?.tumorCellContent?.method,
        value: editElement?.tumorCellContent?.value,
      },
      differentiationDegree: editElement?.differentiationDegree ?? undefined,
    })
    setDifferentiationDegreeOptions(differentiationDegreeList)
  }, [open])

  // Cross dependency: Code and version must always be set together. It is invalid if one of them is empty.
  const watchTumorMorphologyCode = methods.watch(
    "tumorMorphology.value.code",
    editElement?.tumorMorphology?.value?.code,
  )
  const watchTumorMorphologyVersion = methods.watch(
    "tumorMorphology.value.version",
    editElement?.tumorMorphology?.value?.version,
  )

  useEffect(() => {
    // Trigger if specimenMap was changed from parent
    setSpecimensOptions(
      Object.entries(specimenMap).map(([uuid, readableName]) => ({
        label: readableName,
        value: uuid,
      })),
    )
  }, [specimenMap])

  const handleSubmit = (formData: HistologyReport) => {
    let isValid = true

    if (!formData.specimen) {
      isValid = false
    }

    // tumor cell content
    if (methods.watch("tumorCellContent.method") && formData.tumorCellContent) {
      formData.tumorCellContent.method = TumorCellContentMethodEnum.Histologic
      formData.tumorCellContent.specimen = formData.specimen
    } else {
      delete formData.tumorCellContent
    }

    formData.tumorMorphology!.specimen = formData.specimen ?? ""

    // tumor morphology
    const tumorMorphologyVersion = formData.tumorMorphology?.value?.version
    const tumorMorphologyCode = formData.tumorMorphology?.value?.code

    // delete tumorMorphology if neither code nor version is given
    if (!tumorMorphologyCode && !tumorMorphologyVersion) {
      delete formData.tumorMorphology
    }

    // tumor morphology requires code and version together
    if (
      (tumorMorphologyVersion && !tumorMorphologyCode) ||
      (!tumorMorphologyVersion && tumorMorphologyCode)
    ) {
      isValid = false
      const errorMessage = "Tumor-Morphologie Code und Version müssen gegeben werden."
      methods.setError("tumorMorphology.value.version", { message: errorMessage })
      methods.setError("tumorMorphology.value.code", { message: errorMessage })
    }

    // tumor morphology and/or tumor cell content are required
    if (!formData.tumorMorphology && !formData.tumorCellContent) {
      isValid = false
      const errorMessage =
        "Der Histologie-Bericht enthält keine Ergebnis-Befunde! Es muss mindestens " +
        "Tumor-Morphologie-Befund (ICD-O-3-M) und/oder Tumorzellgehalt angegeben werde."
      methods.setError("tumorMorphology.value.version", { message: errorMessage })
      methods.setError("tumorMorphology.value.code", { message: errorMessage })
      methods.setError("tumorCellContent.value", { message: errorMessage })
    }

    // tumor morphology version validation
    if (
      tumorMorphologyVersion &&
      !TUMOR_MORPHOLOGY_VALID_VERSIONS.includes(tumorMorphologyVersion)
    ) {
      isValid = false
      methods.setError("tumorMorphology.value.version", {
        message: `Die Version muss eine der Folgenden sein: ${TUMOR_MORPHOLOGY_VALID_VERSIONS.join(", ")}.`,
      })
    }

    if (isValid) {
      const addOrUpdatePromise =
        editElement === undefined || formData.id === undefined
          ? HistologyReportApi.addHistologyReport(episodeId, formData)
          : HistologyReportApi.updateHistologyReport(episodeId, formData.id, formData)

      addOrUpdatePromise
        .then(() => {
          showSuccessNotification("Der Histology Report wurde erfolgreich gespeichert")
          onClose()
        })
        .catch((err: { data: any }) => {
          showErrorNotification("Beim Speichern des Histologie-Berichts ist ein Fehler aufgetreten")
        })
    } else {
      return
    }
  }

  return (
    <Dialog
      disableEnforceFocus
      open={open}
      onClose={(event, reason) => {
        if (reason && reason == "backdropClick") return
      }}
    >
      <FormProvider {...methods}>
        <form onSubmit={methods.handleSubmit(handleSubmit)}>
          <DialogTitle>Histologie-Bericht</DialogTitle>
          <DialogContent>
            <DialogContentText>
              <Grid container spacing={2} sx={{ marginTop: 2 }}>
                <Grid item xs={12}>
                  <FormSelect
                    validationRules={{ required: true }}
                    name={"specimen"}
                    label={"Tumorproben"}
                    options={specimensOptions}
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormDatePicker name={"issuedOn"} label={"Erstellungsdatum"} maxdate={dayjs()} />
                </Grid>
                <Grid item xs={12}>
                  <FormTextField
                    name={"tumorMorphology.value.code"}
                    label={"Tumor-Morphologie Code (ICD-O-3-M)"}
                    validationRules={{
                      required: !!watchTumorMorphologyVersion || !!watchTumorMorphologyCode,
                    }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormTextField
                    name={"tumorMorphology.value.version"}
                    label={"Tumor-Morphologie Version"}
                    validationRules={{
                      required: !!watchTumorMorphologyCode || !!watchTumorMorphologyVersion,
                    }}
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormTextField
                    name={"tumorMorphology.note"}
                    label={"Tumor-Morphologie Anmerkung"}
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormCheckbox
                    name={"tumorCellContent.method"}
                    label={"Tumorzellgehalt Methode: histologisch"}
                  />
                </Grid>
                {methods.watch("tumorCellContent.method") && (
                  <Grid item xs={12}>
                    <FormNumberField
                      name={"tumorCellContent.value"}
                      label={"Tumorzellgehalt Wert"}
                      validationRules={{ required: true, min: 0, max: 1 }}
                    />
                    <NumberField
                      name={"tumorCellContent.value"}
                      label={"Tumorzellgehalt Wert"}
                      min={0}
                      max={1}
                      step={0.01}
                    />
                  </Grid>
                )}
                <Grid item xs={12}>
                  <FormSelect
                    name={"differentiationDegree"}
                    label={"Differenzierungsgrad"}
                    options={differentiationDegreeOptions}
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
            <Button variant="contained">abbrechen</Button>
          </DialogActions>
        </form>
      </FormProvider>
    </Dialog>
  )
}
