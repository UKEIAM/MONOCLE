import React, { useEffect, useState } from "react"
import { DiagnosesTable } from "./DiagnosesTable"
import { DiagnosesDialog } from "./DiagnosesDialog"
import Session from "hooks/Session"
import { Diagnose } from "gen/api"
import { histologyReportToString } from "utils/Formats"
import { TabProps } from "../CoreDataFormTabs"
import { useApi } from "hooks/useApi"

export function Diagnoses({ selected }: TabProps) {
  const { DiagnoseApi, HistologyReportApi } = useApi()
  const [isOpen, setIsOpen] = useState<boolean>(false)
  const [editElement, setEditElement] = useState<Diagnose>()
  const [diagnoses, setDiagnoses] = useState<Diagnose[]>()
  const episodeId = Session.getEpisodeId().toString()
  const [histologyReportMap, setHistologyReportMap] = useState<{ [key: string]: string }>({})

  useEffect(() => {
    // Trigger on selected
    if (selected) {
      fillHistologyReportMap()
    }
  }, [selected])

  useEffect(() => {
    // Trigger if isOpen === true (Dialog opens)
    if (isOpen) fillHistologyReportMap()
  }, [isOpen])

  const fillHistologyReportMap = () => {
    HistologyReportApi.getAllHistologyReports(episodeId).then(({ data: histologyReports }) => {
      let tempHistologyReportMap: { [key: string]: string } = {}
      histologyReports.map((histo) => {
        tempHistologyReportMap[histo.id!] = histologyReportToString(histo)
      })
      setHistologyReportMap(tempHistologyReportMap)
    })
  }

  const handleEdit = (uuid?: string) => {
    if (uuid) {
      DiagnoseApi.getDiagnose(episodeId, uuid).then(({ data }) => {
        setEditElement(data)
        setIsOpen(true)
      })
    } else {
      setEditElement(undefined)
      setIsOpen(true)
    }
  }

  const handleClose = () => {
    setIsOpen(false)
    DiagnoseApi.getAllDiagnoses(episodeId).then(({ data: diagnoses }) => {
      setDiagnoses([...diagnoses])
    })
  }

  return (
    <>
      <DiagnosesTable
        onEdit={handleEdit}
        diagnoses={diagnoses ?? []}
        setDiagnoses={setDiagnoses}
        histologyReportMap={histologyReportMap}
      ></DiagnosesTable>
      <DiagnosesDialog
        open={isOpen}
        onClose={handleClose}
        editElement={editElement}
        histologyReportMap={histologyReportMap}
      ></DiagnosesDialog>
    </>
  )
}
