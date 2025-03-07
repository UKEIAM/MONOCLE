import { useApi } from "hooks/useApi"
import React, { useEffect, useMemo, useState } from "react"
import { useNotification } from "hooks/useNotification"
import { Patient } from "gen/api"
import dayjs from "dayjs"
import { FormProvider, useForm } from "react-hook-form"
import { Alert, Button, Card, Grid } from "@mui/material"
import { EditableTable, FieldType } from "components/FormFields/EditableTable"
import SendIcon from "@mui/icons-material/Send"
import FormCheckbox from "components/FormFields/FormCheckbox"

interface LabNumberCombinator {
  readonly episodeId: string
  readonly enNumber: string
  readonly labNumber: string
}

interface LabNumberForm {
  readonly confirm: boolean
  readonly table: LabNumberCombinator[]
}

const defaultForm = {
  confirm: false,
  table: [{ episodeId: "", enNumber: "", labNumber: "" }],
}

export default function PatientLabNumber() {
  const { LabNumberApi, PatientApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const methods = useForm<LabNumberForm>({
    defaultValues: defaultForm,
  })

  const [patients, setPatients] = useState<Patient[]>()
  useEffect(() => {
    PatientApi.getPatients()
      .then(({ data }) => setPatients(data))
      .catch(() =>
        showErrorNotification(
          "Die Patienten konnten nicht geladen werden. Bitte versuchen Sie es später erneut.",
        ),
      )
  }, [PatientApi])

  const patientSelectOptions = useMemo<{ id: string; label: string }[]>(() => {
    return (
      patients?.map((patient) => {
        let dateOfBirth = dayjs(patient.dateOfBirth).format("DD.MM.YYYY")
        let patientValue = `${patient.surname}, ${patient.firstName} (${dateOfBirth})`
        let episodeId = patient.episodes?.at(0)?.id ?? ""
        return {
          id: episodeId,
          label: patientValue,
        }
      }) ?? []
    )
  }, [patients])

  const handleSubmit = async (data: LabNumberForm) => {
    if (!data.confirm) {
      showErrorNotification("Bitte bestätigen Sie, dass Sie alle Kombinationen geprüft haben.")
      return
    }

    let failedCombinations: LabNumberCombinator[] = []
    await Promise.allSettled(
      data.table.map((combination: LabNumberCombinator) =>
        LabNumberApi.addLabNumber({
          id: combination.labNumber,
          specimenLabelling: combination.enNumber,
          episodeId: combination.episodeId,
        })
          .then()
          .catch(() => {
            failedCombinations.push(combination)
          }),
      ),
    )
    if (failedCombinations.length === 0) {
      showSuccessNotification("Die Kombinationen wurden erfolgreich gespeichert!")
      methods.reset(defaultForm)
    } else {
      showErrorNotification(
        "Einige Kombinationen konnten nicht gespeichert werden. Bitte überprüfen Sie die Labornummern von gebliebene Kombinationen.",
      )
      methods.reset({ confirm: false, table: failedCombinations })
    }
  }

  const isLabNumberExistInDB = async (labNumber: string): Promise<boolean> => {
    return LabNumberApi.isLabNumberExists(labNumber).then((responseBoolean) => {
      return responseBoolean.data
    })
  }

  const rows: FieldType[] = [
    {
      fieldType: "autocomplete",
      required: true,
      fieldElement: "episodeId",
      selectItems: patientSelectOptions,
      columnWidth: "60%",
    } as FieldType,
    {
      fieldType: "input",
      required: true,
      fieldElement: "enNumber",
      columnWidth: "20%",
    } as FieldType,
    {
      fieldType: "input",
      required: true,
      fieldElement: "labNumber",
      columnWidth: "20%",
      onBlur: (index: number) => {
        const labnumber = methods.getValues("table")[index].labNumber
        // do not check if empty
        if (labnumber) {
          isLabNumberExistInDB(labnumber).then((isExist) => {
            if (isExist) {
              showErrorNotification("Labornummer existiert bereits!")
            }
          })
        }
      },
    } as FieldType,
  ]

  return (
    <FormProvider {...methods}>
      <Card>
        <Alert severity="warning">
          Die Tabelle wird erst final gespeichert, wenn sie unten auf "Endgültig Speichern" klicken!
        </Alert>
        <form onSubmit={methods.handleSubmit(handleSubmit)}>
          <Grid container spacing={2} padding={"2rem"}>
            <Grid item xs={12}>
              <EditableTable
                fieldName={"table"}
                rowTypes={rows}
                headerText={"Patient-Labornummer-Kombination"}
                headerLabel={["Patient:in", "E-/N-Nummer", "Labornummer"]}
                buttonText={"Zeile hinzufügen"}
              />
            </Grid>
            <Grid item xs={12}>
              <FormCheckbox
                name={"confirm"}
                label={"Ich habe alle Kombinationen geprüft und es sind keine Fehler vorhanden."}
                validationRules={{
                  required: {
                    value: true,
                    message: "Die Checkbox ist ein Pflichtfeld",
                  },
                }}
              />
            </Grid>

            <Grid item xs={12} padding={"0 2rem 2rem 2rem"}>
              <Button fullWidth variant="contained" type="submit" endIcon={<SendIcon />}>
                Endgültig Speichern
              </Button>
            </Grid>
          </Grid>
        </form>
      </Card>
    </FormProvider>
  )
}
