import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid } from "@mui/material"
import DatePicker from "components/FormFields/DatePicker"
import { FormProvider, useForm } from "react-hook-form"
import React, { useEffect } from "react"
import session from "hooks/Session"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

type Props = {
  open: boolean
  onClose: () => void
}

export default function PresentationsDialog({ open, onClose }: Props) {
  const { PresentationApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const methods = useForm<{ date: string }>()
  useEffect(() => {
    if (!open) return
    methods.reset({
      date: "",
    })
  }, [open])
  const episodeid = session.getEpisodeId()
  const onSubmit = (formData: { date: string }) => {
    PresentationApi.addPresentation(episodeid, {
      episodeId: episodeid,
      dateOfPresentation: formData.date,
    })
      .then((_) => {
        showSuccessNotification("Die Änderungen wurden gespeichert.")
        onClose()
      })
      .catch((_) =>
        showErrorNotification(
          "Die Änderungen konnten nicht gespeichert werden. Bitte versuchen Sie es später erneut.",
        ),
      )
  }
  return (
    <FormProvider {...methods}>
      <Dialog open={open}>
        <form onSubmit={methods.handleSubmit(onSubmit)}>
          <DialogTitle>MTB Vorstellung</DialogTitle>
          <DialogContent>
            <Grid container spacing={2} sx={{ marginTop: 2 }}>
              <Grid item xs={12}>
                <DatePicker label={"Datum"} name={"date"} isRequired={true} />
              </Grid>
            </Grid>
          </DialogContent>
          <DialogActions>
            <Button variant="contained" type={"submit"}>
              {"hinzufügen"}
            </Button>
            <Button variant="contained" onClick={onClose}>
              abbrechen
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </FormProvider>
  )
}
