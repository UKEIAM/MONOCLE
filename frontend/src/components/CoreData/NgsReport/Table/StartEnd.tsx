import { Box, Table, TableBody, TableCell, TableHead, TableRow, Typography } from "@mui/material"
import React from "react"
import { StartEndRange } from "gen/api"

export function StartEnd({ startEndItem }: { startEndItem?: StartEndRange }) {
  return (
    <Box sx={{ margin: 1 }}>
      <Typography variant="h6" gutterBottom component="div">
        Start-Ende-Positionsbereich
      </Typography>
      <Table size="small" aria-label="purchases">
        <TableHead>
          <TableRow>
            <TableCell>Start*</TableCell>{" "}
            {/* FIXME make actually required when its part of a form instead of placeholder "*" */}
            <TableCell>Ende</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {
            <TableRow key={typeof startEndItem}>
              <TableCell> {startEndItem?.start ?? ""}</TableCell>{" "}
              {/* FIXME seems to be unix timestamp, make humanreadable*/}
              <TableCell>{startEndItem?.end ?? ""}</TableCell>
            </TableRow>
          }
        </TableBody>
      </Table>
    </Box>
  )
}
