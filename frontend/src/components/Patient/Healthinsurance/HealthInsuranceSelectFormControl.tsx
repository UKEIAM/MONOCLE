import React, { useEffect, useRef, useState } from "react"
import { Grid } from "@mui/material"
import Autocomplete from "components/FormFields/Autocomplete"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

type PropsType = {
  patientInsuranceId?: number | undefined
}

type HealthInsuranceType = {
  id: number | undefined
  label: string | undefined
}

export function HealthInsuranceSelectFormControl({ patientInsuranceId }: PropsType) {
  const { HealthinsuranceApi } = useApi()
  const { showErrorNotification } = useNotification()
  const [autofillHealthInsurances, setAutofillHealthInsurances] = useState<HealthInsuranceType[]>([
    {
      id: undefined,
      label: "",
    },
  ])
  const [patientHealthInsurance, setPatientHealthInsurance] = useState<
    HealthInsuranceType | undefined
  >()
  const isServiceCallAlreadyMadeForHealthinsurances = useRef<boolean>(false)

  useEffect(() => {
    if (isServiceCallAlreadyMadeForHealthinsurances.current) return
    HealthinsuranceApi.getHealthInsurance()
      .then(({ data }) => {
        if (Object.keys(data).length === 0) setAutofillHealthInsurances([])
        else {
          let autofill = []

          for (let i = 0; i < Object.keys(data).length; i++) {
            let healthInsurenceValueLabel = `${data[i].Namenszeile_1} ${data[i].Namenszeile_2} ${data[i].Namenszeile_3} ${data[i].Namenszeile_4} ${data[i].P_Ort} (${data[i].IK})`
            let healthInsurenceValueId = data[i].IK

            if (healthInsurenceValueLabel && healthInsurenceValueId) {
              autofill.push({ id: healthInsurenceValueId, label: healthInsurenceValueLabel.trim() })
            }
          }
          // sort alphabetical
          autofill.sort((a, b) => {
            if (b.label < a.label) return 1
            if (b.label > a.label) return -1
            return 0
          })

          const foundIdx = autofill.findIndex((el) => el.id == 99999999)
          autofill.splice(foundIdx, 1)
          autofill.unshift({ id: 99999999, label: "zur Zeit unbekannt" })

          setAutofillHealthInsurances(autofill)
        }
      })
      .catch(() =>
        showErrorNotification(
          "Krankenkassen konnten nicht geladen werden. Bitte versuchen Sie es später erneut.",
        ),
      )
    isServiceCallAlreadyMadeForHealthinsurances.current = true
  }, [])

  useEffect(() => {
    if (autofillHealthInsurances.length === 1) return
    // find patient insurance in the list of health insurances (autofillHealthInsurances)
    const healthInsurance = autofillHealthInsurances.find(
      (healthInsurance) => healthInsurance.id === patientInsuranceId,
    )
    if (healthInsurance) {
      setPatientHealthInsurance({
        id: healthInsurance.id,
        label: healthInsurance.label,
      } as HealthInsuranceType)
    }
  }, [autofillHealthInsurances])

  return (
    <Grid item xs={12}>
      <Autocomplete
        label={"Krankenkasse"}
        name={"healthInsurance"}
        key={!!patientHealthInsurance ? "initialized" : "empty"}
        options={autofillHealthInsurances}
        loading={true}
        required={true}
        getOptionLabel={(option: any) => option.label}
        getOptionId={(option: any) => option.id}
        getOptionValue={(option: any) => option.id}
        defaultValue={patientHealthInsurance}
      />
    </Grid>
  )
}
