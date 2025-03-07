import React from "react"
import Select from "components/FormFields/Select"
import { levelOfEvidenceGradingCodeEnum } from "./LevelOfEvidenceTypes"

interface Props {
  name: string
  label: string
  isRequired: boolean
}

export default function LevelOfEvidenceGrading({ name, label, isRequired }: Props) {
  function getEnumKeys<T extends string, TEnumValue extends string | number>(enumVariable: {
    [key in T]: TEnumValue
  }) {
    return Object.values(enumVariable) as Array<T>
  }

  return (
    <Select
      isRequired={isRequired}
      name={name}
      label={label}
      options={getEnumKeys(levelOfEvidenceGradingCodeEnum).map((v) => ({ label: v, value: v }))}
    />
  )
}
