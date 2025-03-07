import { FormProvider, useForm } from "react-hook-form"
import {
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  Grid,
  Typography,
} from "@mui/material"
import React, { useEffect, useState } from "react"
import dayjs from "dayjs"
import Select from "components/FormFields/Select"
import DatePicker from "components/FormFields/DatePicker"
import FormRadioGroup from "../../FormFields/FormRadioGroup"
import Autocomplete from "components/FormFields/Autocomplete"
import { MedicationComponent } from "components/FormFields/MedicationComponent"
import LevelOfEvidenceGrading from "../LevelOfEvidence/LevelOfEvidenceGrading"
import LevelOfEvidenceAddendums from "../LevelOfEvidence/LevelOfEvidenceAddendums"
import Session from "hooks/Session"
import { TherapyRecommendation, TherapyRecommendationPriorityEnum, ValueSet } from "gen/api"
import { EditableTable, FieldType } from "components/FormFields/EditableTable"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

type Props = {
  open: boolean
  onClose: () => void
  diagnosesOptions: { label: string; value: string }[]
  ngsReportsOptions: { label: string; value: string }[]
  supportingVariantsOptions: { id: string; values: { label: string; value: string }[] }[]
  editElement?: TherapyRecommendation
}

const evidenceLevelPublicationFieldDefinition = [
  {
    fieldType: "input",
    required: false,
    fieldElement: "pmid",
  },
  {
    fieldType: "input",
    required: false,
    fieldElement: "doi",
  },
] as FieldType[]

const priorityRadioGroupOptions: { label: string; value: string }[] = [
  { label: "1", value: TherapyRecommendationPriorityEnum._1 },
  { label: "2", value: TherapyRecommendationPriorityEnum._2 },
  { label: "3", value: TherapyRecommendationPriorityEnum._3 },
  { label: "4", value: TherapyRecommendationPriorityEnum._4 },
]

