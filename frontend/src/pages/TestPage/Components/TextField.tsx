import { TextField as MuiTextField } from "@mui/material"
import {
  Controller,
  FieldValues,
  GlobalError,
  RegisterOptions,
  useFormContext,
} from "react-hook-form"

type Props = {
  name: string
  label: string
  defaultValue?: string
  disabled?: boolean
  validation?: RegisterOptions<FieldValues, string>
  multiline?: boolean
  maxRows?: number
  InputProps?: object
  InputLabelProps?: object
}

export function TextField({
  name,
  label,
  defaultValue = "",
  disabled = false,
  validation,
  multiline = false,
  maxRows = 0,
  InputProps,
  InputLabelProps,
}: Props) {
  const {
    control,
    formState: { errors },
  } = useFormContext()

  return (
    <Controller
      name={name}
      control={control}
      rules={validation}
      defaultValue={defaultValue}
      render={({ field }) => (
        <MuiTextField
          {...field}
          multiline={multiline}
          rows={4}
          maxRows={maxRows}
          label={label}
          aria-label={label}
          disabled={disabled}
          fullWidth={true}
          variant="outlined"
          InputProps={InputProps}
          InputLabelProps={InputLabelProps}
          error={!!errors[name]}
          helperText={errors[name] && (errors[name] as GlobalError).message}
        />
      )}
    />
  )
}
