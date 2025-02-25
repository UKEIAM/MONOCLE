import { Collapse, IconButton, TableCell, TableRow, useTheme } from "@mui/material"
import React, { useEffect, useState } from "react"
import { KeyboardArrowDown, KeyboardArrowUp } from "@mui/icons-material"
import { Gene } from "./Table/Gene"
import { StartEnd } from "./Table/StartEnd"
import { CodeTable } from "./Table/Code"
import { SimpleVariant } from "gen/api"

type ColumnType = "gene" | "startEnd" | "dnaChange" | "aminoAcidChange" | "interpretation" | ""

type IconStyleType = {
  gene: object
  startEnd: object
  dnaChange: object
  aminoAcidChange: object
  interpretation: object
}

export function SimpleVariantsRow({ simpleVariant }: { simpleVariant: SimpleVariant }) {
  const theme = useTheme()
  const initialIconColors: IconStyleType = {
    gene: { backgroundColor: "transparent" },
    startEnd: { backgroundColor: "transparent" },
    dnaChange: { backgroundColor: "transparent" },
    aminoAcidChange: { backgroundColor: "transparent" },
    interpretation: { backgroundColor: "transparent" },
  }
  const [open, setOpen] = useState(false)
  const [selectedColumn, setSelectedColumn] = useState<ColumnType>("")
  const [iconStyle, setIconStyle] = useState<IconStyleType>(initialIconColors)
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
    if (selectedColumn === "gene") {
      return <Gene geneItems={simpleVariant.gene ? [simpleVariant.gene] : []} />
    } else if (selectedColumn === "startEnd") {
      return <StartEnd startEndItem={simpleVariant.startEnd} />
    } else if (selectedColumn === "dnaChange") {
      return <CodeTable codeItem={simpleVariant.dnaChange} header="cDNA Change" />
    } else if (selectedColumn === "aminoAcidChange") {
      return <CodeTable codeItem={simpleVariant.aminoAcidChange} header="Amino Acid Change" />
    } else if (selectedColumn === "interpretation") {
      return <CodeTable codeItem={simpleVariant.interpretation} header="Interpretation" />
    }
    return null
  }
  return (
    <React.Fragment>
      <TableRow sx={{ "& > *": { borderBottom: "unset" } }}>
        <TableCell> {simpleVariant.id} </TableCell>
        <TableCell>{simpleVariant.chromosome}</TableCell>
        <TableCell>
          <IconButton
            aria-label="expand row"
            sx={iconStyle.gene}
            onClick={() => handleClick("gene")}
          >
            {open ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
          </IconButton>
        </TableCell>
        <TableCell>
          <IconButton
            aria-label="expand row"
            sx={iconStyle.startEnd}
            onClick={() => handleClick("startEnd")}
          >
            {open ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
          </IconButton>
        </TableCell>
        <TableCell>{simpleVariant.refAllele}</TableCell>
        <TableCell>{simpleVariant.altAllele}</TableCell>
        <TableCell>
          <IconButton
            aria-label="expand row"
            sx={iconStyle.dnaChange}
            onClick={() => handleClick("dnaChange")}
          >
            {open ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
          </IconButton>
        </TableCell>
        <TableCell>
          <IconButton
            aria-label="expand row"
            sx={iconStyle.aminoAcidChange}
            onClick={() => handleClick("aminoAcidChange")}
          >
            {open ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
          </IconButton>
        </TableCell>
        <TableCell>{simpleVariant.readDepth}</TableCell>
        <TableCell>{simpleVariant.allelicFrequency}</TableCell>
        <TableCell>{simpleVariant.cosmicId}</TableCell>
        <TableCell>{simpleVariant.dbSNPId}</TableCell>
        <TableCell>
          <IconButton
            aria-label="expand row"
            sx={iconStyle.interpretation}
            onClick={() => handleClick("interpretation")}
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
