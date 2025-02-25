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
import { RnaSequencesRow } from "./RnaSequencesRow"
import { RnaSequence } from "gen/api"
import ExpandMoreIcon from "@mui/icons-material/ExpandMore"

interface Column {
  id:
    | "id"
    | "entrezId"
    | "ensemblId"
    | "gene"
    | "transcriptId"
    | "fragmentsPerKilobaseMillion"
    | "fromNGS"
    | "tissueCorrectedExpression"
    | "rawCounts"
    | "librarySize"
    | "cohortRanking"
  label: string
  minWidth?: number
  align?: "right"
  format?: (value: number) => string
}

const columns: readonly Column[] = [
  // FIXME remove id
  // FIXME make actually required when its part of a form instead of placeholder "*"
  { id: "id", label: "Varianten Id*", minWidth: 250 },
  { id: "entrezId", label: "Entrez ID*", minWidth: 100 },
  { id: "ensemblId", label: "Ensemble ID*", minWidth: 100 },
  { id: "gene", label: "Gen*", minWidth: 100 },
  { id: "transcriptId", label: "Transcript ID*", minWidth: 100 },
  { id: "fragmentsPerKilobaseMillion", label: "Fragment per Kb Million*", minWidth: 100 },
  { id: "fromNGS", label: "Identified from NGS*", minWidth: 100 },
  { id: "tissueCorrectedExpression", label: "Tissue corrected expression*", minWidth: 100 },
  { id: "librarySize", label: "Library size*", minWidth: 100 },
  { id: "cohortRanking", label: "Cohort ranking", minWidth: 100 },
]

export function RnaSequencesTable({ rnaSequences = [] }: { rnaSequences?: RnaSequence[] }) {
  const theme = useTheme()
  return (
    <Accordion style={{ backgroundColor: theme.palette.primary.light }}>
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls="panel1d-content"
        id="panel1d-header"
      >
        <Typography> RNA Seq </Typography>
      </AccordionSummary>
      <AccordionDetails>
        {rnaSequences.length === 0 ? (
          <Alert severity="info"> Es liegen keine Rna-Seq-Daten vor </Alert>
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
                {rnaSequences.map((rnaSequence, index) => {
                  return <RnaSequencesRow rnaSequence={rnaSequence} />
                })}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </AccordionDetails>
    </Accordion>
  )
}
