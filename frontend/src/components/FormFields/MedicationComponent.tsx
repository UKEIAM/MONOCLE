import React, { useState } from "react"
import { useFormContext } from "react-hook-form"
import { EditableTable, FieldType } from "./EditableTable"
import { MEDICATION_ATC_VERSIONS } from "utils/Versions"

type MedicationProps = {
  fieldName?: string
  isRequired?: boolean
}

export type MedicationType = {
  code: string
  system: string
  version: string
  display: string
}

const rows = () => {
  return [
    { fieldType: "input", required: true, fieldElement: "code" } as FieldType,
    {
      fieldType: "select",
      required: true, // FIXME is being ignored onSubmit for some reason
      fieldElement: "system",
      selectItems: [
        { id: "ATC", label: "ATC" },
        { id: "Unregistered", label: "Unregistered" },
      ],
    } as FieldType,
    { fieldType: "input", required: false, fieldElement: "version" } as FieldType,
    { fieldType: "input", required: false, fieldElement: "display" } as FieldType,
  ]
}

export function MedicationComponent({ fieldName, isRequired }: MedicationProps) {
  const { getValues } = useFormContext()
  const [validVersion, setValidVersion] = useState<boolean>(true)
  const itemCheck = () => {
    setValidVersion(true)
    const medicationList = getValues(fieldName ?? "medication")
    const lastMedicationElement = medicationList[medicationList.length - 1]
    // Check Version
    const isValidVersion =
      lastMedicationElement !== undefined && lastMedicationElement.system === "ATC"
        ? MEDICATION_ATC_VERSIONS.includes(lastMedicationElement.version)
        : true
    setValidVersion(isValidVersion)
    if (lastMedicationElement && !isValidVersion) return false

    return !(
      lastMedicationElement !== undefined &&
      (lastMedicationElement.system === "" ||
        lastMedicationElement.system === undefined ||
        (lastMedicationElement.system === "ATC" &&
          (lastMedicationElement.code === "" || lastMedicationElement.version === "")))
    )
  }

  return (
    <EditableTable
      fieldName={fieldName ?? "medication"}
      rowTypes={rows()}
      addItemIsOk={itemCheck}
      headerText={`Wirkstoff ${isRequired ? "*" : ""}`}
      buttonText={"Wirkstoff Hinzufügen"}
      alterText={
        !validVersion
          ? `Die Version der Wirkstoffe muss eins der folgenden Daten haben ${MEDICATION_ATC_VERSIONS}.`
          : "Bitte füllen Sie die Wirkstofftabelle aus, bevor Sie weitere Wirkstoffe hinzufügen"
      }
      headerLabel={["Code", "System", "Version", "Display"]}
      placeholder={"Wirkstoff..."}
    ></EditableTable>
  )
}
