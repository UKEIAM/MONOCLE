import { StepInfo, StepStatus } from "gen/api"
import { Button, Typography, useTheme } from "@mui/material"
import React, { useEffect, useState } from "react"
import SendIcon from "@mui/icons-material/Send"
import Session from "hooks/Session"
import { useNavigate, useParams } from "react-router-dom"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

export default function Transfer() {
  const { StepsinfoApi, BwhcTransferApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const theme = useTheme()
  const episodeId = Session.getEpisodeId()
  const { stepId } = useParams()
  const navigate = useNavigate()
  const [isDisabled, setIsDisabled] = useState<boolean>(true)
  const clinicalDataStepId = 0

  useEffect(() => {
    getStepInfo()
  }, [])

  const getStepInfo = () => {
    StepsinfoApi.getStepsInfo(episodeId)
      .then(({ data }) => {
        const StepInfo = data.find((stepInfo) => stepInfo.stepId === clinicalDataStepId)
        setIsDisabled(StepInfo?.stepStatus !== StepStatus.Complete)
      })
      .catch((_) =>
        showErrorNotification(
          "Aktuell konnte der Step Status für die klinischen Daten nicht abgefragt werden. Bitte warten sie mit der Übermittlung ans bwC.",
        ),
      )
  }

  const sendToBwhc = () => {
    BwhcTransferApi.addBwhcTransfer(episodeId)
      .then((response) => {
        showSuccessNotification("Daten werden an den bwHC Knotenpunkt übermittelt")
      })
      .catch(() =>
        showErrorNotification(
          "Bei der Übertragung ist etwas schief gegangen. Bitte versuchen Sie es später erneut.",
        ),
      )

    completeStep()
  }

  const completeStep = () => {
    const stepInfo: StepInfo = {
      episodeId: episodeId,
      stepId: Number(stepId),
      stepStatus: StepStatus.Complete,
    }
    StepsinfoApi.updateStepsInfo(episodeId, [stepInfo])
      .then(() => {
        showSuccessNotification("Daten werden an den bwHC Knotenpunkt übermittelt")
        navigate(`../../`, { relative: "path" })
      })
      .catch((_) =>
        showErrorNotification(
          "Die Änderungen konnten nicht gespeichert werden. Bitte versuchen Sie es später erneut.",
        ),
      )
  }

  return (
    <div
      style={{
        padding: "2rem",
        backgroundColor: theme.palette.primary.light,
        display: "flex",
        justifyContent: "center",
      }}
    >
      <Typography>
        {isDisabled ? (
          <Typography style={{ paddingBottom: 20 }}>
            Bitte prüfen sie die klinischen Daten auf Richtigkeit und setzen sie entsprechend den
            Haken "Ich habe die Bearbeitung von Klinische Daten abgeschlossen.".
          </Typography>
        ) : (
          <Typography style={{ paddingBottom: 20 }}>
            Sie können die Daten nun an bwHC senden.
          </Typography>
        )}
        <Button
          disabled={isDisabled}
          variant={"contained"}
          endIcon={<SendIcon />}
          onClick={sendToBwhc}
          sx={{ margin: "0 auto", display: "flex" }}
        >
          Daten an bwHC übermitteln
        </Button>
      </Typography>
    </div>
  )
}
