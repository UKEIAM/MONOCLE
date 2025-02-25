import { EditableTable, FieldType } from "components/FormFields/EditableTable"
import { icScoreOptions, proteinOptions, tcScoreOptions, valueOptions } from "./IHCReportsTypes"

const rowTypes = [
  {
    fieldType: "autocompleteFreeSolo",
    required: false,
    fieldElement: "protein.code",
    selectItems: proteinOptions,
  } as FieldType,
  {
    fieldType: "select",
    required: false,
    fieldElement: "value.code",
    selectItems: valueOptions,
  } as FieldType,
  {
    fieldType: "numberInput",
    required: false,
    fieldElement: "tpsScore",
    numberInputProps: {
      min: 0,
      max: 100,
      step: 1,
    },
  } as FieldType,
  {
    fieldType: "numberInput",
    required: false,
    fieldElement: "cpsScore",
    numberInputProps: {
      step: 1,
    },
  } as FieldType,
  {
    fieldType: "select",
    required: false,
    fieldElement: "icScore.code",
    selectItems: icScoreOptions,
  } as FieldType,
  {
    fieldType: "select",
    required: false,
    fieldElement: "tcScore.code",
    selectItems: tcScoreOptions,
  } as FieldType,
]

type Props = {
  name: string
  label: string
}

export const ProteinExpressions = ({ name, label }: Props) => {
  return (
    <EditableTable
      fieldName={name}
      headerText={label}
      alterText="foo"
      placeholder=" "
      headerLabel={["Protein", "Ergebnis", "TPS-Score", "CPS-Score", "IC-Score", "TC-Score"]}
      rowTypes={rowTypes}
    />
  )
}
