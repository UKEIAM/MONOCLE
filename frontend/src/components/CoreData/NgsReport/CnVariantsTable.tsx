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
import { CnVariantsRow } from "./CnVariantsRow"
import { CopyNumberVariant } from "gen/api"
import ExpandMoreIcon from "@mui/icons-material/ExpandMore"

interface Column {
  id:
    | "id"
    | "chromosome"
    | "startRange"
    | "endRange"
    | "totalCopyNumber"
    | "relativeCopyNumber"
    | "cnA"
    | "cnB"
    | "reportedAffectedGenes"
    | "reportedFocality"
    | "type"
    | "copyNumberNeutralLoH"
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
  { id: "startRange", label: "StartRange*", minWidth: 100 },
  { id: "endRange", label: "EndRange*", minWidth: 100 },
  { id: "totalCopyNumber", label: "Total CN", minWidth: 100 },
  { id: "relativeCopyNumber", label: "Relative CN", minWidth: 100 },
  { id: "cnA", label: "CNA", minWidth: 100 },
  { id: "cnB", label: "CNB", minWidth: 100 },
  { id: "reportedAffectedGenes", label: "Reported affected Genes", minWidth: 100 },
  { id: "reportedFocality", label: "Reported Focality", minWidth: 100 },
  { id: "type", label: "Type*", minWidth: 100 },
  { id: "copyNumberNeutralLoH", label: "Copy Number Neutral LoH", minWidth: 100 },
]

export function CnVariantsTable({ cnVariants = [] }: { cnVariants?: CopyNumberVariant[] }) {
  const theme = useTheme()
  return (
    <Accordion style={{ backgroundColor: theme.palette.primary.light }}>
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls="panel1d-content"
        id="panel1d-header"
      >
        <Typography> Copy Number Variant </Typography>
      </AccordionSummary>
      <AccordionDetails>
        {cnVariants.length === 0 ? (
          <Alert severity="info"> Es liegen keine Copy-Number-Variante-Daten vor </Alert>
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
                {cnVariants.map((cnVariant, index) => (
                  <CnVariantsRow cnVariant={cnVariant} />
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </AccordionDetails>
    </Accordion>
  )
}
