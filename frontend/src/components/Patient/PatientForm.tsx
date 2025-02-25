import { Checkbox, FormControlLabel, Grid } from "@mui/material"
import { TextField } from "components/FormFields/TextField"
import { GenderSelect } from "components/FormFields/GenderSelect"
import DatePicker from "components/FormFields/DatePicker"
import dayjs from "dayjs"
import { Controller, FieldErrors } from "react-hook-form"
import { WarningModal } from "components/WarningModal"
import React from "react"
import { Patient } from "../../gen/api"
import { HealthInsuranceSelectFormControl } from "./Healthinsurance/HealthInsuranceSelectFormControl"

type Props = {
  patientInsuranceId?: number
  isEditMode?: boolean
  errors: FieldErrors<Patient>
  onNewEpisode?: () => void
  showWarningModal?: boolean
  onWarningModalClose?: () => void
}

export function PatientForm({
  patientInsuranceId,
  isEditMode = false,
  errors,
  onNewEpisode,
  showWarningModal,
  onWarningModalClose,
}: Props) {
  const warningModalText =
    "Der Patient, identifiziert durch die vorliegende PatID, ist bereits im System erfasst. " +
    "Möchten Sie eine neue Behandlungsepisode für diese:n Patient:in anlegen?"

  return (
    <Grid container spacing={2} padding={"2rem"}>
      <Grid item xs={12}>
        <TextField name={"firstName"} label={"Vorname"} isRequired={true}></TextField>
      </Grid>
      <Grid item xs={12}>
        <TextField name={"surname"} label={"Nachname"} isRequired={true}></TextField>
      </Grid>

      <GenderSelect />

      <Grid item xs={12}>
        <TextField name={"soarianId"} label={"PatID"} isRequired={true}></TextField>
      </Grid>

      <HealthInsuranceSelectFormControl patientInsuranceId={patientInsuranceId} />

      <Grid item xs={12}>
        <DatePicker
          name={"dateOfBirth"}
          label={"Geburtsdatum"}
          isRequired={true}
          maxdate={dayjs()}
        ></DatePicker>
        <span style={{ color: "red" }}>{errors.dateOfBirth?.message}</span>
      </Grid>

      {isEditMode && (
        <Grid item xs={12}>
          <DatePicker name={"dateOfDeath"} label={"Todesdatum"} maxdate={dayjs()}></DatePicker>
          <span style={{ color: "red" }}>{errors.dateOfDeath?.message}</span>
        </Grid>
      )}

      <Grid item xs={12}>
        <TextField
          name={"municipalityKey"}
          label={"Amtlicher Gemeindeschlüssel (ersten 5 Ziffern)"}
          isRequired={true}
          InputProps={{
            inputProps: { pattern: "([0-9]{5})" },
          }}
        ></TextField>
        <span style={{ color: "red" }}>{errors.municipalityKey?.message}</span>
      </Grid>

      {!isEditMode && (
        <Grid item xs={12}>
          <FormControlLabel
            control={
              <Controller
                name={"consent"}
                render={({ field: props }) => (
                  <Checkbox
                    {...props}
                    required={true}
                    checked={props.value}
                    onChange={(e) => props.onChange(e.target.checked)}
                  />
                )}
              />
            }
            label="* Der Patient / die Patientin ist damit einverstanden, dass seine/ihre Daten für wissenschaftliche Forschungszwecke gespeichert werden."
          />
        </Grid>
      )}

      <Grid item xs={12}>
        * Pflichtfelder
      </Grid>
      <WarningModal
        title={"Der/die Patient:in existiert bereits"}
        message={warningModalText}
        open={showWarningModal ?? false}
        handleClose={onWarningModalClose ?? (() => {})}
        submitMethod={onNewEpisode ?? (() => {})}
        buttonSubmitText={"Ja, neue Episode anlegen"}
      />
    </Grid>
  )
}
