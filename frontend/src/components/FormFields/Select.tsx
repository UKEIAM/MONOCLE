import {
  FormControl,
  InputLabel,
  MenuItem,
  Select as MuiSelect,
  SxProps,
  Theme,
} from "@mui/material"
import { Controller, useFormContext } from "react-hook-form"
import React from "react"

export interface SelectProps {
  name: string
  label?: string
  error?: boolean
  options: { label: string; value: string }[]
  defaultValue?: any
  isRequired?: boolean
  addHandleOnChange?: (event: any) => void
  // shrink?: boolean
  key?: string
  sx?: SxProps<Theme>
}

export default function Select({
  name,
  label,
  error = false,
  options,
  defaultValue,
  isRequired = false,
  // shrink = undefined,
  addHandleOnChange,
  key,
  sx = {},
}: SelectProps) {
  const { setValue } = useFormContext()

  return (
    <FormControl required={isRequired} fullWidth error={error}>
      {label !== undefined && <InputLabel>{label}</InputLabel>}
      <Controller
        name={name}
        defaultValue={defaultValue}
        render={({ field }) => {
          return (
            <MuiSelect
              {...field}
              key={key}
              // notched={shrink}
              value={field.value}
              label={label}
              sx={sx}
              onChange={(event) => {
                if (addHandleOnChange) addHandleOnChange(event)
                setValue(name, event.target.value)
              }}
            >
              <MenuItem sx={{ height: 40 }} disabled={options.length === 0} key={""} value={""}>
                {options.length === 0 ? "Keine Auswahlmöglichkeiten vorhanden" : ""}
              </MenuItem>
              {options.map((option) => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </MuiSelect>
          )
        }}
      />
    </FormControl>
  )
}
