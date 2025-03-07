import React from "react"
import { useFormContext } from "react-hook-form"
import { StatusHistoryStatusEnum } from "gen/api"
import { EditableTable, FieldType } from "components/FormFields/EditableTable"
import { statusHistoryGermanTranslation } from "./DiagnosesTypes"

const rows = () => {
  return [
    { fieldType: "date", required: true, fieldElement: "date" } as FieldType,
    {
      fieldType: "select",
      required: true,
      fieldElement: "status",
      selectItems: Object.values(StatusHistoryStatusEnum).map((status) => {
        return { label: statusHistoryGermanTranslation[status], id: status }
      }),
    } as FieldType,
  ]
}

export function StatusHistoryComponent({ fieldName }: { fieldName?: string }) {
  const { getValues } = useFormContext()

  const itemCheck = () => {
    const statusHistoryList = getValues("statusHistory")
    if (statusHistoryList?.length !== undefined && statusHistoryList?.length > 0) {
      const lastElement = statusHistoryList[statusHistoryList?.length - 1]
      if (lastElement.status === undefined || lastElement.date === undefined) {
        return false
      }
    }
    return true
  }

  return (
    <EditableTable
      fieldName={fieldName ?? "statusHistory"}
      rowTypes={rows()}
      addItemIsOk={itemCheck}
      headerText={`Tumorausbreitung`}
      buttonText={"Tumorausbreitung Hinzufügen"}
      alterText={
        "Bitte füllen Sie die Tumorausbreitungtabelle aus, bevor Sie weitere Tumorausbreitungen hinzufügen"
      }
      headerLabel={["Zeitpunkt", "Wert"]}
      placeholder={"Ausbreitung..."}
    ></EditableTable>
  )
}
