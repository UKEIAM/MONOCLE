import { FormProvider, useForm } from "react-hook-form"
import React, { useEffect, useState } from "react"
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
import Autocomplete from "components/FormFields/Autocomplete"
import FormSelect from "components/FormFields/FormSelect"
import FormTextField from "../../FormFields/FormTextField"
import FormDatePicker from "../../FormFields/FormDatePicker"
import dayjs from "dayjs"
import { WarningModal } from "components/WarningModal"
import Session from "hooks/Session"
import { CarePlan } from "gen/api"
import { reasonOptions } from "./CarePlansTypes"
import AutocompleteFreeSolo from "components/FormFields/AutocompleteFreeSolo"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

type Props = {
  open: boolean
  editElement?: CarePlan
  onClose: () => void
  diagnosesMap: { [key: string]: string }
  rebiopsyMap: { [key: string]: string }
  studyInclusionMap: { [key: string]: string }
  therapyRecommendationMap: { [key: string]: string }
}

export function CarePlansDialog({
  open,
  editElement,
  onClose,
  diagnosesMap,
  rebiopsyMap,
  studyInclusionMap,
  therapyRecommendationMap,
}: Props) {
  const { CarePlanApi } = useApi()
  const methods = useForm<CarePlan>()
  const [openIssuedOnWarning, setopenIssuedOnWarning] = useState<boolean>(false)
  const [noTargetFinding, setNoTargetFinding] = useState<boolean>(false)
  const episodeId = Session.getEpisodeId()
  const { showSuccessNotification, showErrorNotification } = useNotification()

  useEffect(() => {
    setNoTargetFinding(!!editElement?.noTargetFinding)
    methods.reset({
      id: editElement?.id,
      episodeId: episodeId,
      recommendations: editElement?.recommendations ?? [],
      rebiopsyRequests: editElement?.rebiopsyRequests ?? [],
      studyInclusionRequests: editElement?.studyInclusionRequests ?? [],
      ...editElement,
    })
  }, [open])

  const handleSubmit = (carePlan: CarePlan) => {
    if (carePlan.diagnosis === "") {
      return
    }

    let isValid = true
    if (!noTargetFinding && carePlan.recommendations?.length! <= 0) {
      isValid = false
      methods.setError("recommendations", {
        message:
          "Entweder 'Keine Therapeutische Konsequenz' oder mindestens eine Therapieempfehlungen müssen angegeben werden.",
      })
    }

    // Todo : why do we need this check?
    if (carePlan.issuedOn === "") {
      setopenIssuedOnWarning(true)
      return
    }
    // Todo : why do we need this check?
    if (carePlan.geneticCounsellingRequest) {
      if (carePlan.geneticCounsellingRequest.issuedOn === "") {
        setopenIssuedOnWarning(true)
        return
      }
    }
    // noTargetFinding is a boolean value for the checkbox and doesn't belong to care plan object
    if (noTargetFinding) {
      // Todo: what should we do with the NoTargetFinding? maybe delete it
      // Add NoTargetFinding Object
      carePlan.noTargetFinding = {
        episodeId: episodeId,
        diagnosis: carePlan.diagnosis,
        issuedOn: dayjs().format("YYYY-MM-DD"),
      }
      // Add StatusReason Object
      carePlan.statusReason = {
        code: "no-target",
        display: "Keine Therapeutische Konsequenz",
        system: "dnpm-dip/mtb/careplan/status-reason",
      }
      // Delete Recommendation
      delete carePlan.recommendations
    } else {
      // TODO : delete this too, if the object noTargetFinding is not needed
      // Delete NoTargetFinding Object
      delete carePlan.noTargetFinding
      delete carePlan.statusReason
    }

    if (isValid) triggerSubmit(carePlan)
  }
  // TODO: can we make this more readable?
  const triggerSubmit = (carePlan: CarePlan) => {
    // fill genetic counselling request object with all necessary values or delete it
    let display = carePlan.geneticCounsellingRequest?.reason?.display
    if (display) {
      // if set in formular
      let codeValueObject = reasonOptions.find((elem) => elem.label === display)
      if (carePlan.geneticCounsellingRequest) {
        carePlan.geneticCounsellingRequest.episodeId = episodeId
        carePlan.geneticCounsellingRequest.reason = {
          display: display,
          code: codeValueObject?.value ?? display,
          system: codeValueObject ? "dnpm-dip/mtb/recommendation/genetic-counseling/reason" : "",
        }
      }
    } else {
      carePlan.geneticCounsellingRequest = undefined
    }

    let addOrUpdatePromise =
      editElement === undefined
        ? CarePlanApi.addCarePlan(episodeId, carePlan) // add care plan
        : CarePlanApi.updateCarePlan(episodeId, carePlan.id!, carePlan) // update care plan

    addOrUpdatePromise
      .then(() => {
        showSuccessNotification(`Der Therapieplan wurde erfolgreich gespeichert.`)
        onClose()
      })
      .catch(() => showErrorNotification("Beim Speichern des Therapieplat ein Fehler aufgetreten."))
  }
  return (
    <FormProvider {...methods}>
      <Dialog open={open}>
        <form onSubmit={methods.handleSubmit(handleSubmit)}>
          <DialogTitle>Therapieplan</DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ marginTop: 2 }}>
              <Grid item xs={12}>
                <FormSelect
                  label={"Diagnose"}
                  name={`diagnosis`}
                  validationRules={{ required: true }}
                  options={Object.entries(diagnosesMap).map(([uuid, readableName]) => ({
                    label: readableName,
                    value: uuid,
                  }))}
                />
              </Grid>
              <Grid item xs={12}>
                <FormDatePicker label={"Erstellungsdatum"} name={`issuedOn`} maxdate={dayjs()} />
              </Grid>
              <Grid item xs={12}>
                <FormTextField name={"description"} label={"Protokollauszug"} />
              </Grid>
              {/*Keine Therapeutische Konsequenz*/}
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Checkbox
                      onChange={(e) => setNoTargetFinding(e.target.checked)}
                      checked={Boolean(noTargetFinding)}
                    />
                  }
                  label={"Keine Therapeutische Konsequenz"}
                />
              </Grid>

              {/*{checkedTumorCellContentMethod &&*/}
              <Grid item xs={12}>
                <Autocomplete
                  label={"Therapie-Empfehlungen (Mehrfachauswahl)"}
                  name={`recommendations`}
                  multiple={true}
                  disabled={noTargetFinding}
                  options={Object.entries(therapyRecommendationMap).map(([uuid, readableName]) => ({
                    label: readableName,
                    value: uuid,
                  }))}
                  loading={false}
                  getOptionLabel={(option: any) => option.label}
                  getOptionId={(option: any) => option.value}
                  getOptionValue={(option: any) => option.value}
                  defaultValue={editElement?.recommendations?.map((item: string) => {
                    return { label: therapyRecommendationMap[item], value: item }
                  })}
                />
                <span style={{ color: "red" }}>
                  {methods.formState.errors.recommendations?.message}
                </span>
              </Grid>
              <Grid item xs={12}>
                <AutocompleteFreeSolo
                  control={methods.control}
                  label={"Auftrag Human-genetische Beratung Begründung"}
                  name={"geneticCounsellingRequest.reason.display"}
                  options={reasonOptions.map((elem) => elem.label)}
                  loading={false}
                  isTextInputPossible={true}
                  key={editElement?.geneticCounsellingRequest?.id}
                ></AutocompleteFreeSolo>
              </Grid>
              <Grid item xs={12}>
                <FormDatePicker
                  label={"Auftrag Human-genetische Beratung Datum"}
                  name={`geneticCounsellingRequest.issuedOn`}
                  maxdate={dayjs()}
                />
              </Grid>
              <Grid item xs={12}>
                {/* TODO: Create rebiopsyRequests String */}
                <Autocomplete
                  label={"Rebiopsie-Auftrag (Mehrfachauswahl)"}
                  name={`rebiopsyRequests`}
                  multiple={true}
                  options={Object.entries(rebiopsyMap).map(([uuid, readableName]) => ({
                    label: readableName,
                    value: uuid,
                  }))}
                  loading={false}
                  getOptionLabel={(option: any) => option.label}
                  getOptionId={(option: any) => option.value}
                  getOptionValue={(option: any) => option.value}
                  defaultValue={editElement?.rebiopsyRequests?.map((item: string) => {
                    return { label: rebiopsyMap[item], value: item }
                  })}
                />
              </Grid>
              <Grid item xs={12}>
                {/* TODO: Create studyInclusionRequests String */}
                <Autocomplete
                  label={"Studien-Einschluss-Empfehlung (Mehrfachauswahl)"}
                  name={`studyInclusionRequests`}
                  multiple={true}
                  options={Object.entries(studyInclusionMap).map(([uuid, readableName]) => ({
                    label: readableName,
                    value: uuid,
                  }))}
                  loading={false}
                  getOptionLabel={(option: any) => option.label}
                  getOptionId={(option: any) => option.value}
                  getOptionValue={(option: any) => option.value}
                  defaultValue={editElement?.studyInclusionRequests?.map((item: string) => {
                    return { label: studyInclusionMap[item], value: item }
                  })}
                />
              </Grid>
              <Grid item xs={12}>
                <Typography>* Pflichtfelder</Typography>
              </Grid>
            </Grid>
            {/*TODO : Should not we delete this warning modal?*/}
            <WarningModal
              title={"Erstellungsdatum ist leer"}
              message={"Möchten Sie den Therapieplan trotzdem hinzufügen?"}
              open={openIssuedOnWarning}
              handleClose={() => setopenIssuedOnWarning(false)}
              submitMethod={methods.handleSubmit(triggerSubmit)}
            />
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
