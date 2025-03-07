import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  Typography,
} from "@mui/material"
import { useEffect } from "react"
import { FormProvider, useForm } from "react-hook-form"
import {
  guidelineTreatmentStatusGermanTranslation,
  whoGradeCodes2016,
  whoGradeCodes2021,
} from "./DiagnosesTypes"
import { Autocomplete } from "components/FormFields/Autocomplete"
import FormDatePicker from "components/FormFields/FormDatePicker"
import FormTextField from "components/FormFields/FormTextField"
import FormSelect from "components/FormFields/FormSelect"
import FormCheckbox from "components/FormFields/FormCheckbox"
import { Diagnose, DiagnoseGuidelineTreatmentStatusEnum } from "gen/api"
import Session from "hooks/Session"
import { StatusHistoryComponent } from "./StatusHistoryComponent"
import { EditableTable, FieldType } from "components/FormFields/EditableTable"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import { LATEST_WHO_GRADE_VERSION } from "utils/Versions"
import { OptionType } from "components/FormFields/types/FormTypes"
import dayjs from "dayjs"

const hpoFieldDefinition = [
  {
    fieldType: "input",
    required: false,
    fieldElement: "code",
  },
  {
    fieldType: "input",
    required: false,
    fieldElement: "version",
  },
] as FieldType[]

const altKeyFieldDefinition = [
  {
    fieldType: "input",
    required: false,
    fieldElement: "code",
  },
  {
    fieldType: "input",
    required: false,
    fieldElement: "system",
  },
  {
    fieldType: "input",
    required: false,
    fieldElement: "version",
  },
] as FieldType[]

const TnmKeySystem: OptionType[] = [
  { label: "AJCC", value: "AJCC" },
  { label: "UICC", value: "UICC" },
  { label: "Unbekannt", value: "unknown" },
]

const icd10CodeRegex: RegExp = /^[A-Z][0-9][0-9AB]\.?[0-9A-Z]{0,4}$/

export type PropType = {
  open: boolean
  onClose: () => void
  editElement?: Diagnose
  histologyReportMap: { [key: string]: string }
}

