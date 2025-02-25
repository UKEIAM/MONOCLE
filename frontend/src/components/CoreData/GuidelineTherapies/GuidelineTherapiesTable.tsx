import {
  intensionOptions,
  procedurePositionOptions,
  reasonStoppedOptions,
} from "./GuidelineTherapiesTypes"
import React, { useState } from "react"
import { Column, Row, Table } from "components/Table"
import { Code, GuidelineTherapy } from "gen/api"
import Session from "hooks/Session"
import {
  guidelineTherapyProcedureToString,
  guidelineTherapyToString,
  medicationsString,
  toGermanDateFormat,
} from "utils/Formats"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type Props = {
  onEdit: (uuid?: string) => void
  guidelineTherapies?: GuidelineTherapy[]
  getAllGuidelineTherapies: () => void
  diagnosisOptions: { label: string; value: string }[]
  molecularTherapyResponse: { label: string; value: string }[]
}

export function GuidelineTherapiesTable({
  onEdit,
  guidelineTherapies,
  getAllGuidelineTherapies,
  diagnosisOptions,
  molecularTherapyResponse,
}: Props) {
  const { GuidelineTherapyApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<GuidelineTherapy>()

  const deleteEntryConfirmation = (item: GuidelineTherapy) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  const handleDelete = () => {
    const guidelineTherapieId = itemToBeDeleted?.id
    // Remove
    if (guidelineTherapieId) {
      GuidelineTherapyApi.deleteGuidelineTherapy(episodeId, guidelineTherapieId)
        .then(() => {
          getAllGuidelineTherapies()
          setConfirmOpen(false)
          showSuccessNotification("Leitlinien-Therapie wurden erfolgreich gelöscht")
        })
        .catch(() =>
          showErrorNotification("Beim Löschen der Leitlinien-Therapie ist ein Fehler aufgetreten."),
        )
    }
  }

  function toRows(): Row[] {
    let rowList: Row[] = []
    guidelineTherapies?.map((guidelineTherapy: GuidelineTherapy) => {
      rowList = [
        ...rowList,
        {
          onEdit: () => onEdit(guidelineTherapy.id),
          onDelete: () => deleteEntryConfirmation(guidelineTherapy),
          rowKey: guidelineTherapy.id,
          cells: [
            { value: guidelineTherapy.diagnosis },
            { value: guidelineTherapy.therapyLine },
            {
              value:
                guidelineTherapy.period?.start && guidelineTherapy.period.end
                  ? toGermanDateFormat(guidelineTherapy.period.start) +
                    " - " +
                    toGermanDateFormat(guidelineTherapy.period.end)
                  : guidelineTherapy.period?.start
                    ? toGermanDateFormat(guidelineTherapy.period.start)
                    : "",
            },
            { value: guidelineTherapy.intention },
            { value: toGermanDateFormat(guidelineTherapy.progressDate) },
            { value: guidelineTherapy.molecularTherapyResponse },
            { value: guidelineTherapy.reasonStopped?.code },
            { value: guidelineTherapy.medication },
            {
              value: guidelineTherapy.procedure
                ? guidelineTherapyProcedureToString(guidelineTherapy.procedure as Code) +
                  ", " +
                  procedurePositionOptions.find(
                    (p) => p.value === guidelineTherapy.procedurePosition,
                  )?.label
                : "",
            },
          ],
        },
      ]
    })
    return rowList
  }

  const columns: Column[] = [
    {
      label: "Diagnose",
      format: (v) => diagnosisOptions.find((elem) => elem.value === v)?.label ?? " ",
    }, // FIXME Using " " instead of "" because of bug in components/Form/Table
    { label: "Therapielinie" },
    { label: "Periode" },
    {
      label: "Intention",
      format: (v) => intensionOptions.find((elem) => elem.value === v)?.label ?? " ",
    },
    { label: "Datum Progress" },
    {
      label: "Therapyansprechen",
      format: (v) => molecularTherapyResponse.find((elem) => elem.value === v)?.label ?? " ",
    },
    {
      label: "Grund für Therapieende",
      format: (v) => reasonStoppedOptions.find((elem) => elem.value === v)?.label ?? " ",
    },
    { label: "Medikation", format: medicationsString },
    { label: "Procedure" },
  ]

  const listOfReferences: string[] = ["Therapieplan"]

  return (
    <>
      <Table
        columns={columns}
        rows={toRows()}
        addRowButton={{
          label: "Neue Vorherige Leitlinien-Therapie Hinzufügen",
          onClick: () => onEdit(),
        }}
      />
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`${guidelineTherapyToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
          itemReferences={listOfReferences}
        />
      )}
    </>
  )
}
