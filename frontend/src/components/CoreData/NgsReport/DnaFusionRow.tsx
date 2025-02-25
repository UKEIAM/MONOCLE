import React, { useEffect, useState } from "react"
import { Collapse, IconButton, TableCell, TableRow, useTheme } from "@mui/material"
import { KeyboardArrowDown, KeyboardArrowUp } from "@mui/icons-material"
import { DnaFusionPartner } from "./Table/DnaFusionPartner"
import { DnaFusion } from "gen/api"

type ColumnType = "fusionPartner5prime" | "fusionPartner3prime" | ""

type IconStyleType = {
  fusionPartner5prime: object
  fusionPartner3prime: object
}

export function DnaFusionRow({ dnaFusion }: { dnaFusion: DnaFusion }) {
  const theme = useTheme()
  const initialIconColors: IconStyleType = {
    fusionPartner5prime: { backgroundColor: "transparent" },
    fusionPartner3prime: { backgroundColor: "transparent" },
  }
  const [iconStyle, setIconStyle] = useState<IconStyleType>(initialIconColors)
  const [open, setOpen] = useState(false)
  const [selectedColumn, setSelectedColumn] = useState<ColumnType>("")

  useEffect(() => {
    const newIconStyle: IconStyleType = { ...initialIconColors }
    if (selectedColumn !== "") {
      newIconStyle[selectedColumn] = {
        ...newIconStyle[selectedColumn],
        backgroundColor: theme.palette.primary.light,
      }
    }
    setIconStyle(newIconStyle)
  }, [selectedColumn])
  const handleClick = (column: ColumnType) => {
    // open will be set to true if either the previous state was false or selectedColumn is not equal to column.
    setOpen((prevOpen) => !prevOpen || selectedColumn !== column)
    // selectedColumn will be set to an empty string if both conditions are true; otherwise, it will be set to the current value of column.
    setSelectedColumn(open && selectedColumn === column ? "" : column)
  }
  const expandSelectedTable = () => {
    if (selectedColumn === "fusionPartner5prime") {
      return (
        <DnaFusionPartner header={"5' Domain"} dnaFusionPartner={dnaFusion.fusionPartner5prime} />
      )
    } else if (selectedColumn === "fusionPartner3prime") {
      return (
        <DnaFusionPartner header={"3' Domain"} dnaFusionPartner={dnaFusion.fusionPartner3prime} />
      )
    }
    return null
  }
  return (
    <React.Fragment>
      <TableRow sx={{ "& > *": { borderBottom: "unset" } }}>
        <TableCell> {dnaFusion.id} </TableCell>
        <TableCell>
          <IconButton
            aria-label="expand row"
            sx={iconStyle.fusionPartner5prime}
            onClick={() => handleClick("fusionPartner5prime")}
          >
            {open ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
          </IconButton>
        </TableCell>

        <TableCell>
          <IconButton
            aria-label="expand row"
            sx={iconStyle.fusionPartner3prime}
            onClick={() => handleClick("fusionPartner3prime")}
          >
            {open ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
          </IconButton>
        </TableCell>

        <TableCell>{dnaFusion.reportedNumReads}</TableCell>
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
            {expandSelectedTable()}
          </Collapse>
        </TableCell>
      </TableRow>
    </React.Fragment>
  )
}
