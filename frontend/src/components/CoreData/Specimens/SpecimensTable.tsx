import "dayjs/locale/de"
import React, { useEffect, useState } from "react"
import {
  specimenLocalizationCodes,
  specimenMethodesCodes,
  specimenTypeCodes,
} from "./SpecimenTypes"
import { Column, Row, Table } from "components/Table"
import Session from "hooks/Session"
import { Specimen } from "gen/api"
import { specimenToString, toGermanDateFormat } from "utils/Formats"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type Props = {
  onAddOrEdit: (uuid?: string) => void
  specimens?: Specimen[]
  setSpecimens: (spec: Specimen[]) => void
}

export function SpecimensTable({ onAddOrEdit, specimens, setSpecimens }: Props) {
  const { SpecimenApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<Specimen>()

  const deleteEntryConfirmation = (item: Specimen) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  useEffect(() => {
    getSpecimenList()
  }, [])

  const handleDelete = () => {
    const specimenId = itemToBeDeleted?.id
    if (specimenId) {
      SpecimenApi.deleteSpecimen(episodeId, specimenId)
        .then(() => {
          getSpecimenList()
          setConfirmOpen(false)
          showSuccessNotification("Die Tumorproben wurden erfolgreich gelöscht")
        })
        .catch(() =>
          showErrorNotification("Beim Löschen der Tumorproben ist ein Fehler aufgetreten."),
        )
    }
  }

  const getSpecimenList = () => {
    SpecimenApi.getAllSpecimens(episodeId).then((specimens) => {
      setSpecimens([...specimens.data])
    })
  }

  const columns: Column[] = [
    { label: "E-/N-Nummer" },
    { label: "ICD-10 Code aus Diagnose" },
    { label: "Tumorproben-Art" },
    { label: "Entnahme" },
  ]

  const rows: Row[] =
    specimens?.map((item: Specimen) => ({
      onEdit: () => onAddOrEdit(item.id),
      onDelete: () => deleteEntryConfirmation(item),
      rowKey: item.id,
      cells: [
        { value: item.labelling ? item.labelling : "Keine E-/N-Nummer vorhanden" },
        {
          value:
            item.icd10?.code !== undefined ? (
              item.icd10?.code
            ) : (
              <strong>Bitte Diagnose auswählen</strong>
            ),
        },
        { value: specimenTypeCodes.find((value) => value.value === item.type)?.label },
        {
          value: (
            <ul>
              {item.collection?.date && (
                <li>{"Datum: " + toGermanDateFormat(item.collection?.date)}</li>
              )}
              {item.collection?.method && (
                <li>
                  {"Methode: " +
                    specimenMethodesCodes.find((value) => value.value === item.collection?.method)
                      ?.label}
                </li>
              )}
              {item.collection?.localization && (
                <li>
                  {"Lokalisation: " +
                    specimenLocalizationCodes.find(
                      (value) => value.value === item.collection?.localization,
                    )?.label}
                </li>
              )}
            </ul>
          ),
        },
      ],
    })) ?? []

  const listOfReferences: string[] = [
    "Histologie-Reevaluations-Auftrag",
    "Histologie-Bericht",
    "Rebiopsie-Auftrag",
    "Molekular-Pathologie-Befund",
    "Ihc-Bericht",
  ]

  return (
    <>
      <Table
        columns={columns}
        rows={rows}
        addRowButton={{ label: "Neue Tumorproben hinzufügen", onClick: () => onAddOrEdit() }}
      />
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`Tumorprobe ${specimenToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
          itemReferences={listOfReferences}
        />
      )}
    </>
  )
}
