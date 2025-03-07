import React, { useState } from "react"
import FileDropzone, { isFileEndingInAllowedFileEndingList } from "./components/FileDropzone"
import { Button } from "@mui/material"
import { useNotification } from "hooks/useNotification"
import { useApi } from "hooks/useApi"

export default function BioInfUpload() {
  const { UploadApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const [files, setFiles] = useState<File[]>([])
  const allowedFileEndings = (window.config.ALLOWED_BIOINF_FILEENDINGS ?? ".pdf").split(",")

  const hasOneFileWrongFileEndings = () => {
    return files.some((value: File) => {
      return !isFileEndingInAllowedFileEndingList(value.name, allowedFileEndings)
    })
  }

  const handleUploadClick = () => {
    if (!files) return

    if (hasOneFileWrongFileEndings()) {
      showErrorNotification("Nicht alle Dateiendungen sind erlaubt.")
      return
    }

    for (const file of files) {
      UploadApi.uploadFile(file)
        .then(() => {
          setFiles([])
          showSuccessNotification("Die Datei(en) wurde(n) erfolgreich hochgeladen.")
        })
        .catch(() => {
          showErrorNotification("Beim Hochladen ist ein Fehler aufgetreten.")
        })
    }
  }

  return (
    <div style={{ display: "flex", justifyContent: "center" }}>
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          gap: 15,
          marginTop: "60px",
        }}
      >
        <FileDropzone
          onFilesSelected={setFiles}
          selectedFiles={files}
          width="40rem"
          height="20rem"
          allowedFileEndings={allowedFileEndings}
        />
        {files.length > 0 && (
          <Button
            variant={"contained"}
            disabled={hasOneFileWrongFileEndings()}
            onClick={handleUploadClick}
          >
            Hochladen
          </Button>
        )}
      </div>
    </div>
  )
}
