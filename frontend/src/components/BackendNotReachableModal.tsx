import { Alert, AlertTitle, Backdrop, Typography } from "@mui/material"

interface BackendNotReachableModalProps {
  readonly isVisible: boolean
}

export const BackendNotReachableModal = ({ isVisible }: BackendNotReachableModalProps) => (
  <Backdrop sx={(theme) => ({ color: "#fff", zIndex: theme.zIndex.modal + 1 })} open={isVisible}>
    <Alert severity="error" sx={{ paddingRight: "30px" }}>
      <AlertTitle sx={{ fontWeight: "bold" }}>Probleme beim Erreichen des Servers</AlertTitle>
      <Typography sx={{ whiteSpace: "pre-line" }}>
        Es wird versucht, die Verbindung zum Server wiederherzustellen.
      </Typography>
    </Alert>
  </Backdrop>
)
