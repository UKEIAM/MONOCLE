import { TextField as MuiTextField } from "@mui/material"
import { useFormContext } from "react-hook-form"

type Props = {
  name: string
  label: string
  isRequired?: boolean
  type?: string
  step?: number
  min?: number
  max?: number
  maxLength?: number
}

// FIXME? MUI advises against using type={"number"} https://mui.com/material-ui/react-text-field/#type-quot-number-quot
export function NumberField({
  name,
  label,
  isRequired = false,
  step = 0.01,
  min = undefined,
  max = undefined,
  maxLength = 1,
}: Props) {
  const { register } = useFormContext()

  return (
    <MuiTextField
      {...register(name)}
      type={"number"}
      name={name}
      label={label}
      fullWidth={true}
      variant="outlined"
      required={isRequired}
      inputProps={{
        maxLength: maxLength,
        step: step,
        min: min,
        max: max,
      }}
    />
  )
}
