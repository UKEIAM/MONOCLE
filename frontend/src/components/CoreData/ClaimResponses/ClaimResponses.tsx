import { useEffect, useState } from "react"
import Session from "hooks/Session"
import { ClaimResponse } from "gen/api"
import ClaimResponsesTable from "./ClaimResponsesTable"
import ClaimResponsesDialog from "./ClaimResponsesDialog"
import { claimToString } from "utils/Formats"
import { TabProps } from "../CoreDataFormTabs"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"

export function ClaimResponses({ selected }: TabProps) {
  const { showErrorNotification } = useNotification()
  const { ClaimResponseApi, ClaimApi } = useApi()
  const episodeId = Session.getEpisodeId()

  const [isOpen, setIsOpen] = useState<boolean>(false)
  const [editElement, setEditElement] = useState<ClaimResponse>()
  const [claimResponses, setClaimResponses] = useState<ClaimResponse[]>([])

  // map of labels for given claimIds, used by the table and the select within the dialog
  const [claimsMap, setClaimsMap] = useState<Map<string, string>>(new Map())

  useEffect(() => {
    // initialize claimsMap
    if (selected) {
      getClaimsMap()
    }
  }, [selected])
  useEffect(() => {
    // update claimsMap
    getClaimsMap()
  }, [isOpen])

  const getClaimsMap = () => {
    ClaimApi.getAllClaims(episodeId).then(({ data: response }) => {
      const newClaimsMap = new Map<string, string>()
      response.forEach((elem) => newClaimsMap.set(elem.id!, claimToString(elem)))
      setClaimsMap(newClaimsMap)
    })
  }

  const handleEdit = (claimResponseId?: string) => {
    if (claimResponseId) {
      ClaimResponseApi.getClaimResponse(episodeId, claimResponseId)
        .then(({ data: response }) => {
          setEditElement(response)
          setIsOpen(true)
        })
        .catch(() => {
          showErrorNotification(
            "Beim Laden der Kostenübernahme Antwort ist ein Fehler aufgetreten.",
          )
        })
    } else {
      setEditElement(undefined)
      setIsOpen(true)
    }
  }

  const handleClose = () => {
    setIsOpen(false)
    ClaimResponseApi.getAllClaimResponses(episodeId)
      .then(({ data: response }) => {
        setClaimResponses(response)
      })
      .catch(() => {
        showErrorNotification(
          "Beim Laden der Kostenübernahme Antworten ist ein Fehler aufgetreten.",
        )
      })
  }

  return (
    <>
      <ClaimResponsesTable
        onEdit={handleEdit}
        claimResponses={claimResponses}
        setClaimResponses={setClaimResponses}
        claimsMap={claimsMap}
      />
      <ClaimResponsesDialog
        open={isOpen}
        onClose={handleClose}
        editElement={editElement}
        claimsMap={claimsMap}
      />
    </>
  )
}
