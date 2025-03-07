import React from "react"
import { EditableTable, FieldType } from "components/FormFields/EditableTable"

interface Props {
  name: string
  label: string
}

export default function LevelOfEvidenceAddendums({ name, label }: Props) {
  const rows = () => {
    return [
      {
        fieldType: "select",
        required: true,
        fieldElement: "code",
        selectItems: [
          { id: "is", label: "is" },
          { id: "iv", label: "iv" },
          { id: "Z", label: "Z" },
          { id: "R", label: "R" },
        ],
      } as FieldType,
    ]
  }

  return (
    <EditableTable
      fieldName={name}
      rowTypes={rows()}
      headerText={label}
      buttonText={"Evidenz Level Zusatz Hinzufügen"}
      alterText={"Bitte füllen Sie den Zusatz aus, bevor Sie weitere hinzufügen"}
      headerLabel={["Zusatzcode"]}
    />
  )
}
