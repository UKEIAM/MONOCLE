import { Card, CardContent, CardHeader, Grid } from "@mui/material"
import React from "react"
import { Episode, Patient } from "gen/api"
import Button from "@mui/material/Button"
import { FormProvider, useForm } from "react-hook-form"
import { TextField } from "components/FormFields/TextField"
import { ReactToPrint } from "react-to-print"
import PrintIcon from "@mui/icons-material/Print"
import { toGermanDateFormat } from "utils/Formats"
import Session from "hooks/Session"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

interface ReportInputProps {
  patient: Patient
}

const ReportInput: React.FC<ReportInputProps> = ({ patient }) => {
  const { showErrorNotification, showSuccessNotification } = useNotification()
  const { EpisodeApi } = useApi()
  const episodeId = Session.getEpisodeId()
  const [episode, setEpisode] = React.useState<Episode>()
  const methods = useForm<Episode>({ defaultValues: episode })
  const componentRef = React.useRef<HTMLDivElement>(null)

  React.useEffect(() => {
    if (episodeId) {
      EpisodeApi.getEpisode(episodeId).then((episodeResponse) => {
        setEpisode(episodeResponse.data)
      })
    }
  }, [episodeId, EpisodeApi])

  React.useEffect(() => {
    methods.setValue("report", episode?.report)
    methods.setValue("decision", episode?.decision)
  }, [episode, methods])

  const submitForm = (formData: Episode) => {
    if (episode) {
      const newEpisode: Episode = {
        ...episode,
        report: formData.report,
        decision: formData.decision,
      }

      EpisodeApi.updateEpisode(episode.id!, newEpisode)
        .then((episodeResponse) => {
          setEpisode(episodeResponse.data)
          showSuccessNotification(
            "Der MTB Bericht und der MTB Beschluss wurden erfolgreich gespeichert.",
          )
        })
        .catch(() => showErrorNotification("Beim Speichern ist ein Fehler aufgetreten."))
    }
  }

  const handleBeforeGetContent = () => {
    const values = methods.getValues()

    const reportHeader = document.getElementById("print-report-header")
    const reportText = document.getElementById("print-report-text")
    const decisionHeader = document.getElementById("print-decision-header")
    const decisionText = document.getElementById("print-decision-text")

    if (reportHeader && reportText && decisionHeader && decisionText) {
      reportHeader.innerText = "MTB-Report"
      decisionHeader.innerText = "MTB-Beschluss"

      reportText.innerText = values.report ?? ""
      decisionText.innerText = values.decision ?? ""
    }
  }

  return (
    <>
      <Card sx={{ boxShadow: 3, borderRadius: 2, margin: "1rem" }}>
        <CardHeader
          title={`Patient-ID: (${patient?.soarianId}) - Geboren am: ${toGermanDateFormat(patient?.dateOfBirth)}`}
          subheader="Eine Übersicht der MTB-Bericht und MTB-Beschluss"
          sx={{ backgroundColor: "#f4f4f4", borderRadius: "8px 8px 0 0" }}
        />

        <CardContent>
          <FormProvider {...methods}>
            <form onSubmit={methods.handleSubmit(submitForm)}>
              <Grid container spacing={2}>
                <Grid container item xs={12}>
                  <TextField
                    name={"report"}
                    label={"MTB Bericht"}
                    multiline={true}
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>

                <Grid container item xs={12}>
                  <TextField
                    name={"decision"}
                    label={"MTB Beschluss"}
                    multiline={true}
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>

                <Grid container item xs={12} columnSpacing={2}>
                  <Grid item>
                    <Button type={"submit"} color="primary" variant="contained">
                      Speichern
                    </Button>
                  </Grid>
                  <Grid item>
                    <ReactToPrint
                      onBeforeGetContent={() => handleBeforeGetContent()}
                      documentTitle={"MTB-Report-und-Beschluss"}
                      pageStyle={"margin: 10"}
                      trigger={() => (
                        <Button variant="contained" color="primary">
                          <PrintIcon />
                        </Button>
                      )}
                      content={() => componentRef.current}
                    />
                  </Grid>
                </Grid>
              </Grid>
            </form>

            {/* Printable content */}
            <div id={"print-component"} ref={componentRef}>
              <img
                src={process.env.PUBLIC_URL + "/uke-logo.png"}
                alt="UKE-Logo"
                width={48}
                height={48}
              />
              <span style={{ marginLeft: 20 }}>
                {patient?.firstName} {patient?.surname} - {toGermanDateFormat(patient?.dateOfBirth)}{" "}
                - PatID: ({patient?.soarianId})
              </span>
              <h3 id={"print-report-header"} />
              <span id="print-report-text">Hallo</span>
              <h3 id={"print-decision-header"} />
              <span id="print-decision-text">Hallo</span>
            </div>
          </FormProvider>
        </CardContent>
      </Card>
    </>
  )
}

export default ReportInput
