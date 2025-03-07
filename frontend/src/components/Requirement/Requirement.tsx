import { Patient, Step } from "../../gen/api"
import { Button, Grid } from "@mui/material"
import React from "react"
import { useNavigate } from "react-router-dom"

type Props = {
  patient: Patient
  step: Step | undefined
}

export default function Requirement({ patient, step }: Props) {
  const navigate = useNavigate()
  const navigateTo = (stepName: string) => {
    const stepId = step?.steps?.find((subStep) => subStep.name === stepName)?.id
    navigate(`/patients/${patient.id}/step/${stepId}`)
  }

  return (
    <React.Fragment>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <Button
            variant="contained"
            onClick={() => navigateTo("Anforderung Eingabe")}
            style={{ display: "block" }}
          >
            Zur Eingabe der Anforderung
          </Button>
        </Grid>
        <Grid item xs={12}>
          <Button
            variant="contained"
            onClick={() => navigateTo("Zuweiser Auswahl")}
            style={{ display: "block" }}
          >
            Zur Auswahl der Zuweiser
          </Button>
        </Grid>
        <Grid item xs={12}>
          <Button
            variant="contained"
            onClick={() => navigateTo("Pathologen Auswahl")}
            style={{ display: "block" }}
          >
            Zur Auswahl der Pathologen
          </Button>
        </Grid>
        <Grid item xs={12}>
          <Button
            variant="contained"
            onClick={() => navigateTo("Überprüfen und Weiterleiten")}
            style={{ display: "block" }}
          >
            Zum Überprüfen und Weiterleiten
          </Button>
        </Grid>
      </Grid>
    </React.Fragment>
  )
}
