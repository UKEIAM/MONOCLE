import {
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select as MuiSelect,
  SxProps,
  Theme,
} from "@mui/material"
import { Controller, FieldValues, RegisterOptions, useFormContext } from "react-hook-form"

export interface SelectProps {
  name: string
  label?: string
  options: { label: string; value: string }[]
  defaultValue?: any
  validation?: RegisterOptions<FieldValues, string>
  // shrink?: boolean
  addHandleOnChange?: (event: any) => void
  key?: string
  sx?: SxProps<Theme>
}

export default function Select({
  name,
  label,
  options,
  defaultValue,
  validation,
  // shrink = undefined,
  addHandleOnChange,
  key,
  sx = {},
}: SelectProps) {
  const {
    setValue,
    control,
    formState: { errors },
  } = useFormContext()

  return (
    <FormControl fullWidth sx={sx} error={!!errors[name]}>
      {label && <InputLabel>{label}</InputLabel>}
      <Controller
        name={name}
        control={control}
        rules={validation}
        defaultValue={defaultValue}
        render={({ field }) => (
          <>
            <MuiSelect
              {...field}
              key={key}
              // notched={shrink}
              value={field.value}
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
            {errors[name] && <FormHelperText>{errors[name]?.message as string}</FormHelperText>}
          </>
        )}
      />
    </FormControl>
  )
}
