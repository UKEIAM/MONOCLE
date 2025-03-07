import "dayjs/locale/de"
import { useEffect, useState } from "react"
import { Column, Row, Table } from "components/Table"
import dayjs from "dayjs"
import { HistologyReport } from "gen/api"
import Session from "hooks/Session"
import { histologyReportToString } from "utils/Formats"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import { differentiationDegreeList } from "./HistologyReportsTypes"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type Probs = {
  onEdit: (uuid?: string) => void
  histologyReports?: HistologyReport[]
  setHisologyReports: (histo: HistologyReport[]) => void
  specimenMap: { [key: string]: string }
}

export function HistologyReportsTable({
  onEdit,
  histologyReports,
  setHisologyReports,
  specimenMap,
}: Probs) {
  const { HistologyReportApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<HistologyReport>()

  const deleteEntryConfirmation = (item: HistologyReport) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  useEffect(() => {
    getHistologyReportsList()
  }, [])

  const handleDelete = () => {
    const histologyReportId = itemToBeDeleted?.id
    if (histologyReportId) {
      HistologyReportApi.deleteHistologyReport(episodeId, histologyReportId)
        .then(() => {
          getHistologyReportsList()
          setConfirmOpen(false)
          showSuccessNotification("Histologie-Bericht wurde erfolgreich gelöscht")
        })
        .catch(() =>
          showErrorNotification("Beim Löschen des Histologie-Berichts ist ein Fehler aufgetreten"),
        )
    }
  }

  const getHistologyReportsList = () => {
    HistologyReportApi.getAllHistologyReports(episodeId).then((histologyReports) => {
      setHisologyReports([...histologyReports.data])
    })
  }

  function toRows() {
    let rowList: Row[] = []

    histologyReports?.map((histologyReport: HistologyReport) => {
      rowList = [
        ...rowList,
        {
          onEdit: () => onEdit(histologyReport.id),
          onDelete: () => deleteEntryConfirmation(histologyReport),
          rowKey: histologyReport.id,
          cells: [
            { value: histologyReport.specimen },
            {
              value: histologyReport.issuedOn
                ? dayjs(histologyReport.issuedOn).format("DD.MM.YYYY")
                : "",
            },
            {
              value: (
                <ul>
                  {histologyReport.tumorMorphology?.value?.code && (
                    <li>Code: {histologyReport.tumorMorphology.value.code}</li>
                  )}
                  {/*<li>Anzeige: {histologyReport.tumorMorphology.value.display}</li>*/}
                  {histologyReport.tumorMorphology?.value?.version && (
                    <li>Version: {histologyReport.tumorMorphology.value.version}</li>
                  )}
                  {/*<li>System: {histologyReport.tumorMorphology.value.system}</li>*/}
                  {histologyReport.tumorMorphology?.note && (
                    <li>Anmerkungen: {histologyReport.tumorMorphology.note}</li>
                  )}
                </ul>
              ),
            },
            {
              value: (
                <ul>
                  {histologyReport.tumorCellContent?.method && (
                    <li>Methode: {histologyReport.tumorCellContent.method}</li>
                  )}
                  {histologyReport.tumorCellContent?.value && (
                    <li>Wert: {histologyReport.tumorCellContent.value}</li>
                  )}
                </ul>
              ),
            },
            {
              value: differentiationDegreeList.find(
                (item: { value: string | undefined }) =>
                  item.value === histologyReport.differentiationDegree,
              )?.label,
            },
          ],
        },
      ]
    })
    return rowList
  }

  const columns: Column[] = [
    { label: "Tumorproben", format: (v) => specimenMap[v] },
    { label: "Erstellungsdatum" },
    { label: "Tumor-Morphologie" },
    { label: "Tumorzellgehalt" },
    { label: "Differenzierungsgrad" },
  ]

  const listOfReferences: string[] = ["Diagnose"]

  return (
    <>
      <Table
        columns={columns}
        rows={toRows()}
        addRowButton={{
          label: "Neuen Histologie-Bericht Hinzufügen",
          onClick: () => onEdit(),
        }}
      />
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`Histologie-Bericht ${histologyReportToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
          itemReferences={listOfReferences}
        />
      )}
    </>
  )
}
