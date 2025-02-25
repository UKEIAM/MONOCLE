import React from "react"
import {
  Button,
  Card,
  CardContent,
  CardHeader,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  useTheme,
} from "@mui/material"
import DeleteIcon from "@mui/icons-material/Delete"
import FileDownloadIcon from "@mui/icons-material/FileDownload"
import CloudUploadIcon from "@mui/icons-material/CloudUpload"
import { KcReport } from "../../gen/api"
import { toGermanDateTimeFormat } from "../../utils/Formats"
import { AxiosHeaders, AxiosResponse } from "axios"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

// Define props interface for ReportsCard
interface ReportsCardProps {
  episodeId: string
}

const KcReportsTable: React.FC<ReportsCardProps> = ({ episodeId }) => {
  const theme = useTheme()
  const { KcReportApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const [kcReports, setKcReports] = React.useState<KcReport[]>([])
  const fileInputRef = React.useRef<HTMLInputElement>(null) // Create a reference for the file input
  const allowedFileEndings = (window.config.ALLOWED_KC_REPORT_FILEENDINGS ?? ".pdf").split(",")

  React.useEffect(() => {
    // Fetch kc reports
    const fetchReports = () => {
      KcReportApi.getKcReportsForEpisode(episodeId)
        .then((response) => setKcReports(response.data))
        .catch(() => {
          showErrorNotification("Fehler beim Abrufen der Berichte.")
        })
    }
    // Fetch kc reports
    fetchReports()
  }, [episodeId, KcReportApi, showErrorNotification])

  const downloadFileFromResponse = (response: AxiosResponse<Blob, any>) => {
    // Create a URL for the file blob
    const url = window.URL.createObjectURL(new Blob([response.data]))

    // Attempt to get the filename from content-disposition header
    const disposition = (response.headers as AxiosHeaders)?.get("content-disposition")
    // give a default file name
    let fileName = "downloaded_file"

    if (typeof disposition === "string" && disposition.includes("filename=")) {
      fileName = disposition.split("filename=")[1].replace(/"/g, "").trim()
    }

    // Create a link to download the file
    const link = document.createElement("a")
    link.href = url
    link.setAttribute("download", fileName)
    document.body.appendChild(link)
    link.click()

    // Clean up the link and URL object
    link.remove()
    window.URL.revokeObjectURL(url)
  }

  const handleUploadClick = (file: File) => {
    if (!file) return

    const fileEnding = "." + file.name.split(".").pop() // Get file extension

    if (!allowedFileEndings.includes(fileEnding)) {
      showErrorNotification("Dateiendung nicht erlaubt.")
      return
    }

    KcReportApi.uploadKcReport(episodeId, file)
      .then((response) => {
        setKcReports(response.data)
        showSuccessNotification("Die Datei wurde erfolgreich hochgeladen.")
      })
      .catch((error) => {
        if (error.response.status === 409) {
          showErrorNotification("Ein Bericht mit diesem Namen existiert bereits.")
        } else if (error.response.status === 415) {
          showErrorNotification("Dateiendung nicht erlaubt.")
        } else {
          showErrorNotification("Beim Hochladen ist ein Fehler aufgetreten.")
        }
      })
  }

  const handleDownloadClick = (episodeId: string | undefined, reportId: string | undefined) => {
    if (reportId === undefined || episodeId === undefined) {
      return
    }
    KcReportApi.getKcReportFile(episodeId, reportId)
      .then((response) => {
        downloadFileFromResponse(response)
      })
      .catch((error: any) => {
        showErrorNotification("Beim Download ist ein Fehler aufgetreten.")
      })
  }

  const handleDeleteClick = (episodeId: string | undefined, reportId: string | undefined) => {
    if (reportId === undefined || episodeId === undefined) {
      return
    }
    KcReportApi.deleteKcReport(episodeId, reportId)
      .then(() => {
        setKcReports(kcReports.filter((kcReport) => kcReport.id !== reportId))
        showSuccessNotification("Die Datei wurde erfolgreich gelöscht.")
      })
      .catch((error: any) => {
        showErrorNotification("Beim Löschen ist ein Fehler aufgetreten.")
      })
  }

  return (
    <>
      <Card sx={{ boxShadow: 3, borderRadius: 2, margin: "1rem" }}>
        <CardHeader
          title="Berichte Übersicht"
          subheader="Liste der hochgeladenen KC-Berichte"
          sx={{ backgroundColor: "#f4f4f4", borderRadius: "8px 8px 0 0" }}
        />
        <CardContent>
          <TableContainer
            component={Paper}
            sx={{ boxShadow: 0, borderRadius: 2, overflow: "hidden" }}
          >
            <Table sx={{ minWidth: 400 }} aria-label="simple table">
              <TableHead>
                <TableRow sx={{ backgroundColor: "#f5f5f5" }}>
                  <TableCell sx={{ fontWeight: "bold", padding: 2 }}>Name</TableCell>
                  <TableCell align="right" sx={{ fontWeight: "bold", padding: 2 }}>
                    Hochgeladen am
                  </TableCell>
                  <TableCell align="right" sx={{ fontWeight: "bold", padding: 2 }}>
                    Aktionen
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {kcReports.map((kcReport) => (
                  <TableRow
                    key={kcReport.id}
                    sx={{
                      "&:hover": { backgroundColor: theme.palette.primary.light },
                    }}
                  >
                    <TableCell component="th" scope="row" sx={{ padding: 2 }}>
                      {kcReport.fileName}
                    </TableCell>
                    <TableCell align="right" sx={{ padding: 2 }}>
                      {toGermanDateTimeFormat(kcReport.createdAt)}
                    </TableCell>
                    <TableCell align="right" sx={{ padding: 2 }}>
                      <IconButton
                        edge="end"
                        aria-label="download"
                        onClick={() => handleDownloadClick(episodeId, kcReport.id)}
                        sx={{ marginRight: 1 }}
                      >
                        <FileDownloadIcon />
                      </IconButton>
                      <IconButton
                        edge="end"
                        aria-label="delete"
                        onClick={() => handleDeleteClick(episodeId, kcReport.id)}
                      >
                        <DeleteIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
        <Button
          variant="contained"
          startIcon={<CloudUploadIcon />}
          style={{ margin: "1rem" }}
          onClick={() => fileInputRef.current?.click()} // Trigger file input click
        >
          Bericht hochladen
          <input
            type="file"
            ref={fileInputRef} // Attach the ref to the file input
            onChange={(event) => {
              const file = event.target.files?.[0]
              if (file) handleUploadClick(file)
            }}
            style={{ display: "none" }}
          />
        </Button>
      </Card>
    </>
  )
}

export default KcReportsTable
