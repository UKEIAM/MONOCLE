import { Column, Row, Table } from "components/Table"
import { IhcReport } from "gen/api"
import { useEffect, useMemo, useState } from "react"
import { ihcReportToString, toGermanDateFormat } from "utils/Formats"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import Session from "hooks/Session"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

const columns: Column[] = [
  { label: "Tumorprobe" },
  { label: "Datum" },
  { label: "Eingangs-/Journal-Nr." },
  { label: "Block-/Material-Nr." },
]

interface Props {
  readonly onEdit: (ihcReport?: IhcReport) => void
  readonly specimenLabelsById: Map<string, string>
}

const IHCReportsTable = ({ onEdit, specimenLabelsById }: Props) => {
  const episodeId = Session.getEpisodeId()
  const { IhcReportApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const [dialogOpen, setDialogOpen] = useState<boolean>(false)
  const [entryToDelete, setEntryToDelete] = useState<IhcReport>()
  const [ihcReports, setIhcReports] = useState<IhcReport[]>([])

  useEffect(() => {
    IhcReportApi.getAllIhcReports(episodeId).then(({ data }) => setIhcReports(data))
  }, [dialogOpen, onEdit])

  useEffect(() => {
    IhcReportApi.getAllIhcReports(episodeId).then(({ data }) => setIhcReports(data))
  }, [dialogOpen, onEdit])

  const rows = useMemo(() => {
    const newRows: Row[] = []
    ihcReports.forEach((ihcReport) => {
      const specimenLabel = specimenLabelsById.get(ihcReport.specimenId!)
      if (specimenLabel === undefined) return
      newRows.push({
        onEdit: () => onEdit(ihcReport),
        onDelete: () => confirmDelete(ihcReport),
        rowKey: ihcReport.id,
        cells: [
          { value: specimenLabel },
          { value: toGermanDateFormat(ihcReport.date) },
          { value: ihcReport.journalId },
          { value: ihcReport.blockId },
        ],
      })
    })
    return newRows
  }, [ihcReports])

  const confirmDelete = (ihcReport: IhcReport) => {
    if (ihcReport) {
      setEntryToDelete(ihcReport)
      setDialogOpen(true)
    }
  }

  const handleDelete = () => {
    const ihcReportId = entryToDelete?.id
    if (ihcReportId) {
      IhcReportApi.deleteIhcReport(episodeId, ihcReportId)
        .then(() => {
          setDialogOpen(false)
          showSuccessNotification("IHC-Bericht wurde erfolgreich gelöscht.")
        })
        .catch(() =>
          showErrorNotification("Beim Löschen des IHC-Berichtes ist ein Fehler aufgetreten."),
        )
    }
  }

  return (
    <>
      <Table
        columns={columns}
        rows={rows}
        addRowButton={{ label: "Neuen IHC-Bericht hinzufügen", onClick: () => onEdit() }}
      />
      {dialogOpen && (
        <DeleteConfirmationDialog
          isOpen={dialogOpen}
          onClose={() => setDialogOpen(false)}
          onConfirm={handleDelete}
          itemNameAndDetails={`${ihcReportToString(entryToDelete ?? {})}`}
        />
      )}
    </>
  )
}

export { IHCReportsTable }
