import { useEffect, useState } from "react"
import Session from "hooks/Session"
import { Claim } from "gen/api"
import { therapyRecommendationToString } from "utils/Formats"
import { ClaimsDialog } from "./ClaimsDialog"
import { ClaimsTable } from "./ClaimsTable"
import { TabProps } from "../CoreDataFormTabs"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

export function Claims({ selected }: TabProps) {
  const { showErrorNotification } = useNotification()
  const { ClaimApi, TherapyRecommendationApi } = useApi()
  const episodeId = Session.getEpisodeId()

  const [isOpen, setIsOpen] = useState<boolean>(false)
  const [editElement, setEditElement] = useState<Claim>()
  const [claims, setClaims] = useState<Claim[]>([])

  // map of labels for given therapyRecommendationIds, used by the table and the select within the dialog
  const [therapyRecommendationsMap, setTherapyRecommendationsMap] = useState<Map<string, string>>(
    new Map(),
  )
  useEffect(() => {
    // initialize therapyRecommendationsMap
    if (selected) {
      getTherapyRecommendationsMap()
    }
  }, [selected])
  useEffect(() => {
    // update therapyRecommendationsMap
    getTherapyRecommendationsMap()
  }, [isOpen])

  const getTherapyRecommendationsMap = () => {
    TherapyRecommendationApi.getAllTherapyRecommendations(episodeId).then(({ data: response }) => {
      const newTherapyRecommendationsMap = new Map<string, string>()
      response.forEach((elem) =>
        newTherapyRecommendationsMap.set(elem.id!, therapyRecommendationToString(elem)),
      )
      setTherapyRecommendationsMap(newTherapyRecommendationsMap)
    })
  }

  const handleEdit = (claimId?: string) => {
    if (claimId) {
      ClaimApi.getClaim(episodeId, claimId)
        .then(({ data: response }) => {
          setEditElement(response)
          setIsOpen(true)
        })
        .catch(() => {
          showErrorNotification(
            "Beim Laden des Kostenübernahme Antrags ist ein Fehler aufgetreten.",
          )
        })
    } else {
      setEditElement(undefined)
      setIsOpen(true)
    }
  }

  const handleClose = () => {
    setIsOpen(false)
    ClaimApi.getAllClaims(episodeId)
      .then(({ data: response }) => {
        setClaims(response)
      })
      .catch(() => {
        showErrorNotification("Beim Laden der Kostenübernahme Anträge ist ein Fehler aufgetreten.")
      })
  }

  return (
    <>
      <ClaimsTable
        onEdit={handleEdit}
        claims={claims}
        setClaims={setClaims}
        therapyRecommendationsMap={therapyRecommendationsMap}
      />
      <ClaimsDialog
        open={isOpen}
        onClose={handleClose}
        editElement={editElement}
        therapyRecommendationsMap={therapyRecommendationsMap}
      />
    </>
  )
}
