import { Box, Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material"
import React from "react"
import { ValueSet } from "gen/api"

export function CodeTable({ codeItem, header }: { codeItem?: ValueSet; header: string }) {
  return (
    <Box sx={{ margin: 1 }}>
      <Typography variant="h6" gutterBottom component="div">
        {header}
      </Typography>
      <Table size="small" aria-label="purchases">
        <TableHead>
          <TableRow>
            <TableCell>Kodierung</TableCell>
            <TableCell>System</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {
            <TableRow key={typeof codeItem}>
              <TableCell> {codeItem?.code ?? ""}</TableCell>{" "}
              {/* FIXME valueSet.code should be required if valueSet is required. i.e. simpleVariant.interpretation.code is missing a "*" */}
              <TableCell>{codeItem?.system ?? ""}</TableCell>
            </TableRow>
          }
        </TableBody>
      </Table>
    </Box>
  )
}
