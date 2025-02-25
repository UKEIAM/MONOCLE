import { SpecimensTable } from "./SpecimensTable"
import { SpecimensDialog } from "./SpecimensDialog"
import { useState } from "react"
import { Specimen } from "gen/api"
import Session from "hooks/Session"
import { useApi } from "hooks/useApi"

export function Specimens() {
  const { SpecimenApi } = useApi()
  const [editElement, setEditElement] = useState<Specimen>()
  const [specimens, setSpecimens] = useState<Specimen[]>()
  const [isOpen, setIsOpen] = useState<boolean>(false)
  const episodeId = Session.getEpisodeId()

  const handleAddOrEdit = (uuid: string | undefined) => {
    if (uuid) {
      SpecimenApi.getSpecimen(episodeId, uuid).then((response) => {
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
    SpecimenApi.getAllSpecimens(episodeId).then((specimens) => {
      setSpecimens([...specimens.data])
    })
  }

  return (
    <>
      <SpecimensTable
        onAddOrEdit={handleAddOrEdit}
        specimens={specimens}
        setSpecimens={setSpecimens}
      ></SpecimensTable>
      <SpecimensDialog
        open={isOpen}
        onClose={handleClose}
        editElement={editElement}
      ></SpecimensDialog>
    </>
  )
}
