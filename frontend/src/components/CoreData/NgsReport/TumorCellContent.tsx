import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Grid,
  Typography,
  useTheme,
} from "@mui/material"
import React from "react"
import { TextField } from "components/FormFields/TextField"
import ExpandMoreIcon from "@mui/icons-material/ExpandMore"

export function TumorCellContent({ readonly }: { readonly: boolean }) {
  const theme = useTheme()
  return (
    <Accordion style={{ backgroundColor: theme.palette.primary.light }}>
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls="panel1d-content"
        id="panel1d-header"
      >
        <Typography>Tumorzellgehalt</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Grid container spacing={2} padding={"2rem"}>
          <Grid item xs={12}>
            <TextField
              name={"tumorCellContent.specimen"}
              label={"Tumorproben"}
              disabled={readonly}
              isRequired
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              name={"tumorCellContent.method"}
              label={"Methode"}
              disabled={readonly}
              isRequired
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              name={"tumorCellContent.value"}
              label={"Wert"}
              disabled={readonly}
              isRequired
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
        </Grid>
      </AccordionDetails>
    </Accordion>
  )
}

/**
 * @deprecated
 */
export type tumorCellContentType = {
  id: string
  specimen: string
  method: string
  value: number
}
