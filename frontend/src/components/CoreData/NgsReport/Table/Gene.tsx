import { Box, Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material"
import React from "react"
import { GeneCoding } from "gen/api"

export function Gene({ geneItems = [] }: { geneItems?: GeneCoding[] }) {
  return (
    <Box sx={{ margin: 1 }}>
      <Typography variant="h6" gutterBottom component="div">
        Gen-Kodierung
      </Typography>

      <Table size="small" aria-label="purchases">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Symbol</TableCell>
            <TableCell>Ensembl ID</TableCell>
            <TableCell>HGNC ID</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {geneItems?.map((geneItem, index) => (
            <TableRow key={index}>
              <TableCell component="th" scope="row">
                {geneItem.name}
              </TableCell>
              <TableCell>{geneItem.ensemblId}</TableCell>
              <TableCell>{geneItem.hgncId}</TableCell>
              <TableCell>{geneItem.symbol}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </Box>
  )
}
