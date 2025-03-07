import { Divider, Typography } from "@mui/material"
import React from "react"

export default function CommentsHeader() {
  return (
    <>
      <Divider variant={"fullWidth"} style={{ marginTop: "8px" }} />
      <Typography variant={"h5"} margin={"16px"}>
        Kommentare
      </Typography>
    </>
  )
}
