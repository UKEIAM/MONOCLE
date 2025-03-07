import { FieldValues, RegisterOptions } from "react-hook-form"

export interface OptionType {
  label: string
  value: string
}

type WarningRules = {
  warning?:
    | {
        value: boolean
        message: string
      }
    | boolean
    | string
}

export type ValidationRuleType = WarningRules & RegisterOptions<FieldValues, string>
