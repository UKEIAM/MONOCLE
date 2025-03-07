import TherapyResponsesDialog from "./TherapyResponsesDialog"
import TherapyResponsesTable from "./TherapyResponsesTable"
import React, { useEffect, useState } from "react"
import { MolecularTherapyResponse } from "gen/api"
import Session from "hooks/Session"
import { molecularTherapyToString } from "utils/Formats"
import { TabProps } from "../CoreDataFormTabs"
import { useApi } from "hooks/useApi"

export default function TherapyResponses({ selected }: TabProps) {
  const { MolecularTherapyApi, MolecularTherapyResponseApi } = useApi()
  const [isOpen, setIsOpen] = useState<boolean>(false)
  const [editElement, setEditElement] = useState<MolecularTherapyResponse>()
  const [therapyResponses, setTherapyResponses] = useState<MolecularTherapyResponse[]>()
  const episodeId = Session.getEpisodeId().toString()
  const [molecularTherapiesMap, setMolecularTherapiesMap] = useState<{ [key: string]: string }>({})

  useEffect(() => {
    // Trigger on selected tab
    if (selected) {
      fillMolecularTherapiesMap()
    }
  }, [selected])

  useEffect(() => {
    // Trigger if isOpen === true (Dialog opens)
    if (isOpen) fillMolecularTherapiesMap()
  }, [isOpen])

  const fillMolecularTherapiesMap = () => {
    MolecularTherapyApi.getAllMolecularTherapies(episodeId).then(({ data: molecularTherapy }) => {
      let tempMolecularTherapiesMap: { [key: string]: string } = {}
      molecularTherapy.map((molThe) => {
        tempMolecularTherapiesMap[molThe.id!] = molecularTherapyToString(molThe)
      })
      setMolecularTherapiesMap(tempMolecularTherapiesMap)
    })
  }

  const handleEdit = (uuid?: string) => {
    if (uuid) {
      MolecularTherapyResponseApi.getMolecularTherapyResponse(episodeId, uuid).then(
        ({ data: response }) => {
          setEditElement(response)
          setIsOpen(true)
        },
      )
    } else {
      setEditElement(undefined)
      setIsOpen(true)
    }
  }

  const handleClose = () => {
    setIsOpen(false)
    MolecularTherapyResponseApi.getAllMolecularTherapyResponses(episodeId).then(
      ({ data: molecularTherapyResponses }) => {
        setTherapyResponses([...molecularTherapyResponses])
      },
    )
  }

  return (
    <>
      <TherapyResponsesTable
        onEdit={handleEdit}
        therapyResponses={therapyResponses ?? []}
        setTherapyResponses={setTherapyResponses}
        molecularTherapiesMap={molecularTherapiesMap}
      ></TherapyResponsesTable>
      <TherapyResponsesDialog
        open={isOpen}
        onClose={handleClose}
        editElement={editElement}
        molecularTherapiesMap={molecularTherapiesMap}
      ></TherapyResponsesDialog>
    </>
  )
}
