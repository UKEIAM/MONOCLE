import {
  FormControl,
  FormHelperText,
  InputLabel,
  MenuItem,
  Select,
  SxProps,
  Theme,
} from "@mui/material"
import { Controller, useFormContext, ValidationRule } from "react-hook-form"
import { generateValidationRulesMessages, getNestedValue } from "./utils/FormUtils"
import { formRequiredMessage } from "./utils/FormMessages"
import { OptionType, ValidationRuleType } from "./types/FormTypes"
import { ValidationValueMessage } from "react-hook-form/dist/types/validator"

interface FormSelectProps {
  readonly name: string
  readonly label?: string
  readonly options: OptionType[]
  readonly defaultValue?: any
  readonly validationRules?: ValidationRuleType
  readonly sx?: SxProps<Theme>
}

export default function FormSelect({
  name,
  label,
  options,
  defaultValue = null,
  validationRules,
  sx,
}: FormSelectProps) {
  const {
    control,
    formState: { errors },
  } = useFormContext()
  // Get the nested error for the given name (e.g., "code.id")
  const formErrors = getNestedValue(errors, name)

  // if only required is given as true, then set the custom error message to "Pflichtfeld"
  if (validationRules?.required === true) {
    validationRules.required = formRequiredMessage
  }

  const rulesWithMessages: Record<
    string,
    ValidationRule<string | number>
  > = generateValidationRulesMessages(validationRules)

  const warning = rulesWithMessages["warning"] as ValidationValueMessage

  return (
    <FormControl fullWidth>
      {label !== undefined && (
        <InputLabel>{validationRules?.required ? label + " *" : label}</InputLabel>
      )}
      <Controller
        name={name}
        control={control}
        defaultValue={defaultValue}
        rules={rulesWithMessages}
        render={({ field }) => (
          <>
            <Select
              {...field}
              label={label !== undefined && validationRules?.required ? label + " *" : label}
              value={field.value ?? ""} // Convert null to empty string for the Select component
              onChange={(e) => {
                // Convert empty string back to null for the form state
                field.onChange(e.target.value === "" ? null : e.target.value)
              }}
              sx={{
                ...sx,
                ".MuiOutlinedInput-notchedOutline": {
                  border: formErrors
                    ? "1px red solid"
                    : (sx as Record<string, any>)?.[".MuiOutlinedInput-notchedOutline"] || {},
                },
              }}
            >
              {/*if there is no options, then the select is disabled*/}
              <MenuItem sx={{ height: 40 }} disabled={options.length === 0} key={""} value={""}>
                {options.length === 0 ? "Keine Auswahlmöglichkeiten vorhanden" : ""}
                {/*if there is options then show them in the select*/}
              </MenuItem>
              {options.map((option) => (
                <MenuItem key={option.value} value={option.value}>
                  {option.label}
                </MenuItem>
              ))}
            </Select>
            {formErrors?.message ? (
              <FormHelperText error>{formErrors.message}</FormHelperText>
            ) : warning && warning.value ? (
              <FormHelperText style={{ color: "orange" }} error>
                {warning.message as string}
              </FormHelperText>
            ) : null}
          </>
        )}
      />
    </FormControl>
  )
}
