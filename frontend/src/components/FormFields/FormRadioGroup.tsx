import {
  FormControl,
  FormControlLabel,
  FormHelperText,
  FormLabel,
  Radio,
  RadioGroup,
} from "@mui/material"
import { Controller, useFormContext, ValidationRule } from "react-hook-form"
import { generateValidationRulesMessages, getNestedValue } from "./utils/FormUtils"
import { formRequiredMessage } from "./utils/FormMessages"
import { OptionType, ValidationRuleType } from "./types/FormTypes"
import { ValidationValueMessage } from "react-hook-form/dist/types/validator"

interface RadioGroupProps {
  readonly name: string
  readonly label?: string
  readonly defaultValue?: string
  readonly validationRules?: ValidationRuleType
  readonly options: OptionType[]
}

export default function FormRadioGroup({
  name,
  label,
  defaultValue,
  validationRules,
  options,
}: RadioGroupProps) {
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
    <FormControl>
      {label !== undefined && <FormLabel>{label}</FormLabel>}
      <Controller
        name={name}
        control={control}
        defaultValue={defaultValue ?? ""}
        rules={rulesWithMessages}
        render={({ field }) => (
          <>
            <RadioGroup row {...field} value={field.value}>
              {options &&
                options.map((option) => (
                  <FormControlLabel
                    label={option.label}
                    value={option.value}
                    key={option.value}
                    control={<Radio />}
                  />
                ))}
            </RadioGroup>
            {/* Display the error message if there is an error */}
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
