import React, { ChangeEvent, Dispatch, DragEvent, SetStateAction, useState } from "react"
import ClearIcon from "@mui/icons-material/Clear"
import UploadFileIcon from "@mui/icons-material/UploadFile"
import { Tooltip } from "@mui/material"

type Props = {
  onFilesSelected: Dispatch<SetStateAction<File[]>>
  selectedFiles: File[]
  width: string
  height: string
  allowedFileEndings: string[]
}

export const isFileEndingInAllowedFileEndingList = (filename: string, fileEndings: String[]) => {
  const fileEnding = "." + filename.split(".").at(-1) // split at . and use the last item
  return fileEndings.includes(fileEnding)
}

// Created from https://medium.com/@dprincecoder/creating-a-drag-and-drop-file-upload-component-in-react-a-step-by-step-guide-4d93b6cc21e0
export default function FileDropzone({
  onFilesSelected,
  selectedFiles,
  width,
  height,
  allowedFileEndings,
}: Props) {
  const [isDraggingOver, setIsDraggingOver] = useState<boolean>()

  const isWrongFileEnding = (filename: string) => {
    return !isFileEndingInAllowedFileEndingList(filename, allowedFileEndings)
  }

  const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = event.target.files
    if (selectedFiles && selectedFiles.length > 0) {
      const newFiles = Array.from(selectedFiles)
      updateFiles(newFiles)
    }
  }

  const handleDrop = (event: DragEvent<HTMLDivElement>) => {
    event.preventDefault()
    const droppedFiles = event.dataTransfer.files
    if (droppedFiles.length > 0) {
      const newFiles = Array.from(droppedFiles)
      updateFiles(newFiles)
    }
    setIsDraggingOver(false)
  }

  const updateFiles = (newFiles: File[]) => {
    // filters out any files that already exist in the previous list, based on the file name and keep the new added files.
    // latest drop or selected duplicated file wins
    onFilesSelected((prevFiles) => {
      const previousFilesWithoutDuplicates = prevFiles.filter(
        (currentFile) => !newFiles.find((file) => file.name === currentFile.name),
      )
      return [...previousFilesWithoutDuplicates, ...newFiles]
    })
  }

  const handleRemoveFile = (index: number) => {
    onFilesSelected((prevFiles) => prevFiles.filter((_, i) => i !== index))
  }

  return (
    <div
      className={`document-uploader ${isDraggingOver ? "drag-over" : ""}`}
      onDrop={handleDrop}
      onDragOver={(event) => event.preventDefault()}
      onDragEnter={() => setIsDraggingOver(true)}
      onDragLeave={(event) => {
        !event.currentTarget.contains(event.relatedTarget as Node | null) &&
          setIsDraggingOver(false)
      }}
      style={{ width: width, height: height }}
    >
      <div className="upload-info">
        <UploadFileIcon className="upload-icon" />
        <div>
          <p>{`"Drag and drop" ihre ${allowedFileEndings} Dateien hier`}</p>
        </div>
      </div>
      {/* The input type=file handles the open file dialog but the label is only seen on website */}
      <input
        type="file"
        hidden
        id="browse"
        onChange={handleFileChange}
        accept={allowedFileEndings.join(",")}
        multiple
      />
      {/* label is styled and used as a button */}
      <label htmlFor="browse" className="browse-btn">
        {"Datei auswählen".toUpperCase()}
      </label>

      {selectedFiles.length > 0 && (
        <div className="file-list">
          {selectedFiles.map((file, index) => (
            <Tooltip
              placement="top"
              title={isWrongFileEnding(file.name) ? "Dateiendung nicht erlaubt" : ""}
              key={`${file.name}-${file.lastModified}`}
            >
              <div
                className={"file-item " + (isWrongFileEnding(file.name) ? "error-file-ending" : "")}
              >
                <div className="file-info">
                  <p>{file.name}</p>
                </div>
                <div className="file-actions">
                  <ClearIcon onClick={() => handleRemoveFile(index)} />
                </div>
              </div>
            </Tooltip>
          ))}
        </div>
      )}

      {selectedFiles.length > 0 && (
        <div className="success-file">
          <p>{selectedFiles.length} Datei(en) selektiert</p>
        </div>
      )}
    </div>
  )
}
