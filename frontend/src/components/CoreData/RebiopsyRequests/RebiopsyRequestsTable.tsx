import { Column, Row, Table } from "components/Table"
import { RebiopsyRequest } from "gen/api"
import Session from "hooks/Session"
import React, { useEffect, useState } from "react"
import { rebiopsyRequestToString, toGermanDateFormat } from "utils/Formats"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type Props = {
  onEdit: (uuid?: string) => void
  rebiopsyRequests?: RebiopsyRequest[]
  setRebiopsyRequests: (rebio: RebiopsyRequest[]) => void
  specimenMap: { [key: string]: string }
}

export default function RebiopsyRequestsTable({
  onEdit,
  rebiopsyRequests,
  setRebiopsyRequests,
  specimenMap,
}: Props) {
  const { RebiopsyRequestApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<RebiopsyRequest>()

  const deleteEntryConfirmation = (item: RebiopsyRequest) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  useEffect(() => {
    getRebiopsyList()
  }, [])

  const handleDelete = () => {
    const rebiopsyId = itemToBeDeleted?.id
    if (rebiopsyId) {
      RebiopsyRequestApi.deleteRebiopsyRequest(episodeId, rebiopsyId)
        .then(() => {
          getRebiopsyList()
          setConfirmOpen(false)
          showSuccessNotification("Rebiopsie-Auftrag wurden erfolgreich gelöscht")
        })
        .catch(() =>
          showErrorNotification("Beim Löschen des Rebiopsie-Auftrag ist ein Fehler aufgetreten."),
        )
    }
  }

  const getRebiopsyList = () => {
    RebiopsyRequestApi.getAllRebiopsyRequests(episodeId).then((rebiopsyRequests) => {
      setRebiopsyRequests([...rebiopsyRequests.data])
    })
  }

  function toRows(): Row[] {
    return (
      rebiopsyRequests?.map((rebiopsyRequest: RebiopsyRequest) => {
        return {
          onEdit: () => onEdit(rebiopsyRequest.id),
          onDelete: () => deleteEntryConfirmation(rebiopsyRequest),
          rowKey: rebiopsyRequest.id,
          cells: [{ value: rebiopsyRequest.specimen }, { value: rebiopsyRequest.issuedOn }],
        }
      }) ?? []
    )
  }

  const listOfReferences: string[] = ["Therapieplan"]

  const columns: Column[] = [
    { label: "Tumorproben", format: (v) => (v ? specimenMap[v] : "") },
    { label: "Erstellungsdatum", format: (v) => toGermanDateFormat(v) },
  ]

  return (
    <>
      <Table
        columns={columns}
        rows={toRows()}
        addRowButton={{ label: "Neuen Rebiopsie-Auftrag Hinzufügen", onClick: () => onEdit() }}
      ></Table>
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`Rebiopsie-Auftrag ${rebiopsyRequestToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
          itemReferences={listOfReferences}
        />
      )}
    </>
  )
}
