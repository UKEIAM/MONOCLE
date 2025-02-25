import React, { useEffect, useState } from "react"
import { Gene } from "./Table/Gene"
import { StartEnd } from "./Table/StartEnd"
import { Collapse, IconButton, TableCell, TableRow, useTheme } from "@mui/material"
import { KeyboardArrowDown, KeyboardArrowUp } from "@mui/icons-material"
import { CopyNumberVariant } from "gen/api"

type ColumnType = "startRange" | "endRange" | "reportedAffectedGenes" | "copyNumberNeutralLoH" | ""

type IconStyleType = {
  startRange: object
  endRange: object
  reportedAffectedGenes: object
  copyNumberNeutralLoH: object
}

export function CnVariantsRow({ cnVariant }: { cnVariant: CopyNumberVariant }) {
  const theme = useTheme()
  const initialIconColors: IconStyleType = {
    startRange: { backgroundColor: "transparent" },
    endRange: { backgroundColor: "transparent" },
    reportedAffectedGenes: { backgroundColor: "transparent" },
    copyNumberNeutralLoH: { backgroundColor: "transparent" },
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
    if (selectedColumn === "startRange") {
      return <StartEnd startEndItem={cnVariant.startRange} />
    } else if (selectedColumn === "endRange") {
      return <StartEnd startEndItem={cnVariant.endRange} />
    } else if (selectedColumn === "reportedAffectedGenes") {
      return <Gene geneItems={cnVariant.reportedAffectedGenes} />
    } else if (selectedColumn === "copyNumberNeutralLoH") {
      return <Gene geneItems={cnVariant.copyNumberNeutralLoH} />
    }
    return null
  }
  return (
    <React.Fragment>
      <TableRow sx={{ "& > *": { borderBottom: "unset" } }}>
        <TableCell> {cnVariant.id} </TableCell>
        <TableCell>{cnVariant.chromosome}</TableCell>
        <TableCell>
          <IconButton
            aria-label="expand row"
            sx={iconStyle.startRange}
            onClick={() => handleClick("startRange")}
          >
            {open ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
          </IconButton>
        </TableCell>
        <TableCell>
          <IconButton
            aria-label="expand row"
            sx={iconStyle.endRange}
            onClick={() => handleClick("endRange")}
          >
            {open ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
          </IconButton>
        </TableCell>
        <TableCell>{cnVariant.totalCopyNumber}</TableCell>
        <TableCell>{cnVariant.relativeCopyNumber}</TableCell>
        <TableCell>{cnVariant.cnA}</TableCell>
        <TableCell>{cnVariant.cnB}</TableCell>
        <TableCell>
          <IconButton
            aria-label="expand row"
            sx={iconStyle.reportedAffectedGenes}
            onClick={() => handleClick("reportedAffectedGenes")}
          >
            {open ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
          </IconButton>
        </TableCell>
        <TableCell>{cnVariant.reportedFocality}</TableCell>
        <TableCell>{cnVariant.type}</TableCell>
        <TableCell>
          <IconButton
            aria-label="expand row"
            sx={iconStyle.copyNumberNeutralLoH}
            onClick={() => handleClick("copyNumberNeutralLoH")}
          >
            {open ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
          </IconButton>
        </TableCell>
      </TableRow>
      {/*This is a new row in the table that will be rendered only if the collapse is open*/}
      <TableRow>
        <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
          <Collapse
            in={open}
            timeout="auto"
            unmountOnExit
            sx={{
              backgroundColor: theme.palette.primary.light,
              borderRadius: "1rem",
              marginBottom: "1rem",
            }}
          >
            {expandSelectedTable()}
          </Collapse>
        </TableCell>
      </TableRow>
    </React.Fragment>
  )
}
