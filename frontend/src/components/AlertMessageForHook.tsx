import { Alert, Snackbar } from "@mui/material"

interface AlertMessageProps {
  open: boolean
  message: string
  severity: "error" | "warning" | "info" | "success"
  onClose: () => void
}

export default function AlertMessageForHook({
  open,
  message,
  severity,
  onClose,
}: AlertMessageProps) {
  return (
    <Snackbar
      open={open}
      autoHideDuration={severity === "error" ? null : 5000}
      onClose={severity === "error" ? () => null : onClose}
      anchorOrigin={{ vertical: "top", horizontal: "center" }}
    >
      <Alert severity={severity} onClose={onClose} variant="filled">
        {message}
      </Alert>
    </Snackbar>
  )
}
