import { GenderType, Patient } from "gen/api"
import { IconButton, Tooltip, Typography, useTheme } from "@mui/material"
import { Link } from "react-router-dom"
import Session from "hooks/Session"
import { toGermanDateFormat } from "utils/Formats"
import React, { useState } from "react"
import EditIcon from "@mui/icons-material/Edit"
import { PatientEditModal } from "components/Patient/PatientEditModal"
import MoreTimeIcon from "@mui/icons-material/MoreTime"
import { WarningModal } from "components/WarningModal"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"

// FIXME use single source for this and src/components/Gender/GenderSelect
const genders = [
  [GenderType.Male, "männlich"],
  [GenderType.Female, "weiblich"],
  [GenderType.Other, "divers"],
  [GenderType.Unknown, "ohne Angabe"],
]

type Props = {
  patient: Patient
  healthInsurance: string
  cardType: string
  onEdit?: () => void
  onNewEpisode?: () => void
}

export function PatientCard({
  patient,
  healthInsurance,
  cardType = "full",
  onEdit = () => {},
  onNewEpisode = () => {},
}: Props) {
  const theme = useTheme()
  const { EpisodeApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const updateSession = () => {
    Session.setPatientId(patient.id ?? "")
    Session.setEpisodeId(patient.episodes?.at(0)?.id ?? "")
  }
  const [hover, setHover] = useState(false)
  const [isModalOpen, setIsModalOpen] = useState(false)

  const [warningModal, setWarningModal] = useState<boolean>(false)
  const warningModalText =
    "Möchten Sie eine neue Behandlungsepisode für diese:n Patient:in anlegen?"

  const addEpisode = () => {
    if (patient.id) {
      EpisodeApi.addEpisode({ patientId: patient.id, workflowId: 1 })
        .then(() => {
          // FIXME: Static Workflow ID, find solution if multiple Workflows
          showSuccessNotification(
            "Es wurde erfolgreich eine neue Behandlungsepisode für diese/n Patient:in angelegt.",
          )
          setWarningModal(false)
          onNewEpisode()
        })
        .catch(() =>
          showErrorNotification(
            "Es konnte leider keine neue Episode angelegt werden, versuchen Sie es später erneut.",
          ),
        )
    }
  }

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        backgroundColor: cardType === "full" && hover ? theme.palette.primary.light : undefined,
        position: "relative",
        padding: "8px",
        width: "100%",
      }}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
    >
      {cardType === "full" ? (
        <>
          <PatientEditModal
            open={isModalOpen}
            patient={patient}
            onClose={() => {
              setIsModalOpen(false)
              onEdit()
            }}
          />
          <Tooltip title={"Patient:in editieren"}>
            <IconButton
              style={{ position: "absolute", right: "40px", top: "0", margin: "4px" }}
              onClick={() => {
                setIsModalOpen(true)
              }}
            >
              <EditIcon />
            </IconButton>
          </Tooltip>
          <Tooltip title={"Neue Episode anlegen"}>
            <IconButton
              style={{ position: "absolute", right: "0", top: "0", margin: "4px" }}
              onClick={() => {
                setWarningModal(true)
              }}
            >
              <MoreTimeIcon />
            </IconButton>
          </Tooltip>
          <Typography variant="h6" maxWidth={"70%"} noWrap>
            {patient.surname}, {patient.firstName}
          </Typography>
        </>
      ) : (
        <Link
          to={"/patients/" + patient.id}
          onClick={updateSession}
          color={"none"}
          style={{ textDecoration: "none", maxWidth: "100%" }}
        >
          <Typography variant="h6" color={"textPrimary"} noWrap>
            {patient.surname}, {patient.firstName}
          </Typography>
        </Link>
      )}
      {patient.dateOfBirth && (
        <Typography variant="body2" color="textSecondary">
          Geboren am {toGermanDateFormat(patient.dateOfBirth)}
        </Typography>
      )}
      <Typography variant="body2" color="textSecondary">
        PatID: {patient.soarianId}
      </Typography>
      {cardType === "full" && (
        <>
          {patient?.dateOfDeath && (
            <Typography variant="body2" color="textSecondary">
              Verstorben am: {toGermanDateFormat(patient?.dateOfDeath)}
            </Typography>
          )}
          <Typography variant="body2" color="textSecondary">
            Geschlecht: {genders.find((e) => e[0] === patient?.gender)?.[1]}
          </Typography>
          <Typography
            variant="body2"
            color="textSecondary"
            noWrap
            maxWidth={"-webkit-fill-available"} //TODO check if this works for all required browsers
          >
            Krankenkasse: {healthInsurance}
          </Typography>
          <Typography variant="body2" color="textSecondary">
            Gemeindeschlüssel: {patient?.municipalityKey}
          </Typography>

          <WarningModal
            title={"Neue Behandlungsepisode anlegen?"}
            message={warningModalText}
            open={warningModal}
            handleClose={() => setWarningModal(false)}
            submitMethod={addEpisode}
            buttonSubmitText={"Ja, neue Episode anlegen"}
          />
        </>
      )}
    </div>
  )
}
