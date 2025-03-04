import React, { useEffect, useState } from "react"
import Session from "hooks/Session"
import { ClaimResponse } from "gen/api"
import { reasonOptions, statusOptions } from "./ClaimResponsesTypes"
import { claimResponseToString, toGermanDateFormat } from "utils/Formats"
import { Column, Row, Table } from "components/Table"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type Props = {
  onEdit: (claimResponseId?: string) => void
  claimResponses: ClaimResponse[]
  setClaimResponses: (claimResponses: ClaimResponse[]) => void
  claimsMap: Map<string, string>
}

export default function ClaimResponsesTable({
  onEdit,
  claimResponses,
  setClaimResponses,
  claimsMap,
}: Props) {
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const { ClaimResponseApi } = useApi()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<ClaimResponse>()

  const deleteEntryConfirmation = (item: any) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  useEffect(() => {
    getClaimResponses()
  }, [])

  const handleDelete = () => {
    const claimResponseId = itemToBeDeleted?.id
    if (claimResponseId) {
      ClaimResponseApi.deleteClaimResponse(episodeId, claimResponseId)
        .then(() => {
          showSuccessNotification("Kostenübernahme Antwort wurde erfolgreich gelöscht")
          getClaimResponses()
          setConfirmOpen(false)
        })
        .catch(() => {
          showErrorNotification(
            "Beim Löschen der Kostenübernahme Antwort ist ein Fehler aufgetreten.",
          )
        })
    }
  }

  const getClaimResponses = () => {
    ClaimResponseApi.getAllClaimResponses(episodeId)
      .then(({ data: response }) => {
        setClaimResponses(response)
      })
      .catch(() => {
        showErrorNotification("Beim Laden der Kostenübernahme Antwort ist ein Fehler aufgetreten.")
      })
  }

  const rows: Row[] = claimResponses.map((elem) => ({
    onEdit: () => onEdit(elem.id),
    onDelete: () => deleteEntryConfirmation(elem),
    rowKey: elem.id,
    cells: [
      { value: claimsMap.get(elem.claim!) },
      { value: elem.issuedOn },
      { value: elem.status },
      { value: elem.reason },
    ],
  }))

  return (
    <>
      <Table
        columns={columns}
        rows={rows}
        addRowButton={{ label: "Neue Kostenübernahme Antwort Hinzufügen", onClick: () => onEdit() }}
      />
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`${claimResponseToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
        />
      )}
    </>
  )
}

const columns: Column[] = [
  { label: "Antrag" },
  { label: "Antwortdatum", format: toGermanDateFormat },
  {
    label: "Status",
    format: (v) => statusOptions.find((e) => e.value === v)?.label ?? "",
  },
  {
    label: "Grund",
    format: (v) => reasonOptions.find((e) => e.value === v)?.label ?? "",
  },
]
