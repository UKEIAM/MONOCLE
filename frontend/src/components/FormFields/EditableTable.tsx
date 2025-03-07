import {
  Alert,
  Grid,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
} from "@mui/material"
import React, { useState } from "react"
import { useFieldArray, useFormContext } from "react-hook-form"
import DeleteIcon from "@mui/icons-material/Delete"
import Button from "@mui/material/Button"
import dayjs from "dayjs"
import AutocompleteFreeSolo from "./AutocompleteFreeSolo"
import "./EditableTable.css"
import { DeleteConfirmationDialog } from "components/DeleteConfirmationDialog"
import FormSelect from "./FormSelect"
import FormDatePicker from "./FormDatePicker"
import FormAutocomplete from "./FormAutocomplete"

type EditableTableProps = {
  fieldName: string
  rowTypes: FieldType[]
  addItemIsOk?: () => boolean
  headerText?: string
  buttonText?: string
  alterText?: string
  headerLabel?: string[]
  placeholder?: string
}

export type FieldType = {
  fieldType: "input" | "numberInput" | "autocompleteFreeSolo" | "autocomplete" | "select" | "date"
  required: boolean
  fieldElement: string
  selectItems?: {
    id: string
    label: string
  }[]
  numberInputProps?: {
    step?: number
    min?: number
    max?: number
  }
  columnWidth?: string
  onBlur?: (index: number | undefined) => void
}

