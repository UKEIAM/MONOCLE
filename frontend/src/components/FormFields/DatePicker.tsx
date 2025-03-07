import { deDE } from "@mui/x-date-pickers/locales"
import { Controller, useFormContext } from "react-hook-form"
import { FormControl } from "@mui/material"
import { DateValidationError, LocalizationProvider } from "@mui/x-date-pickers"
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs"
import { DatePicker as MuiDatePicker } from "@mui/x-date-pickers/DatePicker/DatePicker"
import dayjs, { Dayjs } from "dayjs"
import React from "react"
import { PickerChangeHandlerContext } from "@mui/x-date-pickers"

export interface DatePickerProps {
  name: string
  label?: string
  maxdate?: Dayjs
  mindate?: Dayjs
  disabled?: boolean
  isRequired?: boolean
  sxStyle?: {} | null
  addHandleOnChange?: (
    date: dayjs.Dayjs | null,
    context?: PickerChangeHandlerContext<DateValidationError>,
    fieldName?: string,
  ) => void
}

export default function DatePicker({
  name,
  label,
  maxdate,
  mindate,
  disabled = false,
  isRequired = false,
  sxStyle,
  addHandleOnChange,
}: DatePickerProps) {
  const germanLocale = deDE.components.MuiLocalizationProvider.defaultProps.localeText
  const { setValue } = useFormContext()

  if (sxStyle === undefined) {
    sxStyle = null
  }

  return (
    <FormControl fullWidth>
      <Controller
        name={name}
        defaultValue={undefined}
        render={({ field }) => (
          <LocalizationProvider
            dateAdapter={AdapterDayjs}
            adapterLocale="de-DE"
            localeText={germanLocale}
          >
            <MuiDatePicker
              {...field}
              label={label}
              format="DD.MM.YYYY"
              sx={sxStyle}
              maxDate={maxdate}
              minDate={mindate}
              disabled={disabled}
              value={
                Boolean(field.value) && field.value !== "Invalid Date"
                  ? dayjs(field.value, { format: "YYYY-MM-DD" })
                  : null
              }
              onChange={(date, context) => {
                if (!date?.isValid()) date = null
                if (addHandleOnChange) addHandleOnChange(date, context, name)
                if (date?.isValid()) setValue(name, date?.format("YYYY-MM-DD"))
              }}
              slotProps={{
                textField: {
                  required: isRequired,
                  // error: isRequired && (field.value === undefined || field.value === "Invalid Date" || (maxdate !== undefined && field.value > maxdate)),
                  // helperText: isRequired && field.value === undefined ? "Dieses Feld ist ein Pflichtfeld" : ""
                },
              }}
            />
          </LocalizationProvider>
        )}
      />
    </FormControl>
  )
}
