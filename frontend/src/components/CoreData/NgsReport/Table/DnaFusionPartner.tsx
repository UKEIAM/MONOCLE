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
import React, { useEffect, useState } from "react"
import { Gene } from "./Gene"
import { KeyboardArrowDown, KeyboardArrowUp } from "@mui/icons-material"
import { DnaFusionPartner as DnaFusionPartnerType } from "gen/api"

export function DnaFusionPartner({
  header,
  dnaFusionPartner,
}: {
  header: string
  dnaFusionPartner?: DnaFusionPartnerType
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
            <TableCell>Chromosom*</TableCell>{" "}
            {/* FIXME make actually required when its part of a form instead of placeholder "*" */}
            <TableCell>Position*</TableCell>
            <TableCell>Gen*</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          <TableRow>
            <TableCell> {dnaFusionPartner?.chromosome ?? ""} </TableCell>
            <TableCell> {dnaFusionPartner?.position ?? ""} </TableCell>
            <TableCell>
              <IconButton aria-label="expand row" sx={iconStyle} onClick={() => setOpen(!open)}>
                {open ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
              </IconButton>
            </TableCell>
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
                <Gene geneItems={dnaFusionPartner?.gene ? [dnaFusionPartner.gene] : []} />
              </Collapse>
            </TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </Box>
  )
}
