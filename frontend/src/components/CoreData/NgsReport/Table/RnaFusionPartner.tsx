import React, { useEffect, useState } from "react"
import {
  Box,
  Collapse,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Typography,
  useTheme,
} from "@mui/material"
import { KeyboardArrowDown, KeyboardArrowUp } from "@mui/icons-material"
import { Gene } from "./Gene"
import { RnaFusionPartner as RnaFusionPartnerType } from "gen/api"

export function RnaFusionPartner({
  header,
  rnaFusionPartner,
}: {
  header: string
  rnaFusionPartner?: RnaFusionPartnerType
}) {
  const theme = useTheme()
  const [open, setOpen] = useState(false)
  const [iconStyle, setIconStyle] = useState<object>({})

  useEffect(() => {
    open ? setIconStyle({ backgroundColor: theme.palette.primary.light }) : setIconStyle({})
  }, [open])

  return (
    <Box sx={{ margin: 1 }}>
      <Typography variant="h6" gutterBottom component="div">
        {header}
      </Typography>

      <Table size="small" aria-label="purchases">
        <TableHead>
          <TableRow>
            <TableCell>Gen*</TableCell>
            <TableCell>Transkript ID*</TableCell>
            <TableCell>Exon ID*</TableCell>
            <TableCell>Transcript Position*</TableCell>
            <TableCell>Strand*</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          <TableRow>
            <TableCell>
              <IconButton aria-label="expand row" sx={iconStyle} onClick={() => setOpen(!open)}>
                {open ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
              </IconButton>
            </TableCell>
            <TableCell> {rnaFusionPartner?.transcriptId ?? ""} </TableCell>
            <TableCell> {rnaFusionPartner?.exon ?? ""} </TableCell>
            <TableCell> {rnaFusionPartner?.position ?? ""} </TableCell>
            <TableCell> {rnaFusionPartner?.strand ?? ""} </TableCell>
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
                <Gene geneItems={rnaFusionPartner?.gene ? [rnaFusionPartner.gene] : []} />
              </Collapse>
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </Box>
  )
}