export default function RecommendationDialog({
  open,
  onClose,
  diagnosesOptions,
  ngsReportsOptions,
  supportingVariantsOptions,
  editElement,
}: Props) {
  const { TherapyRecommendationApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const methods = useForm<TherapyRecommendation>()
  const [checkedLevelOfEvidenceStatus, setCheckedLevelOfEvidenceStatus] = useState(false)
  const selectedNgsReport = methods.watch("ngsReport")

  useEffect(() => {
    setCheckedLevelOfEvidenceStatus(!!editElement?.levelOfEvidence?.grading?.code)
    methods.reset({
      id: editElement?.id,
      episodeId: episodeId,
      diagnosis: editElement?.diagnosis ?? "",
      issuedOn: editElement?.issuedOn ?? undefined,
      medication: editElement?.medication ?? [],
      priority: editElement?.priority ?? undefined,
      levelOfEvidence: {
        grading: {
          code: editElement?.levelOfEvidence?.grading?.code ?? undefined,
          system: "MTB-CDS:Level-of-Evidence:Grading",
        },
        addendums: editElement?.levelOfEvidence?.addendums,
        publications: editElement?.levelOfEvidence?.publications,
      },
      ngsReport: editElement?.ngsReport,
      supportingVariants: editElement?.supportingVariants ?? [],
    })
  }, [open])

  const getSupportingVariants = (ngsUuid: string | undefined) => {
    if (ngsUuid === undefined) {
      ngsUuid = editElement?.ngsReport
    }

    const elems = supportingVariantsOptions.find((entry) => entry.id === ngsUuid)?.values || []
    return (
      editElement?.supportingVariants?.map((sV) => elems.find((entry) => entry?.value == sV)) ?? []
    )
  }

  const handleSubmit = (recommendationForm: TherapyRecommendation) => {
    // Check if level of evidence is set
    if (!checkedLevelOfEvidenceStatus) {
      delete recommendationForm.levelOfEvidence
    } else if (recommendationForm.levelOfEvidence) {
      // Add Addendums system before sending to backend
      recommendationForm.levelOfEvidence.addendums =
        recommendationForm.levelOfEvidence?.addendums?.map((valueSet: ValueSet) => {
          return { code: valueSet.code, system: "MTB-CDS:Level-of-Evidence:Addendum" }
        })
    }

    // Check date
    let isValid = true
    if (recommendationForm.issuedOn === "Invalid Date") {
      isValid = false
      methods.setError("issuedOn", { message: "Das Datum ist nicht valide!" })
    } else if (dayjs(recommendationForm.issuedOn).isAfter(dayjs())) {
      isValid = false
      methods.setError("issuedOn", { message: "Das Datum liegt in der Zukunft" })
    }

    if (recommendationForm.ngsReport === "") {
      recommendationForm.ngsReport = undefined
    }

    // Check if valid and if diagnosis is given
    if (recommendationForm.diagnosis === "" || !isValid) {
      return
    }

    triggerSubmit(recommendationForm)
  }

  const triggerSubmit = (recommendationForm: TherapyRecommendation) => {
    const addOrUpdateResponse = editElement
      ? TherapyRecommendationApi.updateTherapyRecommendation(
          episodeId,
          recommendationForm.id!,
          recommendationForm,
        )
      : TherapyRecommendationApi.addTherapyRecommendation(episodeId, recommendationForm)

    addOrUpdateResponse
      .then(() => {
        showSuccessNotification(
          `Die Therapie-Empfehlung wurde erfolgreich ${editElement ? "geändert" : "gespeichert"}.`,
        )
        onClose()
        setCheckedLevelOfEvidenceStatus(false)
      })
      .catch(() => {
        showErrorNotification("Beim Speichern der Therapie-Empfehlung ist ein Fehler aufgetreten.")
      })
  }

  const handleLevelOfEvidenceStatus = (event: React.ChangeEvent<HTMLInputElement>) => {
    setCheckedLevelOfEvidenceStatus(event.target.checked)
  }

  const getAvailableVariantsOptions = () => {
    return supportingVariantsOptions
      .filter((entry) => selectedNgsReport?.includes(entry.id))
      .flatMap((elem) => elem.values)
      .filter((item) => item?.label)
      .filter((opt) => opt !== undefined && opt !== null)
  }

  return (
    <FormProvider {...methods}>
      <Dialog open={open}>
        <form onSubmit={methods.handleSubmit(handleSubmit)}>
          <DialogTitle>Therapie-Empfehlung</DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ marginTop: 2 }}>
              <Grid item xs={12}>
                <Select
                  isRequired={true}
                  label={"Diagnose"}
                  name={`diagnosis`}
                  options={diagnosesOptions}
                />
              </Grid>

              <Grid item xs={12}>
                <DatePicker label={"Erstellungsdatum"} name={"issuedOn"} maxdate={dayjs()} />
                <span style={{ color: "red" }}>{methods.formState.errors.issuedOn?.message}</span>
              </Grid>

              <MedicationComponent />

              <Grid item xs={12}>
                <FormRadioGroup
                  label={"Priorität"}
                  name={"priority"}
                  options={priorityRadioGroupOptions}
                />
              </Grid>

              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={checkedLevelOfEvidenceStatus}
                      onChange={handleLevelOfEvidenceStatus}
                    />
                  }
                  name={"levelOfEvidenceStatus"}
                  label={"Evidenz Level hinzufügen"}
                />
              </Grid>

              {checkedLevelOfEvidenceStatus && (
                <>
                  <Grid item xs={12}>
                    <LevelOfEvidenceGrading
                      isRequired={true}
                      name={"levelOfEvidence.grading.code"}
                      label={"Evidenz Level Graduierung"}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <LevelOfEvidenceAddendums
                      name={"levelOfEvidence.addendums"}
                      label={"Evidenz Level Zusatz"}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <EditableTable
                      fieldName={"levelOfEvidence.publications"}
                      rowTypes={evidenceLevelPublicationFieldDefinition}
                      headerLabel={["PubMed Identifier (PMID)", "Digital Object Identifier (DOI)"]}
                      headerText={"Publikationen für den Evindenz Level hinzufügen"}
                      buttonText={"Publikation Hinzufügen"}
                    />
                  </Grid>
                </>
              )}

              <Grid item xs={12}>
                <Select label={"NGS Befund"} name={`ngsReport`} options={ngsReportsOptions} />
              </Grid>

              <Grid item xs={12}>
                <Autocomplete
                  label={"Stützende molekulare Alteration(en)"}
                  name={`supportingVariants`}
                  multiple={true}
                  options={getAvailableVariantsOptions()}
                  loading={false}
                  getOptionLabel={(option: any) => option.label}
                  getOptionId={(option: any) => option.value}
                  getOptionValue={(option: any) => option.value}
                  defaultValue={getSupportingVariants(selectedNgsReport)}
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
