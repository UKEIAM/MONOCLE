// Utility function to safely access nested properties in an object
import { ValidationRule } from "react-hook-form"
import {
  formMaxLengthMessage,
  formMaxMessage,
  formMinLengthMessage,
  formMinMessage,
  formPatternMessage,
  formRequiredMessage,
  formWarningMessage,
} from "./FormMessages"
import { ValidationRuleType } from "../types/FormTypes"

export function getNestedValue(obj: any, path: string) {
  const parts = path.split(".") // ["code", "id"]
  return parts.reduce(
    (currentObject, currentKey) => currentObject && currentObject[currentKey],
    obj,
  )
}

// this method will convert min, max, minLength, maxLength, required to object form if they are in primitive form
// so if the given value is min: 5, it will convert it to { value: 5 }
// if the given value is { value: 5, message: "Custom message" }, it will return as-is
const convertToValidationRule = <T>(
  rule: T | { value: T; message?: string },
): { value: T; message?: string } => {
  if (rule && typeof rule === "object" && "value" in rule) {
    return rule // Already in object form, return as-is.
  }
  return { value: rule } // Convert primitive value to object.
}

const convertWarningToValidationRule = <T>(
  rule: boolean | { value: boolean; message?: string } | string,
): { value: boolean; message?: string } => {
  if (rule && typeof rule === "object" && "value" in rule) {
    return rule // Already in object form, return as-is.
  } else if (rule && typeof rule === "string") {
    return { value: true, message: rule } // rule is the message
  } else if (rule && typeof rule === "boolean") {
    return { value: rule } // rule is true without a message
  }
  return { value: !!rule }
}

// this method will generate the validation rule messages for the given validation rules if the message is not provided
export const generateValidationRulesMessages = (
  validationRules: ValidationRuleType | undefined,
): Record<string, ValidationRule<any>> => {
  return {
    // Handling 'required' validation rule
    ...((validationRules?.required !== undefined &&
      (() => {
        const ruleObject = convertToValidationRule(validationRules.required)
        return {
          required: {
            ...ruleObject,
            message: ruleObject.message || formRequiredMessage,
          },
        }
      })()) as Record<string, ValidationRule<string | number>>),

    // Handling 'minLength' validation rule
    ...(validationRules?.minLength !== undefined &&
      (() => {
        const ruleObject = convertToValidationRule(validationRules.minLength)
        return {
          minLength: {
            ...ruleObject,
            message: ruleObject.message || formMinLengthMessage(ruleObject.value as number),
          },
        }
      })()),

    // Handling 'maxLength' validation rule
    ...(validationRules?.maxLength !== undefined &&
      (() => {
        const ruleObject = convertToValidationRule(validationRules.maxLength)
        return {
          maxLength: {
            ...ruleObject,
            message: ruleObject.message || formMaxLengthMessage(ruleObject.value as number),
          },
        }
      })()),

    // Handling 'min' validation rule
    ...(validationRules?.min !== undefined &&
      (() => {
        const ruleObject = convertToValidationRule(validationRules.min)
        return {
          min: {
            ...ruleObject,
            message: ruleObject.message || formMinMessage(ruleObject.value as number),
          },
        }
      })()),

    // Handling 'max' validation rule
    ...(validationRules?.max !== undefined &&
      (() => {
        const ruleObject = convertToValidationRule(validationRules.max)
        return {
          max: {
            ...ruleObject,
            message: ruleObject.message || formMaxMessage(ruleObject.value as number),
          },
        }
      })()),
    // Handling 'warning' validation rule
    ...((validationRules?.warning !== undefined &&
      (() => {
        const ruleObject = convertWarningToValidationRule(validationRules.warning)
        return {
          warning: {
            ...ruleObject,
            message: ruleObject.message || formWarningMessage,
          },
        }
      })()) as Record<string, ValidationRule<boolean>>),

    // Handling 'pattern' validation rule
    ...((validationRules?.pattern !== undefined &&
      (() => {
        const ruleObject = convertToValidationRule(validationRules.pattern)
        return {
          pattern: {
            ...ruleObject,
            message: ruleObject.message || formPatternMessage,
          },
        }
      })()) as Record<string, ValidationRule<RegExp>>),
  }
}
