import { FormControl, FormHelperText, SxProps, TextField, Theme } from "@mui/material"
import { Controller, useFormContext, ValidationRule } from "react-hook-form"
import { generateValidationRulesMessages, getNestedValue } from "./utils/FormUtils"
import { ValidationRuleType } from "./types/FormTypes"
import { ValidationValueMessage } from "react-hook-form/dist/types/validator"

interface FormNumberFieldProps {
  readonly name: string
  readonly label?: string
  readonly validationRules?: ValidationRuleType
  readonly sx?: SxProps<Theme>
}

export default function FormNumberField({
  name,
  label,
  validationRules,
  sx,
}: FormNumberFieldProps) {
  const {
    control,
    formState: { errors },
  } = useFormContext()

  // Get the nested error for the given name
  const formErrors = getNestedValue(errors, name)
  // record is like a map of key-value pairs where the key is a string and the value is a ValidationRule
  // it is like Hashmap in Java and like Dict in Python
  const rulesWithMessages: Record<
    string,
    ValidationRule<string | number | boolean>
  > = generateValidationRulesMessages(validationRules)
  const warning = rulesWithMessages["warning"] as ValidationValueMessage

  return (
    <FormControl fullWidth sx={{ borderColor: "orange" }}>
      <Controller
        name={name}
        control={control}
        defaultValue=""
        rules={rulesWithMessages}
        render={({ field }) => (
          <>
            <TextField
              {...field}
              label={label}
              type={"number"}
              error={!!formErrors}
              helperText={formErrors?.message}
              sx={{
                //TODO not tested yet
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
