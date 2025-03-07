import { MolecularPathoFindingsTable } from "./MolecularPathoFindingsTable"
import { MolecularPathoFindingsDialog } from "./MolecularPathoFindingsDialog"
import { useEffect, useState } from "react"
import { MolecularPathologyFinding } from "gen/api"
import Session from "hooks/Session"
import { specimenToString } from "utils/Formats"
import { TabProps } from "../CoreDataFormTabs"
import { useApi } from "hooks/useApi"

export function MolecularPathoFindings({ selected }: TabProps) {
  const { MolecularPathologyFindingApi, SpecimenApi } = useApi()
  const [isOpen, setIsOpen] = useState<boolean>(false)
  const [editElement, setEditElement] = useState<MolecularPathologyFinding>()
  const [molecularPathoFindings, setMolecularPathoFindings] =
    useState<MolecularPathologyFinding[]>()
  const [specimenMap, setSpecimenMap] = useState<{ [key: string]: string }>({})
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
      MolecularPathologyFindingApi.getMolecularPathologyFinding(episodeId, uuid).then(
        (response) => {
          setEditElement(response.data)
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
    MolecularPathologyFindingApi.getAllMolecularPathologyFindings(episodeId).then(
      (molecularPathoFindings) => {
        setMolecularPathoFindings([...molecularPathoFindings.data])
      },
    )
  }
  return (
    <>
      <MolecularPathoFindingsTable
        onEdit={handleEdit}
        molecularPathoFindings={molecularPathoFindings ?? []}
        setMolecularPathoFindings={setMolecularPathoFindings}
        specimenMap={specimenMap}
      />
      <MolecularPathoFindingsDialog
        open={isOpen}
        onClose={handleClose}
        editElement={editElement}
        specimenMap={specimenMap}
      />
    </>
  )
}
