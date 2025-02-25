import { Column, Row, Table } from "components/Table"
import { useState } from "react"
import {
  medicationToString,
  therapyRecommendationToString,
  toGermanDateFormat,
} from "utils/Formats"
import { Code, TherapyRecommendation } from "gen/api"
import Session from "hooks/Session"
import LevelOfEvidenceDisplay from "../LevelOfEvidence/LevelOfEvidenceDisplay"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type RecomendationTableProps = {
  onEdit: (uuid?: string) => void
  diagnosesOptions: { label: string; value: string }[]
  ngsReportsOptions: { label: string; value: string }[]
  supportingVariantsOptions: { id: string; values: { label: string; value: string }[] }[]
  recommendations: TherapyRecommendation[]
  setRecommendations: (therapyRecommendations: TherapyRecommendation[]) => void
}

export default function RecommendationTable({
  onEdit,
  diagnosesOptions,
  ngsReportsOptions,
  supportingVariantsOptions,
  recommendations,
  setRecommendations,
}: RecomendationTableProps) {
  const { TherapyRecommendationApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<TherapyRecommendation>()

  const deleteEntryConfirmation = (item: TherapyRecommendation) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  const handleDelete = () => {
    const therapyRecommendationId = itemToBeDeleted?.id
    if (therapyRecommendationId) {
      TherapyRecommendationApi.deleteTherapyRecommendation(episodeId, therapyRecommendationId)
        .then(() => {
          showSuccessNotification("Das Löschen der Therapie-Empfehlung war erfolgreich.")
          setConfirmOpen(false)
          TherapyRecommendationApi.getAllTherapyRecommendations(episodeId).then((response) => {
            setRecommendations([...response.data])
          })
        })
        .catch(() =>
          showErrorNotification("Beim Löschen der Therapie-Empfehlung ist ein Fehler aufgetreten."),
        )
    }
  }

  function toRows(): Row[] {
    let rows: Row[] = []
    recommendations?.map((recommandation: TherapyRecommendation) => {
      const elems =
        supportingVariantsOptions.find((entry) => entry.id === recommandation.ngsReport)?.values ||
        []
      rows = [
        ...rows,
        {
          onEdit: () => onEdit(recommandation.id),
          onDelete: () => deleteEntryConfirmation(recommandation),
          rowKey: recommandation.id,
          cells: [
            { value: recommandation.diagnosis },
            { value: recommandation.issuedOn },
            { value: recommandation.medication },
            { value: recommandation.priority },
            { value: <LevelOfEvidenceDisplay levelOfEvidence={recommandation.levelOfEvidence} /> },
            { value: recommandation.ngsReport },
            {
              value: (
                <ul>
                  {recommandation.supportingVariants?.map((sV) => (
                    <li>{elems.find((entry) => entry?.value === sV)?.label}</li>
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
    {
      label: "Diagnose",
      format: (v) => diagnosesOptions.find((item) => item.value === v)?.label ?? "",
    },
    { label: "Erstellungsdatum", format: toGermanDateFormat },
    { label: "Wirkstoffe", format: (v) => v.map((medi: Code) => medicationToString(medi)) },
    { label: "Priorität" },
    { label: "Evidenz Level" },
    {
      label: "NGS Befund",
      format: (v) => ngsReportsOptions.find((item) => item.value === v)?.label ?? "",
    },
    { label: "Stützende molekulare Alteration(en)" },
  ]

  const listOfReferences: string[] = [
    "Therapieplan",
    "Kostenübernahme Antrag",
    "Molekular-Therapie",
  ]

  return (
    <>
      <Table
        columns={columns}
        rows={toRows()}
        addRowButton={{ label: "Neuer Therapie-Empfehlung hinzufügen", onClick: () => onEdit() }}
      />
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`${therapyRecommendationToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
          itemReferences={listOfReferences}
        />
      )}
    </>
  )
}
