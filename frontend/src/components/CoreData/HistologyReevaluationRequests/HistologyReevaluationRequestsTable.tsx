import { Column, Row, Table } from "components/Table"
import { HistologyReevaluationRequest } from "gen/api"
import Session from "hooks/Session"
import { histologyReevaluationRequestToString, toGermanDateFormat } from "utils/Formats"
import React, { useEffect, useState } from "react"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type Props = {
  onEdit: (uuid?: string) => void
  histologyReevaluationRequests?: HistologyReevaluationRequest[]
  setHistologyReevaluationRequests: (histo: HistologyReevaluationRequest[]) => void
  specimenMap: { [key: string]: string }
}

export default function HistologyReevaluationRequestsTable({
  onEdit,
  histologyReevaluationRequests,
  setHistologyReevaluationRequests,
  specimenMap,
}: Props) {
  const { HistologyReevaluationRequestApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<HistologyReevaluationRequest>()

  const deleteEntryConfirmation = (item: HistologyReevaluationRequest) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  useEffect(() => {
    getHistologyReevaluationsRequestList()
  }, [])

  const handleDelete = () => {
    const histologyReevaluationRequestId = itemToBeDeleted?.id

    if (histologyReevaluationRequestId) {
      HistologyReevaluationRequestApi.deleteHistologyReevaluationRequest(
        episodeId,
        histologyReevaluationRequestId,
      )
        .then(() => {
          getHistologyReevaluationsRequestList()
          setConfirmOpen(false)
          showSuccessNotification(
            "Der Histologie-Reevaluations-Auftrag wurde erfolgreich gelöscht.",
          )
        })
        .catch(() =>
          showErrorNotification(
            "Beim Löschen des Histologie-Reevaluations-Auftrags ist ein Fehler aufgetreten.",
          ),
        )
    }
  }

  const getHistologyReevaluationsRequestList = () => {
    HistologyReevaluationRequestApi.getAllHistologyReevaluationRequests(episodeId)
      .then(({ data }) => {
        setHistologyReevaluationRequests([...data])
      })
      .catch(() => {
      })
  }

  function toRows(): Row[] {
    return (
      histologyReevaluationRequests?.map(
        (histologyReevaluationRequest: HistologyReevaluationRequest) => {
          return {
            onEdit: () => onEdit(histologyReevaluationRequest.id),
            onDelete: () => deleteEntryConfirmation(histologyReevaluationRequest),
            rowKey: histologyReevaluationRequest.id,
            cells: [
              { value: histologyReevaluationRequest.specimen },
              { value: histologyReevaluationRequest.issuedOn },
            ],
          }
        },
      ) ?? []
    )
  }

  const columns: Column[] = [
    { label: "Tumorproben", format: (v) => specimenMap[v] },
    { label: "Erstellungsdatum", format: (v) => toGermanDateFormat(v) },
  ]

  return (
    <>
      <Table
        columns={columns}
        rows={toRows()}
        addRowButton={{
          label: "Neuen Histologie-Reevaluations-Auftrag Hinzufügen",
          onClick: () => onEdit(),
        }}
      ></Table>
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`${histologyReevaluationRequestToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
        />
      )}
    </>
  )
}
