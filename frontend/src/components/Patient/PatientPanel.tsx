import { Patient, Step } from "gen/api"
import { Grid } from "@mui/material"
import { useNavigate } from "react-router-dom"
import { PatientCard } from "components/Patient/PatientCard"
import { PatientTimeLine } from "./PatientTimeLine"
import Session from "hooks/Session"

type Props = {
  patient: Patient
  healthInsurance?: string
  cardType?: string
  onEdit?: () => void
  onNewEpisode?: () => void
}

export default function PatientPanel({
  patient,
  healthInsurance = "",
  cardType = "standard",
  onEdit = () => {},
  onNewEpisode = () => {},
}: Props) {
  const navigate = useNavigate()

  const navigateToStep = (step: Step) => {
    Session.setPatientId(patient.id ?? "")
    Session.setEpisodeId(patient.episodes?.at(0)?.id ?? "")
    navigate(`/patients/${patient.id}/step/${step.id}`)
  }

  return (
    <Grid container item alignItems="center">
      <Grid item md={4}>
        <PatientCard
          patient={patient}
          cardType={cardType}
          healthInsurance={healthInsurance}
          onEdit={onEdit}
          onNewEpisode={onNewEpisode}
        />
      </Grid>
      <Grid item md={8}>
        <PatientTimeLine patient={patient} onStepClick={navigateToStep} />
      </Grid>
    </Grid>
  )
}
