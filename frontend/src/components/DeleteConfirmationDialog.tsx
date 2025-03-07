import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Grid,
} from "@mui/material"
import React from "react"
import DangerousIcon from "@mui/icons-material/Dangerous"

interface ConfirmationDialogProps {
  readonly itemNameAndDetails: string
  readonly isOpen: boolean
  readonly onClose: () => void
  readonly onConfirm: () => void
  readonly message?: string
  readonly closeButtonText?: string
  readonly confirmButtonText?: string
  // A list of references that are related to the item that is going to be deleted
  readonly itemReferences?: string[]
}

export const DeleteConfirmationDialog: React.FC<ConfirmationDialogProps> = ({
  itemNameAndDetails,
  isOpen,
  onClose,
  onConfirm,
  message,
  closeButtonText,
  confirmButtonText,
  itemReferences,
}) => {
  return (
    <Dialog open={isOpen} aria-describedby="alert-dialog-slide-description">
      <DialogTitle>{`Löschen von ${itemNameAndDetails}?`}</DialogTitle>
      <DialogContent>
        <DialogContentText id="alert-dialog-slide-description" sx={{ whiteSpace: "pre-line" }}>
          {message ?? (
            <Grid container>
              <Grid item xs={12} md={12}>
                {`Sie sind dabei ${itemNameAndDetails} endgültig zu löschen.
              Stellen Sie sicher, dass zuvor alle Verweise darauf entfernt wurden, da die Aktion andernfalls fehlschlägt.`}
              </Grid>
              {itemReferences && itemReferences.length > 0 ? (
                <Grid item xs={12} md={12}>
                  {"Mögliche Verweise sind:"}
                  <ul>
                    {itemReferences.map((itemOrId: string, index) => (
                      <li key={index}>{itemOrId}</li>
                    ))}
                  </ul>
                </Grid>
              ) : null}
            </Grid>
          )}
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button
          onClick={onConfirm}
          variant="contained"
          color={"error"}
          startIcon={<DangerousIcon />}
          autoFocus
        >
          {confirmButtonText ?? "löschen"}
        </Button>
        <Button onClick={onClose} variant="outlined" color="secondary">
          {closeButtonText ?? "Abbrechen"}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
