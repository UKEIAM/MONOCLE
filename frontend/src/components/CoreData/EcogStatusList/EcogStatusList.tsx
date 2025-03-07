import React, { useState } from "react"
import { EcogStatusListTable } from "./EcogStatusListTable"
import { EcogStatusListDialog } from "./EcogStatusListDialog"
import Session from "hooks/Session"
import { EcogStatus } from "gen/api"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

export function EcogStatusList() {
  const { EcogStatusApi } = useApi()
  const { showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()

  const [isOpen, setIsOpen] = useState<boolean>(false)
  const [editElement, setEditElement] = useState<EcogStatus>()
  const [ecogStatusList, setEcogStatusList] = useState<EcogStatus[]>([])

  const handleEdit = (ecogStatusId?: string) => {
    if (ecogStatusId !== undefined) {
      EcogStatusApi.getEcogStatus(episodeId, ecogStatusId)
        .then(({ data }) => {
          setEditElement(data)
          setIsOpen(true)
        })
        .catch(() => {
          showErrorNotification(
            "Beim Laden des ECOG Performance Status Befund ist ein Fehler aufgetreten.",
          )
        })
    } else {
      setEditElement(undefined)
      setIsOpen(true)
    }
  }

  const handleClose = () => {
    setIsOpen(false)
    EcogStatusApi.getAllEcogStatus(episodeId)
      .then(({ data }) => {
        setEcogStatusList(data)
      })
      .catch(() => {
        showErrorNotification(
          "Beim Laden der ECOG Performance Status Befunde ist ein Fehler aufgetreten.",
        )
      })
  }

  return (
    <>
      <EcogStatusListTable
        onEdit={handleEdit}
        ecogStatusList={ecogStatusList}
        setEcogStatusList={setEcogStatusList}
      />
      <EcogStatusListDialog open={isOpen} onClose={handleClose} editElement={editElement} />
    </>
  )
}
