import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  useTheme,
} from "@mui/material"
import React from "react"
import { DnaFusionRow } from "./DnaFusionRow"
import { DnaFusion } from "gen/api"
import ExpandMoreIcon from "@mui/icons-material/ExpandMore"

interface Column {
  id: "id" | "fusionPartner5prime" | "fusionPartner3prime" | "reportedNumReads"
  label: string
  minWidth?: number
  align?: "right"
  format?: (value: number) => string
}

const columns: readonly Column[] = [
  // FIXME remove id
  // FIXME make actually required when its part of a form instead of placeholder "*"
  { id: "id", label: "Varianten Id*", minWidth: 250 },
  { id: "fusionPartner5prime", label: "5' Domain", minWidth: 100 },
  { id: "fusionPartner3prime", label: "3' Domain", minWidth: 100 },
  { id: "reportedNumReads", label: "Number reported reads", minWidth: 100 },
]

export function DnaFusionTable({ dnaFusions = [] }: { dnaFusions?: DnaFusion[] }) {
  const theme = useTheme()
  return (
    <Accordion style={{ backgroundColor: theme.palette.primary.light }}>
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls="panel1d-content"
        id="panel1d-header"
      >
        <Typography> DNA Fusion </Typography>
      </AccordionSummary>
      <AccordionDetails>
        {dnaFusions.length === 0 ? (
          <Alert severity="info"> Es liegen keine Dna-Fusion-Daten vor </Alert>
        ) : (
          <TableContainer>
            <Table stickyHeader aria-label="sticky table">
              <TableHead>
                <TableRow>
                  {columns.map((column) => (
                    <TableCell
                      key={column.id}
                      align={column.align}
                      style={{
                        minWidth: column.minWidth,
                        backgroundColor: theme.palette.primary.light,
                      }}
                    >
                      {column.label}
                    </TableCell>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {dnaFusions.map((dnaFusion, index) => (
                  <DnaFusionRow dnaFusion={dnaFusion} />
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </AccordionDetails>
    </Accordion>
  )
}
