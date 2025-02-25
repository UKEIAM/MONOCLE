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
import { SimpleVariantsRow } from "./SimpleVariantsRow"
import { SimpleVariant } from "gen/api"
import ExpandMoreIcon from "@mui/icons-material/ExpandMore"

interface Column {
  id:
    | "id"
    | "chromosome"
    | "gene"
    | "startEnd"
    | "refAllele"
    | "altAllele"
    | "dnaChange"
    | "aminoAcidChange"
    | "readDepth"
    | "allelicFrequency"
    | "cosmicId"
    | "dbSNPId"
    | "interpretation"
  label: string
  minWidth?: number
  align?: "right"
  format?: (value: number) => string
}

const columns: readonly Column[] = [
  // FIXME remove id
  // FIXME make actually required when its part of a form instead of placeholder "*"
  { id: "id", label: "Varianten Id*", minWidth: 250 },
  { id: "chromosome", label: "Chromosom*", minWidth: 100 },
  { id: "gene", label: "Gen", minWidth: 100 },
  { id: "startEnd", label: "StartEnde*", minWidth: 100 },
  { id: "refAllele", label: "Ref*", minWidth: 100 },
  { id: "altAllele", label: "Alt*", minWidth: 100 },
  { id: "dnaChange", label: "cDNA Change", minWidth: 100 },
  { id: "aminoAcidChange", label: "Amino Acid Change", minWidth: 100 },
  { id: "readDepth", label: "Read Depth*", minWidth: 100 },
  { id: "allelicFrequency", label: "Allelic Frequency*", minWidth: 100 },
  { id: "cosmicId", label: "COSMIC ID", minWidth: 100 },
  { id: "dbSNPId", label: "dbSNP ID", minWidth: 100 },
  { id: "interpretation", label: "Interpretation*", minWidth: 100 },
]

export function SimpleVariantsTable({ simpleVariants }: { simpleVariants: SimpleVariant[] }) {
  const theme = useTheme()
  return (
    <Accordion style={{ backgroundColor: theme.palette.primary.light }}>
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls="panel1d-content"
        id="panel1d-header"
      >
        <Typography> Einfache Variante Tabelle </Typography>
      </AccordionSummary>
      <AccordionDetails>
        {simpleVariants.length === 0 ? (
          <Alert severity="info"> Es liegen keine einfache-Variante-Daten vor. </Alert>
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
                {simpleVariants.map((simpleVariant, index) => (
                  <SimpleVariantsRow simpleVariant={simpleVariant} />
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </AccordionDetails>
    </Accordion>
  )
}
