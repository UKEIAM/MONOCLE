import { FormControl, FormHelperText, SxProps, TextField, Theme } from "@mui/material"
import { Controller, useFormContext, ValidationRule } from "react-hook-form"
import { ValidationValueMessage } from "react-hook-form/dist/types/validator"
import { ValidationRuleType } from "./types/FormTypes"
import { generateValidationRulesMessages, getNestedValue } from "./utils/FormUtils"

type FormTextFieldProps = {
  readonly name: string
  readonly label?: string
  readonly validationRules?: ValidationRuleType
  readonly sx?: SxProps<Theme>
}

export default function FormTextField({ name, label, validationRules, sx }: FormTextFieldProps) {
  const {
    control,
    formState: { errors },
  } = useFormContext()

  // Get the nested error for the given name (e.g., "code.id")
  const formErrors = getNestedValue(errors, name)
  // record is like a map of key-value pairs where the key is a string and the value is a ValidationRule
  // This function generates the error messages for the validation rules if they are not provided by the user
  // like "required" -> "Pflichtfeld" , "min" -> "Wert muss größer oder gleich {value} sein"
  const rulesWithMessages: Record<
    string,
    ValidationRule<string | number | boolean | RegExp>
  > = generateValidationRulesMessages(validationRules)
  const warning = rulesWithMessages["warning"] as ValidationValueMessage

  return (
    <FormControl fullWidth={true}>
      <Controller
        name={name}
        control={control}
        defaultValue=""
        rules={rulesWithMessages}
        render={({ field }) => (
          <>
            <TextField
              {...field}
              label={validationRules?.required ? label + " *" : label}
              error={!!formErrors}
              helperText={formErrors?.message}
              multiline={true}
              sx={{
                ...sx,
                ".MuiOutlinedInput-notchedOutline": {
                  border: formErrors
                    ? "1px red solid"
                    : (sx as Record<string, any>)?.[".MuiOutlinedInput-notchedOutline"] || {},
                },
              }}
            />
            {!formErrors?.message && warning && warning.value && (
              <FormHelperText style={{ color: "orange" }} error>
                {warning.message as string}
              </FormHelperText>
            )}
          </>
        )}
      />
    </FormControl>
  )
}
