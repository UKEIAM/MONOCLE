import { useState, useMemo } from "react"

import { Patient } from "gen/api"

type SortDirection = "asc" | "desc"

export interface SortParameters {
  readonly attribute?: keyof Patient
  readonly direction: SortDirection
}

export interface FilterParameters {
  readonly attribute?: keyof Patient
  readonly value: string
}

const filterPatients = (patients: Patient[], attribute: keyof Patient, value: string) =>
  patients.filter((patient) =>
    patient[attribute] === undefined
      ? false
      : patient[attribute]?.toString().toLowerCase().includes(value.toLowerCase()),
  )

const sortPatients = (patients: Patient[], attribute: keyof Patient, direction: SortDirection) =>
  patients.sort((patientA, patientB) => {
    if (patientA[attribute] === undefined) return 1
    if (patientB[attribute] === undefined) return -1

    const valueA = patientA[attribute]?.toString() || ""
    const valueB = patientB[attribute]?.toString() || ""

    return direction === "asc" ? valueA.localeCompare(valueB) : valueB.localeCompare(valueA)
  })

export const useFilteredAndSortedPatients = (patients: Patient[] | undefined) => {
  const [sortParameters, setSortParameters] = useState<SortParameters>({
    attribute: undefined,
    direction: "asc",
  })

  const [filterParameters, setFilterParameters] = useState<FilterParameters>({
    attribute: undefined,
    value: "",
  })

  const filteredAndSortedPatients = useMemo(() => {
    if (patients === undefined) return undefined

    let result = patients

    if (filterParameters.attribute) {
      result = filterPatients(result, filterParameters.attribute, filterParameters.value)
    }

    if (sortParameters.attribute) {
      result = sortPatients(result, sortParameters.attribute, sortParameters.direction)
    }

    return result
  }, [patients, sortParameters, filterParameters])

  return {
    filteredAndSortedPatients,
    sortParameters,
    filterParameters,
    setSortParameters,
    setFilterParameters,
  }
}
