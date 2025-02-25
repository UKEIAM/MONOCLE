import { Column, Row, Table } from "components/Table"
import { CarePlan } from "gen/api"
import { carePlanToString, toGermanDateFormat } from "utils/Formats"
import Session from "hooks/Session"
import React, { useState } from "react"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type Props = {
  onEdit: (uuid?: string) => void
  carePlans: CarePlan[]
  updateTable: () => void
  diagnosesMap: { [key: string]: string }
  rebiopsyMap: { [key: string]: string }
  studyInclusionMap: { [key: string]: string }
  therapyRecommendationMap: { [key: string]: string }
}

export function CarePlansTable({
  onEdit,
  carePlans,
  updateTable,
  diagnosesMap,
  rebiopsyMap,
  therapyRecommendationMap,
  studyInclusionMap,
}: Props) {
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const { CarePlanApi } = useApi()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<CarePlan>()

  const deleteEntryConfirmation = (item: CarePlan) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  const handleDelete = () => {
    const carePlanId = itemToBeDeleted?.id
    if (carePlanId) {
      CarePlanApi.deleteCarePlan(episodeId, carePlanId!)
        .then(() => {
          showSuccessNotification("Der Therapieplan wurde erfolgreich gelöscht.")
          updateTable()
          setConfirmOpen(false)
        })
        .catch(() =>
          showErrorNotification("Beim Löschen des Therapieplans ist ein Fehler aufgetreten."),
        )
    }
  }

  function toRows(): Row[] {
    let rows: Row[] = []
    carePlans?.map((carePlan: CarePlan) => {
      rows = [
        ...rows,
        {
          onEdit: () => onEdit(carePlan.id),
          onDelete: () => deleteEntryConfirmation(carePlan),
          rowKey: carePlan.id,
          cells: [
            { value: carePlan.diagnosis ? diagnosesMap[carePlan.diagnosis] : "" },
            { value: carePlan.issuedOn },
            { value: carePlan.description },
            {
              value: (
                <ul>
                  {carePlan.noTargetFinding === null || carePlan.statusReason === null ? (
                    carePlan.recommendations?.map((recommendationUUID: string, index: number) => (
                      <li key={index}>{therapyRecommendationMap[recommendationUUID]}</li>
                    ))
                  ) : (
                    <li key={"not-target"}>{"Keine Therapeutische Konsequenz"}</li>
                  )}
                </ul>
              ),
            },
            { value: carePlan.geneticCounsellingRequest?.reason?.display },
            { value: carePlan.geneticCounsellingRequest?.issuedOn },
            {
              value: (
                <ul>
                  {carePlan.rebiopsyRequests?.map((rebiopsyRequest, index: number) => (
                    <li key={index}>{rebiopsyMap[rebiopsyRequest]}</li>
                  ))}
                </ul>
              ),
            },
            {
              value: (
                <ul>
                  {carePlan.studyInclusionRequests?.map((studyInclusionRequest, index: number) => (
                    <li key={index}>{studyInclusionMap[studyInclusionRequest]}</li>
                  ))}
                </ul>
              ),
            },
          ],
        },
      ]
    })
    return rows
  }

  const columns: Column[] = [
    { label: "Diagnose" },
    { label: "Erstellungsdatum", format: toGermanDateFormat },
    { label: "Protokollauszug" },
    { label: "Therapie-Empfehlungen" },
    { label: "Auftrag Human-genetische Beratung Begründung" },
    { label: "Auftrag Human-genetische Beratung Datum", format: toGermanDateFormat },
    { label: "Rebiopsie-Auftrag" },
    { label: "Studien-Einschluss-Empfehlung" },
  ]

  return (
    <>
      <Table
        columns={columns}
        rows={toRows()}
        addRowButton={{ label: "Neuer Therapieplan hinzufügen", onClick: () => onEdit() }}
      />
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`${carePlanToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
        />
      )}
    </>
  )
}
