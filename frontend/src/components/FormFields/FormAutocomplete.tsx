import { Autocomplete, FormControl, SxProps, TextField, Theme } from "@mui/material"
import { Controller, useFormContext } from "react-hook-form"
import React from "react"
import { OptionType } from "./types/FormTypes"

export interface AutocompleteProps {
  name: string
  label?: string
  error?: boolean
  options: { label: string; value: string }[]
  defaultValue?: any
  isRequired?: boolean
  key?: string
  sx?: SxProps<Theme>
  multiple?: boolean
  disabled?: boolean
  loading?: boolean
}

export default function FormAutocomplete({
  name,
  label,
  options,
  defaultValue,
  isRequired = false,
  key,
  sx = {},
  multiple,
  disabled = false,
  loading = false,
}: AutocompleteProps) {
  const { control, setValue } = useFormContext()

  return (
    <FormControl required={isRequired} fullWidth>
      <Controller
        name={name}
        control={control}
        defaultValue={defaultValue}
        render={({ field }) => {
          // Find the option corresponding to the stored value
          const selectedOptions = multiple
            ? options.filter((option) => field.value.includes(option.value)) // Find options that match the selected values
            : options.find((option) => option.value === field.value) // Single selection handling
          return (
            <Autocomplete
              {...field}
              key={key}
              value={selectedOptions || null} // Ensuring the value is correctly handled
              onChange={(event, newValue: OptionType[] | OptionType) => {
                setValue(
                  name,
                  Array.isArray(newValue)
                    ? newValue.map((item) => item.value)
                    : newValue
                      ? newValue.value
                      : "",
                )
              }}
              options={options}
              getOptionLabel={(option) => option.label}
              renderInput={(params) => (
                <TextField {...params} label={label} variant="outlined" sx={sx} />
              )}
              isOptionEqualToValue={(option, value) => option.value === value} // Ensuring the selected value matches the options
              defaultValue={defaultValue}
              multiple={multiple}
              disabled={disabled}
              loading={loading}
            />
          )
        }}
      />
    </FormControl>
  )
}