export function DiagnosesDialog({ open, onClose, editElement, histologyReportMap }: PropType) {
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const methods = useForm<Diagnose>()
  const episodeId = Session.getEpisodeId()
  const { DiagnoseApi } = useApi()

  useEffect(() => {
    methods.reset({
      ...editElement,
      episodeId: episodeId,
      histologyResults: editElement?.histologyResults ?? [],
      icd10: {
        ...editElement?.icd10,
        system: "ICD-10-GM",
      },
      icdO3T: {
        ...editElement?.icdO3T,
        system: "ICD-O-3-T",
      },
      whoGrade: {
        code: editElement?.whoGrade?.code || "",
        system: "WHO-Grading-CNS-Tumors",
        version: whoGradeCodes2016.map((a) => a.value).includes(editElement?.whoGrade?.code ?? "")
          ? "2016"
          : LATEST_WHO_GRADE_VERSION,
        display: "",
      },
      germlineDiagnosisIcd10: {
        ...editElement?.germlineDiagnosisIcd10,
        system: "ICD-10-GM",
      },
      isGermlineDiagnosisExist: editElement?.isGermlineDiagnosisExist,
      tnmKey: {
        ...editElement?.tnmKey,
        code: editElement?.tnmKey?.code === "unknown" ? "Unbekannt" : editElement?.tnmKey?.code,
      },
    })
  }, [editElement, episodeId, methods, open])

  const handleSubmit = (formData: Diagnose) => {
    // delete all unnecessary elements if values are empty, so bwhc has no error message
    if (formData.whoGrade?.code === "") delete formData.whoGrade
    if (formData.recordedOn === "") delete formData.recordedOn
    if (formData.guidelineTreatmentStatus === undefined) delete formData.guidelineTreatmentStatus

    // if the checkbox is unchecked
    if (!formData.isGermlineDiagnosisExist && formData.germlineDiagnosisIcd10) {
      formData.germlineDiagnosisIcd10.code = ""
      formData.germlineDiagnosisIcd10.version = ""
    }

    if (formData.tnmKey?.code === "Unbekannt") {
      formData.tnmKey.code = "unknown"
    }

    const addOrUpdatePromise =
      editElement === undefined || formData.id === undefined
        ? DiagnoseApi.addDiagnose(episodeId, formData)
        : DiagnoseApi.updateDiagnose(episodeId, formData.id, formData)

    addOrUpdatePromise
      .then(() => {
        showSuccessNotification("Die Diagnose wurde erfolgreich gespeichert")
        onClose()
      })
      .catch(() => {
        showErrorNotification("Beim Speichern der Diagnose ist ein Fehler aufgetreten.")
      })
  }

  const handleWhoGradeOptions = () => {
    if (editElement?.whoGrade?.code === undefined) {
      // if it is a new who object then always use the 2021 version
      return whoGradeCodes2021
    } else {
      // if it is an "old" object which gets modified, then check its version
      return whoGradeCodes2016.map((a) => a.value).includes(editElement?.whoGrade?.code)
        ? whoGradeCodes2016
        : whoGradeCodes2021
    }
  }

  return (
    <Dialog
      disableEnforceFocus
      open={open}
      onClose={(event, reason) => {
        if (reason && reason === "backdropClick") return
      }}
    >
      <FormProvider {...methods}>
        <form onSubmit={methods.handleSubmit(handleSubmit)}>
          <DialogTitle>Diagnose</DialogTitle>
          <DialogContent style={{ paddingTop: "1em" }}>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <FormDatePicker
                  label={"Erstdiagnosedatum"}
                  name={"recordedOn"}
                  validationRules={{ required: true }}
                  maxdate={dayjs(new Date())}
                ></FormDatePicker>
              </Grid>
              <Grid item xs={6}>
                <FormTextField
                  name={"icd10.code"}
                  label={"ICD-10"}
                  validationRules={{
                    required: true,
                    pattern: {
                      value: icd10CodeRegex,
                      message: "Bitte geben Sie einen validen ICD-10 Code ein.",
                    },
                  }}
                ></FormTextField>
              </Grid>
              <Grid item xs={6}>
                <FormTextField
                  name={"icd10.version"}
                  label={"Version"}
                  validationRules={{
                    required: true,
                    pattern: {
                      value: /2019|2020|2021|2022|2023|2024|2025/,
                      message: "Valide Versionen sind: 2019, 2020, 2021, 2022, 2023, 2024, 2025",
                    },
                  }}
                ></FormTextField>
              </Grid>
              <Grid item xs={6}>
                <FormTextField
                  name={"icdO3T.code"}
                  label={"ICD-O-3-T"}
                  validationRules={{
                    required: true,
                    pattern: {
                      value: icd10CodeRegex,
                      message: "Bitte geben Sie einen validen ICD-O-3-T Code ein.",
                    },
                  }}
                ></FormTextField>
              </Grid>
              <Grid item xs={6}>
                <FormTextField
                  name={"icdO3T.version"}
                  label={"Version"}
                  validationRules={{
                    required: true,
                    pattern: {
                      value: /(Erste Revision|Zweite Revision|2014|2019)/gi,
                      message: "Valide Versionen sind: Erste Revision, 2014, Zweite Revision, 2019",
                    },
                  }}
                ></FormTextField>
              </Grid>
              <Grid item xs={12}>
                <FormCheckbox
                  name={"isGermlineDiagnosisExist"}
                  label={"Keimbahndiagnose ist vorhanden"}
                />
              </Grid>
              {methods.watch("isGermlineDiagnosisExist") && (
                <Grid container item xs={12} spacing={2}>
                  <Grid item xs={6}>
                    <FormTextField
                      name={"germlineDiagnosisIcd10.code"}
                      label={"Keimbahndiagnosen (ICD-10)"}
                      validationRules={{
                        required: true,
                        pattern: {
                          value: icd10CodeRegex,
                          message: "Bitte geben Sie einen validen ICD-10 Code ein.",
                        },
                      }}
                    ></FormTextField>
                  </Grid>
                  <Grid item xs={6}>
                    <FormTextField
                      name={"germlineDiagnosisIcd10.version"}
                      label={"Version"}
                      validationRules={{
                        required: true,
                      }}
                    ></FormTextField>
                  </Grid>
                </Grid>
              )}
              <Grid item xs={12}>
                <FormSelect
                  name={"whoGrade.code"}
                  label={"WHO-Grad ZNS, Version " + LATEST_WHO_GRADE_VERSION}
                  options={handleWhoGradeOptions()}
                ></FormSelect>
              </Grid>
              <Grid container item xs={12} spacing={2}>
                <Grid item xs={12}>
                  <Typography style={{ fontWeight: "bold" }}>
                    TNM-Schlüssel (falls vorhanden)
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <FormTextField
                    name={"tnmKey.code"}
                    label={'TNM-Schlüssel oder "Unbekannt"'}
                    validationRules={{
                      required: true,
                      pattern: {
                        value: /(T[0-4]N[0-3]M[01])|Unbekannt/,
                        message: "Entspricht keinem validen TNM-Schlüssel",
                      },
                    }}
                  ></FormTextField>
                </Grid>

                <Grid item xs={4}>
                  <FormSelect
                    name={"tnmKey.system"}
                    label={"System"}
                    options={TnmKeySystem}
                    validationRules={{ required: true }}
                  ></FormSelect>
                </Grid>
                <Grid item xs={2}>
                  <FormTextField name={"tnmKey.version"} label={"Version"}></FormTextField>
                </Grid>
              </Grid>
              <Grid container item xs={12} spacing={2}>
                <EditableTable
                  fieldName={"altTumorKey"}
                  rowTypes={altKeyFieldDefinition}
                  headerLabel={[
                    "Schlüssel",
                    "Schlüsselbezeichner/Klassifikations-System (z.B. FIGO)",
                    "Version",
                  ]}
                  headerText={
                    "Diagnosespezifischen Klassifikation für Tumorformen, falls TNM keine Anwendung findet"
                  }
                  buttonText={"Schlüssel Hinzufügen"}
                />
              </Grid>
              <Grid item xs={12}>
                <Autocomplete
                  label={"Histologie-Berichte (Mehrfachauswahl)"}
                  name={"histologyResults"}
                  multiple={true}
                  options={Object.entries(histologyReportMap).map(([uuid, readableName]) => ({
                    label: readableName,
                    value: uuid,
                  }))}
                  loading={false}
                  getOptionLabel={(option: any) => option.label}
                  getOptionId={(option: any) => option.value}
                  getOptionValue={(option: any) => option.value}
                  defaultValue={editElement?.histologyResults?.map((item: string) => {
                    return { label: histologyReportMap[item], value: item }
                  })}
                />
              </Grid>
              <StatusHistoryComponent fieldName={"statusHistory"} />
              <Grid item xs={12}>
                <FormSelect
                  validationRules={{
                    warning: {
                      value: !methods.watch("guidelineTreatmentStatus"),
                      message: "Tragen Sie den Leitlinienbehandlung-Status ein, falls möglich",
                    },
                  }}
                  name={"guidelineTreatmentStatus"}
                  label={"Leitlinienbehandlung-Status"}
                  options={Object.values(DiagnoseGuidelineTreatmentStatusEnum).map((status) => {
                    return {
                      label: guidelineTreatmentStatusGermanTranslation[status],
                      value: status,
                    }
                  })}
                ></FormSelect>
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
