import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid } from "@mui/material"
import FormRadioGroup from "components/FormFields/FormRadioGroup"
import { TextField } from "components/FormFields/TextField"
import { OptionType } from "components/FormFields/types/FormTypes"
import { AddressbookEntry } from "gen/api"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import React, { useEffect } from "react"
import { FormProvider, useForm } from "react-hook-form"

export const internExternRadioGroupOptions: OptionType[] = [
  { label: "intern", value: "true" },
  { label: "extern", value: "false" },
]

export const zuweiserSonstigesRadioGroupOptions: OptionType[] = [
  { label: "Zuweiser", value: "ZUWEISER" },
  { label: "Sonstiges", value: "SONSTIGES" },
]

interface AddressbookDialogProps {
  addressBookEntry: AddressbookEntry | undefined
  open: boolean
  onSave: () => void
  onAbort: () => void
}

export const AddEditEntryDialog = ({
  addressBookEntry,
  open,
  onSave,
  onAbort,
}: AddressbookDialogProps) => {
  const editing = addressBookEntry != undefined
  const { AddressbookentryApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const methods = useForm<AddressbookEntry>()

  useEffect(() => {
    if (!open) return
    methods.reset({
      // default values
      ukeinternal: true,
      addressbookEntryType: "ZUWEISER",
      // overrides
      ...addressBookEntry,
    })
  }, [open])

  const onSubmit = (formData: AddressbookEntry) => {
    const addressbookPromise = editing
      ? AddressbookentryApi.updateAddressbookEntry(addressBookEntry.id!, formData)
      : AddressbookentryApi.addAddressbookEntry(formData)

    addressbookPromise
      .then(() => {
        showSuccessNotification(
          `Adresse wurden erfolgreich ${editing ? "geändert" : "gespeichert"}.`,
        )
        onSave()
      })
      .catch(() =>
        showErrorNotification(
          "Die Änderungen konnten nicht gespeichert werden. Bitte versuchen Sie es später erneut.",
        ),
      )
  }

  return (
    <React.Fragment>
      <Dialog
        open={open}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
      >
        <FormProvider {...methods}>
          <form onSubmit={methods.handleSubmit(onSubmit)}>
            <DialogTitle>{"Adresseintrag"}</DialogTitle>
            <DialogContent>
              <Grid container spacing={2} sx={{ marginTop: 1 }}>
                <Grid item xs={2}>
                  {/*<Select name={"title"} label={"Anrede"} options={[{label: "Frau", value: "Frau"}, {label: "Herr", value: "Herr"}]}*/}
                  {/*        shrink={true}/>*/}
                  <TextField name="title" label="Anrede" InputLabelProps={{ shrink: true }} />
                </Grid>
                <Grid item xs={5}>
                  <TextField
                    name="firstname"
                    label="Vorname"
                    InputLabelProps={{ shrink: true }}
                    isRequired={true}
                  />
                </Grid>
                <Grid item xs={5}>
                  <TextField
                    name="lastname"
                    label="Nachname"
                    InputLabelProps={{ shrink: true }}
                    isRequired={true}
                  />
                </Grid>
                <Grid item xs={3}>
                  <TextField name="telephone" label="Telefon" InputLabelProps={{ shrink: true }} />
                </Grid>
                <Grid item xs={3}>
                  <TextField name="fax" label="Fax" InputLabelProps={{ shrink: true }} />
                </Grid>
                <Grid item xs={3}>
                  <TextField name="email" label="E-Mail" InputLabelProps={{ shrink: true }} />
                </Grid>
                <Grid item xs={3}>
                  <TextField name="weburl" label="Web" InputLabelProps={{ shrink: true }} />
                </Grid>
                <Grid item xs={3}>
                  <TextField name="street" label="Straße" InputLabelProps={{ shrink: true }} />
                </Grid>
                <Grid item xs={3}>
                  <TextField
                    name="streetnumber"
                    label="Hausnummer"
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>
                <Grid item xs={3}>
                  <TextField name="plz" label="Postleitzahl" InputLabelProps={{ shrink: true }} />
                </Grid>
                <Grid item xs={3}>
                  <TextField name="location" label="Ort" InputLabelProps={{ shrink: true }} />
                </Grid>
                <Grid item xs={12}>
                  <FormRadioGroup
                    name={"ukeinternal"}
                    label={"UKE Zugehörigkeit"}
                    options={internExternRadioGroupOptions}
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormRadioGroup
                    name={"addressbookEntryType"}
                    label={"Kontakt Typ"}
                    options={zuweiserSonstigesRadioGroupOptions}
                  />
                </Grid>
              </Grid>
            </DialogContent>
            <DialogActions>
              <Button type="submit">{editing ? "aktualisieren" : "hinzufügen"}</Button>
              <Button onClick={onAbort}>Abbrechen</Button>
            </DialogActions>
          </form>
        </FormProvider>
      </Dialog>
    </React.Fragment>
  )
}
