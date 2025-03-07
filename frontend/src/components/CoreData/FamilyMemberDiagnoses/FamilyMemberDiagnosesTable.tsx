import { FamilyMemberDiagnosis } from "gen/api"
import { Column, Row, Table } from "components/Table"
import Session from "hooks/Session"
import React, { useEffect, useState } from "react"
import { relationshipCodes } from "./FamilyMemberDiagnosesTypes"
import { familyMemberDiagnosisToString } from "utils/Formats"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type Props = {
  onEdit: (familyMemberDiagnosisId?: string) => void
  familyMemberDiagnoses?: FamilyMemberDiagnosis[]
  setFamilyMemberDiagnoses: (familyMemberDiagnoses: FamilyMemberDiagnosis[]) => void
}

export function FamilyMemberDiagnosesTable({
  onEdit,
  familyMemberDiagnoses,
  setFamilyMemberDiagnoses,
}: Props) {
  const { FamilyMemberDiagnosisApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<FamilyMemberDiagnosis>()

  const deleteEntryConfirmation = (item: any) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  useEffect(() => {
    getFamilyMemberDiagnosisList()
  }, []) // eslint-disable-line react-hooks/exhaustive-deps -- FIXME potential misuse of useEffect to initialize?

  const handleDelete = () => {
    const familyMemberDiagnosisId = itemToBeDeleted?.id
    if (familyMemberDiagnosisId) {
      FamilyMemberDiagnosisApi.deleteFamilyMemberDiagnosis(episodeId, familyMemberDiagnosisId)
        .then(() => {
          showSuccessNotification("Familienanamnese wurden erfolgreich gelöscht")
          getFamilyMemberDiagnosisList()
          setConfirmOpen(false)
        })
        .catch(() => {
          showErrorNotification("Beim Löschen der Familienanamnese ist ein Fehler aufgetreten.")
        })
    }
  }

  const getFamilyMemberDiagnosisList = () => {
    FamilyMemberDiagnosisApi.getAllFamilyMemberDiagnosis(episodeId)
      .then(({ data }) => {
        setFamilyMemberDiagnoses(data)
      })
      .catch(() => {
        showErrorNotification("Beim Laden der Familienanamnesen ist ein Fehler aufgetreten.")
      })
  }

  const rows: Row[] =
    familyMemberDiagnoses?.map((elem) => ({
      onEdit: () => onEdit(elem.id),
      onDelete: () => deleteEntryConfirmation(elem),
      rowKey: elem.id,
      cells: [
        {
          value:
            relationshipCodes.find((value) => value.value === elem.relationship?.code)?.label ?? "",
        },
        { value: elem.details },
      ],
    })) ?? []

  const columns: Column[] = [{ label: "Verwandschaftsgrad" }, { label: "Details" }]

  return (
    <>
      <Table
        columns={columns}
        rows={rows}
        addRowButton={{ label: "Neue Familienanamnese hinzufügen", onClick: () => onEdit() }}
      />
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`Familienanamese ${familyMemberDiagnosisToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
        />
      )}
    </>
  )
}
