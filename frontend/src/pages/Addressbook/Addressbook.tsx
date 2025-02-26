import { useEffect, useState } from "react"
import DeleteIcon from "@mui/icons-material/Delete"
import EditIcon from "@mui/icons-material/Edit"
import { Button, Card, CardContent, CardHeader, Grid, IconButton, Typography } from "@mui/material"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"
import { AddressbookEntry } from "gen/api"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import { Link } from "react-router-dom"
import { addressEntryToString } from "utils/Formats"
import { AddEditEntryDialog } from "./components/AddEditEntryDialog"

export default function Addressbook() {
  const { AddressbookentryApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()

  const [addressbook, setAddressbook] = useState<AddressbookEntry[]>([])
  const [selected, setSelected] = useState<AddressbookEntry>()
  const [dialogOpen, setDialogOpen] = useState<boolean>(false)
  const [deleteOpen, setDeleteOpen] = useState<boolean>(false)

  useEffect(() => {
    fetchAdressbook()
  }, [])

  const fetchAdressbook = () => {
    AddressbookentryApi.getAddressbook()
      .then(({ data }) => {
        setAddressbook(data)
      })
      .catch(() =>
        showErrorNotification(
          "Das Addressbuch konnten nicht geladen werden. Bitte versuchen Sie es später erneut",
        ),
      )
  }

  const handleDelete = () => {
    const addressEntryId = selected?.id
    if (addressEntryId == undefined) {
      // this should not happen
      showErrorNotification("Beim Löschen ist ein Fehler aufgetreten.")
      return
    }

    AddressbookentryApi.deleteAddressbookEntry(addressEntryId)
      .then(() => {
        setDeleteOpen(false)
        showSuccessNotification("Der Adresseintrag wurde erfolgreich gelöscht.")
        fetchAdressbook()
      })
      .catch(() => showErrorNotification("Beim Löschen ist ein Fehler aufgetreten."))
  }

  return (
    <>
      <Grid container spacing={2}>
        <Grid item xs={12} sx={{ display: "flex", alignItems: "center" }}>
          <Grid item xs={6}>
            <Link to="../patients" style={{ textDecoration: "none" }}>
              <Button variant="contained"> Patientenübersicht </Button>
            </Link>
          </Grid>

          <Grid item xs={6} sx={{ textAlign: "end !important" }}>
            <Button
              onClick={() => {
                setSelected(undefined)
                setDialogOpen(true)
              }}
              variant="contained"
            >
              Adresse hinzufügen
            </Button>
          </Grid>
        </Grid>
        {addressbook?.map((addressbookEntry: AddressbookEntry) => (
          <Grid item xs={3}>
            <Card>
              <CardHeader
                action={
                  <>
                    <IconButton
                      aria-label="settings"
                      onClick={() => {
                        setSelected(addressbook.find((elem) => elem.id === addressbookEntry.id))
                        setDialogOpen(true)
                      }}
                    >
                      <EditIcon />
                    </IconButton>
                    <IconButton
                      onClick={() => {
                        setSelected(addressbookEntry)
                        setDeleteOpen(true)
                      }}
                    >
                      <DeleteIcon />
                    </IconButton>
                  </>
                }
                title={`${addressbookEntry.title} ${addressbookEntry.firstname} ${addressbookEntry.lastname}`}
                subheader={`${addressbookEntry.ukeinternal === true ? "UKE intern" : "UKE extern"} (${addressbookEntry.addressbookEntryType})`}
              />
              <CardContent>
                <Typography style={{ display: "flex" }}>
                  {addressbookEntry.telephone ? "Tel: " + addressbookEntry.telephone : null}
                </Typography>
                <Typography style={{ display: "flex" }}>
                  {addressbookEntry.fax ? "Fax: " + addressbookEntry.fax : null}
                </Typography>
                <Typography style={{ display: "flex" }}>
                  {addressbookEntry.email ? "Mail: " + addressbookEntry.email : null}
                </Typography>
                <Typography style={{ display: "flex" }}>
                  {addressbookEntry.weburl ? "Web: " + addressbookEntry.weburl : null}
                </Typography>
                <Typography style={{ display: "flex", paddingTop: 10 }}>
                  {addressbookEntry.street && addressbookEntry.streetnumber
                    ? `${addressbookEntry.street}  ${addressbookEntry.streetnumber}, ${addressbookEntry.plz} ${addressbookEntry.location}`
                    : null}
                </Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <AddEditEntryDialog
        addressBookEntry={selected}
        open={dialogOpen}
        onSave={() => {
          setDialogOpen(false)
          fetchAdressbook()
        }}
        onAbort={() => setDialogOpen(false)}
      />

      {selected && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`Adresseintrag ${addressEntryToString(selected)}`}
          isOpen={deleteOpen}
          onClose={() => setDeleteOpen(false)}
          onConfirm={handleDelete}
        />
      )}
    </>
  )
}
