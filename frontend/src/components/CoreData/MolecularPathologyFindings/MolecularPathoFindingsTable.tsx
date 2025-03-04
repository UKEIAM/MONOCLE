import "dayjs/locale/de"
import React, { useEffect, useState } from "react"
import { Column, Row, Table } from "components/Table"
import { MolecularPathologyFinding } from "gen/api"
import Session from "hooks/Session"
import { molecularPathologyFindingToString, toGermanDateFormat } from "utils/Formats"
import { Pathology } from "./MolecularPathoFindingsTypes"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type Props = {
  onEdit: (uuid?: string) => void
  molecularPathoFindings?: MolecularPathologyFinding[]
  setMolecularPathoFindings: (molPaFi: MolecularPathologyFinding[]) => void
  specimenMap: { [key: string]: string }
}

export function MolecularPathoFindingsTable({
  onEdit,
  molecularPathoFindings,
  setMolecularPathoFindings,
  specimenMap,
}: Props) {
  const { MolecularPathologyFindingApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<MolecularPathologyFinding>()

  const deleteEntryConfirmation = (item: MolecularPathologyFinding) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  useEffect(() => {
    getMolecularPathoFindingsList()
  }, [])

  const handleDelete = () => {
    const molecularPathoFindingId = itemToBeDeleted?.id
    if (molecularPathoFindingId) {
      MolecularPathologyFindingApi.deleteMolecularPathologyFinding(
        episodeId,
        molecularPathoFindingId,
      )
        .then(() => {
          getMolecularPathoFindingsList()
          setConfirmOpen(false)
          showSuccessNotification("Molekular-Pathologie-Befund wurde erfolgreich gelöscht")
        })
        .catch(() =>
          showErrorNotification(
            "Beim Löschen des Molekular-Pathologie-Befunds ist ein Fehler aufgetreten.",
          ),
        )
    }
  }

  const getMolecularPathoFindingsList = () => {
    MolecularPathologyFindingApi.getAllMolecularPathologyFindings(episodeId).then(
      (molecularPathoFindings) => {
        setMolecularPathoFindings([...molecularPathoFindings.data])
      },
    )
  }

  const getInstitutFromUUID = (institutUUID: string): string => {
    return Pathology.find((x) => x.value === institutUUID)?.label ?? ""
  }

  function toRows() {
    let rowList: Row[] = []
    molecularPathoFindings?.map((molFindings: MolecularPathologyFinding) => {
      rowList = [
        ...rowList,
        {
          onEdit: () => onEdit(molFindings.id),
          onDelete: () => deleteEntryConfirmation(molFindings),
          rowKey: molFindings.id,
          cells: [
            { value: molFindings.specimen },
            {
              value: molFindings.performingInstitute
                ? getInstitutFromUUID(molFindings.performingInstitute)
                : "",
            },
            { value: toGermanDateFormat(molFindings.issuedOn) },
            { value: molFindings.note },
            {
              value: molFindings.typeOfDiagnostic
                ?.filter((item) => item && item.display) // Filter out invalid or undefined items
                .map((item) => item.display) // Extract the display property
                .join(", "), // Join them into a single string with commas
            },
            // {
            //   value: molFindings.typeOfDiagnostic?.code
            //     ? molFindings.typeOfDiagnostic.code +
            //       " (" +
            //       molFindings.typeOfDiagnostic.display +
            //       "); "
            //     : "",
            // },
          ],
        },
      ]
    })
    return rowList
  }

  const columns: Column[] = [
    { label: "Tumorproben", format: (v) => specimenMap[v] },
    { label: "Institut" },
    { label: "Erstellungsdatum" },
    { label: "Notizen" },
    { label: "Art der Diagnostik" },
  ]

  return (
    <>
      <Table
        columns={columns}
        rows={toRows()}
        addRowButton={{
          label: "Neuen Molekular-Pathologie-Befund hinzufügen",
          onClick: () => onEdit(),
        }}
      />
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`${molecularPathologyFindingToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
        />
      )}
    </>
  )
}
