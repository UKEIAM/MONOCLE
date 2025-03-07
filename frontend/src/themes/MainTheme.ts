import { createTheme } from "@mui/material"

// link to uke corporate design https://www.uke.de/dateien/einrichtungen/unternehmenskommunikation/dokumente/cd-manual/manual_va3.2.4._anlage1_stand20190722.pdf
export const mainTheme = createTheme({
  palette: {
    primary: {
      light: "#00499210", // unsure where this whould be accessed
      main: "#004992",
      dark: "#004992B0", // B0 ~ 70% Obpacity
      contrastText: "#fff",
    },
    secondary: {
      light: "#ff8b42", // unsure where this whould be accessed
      main: "#575756",
      dark: "#575756B0",
      contrastText: "#fff",
    },
  },
  components: {
    MuiCard: {
      defaultProps: {
        variant: "outlined",
      },
    },
  },
})
