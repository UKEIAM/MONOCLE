import React, { useEffect, useState } from "react"
import { Collapse, IconButton, TableCell, TableRow, useTheme } from "@mui/material"
import { KeyboardArrowDown, KeyboardArrowUp } from "@mui/icons-material"
import { Gene } from "./Table/Gene"
import { RnaSequence } from "gen/api"

export function RnaSequencesRow({ rnaSequence }: { rnaSequence: RnaSequence }) {
  const theme = useTheme()
  const [open, setOpen] = useState(false)
  const [iconStyle, setIconStyle] = useState<object>()

  useEffect(() => {
    open ? setIconStyle({ backgroundColor: theme.palette.primary.light }) : setIconStyle({})
  }, [open])

  return (
    <React.Fragment>
      <TableRow role="checkbox" key={rnaSequence.id}>
        <TableCell> {rnaSequence.id} </TableCell>
        <TableCell> {rnaSequence.entrezId} </TableCell>
        <TableCell> {rnaSequence.ensemblId} </TableCell>
        <TableCell>
          <IconButton aria-label="expand row" sx={iconStyle} onClick={() => setOpen(!open)}>
            {open ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
          </IconButton>
        </TableCell>
        <TableCell> {rnaSequence.transcriptId} </TableCell>
        <TableCell> {rnaSequence.fragmentsPerKilobaseMillion} </TableCell>
        <TableCell> {rnaSequence.fromNGS} </TableCell>
        <TableCell> {rnaSequence.tissueCorrectedExpression} </TableCell>
        <TableCell> {rnaSequence.librarySize} </TableCell>
        <TableCell> {rnaSequence.cohortRanking} </TableCell>
      </TableRow>

      {/*This is a new row in the table that will be rendered only if the collapse is open*/}
      <TableRow>
        <TableCell
          style={{ paddingBottom: 0, paddingTop: 0 }}
          colSpan={6}
          sx={{
            backgroundColor: theme.palette.primary.light,
            borderRadius: "1rem",
            marginBottom: "1rem",
          }}
        >
          <Collapse in={open} timeout="auto" unmountOnExit>
            <Gene geneItems={rnaSequence.gene ? [rnaSequence.gene] : []} />
          </Collapse>
        </TableCell>
      </TableRow>
    </React.Fragment>
  )
}
