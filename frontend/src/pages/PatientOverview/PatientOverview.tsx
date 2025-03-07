import { Divider, Grid } from "@mui/material"
import { useEffect, useState } from "react"

import NumberOfPatientsPanel from "components/Patient/NumberOfPatientsPanel"
import PatientPanel from "components/Patient/PatientPanel"
import { Patient } from "gen/api"
import { useApi } from "hooks/useApi"
import PatientFilterBar from "./components/PatientFilterBar"
import { useFilteredAndSortedPatients } from "./hooks/useFilteredAndSortedPatients"

export default function PatientOverview() {
  const { PatientApi } = useApi()
  const [patients, setPatients] = useState<Patient[]>()

  const {
    filteredAndSortedPatients,
    filterParameters,
    sortParameters,
    setFilterParameters,
    setSortParameters,
  } = useFilteredAndSortedPatients(patients)

  useEffect(() => {
    PatientApi.getPatients().then(({ data: patients }) => {
      setPatients(patients)
    })
  }, [PatientApi])

  return (
    <div>
      <Grid container>
        <Grid
          container
          item
          justifyContent="end"
          style={{ margin: "12px 8px 8px 8px", position: "relative" }}
          columnGap={2}
        >
          <div style={{ display: "flex", justifyContent: "space-between", width: "100%" }}>
            <NumberOfPatientsPanel numberOfPatients={filteredAndSortedPatients?.length ?? 0} />
            <PatientFilterBar
              filterParameters={filterParameters}
              sortParameters={sortParameters}
              setFilterParameters={setFilterParameters}
              setSortParameters={setSortParameters}
            />
          </div>
        </Grid>
        <Grid item xs={12}>
          <Divider />
        </Grid>
        <Grid container item>
          {filteredAndSortedPatients?.map((patient) => (
            <Grid
              item
              xs={12}
              key={patient.soarianId}
              style={{ borderBottom: "1px solid #0000001F", padding: "16px 0 16px 0" }}
            >
              <PatientPanel key={patient.id} patient={patient} />
            </Grid>
          ))}
        </Grid>
      </Grid>
    </div>
  )
}
