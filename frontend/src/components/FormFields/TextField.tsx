import { TextField as MuiTextField } from "@mui/material"
import { useFormContext } from "react-hook-form"

type Props = {
  name: string
  label: string
  disabled?: boolean
  isRequired?: boolean
  multiline?: boolean
  maxRows?: number
  InputProps?: object
  InputLabelProps?: object
}

export function TextField({
  name,
  label,
  disabled = false,
  isRequired = false,
  multiline = false,
  maxRows = 0,
  InputProps,
  InputLabelProps,
}: Props) {
  const { register } = useFormContext()

  return (
    <MuiTextField
      {...register(name)}
      multiline={multiline}
      rows={4}
      maxRows={maxRows}
      name={name}
      label={label}
      aria-label={label}
      disabled={disabled}
      fullWidth={true}
      variant="outlined"
      required={isRequired}
      InputProps={InputProps}
      InputLabelProps={InputLabelProps}
    />
  )
}
