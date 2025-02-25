import MolecularTherapiesTable from "./MolecularTherapiesTable"
import MolecularTherapiesDialog from "./MolecularTherapiesDialog"
import React, { useEffect, useState } from "react"
import Session from "hooks/Session"
import { MolecularTherapy } from "gen/api"
import { therapyRecommendationToString } from "utils/Formats"
import { TabProps } from "../CoreDataFormTabs"
import { useApi } from "hooks/useApi"

// Systemische Therapie
export function MolecularTherapies({ selected }: TabProps) {
  const [isOpen, setIsOpen] = useState<boolean>(false)
  const { TherapyRecommendationApi, MolecularTherapyApi } = useApi()
  const [editElement, setEditElement] = useState<MolecularTherapy>()
  const [molecularTherapies, setMolecularTherapies] = useState<MolecularTherapy[]>()
  const episodeId = Session.getEpisodeId().toString()
  const [recommendationMap, setRecommendationMap] = useState<{ [key: string]: string }>({})

  useEffect(() => {
    // Trigger on selected tab
    if (selected) {
      fillRecommendationMap()
    }
  }, [selected])

  useEffect(() => {
    // Trigger if isOpen === true (Dialog opens)
    if (isOpen) fillRecommendationMap()
  }, [isOpen])

  const fillRecommendationMap = () => {
    TherapyRecommendationApi.getAllTherapyRecommendations(episodeId).then(
      ({ data: recommendations }) => {
        let tempRecommendationMap: { [key: string]: string } = {}
        recommendations.map((recom) => {
          tempRecommendationMap[recom.id!] = therapyRecommendationToString(recom)
        })
        setRecommendationMap(tempRecommendationMap)
      },
    )
  }

  const handleEdit = (uuid?: string) => {
    if (uuid) {
      MolecularTherapyApi.getMolecularTherapy(episodeId, uuid).then(({ data: response }) => {
        setEditElement(response)
        setIsOpen(true)
      })
    } else {
      setEditElement(undefined)
      setIsOpen(true)
    }
  }

  const handleClose = () => {
    setIsOpen(false)
    MolecularTherapyApi.getAllMolecularTherapies(episodeId).then(({ data: molecularTherapies }) => {
      setMolecularTherapies([...molecularTherapies])
    })
  }
  return (
    <>
      <MolecularTherapiesTable
        onEdit={handleEdit}
        molecularTherapies={molecularTherapies ?? []}
        setMolecularTherapies={setMolecularTherapies}
        recommendationMap={recommendationMap}
      ></MolecularTherapiesTable>
      <MolecularTherapiesDialog
        open={isOpen}
        onClose={handleClose}
        editElement={editElement}
        recommendationMap={recommendationMap}
      ></MolecularTherapiesDialog>
    </>
  )
}
