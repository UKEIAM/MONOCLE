import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material"
import Button from "@mui/material/Button"
import React from "react"

type WarningProbs = {
  title: string
  message: string
  open: boolean
  handleClose: () => void
  submitMethod: (data: any) => void
  color?: string
  buttonSubmitText?: string
  buttonDeclineText?: string
}

export function WarningModal({
  title,
  message,
  open,
  handleClose,
  submitMethod,
  color,
  buttonSubmitText,
  buttonDeclineText,
}: WarningProbs) {
  return (
    <React.Fragment>
      <Dialog
        open={open}
        // sx={{
        //   color: "red",
        //   border: "2px solid red",
        //   backgroundColor: "lightyellow",
        // }}
      >
        <DialogTitle id="alert-dialog-title">
          {"Warnung: "}
          {title}
        </DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">{message}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={submitMethod} autoFocus>
            {" "}
            {buttonSubmitText ?? "Trotzdem hinzufügen"}{" "}
          </Button>
          <Button onClick={handleClose}>{buttonDeclineText ?? "Abbrechen"}</Button>
        </DialogActions>
      </Dialog>
    </React.Fragment>
  )
}
