import React, { useEffect, useState } from "react"
import RebiopsyRequestsTable from "./RebiopsyRequestsTable"
import RebiopsyRequestsDialog from "./RebiopsyRequestsDialog"
import { RebiopsyRequest } from "gen/api"
import Session from "hooks/Session"
import { specimenToString } from "utils/Formats"
import { TabProps } from "../CoreDataFormTabs"
import { useApi } from "hooks/useApi"

export function RebiopsyRequests({ selected }: TabProps) {
  const { RebiopsyRequestApi, SpecimenApi } = useApi()
  const [isOpen, setIsOpen] = useState<boolean>(false)
  const [editElement, setEditElement] = useState<RebiopsyRequest>()
  const [rebiopsyRequests, setRebiopsyRequests] = useState<RebiopsyRequest[]>()
  const [specimenMap, setSpecimenMap] = useState<{ [key: string]: string }>({})
  const episodeId = Session.getEpisodeId()

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

  const handleEdit = (uuid?: string) => {
    if (uuid) {
      RebiopsyRequestApi.getRebiopsyRequest(episodeId, uuid).then(({ data }) => {
        setEditElement(data)
        setIsOpen(true)
      })
    } else {
      setEditElement(undefined)
      setIsOpen(true)
    }
  }

  const fillSpecimenMap = () => {
    SpecimenApi.getAllSpecimens(episodeId).then(({ data }) => {
      let tempSpecimenMap: { [key: string]: string } = {}
      data.map((speci) => {
        tempSpecimenMap[speci.id!] = specimenToString(speci)
      })
      setSpecimenMap(tempSpecimenMap)
    })
  }

  const handleClose = () => {
    setIsOpen(false)
    RebiopsyRequestApi.getAllRebiopsyRequests(episodeId).then(({ data }) => {
      setRebiopsyRequests([...data])
    })
    fillSpecimenMap()
  }

  return (
    <>
      <RebiopsyRequestsTable
        onEdit={handleEdit}
        rebiopsyRequests={rebiopsyRequests ?? []}
        setRebiopsyRequests={setRebiopsyRequests}
        specimenMap={specimenMap}
      ></RebiopsyRequestsTable>
      <RebiopsyRequestsDialog
        open={isOpen}
        onClose={handleClose}
        editElement={editElement}
        specimenMap={specimenMap}
      ></RebiopsyRequestsDialog>
    </>
  )
}
