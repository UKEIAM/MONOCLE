import { Column, Row, Table } from "components/Table"
import dayjs from "dayjs"
import { EcogStatus } from "gen/api"
import Session from "hooks/Session"
import React, { useEffect, useState } from "react"
import { ecogStatusToString } from "utils/Formats"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"

type Props = {
  onEdit: (ecogStatusId?: string) => void
  ecogStatusList: EcogStatus[]
  setEcogStatusList: (ecogStatusList: EcogStatus[]) => void
}

export function EcogStatusListTable({ onEdit, ecogStatusList, setEcogStatusList }: Props) {
  const { EcogStatusApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<EcogStatus>()

  const deleteEntryConfirmation = (item: EcogStatus) => {
    setItemToBeDeleted(item)
    setConfirmOpen(true)
  }

  useEffect(() => {
    getEcogStatusList()
  }, [])

  const handleDelete = () => {
    const ecogStatusId = itemToBeDeleted?.id
    if (ecogStatusId) {
      EcogStatusApi.deleteEcogStatus(episodeId, ecogStatusId)
        .then(() => {
          showSuccessNotification("Der ECOG-Performance-Status-Befund wurde erfolgreich gelöscht")
          getEcogStatusList()
          setConfirmOpen(false)
        })
        .catch(() => {
          showErrorNotification(
            "Beim Löschen des ECOG-Performance-Status-Befunds ist ein Fehler aufgetreten.",
          )
        })
    }
  }

  const getEcogStatusList = () => {
    EcogStatusApi.getAllEcogStatus(episodeId)
      .then(({ data }) => {
        setEcogStatusList(data)
      })
      .catch(() => {
        showErrorNotification(
          "Beim Laden der ECOG-Performance-Status-Befunde ist ein Fehler aufgetreten.",
        )
      })
  }

  const columns: Column[] = [{ label: "Zeitpunkt" }, { label: "ECOG-Performance-Status" }]

  const rows: Row[] =
    ecogStatusList?.map((elem) => ({
      onEdit: () => onEdit(elem.id),
      onDelete: () => deleteEntryConfirmation(elem),
      rowKey: elem.id,
      cells: [
        { value: elem.effectiveDate ? dayjs(elem.effectiveDate).format("DD.MM.YYYY") : "" },
        { value: elem.value?.code },
      ],
    })) ?? []

  return (
    <>
      <Table
        columns={columns}
        rows={rows}
        addRowButton={{
          label: "Neuen ECOG-Performance-Status-Befund hinzufügen",
          onClick: () => onEdit(),
        }}
      />
      {itemToBeDeleted && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`${ecogStatusToString(itemToBeDeleted)}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={handleDelete}
        />
      )}
    </>
  )
}
