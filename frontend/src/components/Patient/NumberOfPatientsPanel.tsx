import { Tooltip, Typography } from "@mui/material"
import PeopleIcon from "@mui/icons-material/People"
import React from "react"

type Props = {
  numberOfPatients: number
}

/**
 * A box that displays the number of patients
 *
 * @param title Title of the dialog
 * @constructor
 */
export default function NumberOfPatientsPanel({ numberOfPatients }: Props) {
  return (
    <Tooltip title={"Anzahl Patient:innen"}>
      <div style={{ display: "inline-flex", alignItems: "center", margin: "5px" }}>
        <PeopleIcon color={"primary"} />
        <Typography style={{ marginLeft: "2px" }}>{numberOfPatients}</Typography>
      </div>
    </Tooltip>
  )
}
