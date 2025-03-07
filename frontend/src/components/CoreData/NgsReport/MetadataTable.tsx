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
import { Metadata } from "gen/api"
import ExpandMoreIcon from "@mui/icons-material/ExpandMore"

interface Column {
  id: "kitType" | "kitManufacturer" | "sequencer" | "referenceGenome" | "pipeline"
  label: string
  minWidth?: number
  align?: "right"
  format?: (value: number) => string
}

const columns: readonly Column[] = [
  // FIXME make actually required when its part of a form instead of placeholder "*"
  { id: "kitType", label: "Kit-Typ*", minWidth: 250 },
  { id: "kitManufacturer", label: "Kit-Hersteller*", minWidth: 100 },
  { id: "sequencer", label: "Sequenziergerät*", minWidth: 100 },
  { id: "referenceGenome", label: "Referenz-Genom*", minWidth: 100 },
  { id: "pipeline", label: "Pipeline", minWidth: 100 },
]

export function MetadataTable({ listOfMetadata }: { listOfMetadata: Metadata[] }) {
  const theme = useTheme()
  return (
    <Accordion style={{ backgroundColor: theme.palette.primary.light }}>
      <AccordionSummary
        expandIcon={<ExpandMoreIcon />}
        aria-controls="panel1d-content"
        id="panel1d-header"
      >
        <Typography> Metadaten </Typography>
      </AccordionSummary>
      <AccordionDetails>
        {listOfMetadata.length === 0 ? (
          <Alert severity="info"> Es liegen keine Metadaten vor. </Alert>
        ) : (
          <TableContainer sx={{ maxHeight: 440 }}>
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
                {listOfMetadata.map((data, index) => {
                  return (
                    <TableRow hover role="checkbox" key={typeof data}>
                      {columns.map((column) => {
                        return (
                          <TableCell key={column.id} align={column.align}>
                            {data[column.id]}
                          </TableCell>
                        )
                      })}
                    </TableRow>
                  )
                })}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </AccordionDetails>
    </Accordion>
  )
}
