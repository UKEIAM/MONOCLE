import { Column, Row, Table } from "components/Table"
import {
  dosageOptions,
  notDoneReasonOptions,
  realisationOptions,
  reasonStoppedOptions,
  statusOptions,
} from "./MolecularTherapiesTypes"
import React, { useEffect, useState } from "react"
import dayjs from "dayjs"
import { medicationsString, molecularTherapyToString } from "utils/Formats"
import { MolecularTherapy } from "gen/api"
import Session from "hooks/Session"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

export const columns: Column[] = [
  { label: "Erfassungsdatum", format: (v) => dayjs(v).format("DD.MM.YYYY") },
  { label: "Therapie-Empfehlung" },
  { label: "Bemerkung" },
  { label: "Status", format: (v) => statusOptions.find((e) => e.value === v)?.label ?? "" },
  {
    label: "Grund für Nicht-Umsetzung",
    format: (v) => notDoneReasonOptions.find((e) => e.value === v)?.label ?? "",
  },
  { label: "Anfang", format: (v) => (v ? dayjs(v).format("DD.MM.YYYY") : "") },
  { label: "Ende", format: (v) => (v ? dayjs(v).format("DD.MM.YYYY") : "") },
  { label: "Wirkstoffe", format: (v) => medicationsString(v) ?? [] },
  { label: "Dosisdichte", format: (v) => dosageOptions.find((e) => e.value === v)?.label ?? "" },
  {
    label: "Abbruchgrund",
    format: (v) => reasonStoppedOptions.find((e) => e.value === v)?.label ?? "",
  },
  {
    label: "Umsetzung der Therapieempfehlung",
    format: (v) => realisationOptions.find((e) => e.value === v)?.label ?? "",
  },
]

type Probs = {
  onEdit: (uuid?: string) => void
  molecularTherapies?: MolecularTherapy[]
  setMolecularTherapies: (diag: MolecularTherapy[]) => void
  recommendationMap: { [key: string]: string }
}

export default function MolecularTherapiesTable({
  onEdit,
  molecularTherapies,
  setMolecularTherapies,
  recommendationMap,
}: Probs) {
  const episodeId = Session.getEpisodeId()
  const { MolecularTherapyApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<MolecularTherapy>()

  const deleteEntryConfirmation = (item: MolecularTherapy) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  useEffect(() => {
    getMolecuarTherapiesList()
  }, [])

  const getMolecuarTherapiesList = () => {
    MolecularTherapyApi.getAllMolecularTherapies(episodeId).then(({ data: molecularTherapies }) => {
      setMolecularTherapies([...molecularTherapies])
    })
  }

  const handleDelete = () => {
    const molecularTherapyId = itemToBeDeleted?.id
    if (molecularTherapyId) {
      MolecularTherapyApi.deleteMolecularTherapy(episodeId, molecularTherapyId)
        .then(() => {
          getMolecuarTherapiesList()
          setConfirmOpen(false)
          showSuccessNotification("Systemische Therapie wurden erfolgreich gelöscht")
        })
        .catch(() =>
          showErrorNotification(
            "Beim Löschen der Systemischen Therapie ist ein Fehler aufgetreten.",
          ),
        )
    }
  }

  function toRows() {
    let rowList: Row[] = []
    molecularTherapies?.map((molecularTherapy: MolecularTherapy) => {
      rowList = [
        ...rowList,
        {
          onEdit: () => onEdit(molecularTherapy.id),
          onDelete: () => deleteEntryConfirmation(molecularTherapy),
          rowKey: molecularTherapy.id,
          cells: [
            { value: molecularTherapy.recordedOn },
            { value: molecularTherapy.basedOn ? recommendationMap[molecularTherapy.basedOn] : "" },
            { value: molecularTherapy.note },
            { value: molecularTherapy.status },
            { value: molecularTherapy.notDoneReason?.code },
            { value: molecularTherapy.period?.start },
            { value: molecularTherapy.period?.end },
            { value: molecularTherapy.medication },
            { value: molecularTherapy.dosage },
            { value: molecularTherapy.reasonStopped?.code },
            { value: molecularTherapy.realisation },
          ],
        },
      ]
    })
    return rowList
  }

  const listOfReferences: string[] = ["Molekular-Therapie-Befund"]

  return (
    <>
      <Table
        columns={columns}
        rows={toRows()}
        addRowButton={{ label: "Neue Systemische Therapie Hinzufügen", onClick: () => onEdit() }}
      />
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`Systemische Therapie ${molecularTherapyToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
          itemReferences={listOfReferences}
        />
      )}
    </>
  )
}
