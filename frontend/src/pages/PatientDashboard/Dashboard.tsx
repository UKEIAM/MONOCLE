import React, { useEffect, useState } from "react"
import { Patient } from "gen/api"
import { useParams } from "react-router-dom"
import PatientPanel from "components/Patient/PatientPanel"
import { Comments } from "pages/PatientDashboard/components/Comments"
import { Presentations } from "pages/PatientDashboard/components/Presentations"
import Session from "hooks/Session"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"

export default function Dashboard() {
  const { showErrorNotification } = useNotification()
  const { PatientApi, HealthinsuranceApi } = useApi()
  const { patientId } = useParams()

  const [patient, setPatient] = useState<Patient>()
  const [healthInsuranceValue, setHealthInsuranceValue] = useState<string>()
  useEffect(() => {
    getPatientAndHealthInsurance()
  }, [patientId]) // eslint-disable-line react-hooks/exhaustive-deps

  useEffect(() => {
    if (patient?.healthInsurance) getHealthInsurenceByID(patient?.healthInsurance)
  }, [patient?.healthInsurance])

  const getPatientAndHealthInsurance = () => {
    if (!patientId) return
    PatientApi.getPatient(patientId)
      .then(({ data: patientData }) => {
        setPatient(patientData)
        Session.setPatientId(patientData.id ?? "")
        Session.setEpisodeId(patientData.episodes?.at(0)?.id ?? "")
        getHealthInsurenceByID(patientData.healthInsurance ? patientData.healthInsurance : -1)
      })
      .catch((_) => showErrorNotification("Es konnte keine Patient mit dieser ID gefunden werden."))
  }

  const getHealthInsurenceByID = (id: number) => {
    HealthinsuranceApi.getHealthInsuranceByID(id)
      .then(({ data: healthInsurance }) => {
        setHealthInsuranceValue(
          healthInsurance.Namenszeile_1 +
            " " +
            healthInsurance.Namenszeile_2 +
            " " +
            healthInsurance.Namenszeile_3 +
            " " +
            healthInsurance.Namenszeile_4,
        )
      })
      .catch((_) =>
        showErrorNotification("Es konnte keine Krankenkasse mit dieser ID gefunden werden."),
      )
  }

  return (
    <>
      {patient ? (
        <PatientPanel
          key={patient.id}
          patient={patient}
          healthInsurance={healthInsuranceValue}
          cardType={"full"}
          onEdit={getPatientAndHealthInsurance}
          onNewEpisode={getPatientAndHealthInsurance}
        />
      ) : undefined}
      <Presentations />
      <Comments />
    </>
  )
}
