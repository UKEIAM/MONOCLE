import { Column, Row, Table } from "components/Table"
import { methodOptions, recistOptions } from "./TherapyResponsesTypes"
import React, { useEffect, useState } from "react"
import { HistologyReport, MolecularTherapyResponse } from "gen/api"
import Session from "hooks/Session"
import { TherapyResponseToString, toGermanDateFormat } from "utils/Formats"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type TherapyResponsesTableProps = {
  onEdit: (uuid?: string) => void
  therapyResponses?: HistologyReport[]
  setTherapyResponses: (histo: HistologyReport[]) => void
  molecularTherapiesMap: { [key: string]: string }
}

export default function TherapyResponsesTable({
  onEdit,
  therapyResponses,
  setTherapyResponses,
  molecularTherapiesMap,
}: TherapyResponsesTableProps) {
  const { MolecularTherapyResponseApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<MolecularTherapyResponse>()

  const deleteEntryConfirmation = (item: MolecularTherapyResponse) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  useEffect(() => {
    getHistologyReportsList()
  }, [])

  const getHistologyReportsList = () => {
    MolecularTherapyResponseApi.getAllMolecularTherapyResponses(episodeId).then(
      ({ data: molecularTherapyResponse }) => {
        setTherapyResponses([...molecularTherapyResponse])
      },
    )
  }

  const deleteEntry = () => {
    const molecularTherapyResponseId = itemToBeDeleted?.id
    if (molecularTherapyResponseId) {
      MolecularTherapyResponseApi.deleteMolecularTherapyResponse(
        episodeId,
        molecularTherapyResponseId,
      )
        .then(() => {
          getHistologyReportsList()
          setConfirmOpen(false)
          showSuccessNotification("Molekular-Therapie-Befund wurde erfolgreich gelöscht")
        })
        .catch(() =>
          showErrorNotification(
            "Beim Löschen des Molekular-Therapie-Befunds ist ein Fehler aufgetreten.",
          ),
        )
    }
  }

  function toRows() {
    let rowList: Row[] = []

    therapyResponses?.map((therapyResponse: MolecularTherapyResponse) => {
      rowList = [
        ...rowList,
        {
          onEdit: () => onEdit(therapyResponse.id),
          onDelete: () => deleteEntryConfirmation(therapyResponse),
          rowKey: therapyResponse.id,
          cells: [
            {
              value: therapyResponse.therapy ? molecularTherapiesMap[therapyResponse.therapy] : "",
            },
            { value: therapyResponse.effectiveDate },
            { value: therapyResponse.value?.code },
            { value: therapyResponse.method?.code },
          ],
        },
      ]
    })
    return rowList
  }

  const columns: Column[] = [
    { label: "Systemische Therapie" },
    { label: "Zeitpunkt", format: (v) => toGermanDateFormat(v) },
    { label: "Wert", format: (v) => recistOptions.find((e) => e.value === v)?.label ?? "" },
    {
      label: "Beurteilungs-Methode",
      format: (value) => methodOptions.find((elem) => elem.value === value)?.label ?? "",
    },
  ]

  return (
    <>
      <Table
        columns={columns}
        rows={toRows()}
        addRowButton={{
          label: "Neuen Molekular-Therapie-Befund Hinzufügen",
          onClick: () => onEdit(),
        }}
      />
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`${TherapyResponseToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={deleteEntry}
        />
      )}
    </>
  )
}
