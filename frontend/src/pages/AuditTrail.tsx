import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
} from "@mui/material"
import React, { useRef } from "react"
import { AuditTrailEntry } from "../gen/api"
import { useApi } from "../hooks/useApi"

interface Column {
  id: "id" | "dateOfEntry" | "entry" | "userId"
  label: string
  minWidth?: number
}

const columns: readonly Column[] = [
  { id: "id", label: "ID", minWidth: 300 },
  { id: "dateOfEntry", label: "Datum", minWidth: 200 },
  { id: "entry", label: "Aktion", minWidth: 400 },
  { id: "userId", label: "Benutzer", minWidth: 100 },
]
export default function AuditTrail() {
  const [auditTrails, setAuditTrails] = React.useState<undefined | AuditTrailEntry[]>(undefined)
  const [page, setPage] = React.useState(0)
  const [rowsPerPage, setRowsPerPage] = React.useState(10)
  const { AudittrailentryApi } = useApi()

  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage)
  }

  const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(+event.target.value)
    setPage(0)
  }
  const fetchedAuditTrails = useRef(false)
  React.useEffect(() => {
    if (!fetchedAuditTrails.current) {
      fetchedAuditTrails.current = true
      const fetchAuditTrails = async () => {
        try {
          const response = await AudittrailentryApi.getAuditTrails()
          setAuditTrails(response.data as AuditTrailEntry[])
          console.log(response.data)
        } catch (error) {
          console.log("Something went wrong: " + error)
        }
      }
      fetchAuditTrails()
    }
  }, [])
  return (
    <Paper sx={{ width: "100%", overflow: "hidden" }}>
      <TableContainer sx={{ maxHeight: 600 }}>
        <Table stickyHeader aria-label="sticky table">
          <TableHead>
            <TableRow>
              {columns?.map((column) => (
                <TableCell key={column.id} style={{ minWidth: column.minWidth }}>
                  {column.label}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {auditTrails
              ?.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
              .map((auditTrail) => {
                return (
                  <TableRow hover role={"checkbox"} tabIndex={-1} key={auditTrail.id}>
                    {columns.map((column) => {
                      const value = auditTrail[column.id]
                      return <TableCell key={column.id}>{value}</TableCell>
                    })}
                  </TableRow>
                )
              })}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        rowsPerPageOptions={[10, 25, 100]}
        component="div"
        count={auditTrails?.length || 0}
        rowsPerPage={rowsPerPage}
        page={page}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
      />
    </Paper>
  )
}
