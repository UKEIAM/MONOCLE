import { Autocomplete as MuiAutocomplete, SxProps, TextField } from "@mui/material"
import { Control, Controller, FieldPath, FieldValues } from "react-hook-form"
import React from "react"

interface AutocompleteProps<T, F extends FieldValues> {
  label: string
  name: FieldPath<F> // Ensures the name corresponds to keys in the control's type
  required?: boolean
  options: T[]
  loading: boolean
  isTextInputPossible?: boolean
  control: Control<F>
  sx?: SxProps
}

export function Autocomplete<T, F extends FieldValues>({
  label,
  name,
  options,
  loading,
  required,
  isTextInputPossible = false,
  control,
  sx,
}: AutocompleteProps<T, F>) {
  return (
    <Controller
      name={name}
      control={control}
      render={({ field }) => (
        <MuiAutocomplete
          freeSolo={isTextInputPossible}
          options={options} // Example options
          inputValue={field.value ?? ""}
          onInputChange={(event, value) => field.onChange(value)}
          renderInput={(params) => (
            <TextField {...params} label={label} required={required} variant="outlined" fullWidth />
          )}
          autoHighlight={true}
          disablePortal={true}
          fullWidth
          loading={loading}
          sx={sx}
        />
      )}
    />
  )
}

export default Autocomplete
