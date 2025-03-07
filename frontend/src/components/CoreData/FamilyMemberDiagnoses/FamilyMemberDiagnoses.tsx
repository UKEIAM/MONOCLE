import React, { useState } from "react"
import { FamilyMemberDiagnosesTable } from "./FamilyMemberDiagnosesTable"
import { FamilyMemberDiagnosesDialog } from "./FamilyMemberDiagnosesDialog"
import { FamilyMemberDiagnosis } from "gen/api"
import Session from "hooks/Session"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

// FIXME Reusable behaviour between tabs -> reusable Tab/TableWithDialog Component?
export function FamilyMemberDiagnoses() {
  const { FamilyMemberDiagnosisApi } = useApi()
  const { showErrorNotification } = useNotification()
  const [isOpen, setIsOpen] = useState<boolean>(false)
  const [editElement, setEditElement] = useState<FamilyMemberDiagnosis>()
  const [familyMemberDiagnoses, setFamilyMemberDiagnoses] = useState<FamilyMemberDiagnosis[]>([])
  const episodeId = Session.getEpisodeId()

  // handlers
  const handleEdit = (familyMemberDiagnosisId?: string) => {
    if (familyMemberDiagnosisId !== undefined) {
      FamilyMemberDiagnosisApi.getFamilyMemberDiagnosis(episodeId, familyMemberDiagnosisId)
        .then(({ data }) => {
          setEditElement(data)
          setIsOpen(true)
        })
        .catch(() => {
          showErrorNotification("Beim Laden der Familienanamnese ist ein Fehler aufgetreten.")
        })
    } else {
      setEditElement(undefined)
      setIsOpen(true)
    }
  }

  // FIXME Aborting add/edit triggers reload of table
  const handleClose = () => {
    setIsOpen(false)
    // FIXME Duplicate code of FamilyMemberDiagnosesTable.getFamilyMemberDiagnosisList
    FamilyMemberDiagnosisApi.getAllFamilyMemberDiagnosis(episodeId)
      .then(({ data }) => {
        setFamilyMemberDiagnoses(data)
      })
      .catch(() => {
        showErrorNotification("Beim Laden der Familienanamnesen ist ein Fehler aufgetreten.")
      })
  }

  return (
    <>
      <FamilyMemberDiagnosesTable
        onEdit={handleEdit}
        familyMemberDiagnoses={familyMemberDiagnoses}
        setFamilyMemberDiagnoses={setFamilyMemberDiagnoses}
      />
      <FamilyMemberDiagnosesDialog open={isOpen} onClose={handleClose} editElement={editElement} />
    </>
  )
}
