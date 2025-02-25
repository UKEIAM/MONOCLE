import { FormProvider, useForm } from "react-hook-form"
import { Grid, Typography } from "@mui/material"
import React, { useEffect, useState } from "react"
import Button from "@mui/material/Button"
import Dialog from "@mui/material/Dialog"
import DialogActions from "@mui/material/DialogActions"
import DialogContent from "@mui/material/DialogContent"
import DialogTitle from "@mui/material/DialogTitle"
import DatePicker from "components/FormFields/DatePicker"
import Select from "components/FormFields/Select"
import { TextField } from "components/FormFields/TextField"
import dayjs from "dayjs"
import { artOfDiagnostic, Pathology } from "./MolecularPathoFindingsTypes"
import { MolecularPathologyFinding } from "gen/api"
import Session from "hooks/Session"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import { EditableTable, FieldType } from "components/FormFields/EditableTable"

export type PropType = {
  open: boolean
  onClose: () => void
  editElement?: MolecularPathologyFinding
  specimenMap: { [key: string]: string }
}

const typeOfDiagnosticFieldDefinition = [
  {
    fieldType: "select",
    required: true,
    fieldElement: "code",
    selectItems: Object.values(artOfDiagnostic).map((artOfDiagnosticValue) => {
      return { id: artOfDiagnosticValue.value, label: artOfDiagnosticValue.label }
    }),
  },
  // TODO: Should the user set the version of the diagnostic type?
  // ,{
  //   fieldType: "input",
  //   required: false,
  //   fieldElement: "version",
  // },
] as FieldType[]

export function MolecularPathoFindingsDialog({
  open,
  onClose,
  editElement,
  specimenMap,
}: PropType) {
  const { MolecularPathologyFindingApi } = useApi()
  const { showErrorNotification, showSuccessNotification } = useNotification()
  const methods = useForm<MolecularPathologyFinding>()

  const episodeId = Session.getEpisodeId()
  const [specimensOptions, setSpecimensOptions] = useState<{ label: string; value: string }[]>([])

  useEffect(() => {
    methods.reset({
      id: editElement?.id,
      episodeId: episodeId,
      specimen: editElement?.specimen ?? "",
      performingInstitute: editElement?.performingInstitute ?? "",
      issuedOn: editElement?.issuedOn ?? "",
      note: editElement?.note ?? "",
      typeOfDiagnostic: editElement?.typeOfDiagnostic ?? [],
      // Todo : either remove the following lines or the last lines depending on the requirements
      // typeOfDiagnostic: editElement?.typeOfDiagnostic ?? undefined,
    })
  }, [open])

  useEffect(() => {
    // Trigger if specimenMap was changed from parent
    setSpecimensOptions(
      Object.entries(specimenMap).map(([uuid, readableName]) => ({
        label: readableName,
        value: uuid,
      })),
    )
  }, [specimenMap])

  const handleSubmit = (formData: MolecularPathologyFinding) => {
    let isValid = true
    if (formData.specimen === "" || !formData.issuedOn) {
      isValid = false
    }

    if (formData.issuedOn === "Invalid Date") {
      methods.setError("issuedOn", { message: "Das Datum ist nicht valide!" })
      isValid = false
    } else if (dayjs(formData.issuedOn).isAfter(dayjs())) {
      methods.setError("issuedOn", { message: "Das Datum liegt in der Zukunft" })
      isValid = false
    }
    // Map code to display value
    // Todo : should we set a default value for version and system of typeOfDiagnostic
    if (formData.typeOfDiagnostic !== undefined) {
      formData.typeOfDiagnostic = formData.typeOfDiagnostic.map((item) => {
        const diagnostic = artOfDiagnostic.find((d) => d.value === item.code)
        return {
          code: item.code || "",
          display: diagnostic ? diagnostic.label : "", // Fallback to "" if not found
        }
      })
    }
    // Todo : either remove the following lines or the last lines depending on the requirements
    // Todo : Maybe optimize the following lines if they will stay in the code
    // if (formData.typeOfDiagnostic && formData.typeOfDiagnostic.code !== undefined) {
    //   formData.typeOfDiagnostic.display = artOfDiagnostic.find(
    //     (item) => item.value === formData.typeOfDiagnostic?.code,
    //   )?.label
    // }

    if (isValid) {
      if (editElement === undefined || formData.id === undefined) {
        // Add new
        MolecularPathologyFindingApi.addMolecularPathologyFinding(episodeId, formData)
          .then(() => {
            showSuccessNotification("Der Molecular Pathologie Befund wurde erfolgreich gespeichert")
            onClose()
          })
          .catch((err) => {
            console.log(err)
            showErrorNotification(
              "Beim Speichern des Molecular Pathologie Befunds ist ein Fehler aufgetreten.",
            )
          })
      } else {
        // Update
        MolecularPathologyFindingApi.updateMolecularPathologyFinding(
          episodeId,
          formData.id,
          formData,
        )
          .then(() => {
            showSuccessNotification("Der Molecular Pathologie Befund wurde erfolgreich geändert")
            onClose()
          })
          .catch((err) => {
            console.log(err)
            showErrorNotification(
              "Beim Speichern des Molecular Pathologie Befunds ist ein Fehler aufgetreten.",
            )
          })
      }
    }
  }

  return (
    <FormProvider {...methods}>
      <Dialog
        disableEnforceFocus
        open={open}
        onClose={(event, reason) => {
          if (reason && reason == "backdropClick") return
        }}
      >
        <form onSubmit={methods.handleSubmit(handleSubmit)}>
          <DialogTitle>Molekular-Pathologie-Befund</DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ marginTop: 2 }}>
              {/* specimen Id */}
              <Grid item xs={12}>
                <Select
                  name={"specimen"}
                  isRequired={true}
                  label={"Tumorproben"}
                  options={specimensOptions}
                />
              </Grid>

              {/* performing institute of specimen */}
              <Grid item xs={12}>
                <Select name={"performingInstitute"} label={"Institut"} options={Pathology} />
              </Grid>

              {/* issued on date */}
              <Grid item xs={12}>
                <DatePicker
                  name={"issuedOn"}
                  label={"Erstellungsdatum"}
                  isRequired={true}
                  maxdate={dayjs()}
                />
                <span style={{ color: "red" }}>{methods.formState.errors.issuedOn?.message}</span>
              </Grid>

              {/* pathology notes */}
              <Grid item xs={12}>
                <TextField name={"note"} isRequired={true} label={"Notizen"} multiline={true} />
              </Grid>

              <Grid item xs={12}>
                <EditableTable
                  fieldName={"typeOfDiagnostic"}
                  rowTypes={typeOfDiagnosticFieldDefinition}
                  headerLabel={["Code"]}
                  headerText={"Art der Diagnostik"}
                  buttonText={"Art der Diagnostik Hinzufügen"}
                />
                {/*Todo : either remove the following lines or the last lines depending on the requirements*/}
                {/*<Select*/}
                {/*  isRequired={true}*/}
                {/*  name={"typeOfDiagnostic.code"}*/}
                {/*  label={"Art der Diagnostik"}*/}
                {/*  options={artOfDiagnostic}*/}
                {/*/>*/}
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
