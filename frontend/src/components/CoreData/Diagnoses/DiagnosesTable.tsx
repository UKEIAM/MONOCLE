import "dayjs/locale/de"
import React, { useEffect, useState } from "react"
import { Column, Row, Table } from "components/Table"
import dayjs from "dayjs"
import { Diagnose } from "gen/api"
import Session from "hooks/Session"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"
import { codeToString, diagnosesToString, toGermanDateFormat } from "utils/Formats"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import {
  guidelineTreatmentStatusGermanTranslation,
  statusHistoryGermanTranslation,
} from "./DiagnosesTypes"

type Probs = {
  onEdit: (uuid?: string) => void
  diagnoses?: Diagnose[]
  setDiagnoses: (diag: Diagnose[]) => void
  histologyReportMap: { [key: string]: string }
}

export function DiagnosesTable({ onEdit, diagnoses, setDiagnoses, histologyReportMap }: Probs) {
  const { showErrorNotification, showSuccessNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<Diagnose>()
  const { DiagnoseApi } = useApi()

  const deleteEntryConfirmation = (item: any) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  useEffect(() => {
    getDiagosesList()
  }, [])

  const handleDelete = () => {
    if (itemToBeDeleted?.id)
      DiagnoseApi.deleteDiagnose(episodeId, itemToBeDeleted.id)
        .then(() => {
          getDiagosesList()
          setConfirmOpen(false)
          showSuccessNotification("Diagnose wurde erfolgreich gelöscht.")
        })
        .catch(() => showErrorNotification("Beim Löschen der Diagnose ist ein Fehler aufgetreten."))
  }

  const getDiagosesList = () => {
    DiagnoseApi.getAllDiagnoses(episodeId).then((diagnoses) => {
      setDiagnoses([...diagnoses.data])
    })
  }

  const tumorKeysToString = (diagnosis: Diagnose) => {
    if (diagnosis.tnmKey) {
      diagnosis.tnmKey.code =
        diagnosis.tnmKey?.code === "unknown" ? "Unbekannt" : diagnosis.tnmKey?.code
      diagnosis.tnmKey.system =
        diagnosis.tnmKey?.system === "unknown" ? "Unbekannt" : diagnosis.tnmKey?.system
    }
    return (
      <>
        {diagnosis.tnmKey?.code && <>{codeToString(diagnosis.tnmKey)}</>}

        {diagnosis.altTumorKey && diagnosis.altTumorKey.length > 0 ? (
          <ul>
            {diagnosis.altTumorKey.map((item, index) => {
              return item?.code ? <li key={index}>{codeToString(item)}</li> : null
            })}
          </ul>
        ) : null}
      </>
    )
  }

  function toRows() {
    let rowList: Row[] = []
    diagnoses?.map((diagnosis: Diagnose) => {
      rowList = [
        ...rowList,
        {
          onEdit: () => onEdit(diagnosis.id),
          onDelete: () => deleteEntryConfirmation(diagnosis),
          rowKey: diagnosis.id,
          cells: [
            { value: diagnosis.recordedOn ? dayjs(diagnosis.recordedOn).format("DD.MM.YYYY") : "" },
            { value: codeToString(diagnosis.icd10) },
            {
              value: codeToString(diagnosis.icdO3T),
            },
            {
              value: diagnosis.whoGrade?.code
                ? diagnosis.whoGrade?.code + " (" + diagnosis.whoGrade?.version + "); "
                : "",
            },
            { value: diagnosis.histologyResults },
            {
              value: (
                <ul>
                  {diagnosis.statusHistory?.map((item, index: number) => {
                    return item !== undefined && item.status ? (
                      <li key={index}>
                        {statusHistoryGermanTranslation[item.status] +
                          " am " +
                          toGermanDateFormat(item.date) +
                          ", "}
                      </li>
                    ) : (
                      ""
                    )
                  })}
                </ul>
              ),
            },
            {
              value: diagnosis.guidelineTreatmentStatus
                ? guidelineTreatmentStatusGermanTranslation[diagnosis.guidelineTreatmentStatus]
                : "",
            },
            {
              value:
                diagnosis.isGermlineDiagnosisExist && diagnosis.germlineDiagnosisIcd10
                  ? codeToString(diagnosis.germlineDiagnosisIcd10)
                  : "",
            },
            {
              value: codeToString(diagnosis.orphanetCode),
            },
            {
              value: diagnosis.alphaIdSeCode?.code,
            },
            {
              value: (
                <ul>
                  {diagnosis.hpoIcd10?.map((item, index: number) => {
                    return item !== undefined && item.code ? (
                      <li key={index}>{codeToString(item)}</li>
                    ) : (
                      ""
                    )
                  })}
                </ul>
              ),
            },
            {
              value: tumorKeysToString(diagnosis),
            },
          ],
        },
      ]
    })
    return rowList
  }

  const columns: Column[] = [
    { label: "Erstdiagnosedatum" },
    { label: "ICD-10" },
    { label: "ICD-O-3-T" },
    { label: "WHO-Grad ZNS" },
    {
      label: "Histologie-Berichte",
      format: (v) => v.map((item: string) => histologyReportMap[item] + ", "),
    },
    { label: "Tumorausbreitung" },
    { label: "Leitlinienbehandlung-Status" },
    { label: "Keimbahndiagnose" },
    { label: "Orphanet" },
    { label: "Alpha-ID-SE" },
    { label: "HPO" },
    { label: "Tumorstadium" },
  ]

  const listOfReferences: string[] = [
    "Therapieplan",
    "Molekular-Therapie-Befund",
    "Studien-Einschluss-Empfehlung",
    "Leitlienien-Therapie",
  ]

  return (
    <>
      <Table
        columns={columns}
        rows={toRows()}
        addRowButton={{ label: "Neue Diagnose Hinzufügen", onClick: () => onEdit() }}
      ></Table>
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          isOpen={confirmOpen}
          itemNameAndDetails={`Diagnose: ${diagnosesToString(itemToBeDeleted)}`}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
          itemReferences={listOfReferences}
        ></DeleteConfirmationDialog>
      )}
    </>
  )
}
