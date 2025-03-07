import {
  FormControl,
  FormControlLabel,
  FormLabel,
  Radio,
  RadioGroup as RadioGr,
} from "@mui/material"
import { Controller } from "react-hook-form"
import React from "react"

interface RadioGroupProps {
  name: string
  label: string
  defaultValue?: string
  isRequired?: boolean
  options: { label: string; value: string }[]
}

export default function RadioGroup({
  name,
  label,
  defaultValue,
  isRequired,
  options,
}: RadioGroupProps) {
  return (
    <FormControl>
      <FormLabel id={label}>{label}</FormLabel>
      <Controller
        name={name}
        defaultValue={defaultValue ?? undefined}
        render={({ field }) => (
          <RadioGr id={label} row {...field} value={field.value}>
            {options &&
              options.map((option) => (
                <FormControlLabel
                  value={option.value}
                  control={<Radio required={isRequired} />}
                  label={option.label}
                />
              ))}
          </RadioGr>
        )}
      />
    </FormControl>
  )
}
