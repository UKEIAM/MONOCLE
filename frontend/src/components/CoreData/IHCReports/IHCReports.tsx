import { useEffect, useState } from "react"
import { IHCReportsTable } from "./IHCReportsTable"
import { IHCReportsDialog } from "./IHCReportsDialog"
import { IhcReport } from "gen/api"
import { useApi } from "hooks/useApi"
import Session from "hooks/Session"
import { specimenToString } from "utils/Formats"

type Props = {
  selected: boolean
}

const IHCReports = ({ selected }: Props) => {
  const [dialogOpen, setDialogOpen] = useState<boolean>(false)
  const [editElement, setEditElement] = useState<IhcReport>()

  const { SpecimenApi } = useApi()
  const episodeId = Session.getEpisodeId()
  const [specimenLabelsById, setSpecimenLabelsById] = useState<Map<string, string>>(
    new Map<string, string>(),
  )
  useEffect(() => {
    if (!selected || dialogOpen) return
    SpecimenApi.getAllSpecimens(episodeId).then(({ data }) => {
      const newSpecimenLabelsById = new Map<string, string>()
      data.forEach((specimen) => {
        newSpecimenLabelsById.set(specimen.id!, specimenToString(specimen))
      })
      setSpecimenLabelsById(newSpecimenLabelsById)
    })
  }, [selected, dialogOpen])

  return (
    <>
      <IHCReportsTable
        onEdit={(ihcReport?: IhcReport) => {
          setEditElement(ihcReport)
          setDialogOpen(true)
        }}
        specimenLabelsById={specimenLabelsById}
      />
      <IHCReportsDialog
        open={dialogOpen}
        editElement={editElement}
        onClose={() => setDialogOpen(false)}
        specimenLabelsById={specimenLabelsById}
      />
    </>
  )
}

export { IHCReports }
