import { Autocomplete as MuiAutocomplete, SxProps, TextField } from "@mui/material"
import { Controller } from "react-hook-form"
import React from "react"

interface AutocompleteProps<T> {
  label: string
  name: string
  multiple?: boolean
  disabled?: boolean
  required?: boolean
  options: T[]
  loading: boolean
  getOptionLabel: (opt: T) => string
  getOptionId: (opt: T) => string
  getOptionValue: (opt: T) => string
  defaultValue?: T[] | T
  sx?: SxProps
}

export function Autocomplete<T>({
  label,
  name,
  multiple = false,
  disabled = false,
  options,
  loading,
  getOptionLabel,
  getOptionId,
  getOptionValue,
  defaultValue,
  required,
  sx,
}: AutocompleteProps<T>) {
  const handleChange =
    (onChange: (...event: any[]) => void) => (_e: React.SyntheticEvent, value: T | T[] | null) => {
      if (value === null) {
        onChange(null)
      } else if (Array.isArray(value)) {
        onChange(value.map((elem) => getOptionValue(elem)))
      } else {
        onChange(getOptionValue(value))
      }
    }

  return (
    <Controller
      name={name}
      render={({ field: { onChange } }) => (
        <MuiAutocomplete
          renderOption={(props, option) => (
            <li {...props} id={getOptionId(option)}>
              {getOptionLabel(option)}
            </li>
          )}
          multiple={multiple}
          disabled={disabled}
          disableCloseOnSelect={multiple}
          renderInput={(params) => (
            <TextField {...params} label={label} required={required} variant="outlined" fullWidth />
          )}
          autoHighlight={true}
          disablePortal={true}
          fullWidth
          loading={loading}
          options={options}
          getOptionLabel={getOptionLabel}
          defaultValue={defaultValue}
          onChange={handleChange(onChange)}
          noOptionsText={"Keine Auswahlmöglichkeiten vorhanden"}
          isOptionEqualToValue={(option, value) => {
            if (!option || !value) {
              return false
            }
            return getOptionId(option) === getOptionId(value)
          }}
          sx={sx}
        />
      )}
    />
  )
}

export default Autocomplete
