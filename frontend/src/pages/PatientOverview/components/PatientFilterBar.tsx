import { ArrowDownward, ArrowUpward } from "@mui/icons-material"
import { Autocomplete, TextField, ToggleButton } from "@mui/material"

import { Patient } from "gen/api"
import { FilterParameters, SortParameters } from "../hooks/useFilteredAndSortedPatients"

interface PatientFilterBarProps {
  readonly sortParameters: SortParameters
  readonly filterParameters: FilterParameters
  readonly setSortParameters: React.Dispatch<React.SetStateAction<SortParameters>>
  readonly setFilterParameters: React.Dispatch<React.SetStateAction<FilterParameters>>
}

type SelectOption = {
  label: string
  attribute: keyof Patient
}

const sortingAttributeOptions: SelectOption[] = [
  { label: "PatID", attribute: "soarianId" },
  { label: "Erstellungsdatum", attribute: "createdAt" },
  { label: "Aktualisierungsdatum", attribute: "updatedAt" },
  { label: "Vorname", attribute: "firstName" },
  { label: "Nachname", attribute: "surname" },
  { label: "Geburtsdatum", attribute: "dateOfBirth" },
  { label: "Krankenkasse", attribute: "healthInsurance" },
]

const filteringAttributeOptions: SelectOption[] = [
  { label: "PatID", attribute: "soarianId" },
  { label: "Vorname", attribute: "firstName" },
  { label: "Nachname", attribute: "surname" },
]

export default function PatientFilterBar({
  sortParameters,
  filterParameters,
  setFilterParameters,
  setSortParameters,
}: PatientFilterBarProps) {
  const handleSortAttributeChange = (sortAttribute: keyof Patient | null) => {
    if (sortAttribute === null) {
      setSortParameters({ attribute: undefined, direction: "asc" })
    } else {
      setSortParameters((prev) => ({ attribute: sortAttribute, direction: prev.direction }))
    }
  }

  const handleSortOrderChange = () => {
    setSortParameters((prev) => ({ ...prev, direction: prev.direction === "asc" ? "desc" : "asc" }))
  }

  const handleFilterAttributeChange = (filterAttribute: keyof Patient | null) => {
    if (filterAttribute === null) {
      setFilterParameters({ attribute: undefined, value: "" })
    } else {
      setFilterParameters((prev) => ({ attribute: filterAttribute, value: prev.value }))
    }
  }

  const handleFilterValueChange = (value: string) => {
    setFilterParameters((prev) => ({ ...prev, value }))
  }

  return (
    <div style={{ display: "flex", gap: "16px" }}>
      <Autocomplete
        size="small"
        style={{ height: "100%", minWidth: 225 }}
        options={filteringAttributeOptions}
        getOptionLabel={(option) => option.label}
        onChange={(_, value) => handleFilterAttributeChange(value?.attribute ?? null)}
        renderInput={(params) => (
          <TextField
            {...params}
            style={{ height: "100%" }}
            variant="standard"
            label="Filtern nach"
            InputProps={{ ...params.InputProps, style: { height: "100%" } }}
          />
        )}
      />
      {filterParameters.attribute !== undefined && (
        <TextField
          fullWidth
          id="standard-basic"
          label="Filtertext"
          variant="standard"
          onChange={(event) => handleFilterValueChange(event.target.value)}
        />
      )}
      <Autocomplete
        size="small"
        style={{ height: "100%", minWidth: 225 }}
        options={sortingAttributeOptions}
        getOptionLabel={(option) => option.label}
        onChange={(_, value) => handleSortAttributeChange(value?.attribute ?? null)}
        renderInput={(params) => (
          <TextField
            {...params}
            style={{ height: "100%" }}
            variant="standard"
            label="Sortieren nach"
            InputProps={{ ...params.InputProps, style: { height: "100%" } }}
          />
        )}
      />
      {sortParameters.attribute !== undefined && (
        <ToggleButton style={{ padding: "4px" }} value="sortOrder" onChange={handleSortOrderChange}>
          {sortParameters?.direction === "asc" ? <ArrowDownward /> : <ArrowUpward />}
        </ToggleButton>
      )}
    </div>
  )
}
