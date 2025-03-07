import React from "react"
import {
  Button,
  IconButton,
  Table as MuiTable,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from "@mui/material"
import SearchIcon from "@mui/icons-material/Search"
import DeleteIcon from "@mui/icons-material/Delete"
import EditIcon from "@mui/icons-material/Edit"

export interface Column {
  id?: string
  label: string
  minWidth?: number
  align?: "right"
  format?: (value: any) => string
  formField?: string
}

export interface Row {
  cells: Cell[]
  onView?: () => void
  onEdit?: () => void
  onDelete?: () => void
  rowKey?: string
}

export interface Cell {
  value: any
}

export interface AddRowButton {
  disabled?: boolean
  label: string
  onClick: () => void
}

export interface TableProps {
  columns: Column[]
  rows: Row[]
  addRowButton?: AddRowButton
}

export function Table({ columns, rows, addRowButton }: TableProps): JSX.Element {
  return (
    <TableContainer>
      <MuiTable>
        <TableHead>
          <TableRow>
            {columns.map((column) => (
              <TableCell
                key={column.label}
                style={{ minWidth: column.minWidth, fontWeight: "bold" }}
              >
                {column.label}
              </TableCell>
            ))}
            <TableCell />
          </TableRow>
        </TableHead>
        <TableBody>
          {rows?.map((row) => (
            <TableRow hover={row.onView !== undefined} onClick={row.onView} key={row.rowKey}>
              {row.cells.map((cell, index) => (
                <TableCell
                  key={row.rowKey + "-" + index}
                  sx={{ whiteSpace: "normal", wordWrap: "break-word" }}
                >
                  {(() => {
                    const value: string = columns[index].format?.(cell.value) || cell.value || ""
                    return (
                      <>
                        {value}
                        {/*{value.split("\n").map(v => <Typography>{v}</Typography>) }*/}
                      </>
                    )
                  })()}
                </TableCell>
              ))}
              <TableCell align="right">
                {row.onView ? (
                  <IconButton
                    onClick={(e) => {
                      row.onView?.()
                      e.stopPropagation()
                    }}
                  >
                    <SearchIcon />
                  </IconButton>
                ) : null}
                {row.onEdit ? (
                  <IconButton
                    onClick={(e) => {
                      row.onEdit?.()
                      e.stopPropagation()
                    }}
                  >
                    <EditIcon />
                  </IconButton>
                ) : null}
                {row.onDelete ? (
                  <IconButton
                    onClick={(e) => {
                      row.onDelete?.()
                      e.stopPropagation()
                    }}
                  >
                    <DeleteIcon />
                  </IconButton>
                ) : null}
              </TableCell>
            </TableRow>
          ))}
          {addRowButton === undefined ? null : (
            <TableRow>
              <TableCell align="center" colSpan={99}>
                <Button
                  type="submit"
                  disabled={addRowButton.disabled}
                  variant="contained"
                  onClick={addRowButton.onClick}
                >
                  {addRowButton.label}
                </Button>
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </MuiTable>
    </TableContainer>
  )
}
