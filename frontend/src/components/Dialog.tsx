import {
  Button,
  Dialog as MuiDialog,
  DialogActions,
  DialogContent,
  DialogTitle,
} from "@mui/material"
import React, { ReactElement } from "react"

interface DialogProps {
  open: boolean
  title: string
  children: ReactElement | ReactElement[]
  submitLabel: string
  abortLabel?: string
  onSubmit: () => void
  onAbort: () => void
}

const Dialog = ({
  open,
  title,
  children,
  submitLabel,
  abortLabel = "Abbrechen",
  onSubmit,
  onAbort,
}: DialogProps) => (
  <>
    {open ? (
      <MuiDialog open={true}>
        {" "}
        <DialogTitle>{title}</DialogTitle>
        <DialogContent>{children}</DialogContent>
        <DialogActions>
          <Button onClick={onSubmit}>{submitLabel}</Button>
          <Button onClick={onAbort}>{abortLabel}</Button>
        </DialogActions>
      </MuiDialog>
    ) : null}
  </>
)

export default Dialog
