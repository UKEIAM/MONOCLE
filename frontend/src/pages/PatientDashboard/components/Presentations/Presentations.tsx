import { Timeline } from "./Timeline"
import { Divider, Typography } from "@mui/material"
import { useEffect, useState } from "react"
import PresentationsDialog from "./PresentationsDialog"
import session from "hooks/Session"
import { Presentation } from "gen/api"
import { toGermanDateFormat } from "utils/Formats"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

export interface PresentationItem {
  id: string
  label: string
}

export default function Presentations() {
  const { PresentationApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const episodeid = session.getEpisodeId()
  const [open, setOpen] = useState(false)
  const [presentations, setPresentations] = useState<Presentation[]>([])
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [itemToBeDeleted, setItemToBeDeleted] = useState<PresentationItem>()

  useEffect(() => {
    refreshPresentations()
  }, [episodeid])

  const refreshPresentations = () => {
    PresentationApi.getAllPresentations(episodeid).then(({ data: response }) => {
      if (!response) {
        setPresentations([])
        return
      }
      const sortedPresentations = response.sort(
        (a, b) =>
          new Date(a.dateOfPresentation).getTime() - new Date(b.dateOfPresentation).getTime(),
      )
      setPresentations(sortedPresentations)
    })
  }

  const handleDeletePresentation = (presentationId: string) => {
    if (presentationId) {
      PresentationApi.deletePresentation(episodeid, presentationId)
        .then((_) => {
          showSuccessNotification("Die Änderungen wurden gespeichert.")
          refreshPresentations()
          setConfirmOpen(false)
        })
        .catch((_) =>
          showErrorNotification(
            "Die Änderungen konnten nicht gespeichert werden. Bitte versuchen Sie es später erneut.",
          ),
        )
    }
  }

  return (
    <>
      <Divider variant={"fullWidth"} style={{ marginTop: "8px" }} />
      <Typography variant={"h5"} margin={"16px"}>
        MTB Vorstellungen
      </Typography>
      <Timeline
        items={presentations.map((elem) => ({
          id: elem.id!,
          label: toGermanDateFormat(elem.dateOfPresentation),
        }))}
        onAdd={() => {
          setOpen(true)
        }}
        onDelete={(item: PresentationItem) => {
          setItemToBeDeleted(item)
          setConfirmOpen(true)
        }}
      />
      <PresentationsDialog
        open={open}
        onClose={() => {
          setOpen(false)
          refreshPresentations()
        }}
      />
      {confirmOpen &&
        itemToBeDeleted && ( // If not checked a nullpointer occures
          // TODO: Use new DeleteConfirmationDialog after merged  https://github.com/UKEIAM/de.uke.iam.mtb.gui/pull/202
          <DeleteConfirmationDialog
            isOpen={confirmOpen}
            onClose={() => setConfirmOpen(false)}
            onConfirm={() => {
              handleDeletePresentation(itemToBeDeleted.id)
            }}
            itemNameAndDetails={`Möchten Sie die Vorstellung vom ${itemToBeDeleted.label} wirklich löschen?`}
          />
        )}
    </>
  )
}
