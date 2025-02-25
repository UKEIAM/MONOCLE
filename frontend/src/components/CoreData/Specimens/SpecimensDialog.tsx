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
import {
  getEnumKeys,
  specimenLocalizationCodes,
  specimenMethodesCodes,
  specimenTypeCodes,
} from "./SpecimenTypes"
import React, { useEffect, useState } from "react"
import { FormProvider, useForm } from "react-hook-form"
import { InfoRounded } from "@mui/icons-material"
import { useDiagnoses } from "../Diagnoses/hooks/useDiagnoses"
import dayjs from "dayjs"
import {
  Specimen,
  SpecimenCollectionLocalizationEnum,
  SpecimenCollectionMethodEnum,
  SpecimenTypeEnum,
} from "gen/api"
import Session from "hooks/Session"
import { LATEST_ICD_10_VERSION } from "utils/Versions"
import { TextField } from "components/FormFields/TextField"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import FormSelect from "components/FormFields/FormSelect"
import FormDatePicker from "../../FormFields/FormDatePicker"

export type PropType = {
  open: boolean
  onClose: () => void
  editElement?: Specimen
}

type Options = {
  label: string
  value: string
}[]

export function SpecimensDialog({ open, onClose, editElement }: PropType) {
  const { SpecimenApi, DiagnoseApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const methods = useForm<Specimen>()
  const { getDiagnosesToIcd10Codes } = useDiagnoses()
  const [diagnosesOptions, setDiagnosesOptions] = useState<Options>()

  useEffect(() => {
    methods.reset({
      id: editElement?.id,
      episodeId: episodeId,
      labelling: editElement?.labelling,
      icd10: {
        code: editElement?.icd10?.code,
        version: LATEST_ICD_10_VERSION,
        system: "ICD-10-GM",
      },
      type: editElement?.type || undefined,
      collection: {
        date: editElement?.collection?.date || undefined,
        localization: editElement?.collection?.localization || undefined,
        method: editElement?.collection?.method || undefined,
      },
    })

    DiagnoseApi.getAllDiagnoses(episodeId).then((diagnoses) => {
      setDiagnosesOptions(getDiagnosesToIcd10Codes(diagnoses.data))
    })
  }, [open, editElement])

  const checkEmptyFields = (fields: (keyof Specimen)[]) => {
    return fields.every((field) => {
      const value = methods.watch(field)
      return value === "" || value === undefined || value === null
    })
  }

  // @ts-ignore
  let isCollectionEmpty = checkEmptyFields([
    // @ts-ignore
    "collection.date",
    // @ts-ignore
    "collection.method",
    // @ts-ignore
    "collection.localization",
  ])

  const handleSubmit = (formData: Specimen) => {
    const pushChanges = formData.id
      ? SpecimenApi.updateSpecimen(episodeId, formData.id!, formData)
      : SpecimenApi.addSpecimen(episodeId, formData)

    pushChanges
      .then(() => {
        showSuccessNotification(
          `Die Tumorproben wurde erfolgreich ${formData.id ? "geändert" : "gespeichert"}.`,
        )
        onClose()
      })
      .catch(() => {
        showErrorNotification("Beim Speichern der Tumorproben ist ein Fehler aufgetreten.")
      })
  }

  return (
    <Dialog disableEnforceFocus open={open}>
      <FormProvider {...methods}>
        <form onSubmit={methods.handleSubmit(handleSubmit)}>
          <DialogTitle>Tumorproben</DialogTitle>
          <DialogContent>
            <DialogContentText>
              <Grid container spacing={2} sx={{ marginTop: 2 }}>
                <Grid item xs={12}>
                  <TextField name={"labelling"} label={"E-/N-Nummer"}></TextField>
                </Grid>

                <Grid item xs={12}>
                  <FormSelect
                    name={"icd10.code"}
                    label={"ICD-10 Code aus Diagnose"}
                    validationRules={{ required: "Diagnose ist erforderlich." }}
                    options={diagnosesOptions ?? ([{ label: "", value: "" }] as Options)}
                  ></FormSelect>
                </Grid>

                <Grid item xs={12}>
                  <FormSelect
                    name={"type"}
                    label={"Tumorproben-Art"}
                    defaultValue={SpecimenTypeEnum.Ffpe}
                    options={getEnumKeys(SpecimenTypeEnum).map((item) => {
                      return {
                        label: specimenTypeCodes.find((value) => value.value === item)?.label ?? "",
                        value: item,
                      }
                    })}
                  ></FormSelect>
                </Grid>

                <Grid item xs={12}>
                  Entnahme:
                </Grid>
                <Grid item xs={12}>
                  <InfoRounded style={{ verticalAlign: "bottom" }} />
                  <em>
                    Die folgenden Entnahme-Parameter sind entweder{" "}
                    <span style={{ fontWeight: "bold" }}>alle </span>
                    vorhanden oder <span style={{ fontWeight: "bold" }}>keiner</span> ist gefüllt.
                  </em>
                </Grid>

                <Grid item xs={12}>
                  <FormDatePicker
                    name={"collection.date"}
                    label={"Entnahmedatum"}
                    validationRules={{
                      required: {
                        value: !isCollectionEmpty,
                        message: "Entnahmedatum ist erforderlich",
                      },
                    }}
                    maxdate={dayjs()}
                  ></FormDatePicker>
                </Grid>

                <Grid item xs={12}>
                  <FormSelect
                    name={"collection.method"}
                    label={"Entnahmemethode"}
                    validationRules={{
                      required: {
                        value: !isCollectionEmpty,
                        message: "Entnahmemethode ist erforderlich",
                      },
                    }}
                    /* TODO: how about create a method in FormUtils to convert Enums to SelectOptions? */
                    options={getEnumKeys(SpecimenCollectionMethodEnum).map((item) => {
                      return {
                        label:
                          specimenMethodesCodes.find((value) => value.value === item)?.label ?? "",
                        value: item,
                      }
                    })}
                  ></FormSelect>
                </Grid>

                <Grid item xs={12}>
                  <FormSelect
                    name={"collection.localization"}
                    label={"Lokalisation"}
                    validationRules={{
                      required: {
                        value: !isCollectionEmpty, // Conditionally apply required rule
                        message: "Lokalisation ist erforderlich", // Custom error message
                      },
                    }}
                    options={getEnumKeys(SpecimenCollectionLocalizationEnum).map((item) => {
                      return {
                        label:
                          specimenLocalizationCodes.find((value) => value.value === item)?.label ??
                          "",
                        value: item,
                      }
                    })}
                  ></FormSelect>
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
  )
}
