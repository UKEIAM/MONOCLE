import React, { useEffect, useState } from "react"
import Session from "hooks/Session"
import { Claim } from "gen/api"
import { claimToString, toGermanDateFormat } from "utils/Formats"
import { Column, Row, Table } from "components/Table"
import { stage } from "./ClaimsTypes"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type Props = {
  onEdit: (claimId?: string) => void
  claims: Claim[]
  setClaims: (claims: Claim[]) => void
  therapyRecommendationsMap: Map<string, string>
}

export function ClaimsTable({ onEdit, claims, setClaims, therapyRecommendationsMap }: Props) {
  const { ClaimApi } = useApi()
  const { showErrorNotification, showSuccessNotification } = useNotification()
  const episodeId = Session.getEpisodeId()

  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<Claim>()

  const deleteEntryConfirmation = (item: any) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  useEffect(() => {
    getClaims()
  }, [])

  const getClaims = () => {
    ClaimApi.getAllClaims(episodeId)
      .then(({ data }) => {
        setClaims(data)
      })
      .catch(() => {
        showErrorNotification("Beim Laden des Kostenübernahme Antrags ist ein Fehler aufgetreten.")
      })
  }

  const handleDelete = () => {
    const claimId = itemToBeDeleted?.id
    if (claimId) {
      ClaimApi.deleteClaim(episodeId, claimId)
        .then(() => {
          showSuccessNotification("Kostenübernahme Antrag wurden erfolgreich gelöscht")
          getClaims()
          setConfirmOpen(false)
        })
        .catch(() => {
          showErrorNotification(
            "Beim Löschen des Kostenübernahme Antrags ist ein Fehler aufgetreten.",
          )
        })
    }
  }

  const rows: Row[] =
    claims?.map((elem) => ({
      onEdit: () => onEdit(elem.id),
      onDelete: () => deleteEntryConfirmation(elem),
      rowKey: elem.id,
      cells: [
        { value: therapyRecommendationsMap.get(elem.therapy!) },
        { value: elem.issuedOn },
        { value: elem.isClaimViaZpmOffice },
        { value: elem.stage?.code },
      ],
    })) ?? []

  const listOfReferences: string[] = ["Kostenübernahme Antwort"]

  return (
    <>
      <Table
        columns={columns}
        rows={rows}
        addRowButton={{ label: "Neuen Kostenübernahme Antrag Hinzufügen", onClick: () => onEdit() }}
      />
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`${claimToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
          itemReferences={listOfReferences}
        />
      )}
    </>
  )
}

export const columns: Column[] = [
  { formField: "therapy", label: "Therapie-Empfehlung" },
  { formField: "issuedOn", label: "Antragsdatum", format: toGermanDateFormat },
  { label: "Antragstellung über ZPM-Geschäftsstelle", format: (v) => (v ? "Ja" : "Nein") },
  {
    label: "Antragsstadium",
    format: (v) => stage.find((s) => s.value == v)?.label ?? "",
  },
]
