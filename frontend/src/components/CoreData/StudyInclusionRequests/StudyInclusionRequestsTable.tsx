import { Column, Row, Table } from "components/Table"
import { useEffect, useState } from "react"
import { StudyInclusionRequest } from "gen/api"
import Session from "hooks/Session"
import { studyInclusionRequestToString, toGermanDateFormat } from "utils/Formats"
import LevelOfEvidenceDisplay from "../LevelOfEvidence/LevelOfEvidenceDisplay"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type Probs = {
  onEdit: (uuid?: string) => void
  studyInclusionRequests: StudyInclusionRequest[]
  ngsReportsOptions: { label: string; value: string }[]
  supportingVariantsOptions: { id: string; values: { label: string; value: string }[] }[]
  setStudyInclusionRequests: (list: StudyInclusionRequest[]) => void
  diagnosesMap: { label: string; value: string }[]
}

export function StudyInclusionRequestsTable({
  onEdit,
  studyInclusionRequests,
  ngsReportsOptions,
  supportingVariantsOptions,
  setStudyInclusionRequests,
  diagnosesMap,
}: Probs) {
  const { StudyInclusionRequestApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<StudyInclusionRequest>()

  const deleteEntryConfirmation = (item: StudyInclusionRequest) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  useEffect(() => {}, [diagnosesMap])

  const handleDelete = () => {
    const studyInclusionRequestId = itemToBeDeleted?.id
    if (studyInclusionRequestId) {
      StudyInclusionRequestApi.deleteStudyInclusionRequest(episodeId, studyInclusionRequestId)
        .then(() => {
          setConfirmOpen(false)
          showSuccessNotification("Die Studien-Einschluss-Empfehlung wurde erfolgreich gelöscht.")
          getStudyInclusionRequestList()
        })
        .catch(() =>
          showErrorNotification(
            "Beim Löschen der Studien-Einschluss-Empfehlung ist ein Fehler aufgetreten.",
          ),
        )
    }
  }

  const getStudyInclusionRequestList = () => {
    StudyInclusionRequestApi.getAllStudyInclusionRequests(episodeId).then(({ data: response }) => {
      setStudyInclusionRequests([...response])
    })
  }

  const toRows = (): Row[] => {
    let rows: Row[] = []
    studyInclusionRequests?.map((studyInclusionRequest: StudyInclusionRequest) => {
      const elems =
        supportingVariantsOptions.find((entry) =>
          studyInclusionRequest.ngsReports?.includes(entry.id),
        )?.values || []
      rows = [
        ...rows,
        {
          onEdit: () => {
            onEdit(studyInclusionRequest.id)
          },
          onDelete: () => deleteEntryConfirmation(studyInclusionRequest),
          rowKey: studyInclusionRequest.id,
          cells: [
            { value: studyInclusionRequest.reason },
            { value: studyInclusionRequest.ngsReports },
            {
              value: (
                <ul>
                  {studyInclusionRequest.supportingVariants?.map((sV, index: number) => (
                    <li key={index}>{elems.find((entry) => entry?.value === sV)?.label}</li>
                  ))}
                </ul>
              ),
            },
            {
              value: (
                <LevelOfEvidenceDisplay levelOfEvidence={studyInclusionRequest.levelOfEvidence} />
              ),
            },
            {
              value: (
                <ul>
                  {studyInclusionRequest.studies?.map((item, index: number) =>
                    item !== undefined && item.system ? (
                      <li key={index}>{item.system + ":" + item.value}</li>
                    ) : (
                      ""
                    ),
                  )}
                </ul>
              ),
            },
            { value: studyInclusionRequest.issuedOn },
          ],
        },
      ]
    })
    return rows
  }

  const columns: Column[] = [
    {
      label: "Diagnose",
      format: (v) => diagnosesMap.find((diagnose) => diagnose.value === v)?.label ?? "",
    },
    {
      label: "NGS Befund",
      format: (v) => {
        if (!v) return ""
        if (Array.isArray(v)) {
          return (
            v.map((id) => ngsReportsOptions.find((item) => item.value === id)?.label).join(", ") ??
            ""
          )
        }
        return ngsReportsOptions.find((item) => item.value === v)?.label ?? ""
      },
    },
    { label: "Stützende molekulare Alteration(en)" },
    { label: "Evidenz-Level-Objekt" },
    { label: "Studien Nummern" },
    { label: "Erstellungsdatum", format: (v) => toGermanDateFormat(v) },
  ]

  const listOfReferences: string[] = ["Therapieplan"]

  return (
    <>
      <Table
        columns={columns}
        rows={toRows()}
        addRowButton={{
          label: "Neue Studien-Einschluss-Empfehlung Hinzufügen",
          onClick: () => onEdit(),
        }}
      ></Table>
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`Studien-Einschluss-Empfehlung ${studyInclusionRequestToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
          itemReferences={listOfReferences}
        />
      )}
    </>
  )
}
