import React, { useEffect, useState } from "react"
import HistologyReevaluationRequestsTable from "./HistologyReevaluationRequestsTable"
import HistologyReevaluationRequestsDialog from "./HistologyReevaluationRequestsDialog"
import { HistologyReevaluationRequest } from "gen/api"
import Session from "hooks/Session"
import { specimenToString } from "utils/Formats"
import { TabProps } from "../CoreDataFormTabs"
import { useApi } from "hooks/useApi"

export type HistologyReevaluationRequestType = {
  id: string
  patient: string
  specimen: string
  issuedOn: string
}

export function HistologyReevaluationRequests({ selected }: TabProps) {
  const { HistologyReevaluationRequestApi, SpecimenApi } = useApi()
  // const { showSuccessNotification, showErrorNotification } = useNotification()
  const [isOpen, setIsOpen] = useState<boolean>(false)
  const [editElement, setEditElement] = useState<HistologyReevaluationRequest>()
  const [histologyReevaluationRequests, setHistologyReevaluationRequests] =
    useState<HistologyReevaluationRequest[]>()
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
      HistologyReevaluationRequestApi.getHistologyReevaluationRequest(episodeId, uuid)
        .then(({ data }) => {
          setEditElement(data)
          setIsOpen(true)
        })
        .catch(() => {
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
    HistologyReevaluationRequestApi.getAllHistologyReevaluationRequests(episodeId)
      .then(({ data }) => {
        setHistologyReevaluationRequests([...data])
      })
      .catch(() => {
      })

    fillSpecimenMap()
  }

  return (
    <>
      <HistologyReevaluationRequestsTable
        onEdit={handleEdit}
        histologyReevaluationRequests={histologyReevaluationRequests}
        setHistologyReevaluationRequests={setHistologyReevaluationRequests}
        specimenMap={specimenMap}
      ></HistologyReevaluationRequestsTable>
      <HistologyReevaluationRequestsDialog
        open={isOpen}
        onClose={handleClose}
        editElement={editElement}
        specimenMap={specimenMap}
      ></HistologyReevaluationRequestsDialog>
    </>
  )
}
