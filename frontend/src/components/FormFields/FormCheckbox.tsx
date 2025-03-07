import { Checkbox, FormControl, FormControlLabel, FormHelperText } from "@mui/material"
import { Controller, useFormContext, ValidationRule } from "react-hook-form"
import { generateValidationRulesMessages, getNestedValue } from "./utils/FormUtils"
import { ValidationRuleType } from "./types/FormTypes"
import { ValidationValueMessage } from "react-hook-form/dist/types/validator"
import React from "react"

interface FormCheckboxProps {
  readonly name: string
  readonly label: string
  readonly defaultValue?: boolean
  readonly validationRules?: ValidationRuleType
}

export default function FormCheckbox({
  name,
  label,
  defaultValue = false,
  validationRules,
}: FormCheckboxProps) {
  const {
    control,
    formState: { errors },
  } = useFormContext()
  const formErrors = getNestedValue(errors, name)

  const rulesWithMessages: Record<
    string,
    ValidationRule<string | number>
  > = generateValidationRulesMessages(validationRules)

  const warning = rulesWithMessages["warning"] as ValidationValueMessage

  return (
    // It is not necessary to use FormControl here, but it is a good practice to wrap the
    // mui input components in a FormControl
    <FormControl>
      <Controller
        name={name}
        control={control}
        defaultValue={defaultValue ?? false}
        rules={rulesWithMessages}
        render={({ field }) => (
          <>
            <FormControlLabel
              control={
                <Checkbox
                  onChange={(e) => field.onChange(e.target.checked)}
                  checked={Boolean(field.value)}
                  style={{
                    // colors are copied from the browser examine element
                    color: formErrors
                      ? "#d32f2f"
                      : Boolean(field.value)
                        ? "#004992"
                        : "rgba(0, 0, 0, 0.6)",
                  }}
                />
              }
              label={validationRules?.required ? label + " *" : label} // This adds the label beside the checkbox
            />
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
