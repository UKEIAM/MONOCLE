import { useEffect, useState } from "react"
import { HistologyReportsTable } from "./HistologyReportsTable"
import { HistologyReportsDialog } from "./HistologyReportsDialog"
import Session from "hooks/Session"
import { HistologyReport } from "gen/api"
import { specimenToString } from "utils/Formats"
import { TabProps } from "../CoreDataFormTabs"
import { useApi } from "hooks/useApi"

export function HistologyReports({ selected }: TabProps) {
  const { SpecimenApi, HistologyReportApi } = useApi()
  const [editElement, setEditElement] = useState<HistologyReport>()
  const [histologyReports, setHistologyReports] = useState<HistologyReport[]>()
  const [specimenMap, setSpecimenMap] = useState<{ [key: string]: string }>({})
  const [isOpen, setIsOpen] = useState<boolean>(false)

  const episodeId = Session.getEpisodeId().toString()

  useEffect(() => {
    // Trigger on selected tab
    if (selected) {
      fillSpecimenMap()
    }
  }, [selected])

  useEffect(() => {
    // Trigger if isOpen === true (Dialog opens)
    if (isOpen) fillSpecimenMap()
  }, [isOpen])

  const fillSpecimenMap = () => {
    SpecimenApi.getAllSpecimens(episodeId).then((specimens) => {
      let tempSpecimenMap: { [key: string]: string } = {}
      specimens.data.map((speci) => {
        tempSpecimenMap[speci.id!] = specimenToString(speci)
      })
      setSpecimenMap(tempSpecimenMap)
    })
  }

  const handleEdit = (uuid?: string) => {
    if (uuid) {
      HistologyReportApi.getHistologyReport(episodeId, uuid).then((response) => {
        setEditElement(response.data)
        setIsOpen(true)
      })
    } else {
      setEditElement(undefined)
      setIsOpen(true)
    }
  }

  const handleClose = () => {
    setIsOpen(false)
    HistologyReportApi.getAllHistologyReports(episodeId).then((histologyReports) => {
      setHistologyReports([...histologyReports.data])
    })
  }

  return (
    <>
      <HistologyReportsTable
        onEdit={handleEdit}
        histologyReports={histologyReports ?? []}
        setHisologyReports={setHistologyReports}
        specimenMap={specimenMap}
      />
      <HistologyReportsDialog
        open={isOpen}
        onClose={handleClose}
        editElement={editElement}
        specimenMap={specimenMap}
      />
    </>
  )
}
