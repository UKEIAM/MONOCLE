import { deDE } from "@mui/x-date-pickers/locales"
import { Controller, useFormContext, ValidationRule } from "react-hook-form"
import { FormControl, FormHelperText, SxProps, Theme } from "@mui/material"
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers"
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs"
import dayjs, { Dayjs } from "dayjs"
import { generateValidationRulesMessages, getNestedValue } from "./utils/FormUtils"
import {
  formDateAfter,
  formDateBefore,
  formInvalidDate,
  formRequiredMessage,
} from "./utils/FormMessages"
import { ValidationRuleType } from "./types/FormTypes"
import { ValidationValueMessage } from "react-hook-form/dist/types/validator"

interface FormDatePickerProps {
  readonly name: string
  readonly label?: string
  readonly defaultValue?: Dayjs | null
  readonly showFormat?: string
  readonly saveFormat?: string
  readonly validationRules?: ValidationRuleType
  readonly mindate?: Dayjs
  readonly maxdate?: Dayjs
  readonly sx?: SxProps<Theme>
}

export default function FormDatePicker({
  name,
  label,
  defaultValue,
  validationRules,
  showFormat = "DD.MM.YYYY",
  // TODO: check if it matters that the saveFormat should be setted. I think it is not, because the backend will convert it to the right format
  saveFormat = "YYYY-MM-DD",
  mindate,
  maxdate,
  sx,
}: FormDatePickerProps) {
  const {
    control,
    formState: { errors },
  } = useFormContext()

  // Get the nested error for the given name
  const formErrors = getNestedValue(errors, name)

  const rulesWithMessages: Record<
    string,
    ValidationRule<string | number>
  > = generateValidationRulesMessages(validationRules)

  const warning = rulesWithMessages["warning"] as ValidationValueMessage

  // if only required is given as true, then set the custom error message to "Pflichtfeld"
  if (validationRules?.required === true) {
    validationRules.required = formRequiredMessage
  }

  const validateDate = (value: Dayjs | null | string) => {
    if (!value) return true // Allow empty values

    const dateValue = dayjs(value) // Convert to `dayjs` if not null

    if (!dateValue.isValid()) {
      return formInvalidDate
      // return "Ungültiges Datum" // Return an error message for invalid dates
    }

    if (mindate && dateValue.isBefore(mindate, "day")) {
      return formDateAfter(mindate.format(showFormat))
      // return `Datum soll nach dem ${mindate.format(showFormat)} sein`
    }

    if (maxdate && dateValue.isAfter(maxdate, "day")) {
      return formDateBefore(maxdate.format(showFormat))
      // return `Datum soll vor dem ${maxdate.format(showFormat)} sein`
    }

    return true
  }

  return (
    <LocalizationProvider
      dateAdapter={AdapterDayjs}
      localeText={deDE.components.MuiLocalizationProvider.defaultProps.localeText}
    >
      <FormControl fullWidth>
        <Controller
          name={name}
          control={control}
          defaultValue={defaultValue}
          rules={{
            ...rulesWithMessages,
            validate: validateDate,
          }}
          /* The ref is destructured from the field object to avoid passing it to the DatePicker,
          which would cause a warning or error since the DatePicker doesn’t accept a ref prop.*/
          render={({ field: { ref, value, onChange, ...rest } }) => (
            <>
              <DatePicker
                label={label !== undefined && validationRules?.required ? label + " *" : label}
                format={showFormat}
                value={value ? dayjs(value) : null}
                onChange={(newValue: Dayjs | null) => {
                  onChange(newValue ? newValue.format(saveFormat) : null)
                }}
                minDate={mindate}
                maxDate={maxdate}
                {...rest}
                sx={{
                  ...sx,
                  ".MuiOutlinedInput-notchedOutline": {
                    border: formErrors
                      ? "1px red solid"
                      : (sx as Record<string, any>)?.[".MuiOutlinedInput-notchedOutline"] || {},
                  },
                }}
                slotProps={{
                  textField: {
                    helperText: formErrors?.message,
                    error: !!formErrors,
                  },
                }}
              />
              {!formErrors?.message && warning && warning.value && !value ? (
                <FormHelperText style={{ color: "orange" }} error>
                  {warning.message as string}
                </FormHelperText>
              ) : null}
            </>
          )}
        />
      </FormControl>
    </LocalizationProvider>
  )
}