export function EditableTable({
  fieldName,
  rowTypes,
  addItemIsOk,
  headerText,
  buttonText,
  alterText,
  headerLabel,
  placeholder,
}: EditableTableProps) {
  const { register, control } = useFormContext()
  const { append, remove, fields } = useFieldArray({ control, name: fieldName })
  const [emptyRowError, setEmptyRowError] = useState<boolean>(false)
  const [confirmOpen, setConfirmOpen] = useState<boolean>(false)
  const [indexToBeDeleted, setIndexToBeDeleted] = useState<number>()

  const deleteEntryConfirmation = (index: number) => {
    setIndexToBeDeleted(index)
    setConfirmOpen(true)
  }

  const addItem = () => {
    if (addItemIsOk === undefined || addItemIsOk()) {
      append({})
      setEmptyRowError(false)
    } else {
      setEmptyRowError(true)
    }
  }

  const noBorder = {
    boxShadow: "none",
    ".MuiOutlinedInput-notchedOutline": { border: 0 },
  }

  return (
    <>
      <Grid container item spacing={2} alignItems="center">
        <Grid item xs={12}>
          <b>{headerText}</b>
        </Grid>

        {emptyRowError && (
          <Grid container spacing={2} padding={"2rem"}>
            <Alert severity="error">
              {" "}
              {alterText || "Bitte füllen Sie die Tabelle aus, bevor Sie weitere Zeilen hinzufügen"}
            </Alert>
          </Grid>
        )}

        <Grid item xs={12}>
          <TableContainer>
            <Table className="editable-table">
              <TableHead>
                <TableRow>
                  {headerLabel &&
                    headerLabel.map((label: string, index) => (
                      <TableCell key={label} align="center" className={"header-cell"}>
                        {label + (rowTypes[index].required && fields.length > 0 ? " *" : "")}
                      </TableCell>
                    ))}
                  <TableCell />
                </TableRow>
              </TableHead>
              <TableBody>
                {fields?.map((field, index: number) => (
                  <TableRow key={index + "-row"} id={"test-row-" + index}>
                    {rowTypes.map(
                      (value: FieldType, cellIndex: number) =>
                        // If Field is an input Field
                        (value.fieldType === "input" && (
                          <TableCell
                            id={cellIndex + "-cell-input"}
                            key={cellIndex + "-cell-input"}
                            align="center"
                            className={"row-cell"}
                            sx={{ width: value.columnWidth }}
                          >
                            <TextField
                              variant="standard"
                              required={value.required}
                              placeholder={placeholder !== undefined ? placeholder : "Eingabe"}
                              InputProps={{
                                disableUnderline: true,
                              }}
                              {...register(`${fieldName}.${index}.${value.fieldElement}`)}
                              onBlur={() => {
                                value.onBlur && value.onBlur(index)
                              }}
                            />
                            {/*<FormTe<xtField*/}
                            {/*  name={`${fieldName}.${index}.${value.fieldElement}`}*/}
                            {/*  validationRules={{ required: value.required }}*/}
                            {/*  sx={noBorder}*/}
                            {/*/>>*/}
                          </TableCell>
                        )) ||
                        (value.fieldType === "numberInput" && (
                          <TableCell
                            id={cellIndex + "-cell-input"}
                            key={cellIndex + "-cell-number-input"}
                            align="center"
                            className={"row-cell"}
                            sx={{ width: value.columnWidth }} // sx={{ padding: "5px", borderRight: "0.5px lightgrey solid" }}
                          >
                            <TextField
                              {...register(`${fieldName}.${index}.${value.fieldElement}`)}
                              type={"number"}
                              variant="standard"
                              required={value.required}
                              placeholder={placeholder || "Eingabe"}
                              // InputProps={{
                              //   disableUnderline: true,
                              // }}
                              sx={noBorder}
                              inputProps={{
                                step: value.numberInputProps?.step,
                                min: value.numberInputProps?.min,
                                max: value.numberInputProps?.max,
                              }}
                            />
                          </TableCell>
                        )) ||
                        // OR if Field is a select Field
                        (value.fieldType === "select" && (
                          <TableCell
                            id={cellIndex + "-cell-select"}
                            key={cellIndex + "-cell-select"}
                            // align="center"
                            className={"row-cell select-cell"}
                            sx={{ width: value.columnWidth }}
                          >
                            <FormSelect
                              key={field.id} // very important, otherwise it will not be deleted correctly
                              name={`${fieldName}.${index}.${value.fieldElement}`}
                              validationRules={{ required: value.required }}
                              options={
                                value.selectItems?.map((elem) => ({
                                  label: elem.label,
                                  value: elem.id,
                                })) ?? []
                              }
                              sx={noBorder}
                            />
                          </TableCell>
                        )) ||
                        // OR if Field is a date picker
                        (value.fieldType === "date" && (
                          <TableCell
                            id={cellIndex + "-cell-date"}
                            key={cellIndex + "-cell-date"}
                            align="center"
                            className={"row-cell date-cell"}
                            sx={{ width: value.columnWidth }}
                          >
                            <FormDatePicker
                              key={field.id} // very important, otherwise it will not be deleted correctly
                              name={`${fieldName}.${index}.${value.fieldElement}`}
                              validationRules={{ required: value.required }}
                              maxdate={dayjs(new Date())}
                              sx={noBorder}
                            />
                          </TableCell>
                        )) ||
                        // OR if Field is an autocomplete free solo
                        (value.fieldType === "autocompleteFreeSolo" && (
                          <TableCell
                            id={cellIndex + "-cell-select"}
                            key={cellIndex + "-cell-autocomplete"}
                            // align="center"
                            className={"row-cell autocomplete-cell"}
                            sx={{ width: value.columnWidth }}
                          >
                            <AutocompleteFreeSolo
                              control={control}
                              label=""
                              name={`${fieldName}.${index}.${value.fieldElement}`}
                              required={value.required}
                              options={value.selectItems?.map((elem) => elem.label) ?? []}
                              loading={false}
                              isTextInputPossible
                              sx={noBorder}
                            />
                          </TableCell>
                        )) ||
                        // OR if Field is an autocomplete
                        (value.fieldType === "autocomplete" && (
                          <TableCell
                            id={cellIndex + "-cell-select"}
                            key={cellIndex + "-cell-autocomplete"}
                            // align="center"
                            className={"row-cell autocomplete-cell"}
                            sx={{ width: value.columnWidth }}
                          >
                            <FormAutocomplete
                              name={`${fieldName}.${index}.${value.fieldElement}`}
                              options={
                                value.selectItems?.map((elem) => ({
                                  label: elem.label,
                                  value: elem.id,
                                })) ?? []
                              }
                              key={field.id}
                              isRequired={value.required}
                              sx={{
                                boxShadow: "none",
                                ".MuiOutlinedInput-notchedOutline": { border: 0 },
                              }}
                            />
                          </TableCell>
                        )),
                    )}
                    {/* Delete Button */}
                    <TableCell>
                      <IconButton
                        className={"delete-button"}
                        onClick={() => deleteEntryConfirmation(index)}
                      >
                        <DeleteIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Grid>
        <Grid item xs={12}>
          <Button type="button" variant="contained" onClick={addItem}>
            {buttonText || "Hinzufügen"}
          </Button>
        </Grid>
      </Grid>
      {indexToBeDeleted !== undefined && (
        <DeleteConfirmationDialog
          itemNameAndDetails={`${headerText} Zeile ${indexToBeDeleted + 1}`}
          isOpen={confirmOpen}
          onClose={() => setConfirmOpen(false)}
          onConfirm={() => {
            remove(indexToBeDeleted)
            setEmptyRowError(false)
            setConfirmOpen(false)
          }}
          message={`Möchten Sie wirklich die Zeile ${indexToBeDeleted + 1} endgültig löschen?`}
        />
      )}
    </>
  )
}
