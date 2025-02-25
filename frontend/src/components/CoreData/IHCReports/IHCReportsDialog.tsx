import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  Grid,
  Typography,
} from "@mui/material"
import FormSelect from "components/FormFields/FormSelect"
import FormNumberField from "components/FormFields/FormNumberField"
import FormDatePicker from "components/FormFields/FormDatePicker"
import FormTextField from "components/FormFields/FormTextField"
import dayjs from "dayjs"
import { IhcReport } from "gen/api"
import Session from "hooks/Session"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import { useEffect, useState } from "react"
import { FormProvider, useForm, UseFormReturn } from "react-hook-form"
import { ProteinExpressions } from "./ProteinExpressions"
import { icScoreOptions, proteinOptions, valueOptions } from "./IHCReportsTypes"
import Autocomplete from "components/FormFields/AutocompleteFreeSolo"

type Props = {
  open: boolean
  onClose: () => void
  specimenLabelsById: Map<string, string>
  editElement?: IhcReport
}

const IHCReportsDialog = ({ open, onClose, specimenLabelsById, editElement }: Props) => {
  const episodeId = Session.getEpisodeId()
  const methods = useForm<IhcReport>()
  useEffect(() => {
    if (!open) return
    methods.reset({
      episodeId,
      ...editElement,
      // TODO fill in code.system? here or onSubmit
    })
  }, [open])
  const { IhcReportApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()

  const [specimenOptions, setSpecimenOptions] = useState<{ label: string; value: string }[]>([])
  useEffect(() => {
    const newSpecimenOptions: { label: string; value: string }[] = []
    specimenLabelsById.forEach((specimenLabel, specimenId) => {
      newSpecimenOptions.push({
        label: specimenLabel,
        value: specimenId,
      })
    })
    setSpecimenOptions(newSpecimenOptions)
  }, [specimenLabelsById])

  const handleSubmit = (formData: IhcReport) => {
    let isValid = true
    if (formData.date === "Invalid Date") {
      isValid = false
      methods.setError("date", { message: "Das Datum ist nicht valide!" })
    } else if (dayjs(formData.date).isAfter(dayjs(new Date()))) {
      isValid = false
      methods.setError("date", { message: "Das Datum liegt in der Zukunft" })
    }

    if (!isValid) return

    const pushChanges =
      formData.id !== undefined
        ? IhcReportApi.updateIhcReport(episodeId, formData.id!, formData)
        : IhcReportApi.addIhcReport(episodeId, formData)

    pushChanges
      .then(() => {
        showSuccessNotification(
          `DeR IHC-Bericht wurde erfolgreich ${formData.id !== undefined ? "geändert" : "gespeichert"}.`,
        )
        onClose()
      })
      .catch(() =>
        showErrorNotification("Beim Speichern des IHC-Berichts ist ein Fehler aufgetreten."),
      )
  }

  return (
    <Dialog disableEnforceFocus open={open}>
      <FormProvider {...methods}>
        <form onSubmit={methods.handleSubmit(handleSubmit)}>
          <DialogTitle>IHC-Bericht</DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ marginTop: 2 }}>
              <Grid item xs={12}>
                <FormSelect
                  name={"specimenId"}
                  label={"Tumorproben"}
                  validationRules={{ required: true }}
                  options={specimenOptions}
                />
              </Grid>
              <Grid item xs={12}>
                <FormDatePicker
                  name={"date"}
                  label={"Datum"}
                  maxdate={dayjs()}
                  validationRules={{ required: true }}
                />
              </Grid>
              <Grid item xs={12}>
                <FormTextField name={"journalId"} label={"Eingangs-/Journal-Nr."} />
              </Grid>
              <Grid item xs={12}>
                <FormTextField name={"blockId"} label={"Block-/Material-Nr."} />
              </Grid>
              <Grid item xs={12}>
                {/* <span style={{ color: "red" }}>{errorMessageProteinExpressionResults}</span> */}
                <ProteinExpressions name={"proteinExpressionResults"} label={"Protein-Befunde"} />
              </Grid>
              <Grid item xs={12}>
                <Typography style={{ fontWeight: "bold" }}>MSI/MMR-Ergebnisse</Typography>
              </Grid>
              <MsiMmrResultForm name="msimmrResults.0" label="MLH1" methods={methods} />
              <MsiMmrResultForm name="msimmrResults.1" label="MSH2" methods={methods} />
              <MsiMmrResultForm name="msimmrResults.2" label="MSH6" methods={methods} />
              <MsiMmrResultForm name="msimmrResults.3" label="PMS2" methods={methods} />
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

type MsiMmrResultFormProps = {
  name: "msimmrResults.0" | "msimmrResults.1" | "msimmrResults.2" | "msimmrResults.3"
  label: string
  methods: UseFormReturn<IhcReport, any, undefined>
}

const MsiMmrResultForm = ({ name, label, methods }: MsiMmrResultFormProps) => {
  return (
    <>
      <Grid item xs={12}>
        <Divider />
      </Grid>
      <Grid item xs={12}>
        <Typography>{label}</Typography>
      </Grid>
      <Grid item xs={12}>
        <Autocomplete
          control={methods.control}
          label="Protein"
          name={`${name}.protein.code`}
          options={proteinOptions.map((elem) => elem.label)}
          loading={false}
          isTextInputPossible={true}
        />
      </Grid>
      <Grid item xs={12}>
        <FormSelect name={`${name}.value.code`} label={"Ergebnis"} options={valueOptions} />
      </Grid>
      <Grid item xs={12}>
        <FormNumberField
          name={`${name}.tpsScore`}
          label={"TPS-Score (in %)"}
          validationRules={{ min: 0, max: 100 }}
        />
      </Grid>
      <Grid item xs={12}>
        <FormNumberField
          name={`${name}.cpsScore`}
          label={"CPS-Score"}
          validationRules={{ min: 0, max: 100 }}
        />
      </Grid>
      <Grid item xs={12}>
        <FormSelect name={`${name}.icScore.code`} label={"Ergebnis"} options={icScoreOptions} />
      </Grid>
    </>
  )
}

export { IHCReportsDialog }
