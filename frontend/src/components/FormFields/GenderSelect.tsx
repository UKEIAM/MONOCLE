import React, { useState } from "react"
import { GenderType } from "gen/api"
import { Grid } from "@mui/material"
import Select from "components/FormFields/Select"

export function GenderSelect() {
  const [genderItems] = useState([
    [GenderType.Male, "männlich"],
    [GenderType.Female, "weiblich"],
    [GenderType.Other, "divers"],
    [GenderType.Unknown, "ohne Angabe"],
  ])

  return (
    <Grid item xs={12}>
      <Select
        name={"gender"}
        label={"Geschlecht"}
        isRequired={true}
        options={genderItems.map((item) => ({ label: item[1], value: item[0] }))}
      />
    </Grid>
  )
}
