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
import React, { useEffect } from "react"
import { FormProvider, useForm } from "react-hook-form"
import Select from "components/FormFields/Select"
import DatePicker from "components/FormFields/DatePicker"
import { v4 as uuidv4 } from "uuid"
import { StudyInclusionRequest, StudySystemEnum } from "gen/api"
import Session from "hooks/Session"
import Autocomplete from "components/FormFields/Autocomplete"
import LevelOfEvidenceGrading from "../LevelOfEvidence/LevelOfEvidenceGrading"
import LevelOfEvidenceAddendums from "../LevelOfEvidence/LevelOfEvidenceAddendums"
import { EditableTable, FieldType } from "components/FormFields/EditableTable"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

type Props = {
  open: boolean
  onClose: () => void
  editElement?: StudyInclusionRequest
  ngsReportsOptions: { label: string; value: string }[]
  supportingVariantsOptions: { id: string; values: { label: string; value: string }[] }[]
  diagnosesMap: { label: string; value: string }[]
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

const studyNumberFieldDefinition = [
  {
    fieldType: "select",
    required: true,
    fieldElement: "system",
    // selectedItems need an object with id and label, where the id is the sent value and label is
    // the shown value
    selectItems: Object.entries(StudySystemEnum).map(([key, value]) => ({
      id: value,
      label: value,
    })),
  },
  {
    fieldType: "input",
    required: true,
    fieldElement: "value",
  },
] as FieldType[]

export function StudyInclusionRequestsDialog({
  open,
  onClose,
  editElement,
  supportingVariantsOptions,
  ngsReportsOptions,
  diagnosesMap,
}: Props) {
  const { StudyInclusionRequestApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const methods = useForm<StudyInclusionRequest>()
  const selectedNgsReports = methods.watch("ngsReports")

  useEffect(() => {
    if (open) {
      methods.reset({
        id: editElement?.id ?? uuidv4(),
        episodeId: episodeId,
        reason: editElement?.reason ?? "",
        // nctNumber: editElement?.nctNumber
        //   ? editElement?.nctNumber?.startsWith(NCT)
        //     ? editElement?.nctNumber.substring(3)
        //     : editElement?.nctNumber
        //   : "", // Format must be NCTxxxxxxxx (where x are digits)
        // eudraCTNumber: editElement?.eudraCTNumber,
        // drksNumber: editElement?.drksNumber,
        // eudamedNumber: editElement?.eudamedNumber,
        // studyRecommendation: editElement?.studyRecommendation,
        // study: editElement?.study,
        ngsReports: editElement?.ngsReports ?? [],
        supportingVariants: editElement?.supportingVariants ?? [],
        levelOfEvidence: editElement?.levelOfEvidence,
        studies: editElement?.studies,
        issuedOn: editElement?.issuedOn ?? undefined,
      })
    }
  }, [open])

  const getSupportingVariants = (ngsUuids: string[] | undefined) => {
    if (ngsUuids === undefined) {
      ngsUuids = editElement?.ngsReports
    }
    const elems =
      supportingVariantsOptions.find((entry) => (ngsUuids ?? []).includes(entry.id))?.values || []
    return (
      editElement?.supportingVariants?.map((sV) => elems.find((entry) => entry?.value === sV)) ?? []
    )
  }

  const onSubmit = (data: StudyInclusionRequest) => {
    if (data.issuedOn === "Invalid Date") {
      methods.setError("issuedOn", { message: "Das Datum ist nicht valide!" })
    } else if (dayjs(data.issuedOn).isAfter(dayjs(new Date()))) {
      methods.setError("issuedOn", { message: "Das Datum liegt in der Zukunft" })
    } else {
      // The input allows starting with nct followed by 8 numbers or start with 8 numbers directly.
      // But the string "NCT" is needed for bwhc, so apply the substring if not existing.
      // if (!data.nctNumber?.startsWith(NCT)) {
      //   data.nctNumber = NCT + data.nctNumber
      // }
      if (editElement !== undefined) {
        // update
        StudyInclusionRequestApi.updateStudyInclusionRequest(episodeId, data.id!, data)
          .then(() => {
            showSuccessNotification(
              "Die Studien-Einschluss-Empfehlung wurde erfolgreich gespeichert.",
            )
            onClose()
          })
          .catch((err) => {
            showErrorNotification(
              "Beim Speichern der Studien-Einschluss-Empfehlung ist ein Fehler aufgetreten.",
            )
          })
      } else {
        // add
        StudyInclusionRequestApi.addStudyInclusionRequest(episodeId, data)
          .then(() => {
            showSuccessNotification("Die Studien-Einschluss-Empfehlung wurde erfolgreich geändert.")
            onClose()
          })
          .catch((err) => {
            showErrorNotification(
              "Beim Speichern der Studien-Einschluss-Empfehlung ist ein Fehler aufgetreten.",
            )
          })
      }
    }
  }

  const getAvailableVariantsOptions = () => {
    return supportingVariantsOptions
      .filter((entry) => selectedNgsReports?.includes(entry.id))
      .flatMap((elem) => elem.values)
      .filter((item) => item?.label)
      .filter((opt) => opt !== undefined && opt !== null)
  }

  return (
    <Dialog
      open={open}
      onClose={(event, reason) => {
        if (reason && reason === "backdropClick") return
      }}
    >
      <FormProvider {...methods}>
        <form onSubmit={methods.handleSubmit(onSubmit)}>
          <DialogTitle>Studien-Einschluss-Empfehlung</DialogTitle>
          <DialogContent>
            <DialogContentText>
              <Grid container spacing={2} sx={{ marginTop: 2 }}>
                <Grid item xs={12}>
                  <Select
                    name={"reason"}
                    label={"Diagnose"}
                    options={diagnosesMap}
                    isRequired={true}
                  ></Select>
                </Grid>
                {/* <Grid item xs={12}>
                  <TextField
                    name={"nctNumber"}
                    label={"NCT-Nummer (Format: NCT gefolgt von 8 Ziffern)"}
                    isRequired={true}
                    InputProps={{
                      startAdornment: <InputAdornment position="start">NCT</InputAdornment>,
                      inputProps: { pattern: "((NCT[0-9]{8})|([0-9]{8}))" },
                    }}
                  ></TextField>
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    name={"eudraCTNumber"}
                    label={"Eudra-CT-Nummer"}
                    isRequired={false}
                  ></TextField>
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    name={"drksNumber"}
                    label={"DRKS-Nummer"}
                    isRequired={false}
                  ></TextField>
                </Grid>

                <Grid item xs={12}>
                  <TextField
                    name={"eudamedNumber"}
                    label={"Eudamed-Nummer"}
                    isRequired={false}
                  ></TextField>
                </Grid> */}

                <Grid item xs={12}>
                  <Autocomplete
                    label={"NGS Befund"}
                    name={`ngsReports`}
                    multiple={true}
                    options={ngsReportsOptions.filter((opt) => opt !== undefined && opt !== null)}
                    loading={false}
                    getOptionLabel={(option: any) => option.label}
                    getOptionId={(option: any) => option.value}
                    getOptionValue={(option: any) => option.value}
                    defaultValue={
                      editElement?.ngsReports?.map((ngsReport) =>
                        ngsReportsOptions.find((option) => option?.value === ngsReport),
                      ) || []
                    }
                  />
                </Grid>

                <Grid item xs={12}>
                  <Autocomplete
                    label={"Stützende molekulare Alteration(en)"}
                    name={`supportingVariants`}
                    multiple={true}
                    options={getAvailableVariantsOptions()}
                    loading={false}
                    getOptionLabel={(option: any) => option?.label ?? ""}
                    getOptionId={(option: any) => option.value}
                    getOptionValue={(option: any) => option.value}
                    defaultValue={getSupportingVariants(selectedNgsReports)}
                  />
                </Grid>

                <Grid item xs={12}>
                  <LevelOfEvidenceGrading
                    isRequired={false}
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
                    headerText={"Publikationen für den Evendenz Level hinzufügen"}
                    buttonText={"Publikation Hinzufügen"}
                  />
                </Grid>

                <Grid container item xs={12} spacing={2}>
                  <EditableTable
                    fieldName={"studies"}
                    rowTypes={studyNumberFieldDefinition}
                    headerLabel={["Studie", "Nummer"]}
                    headerText={"Studien Nummer Bsp.: NCT-12345678"}
                    buttonText={"Studien Nummer hinzufügen"}
                  />
                </Grid>

                <Grid item xs={12}>
                  <DatePicker
                    name={"issuedOn"}
                    label={"Erstellungsdatum"}
                    maxdate={dayjs()}
                  ></DatePicker>
                  <span style={{ color: "red" }}>{methods.formState.errors.issuedOn?.message}</span>
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
