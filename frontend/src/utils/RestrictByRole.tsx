import { accessToken } from "../models/accessToken"
import jwt_decode from "jwt-decode"
import { useAuth } from "react-oidc-context"
import * as React from "react"
import { ReactElement, ReactNode } from "react"
import { Grid } from "@mui/material"

interface RequireType {
  restrictedComponent: ReactNode | ReactElement
  allowedRole: string[]
}

export function RestrictByRole({
  allowedRole,
  restrictedComponent,
}: RequireType): ReactElement | null {
  const auth = useAuth()

  if (auth.user?.access_token) {
    const decoded: accessToken = jwt_decode(auth.user?.access_token)
    const givenUserRoles: string[] = decoded.realm_access?.roles

    if (givenUserRoles.some((item) => allowedRole.includes(item))) {
      return <>{restrictedComponent}</>
    } else {
      return (
        <Grid container spacing={0} direction="column" alignItems="center" justifyContent="center">
          <Grid item xs={12} md={6} lg={12} fontSize={"larger"}>
            {"Sie sind nicht berechtigt diese Seite aufzurufen! Zur"} <a href="/">Startseite</a>
          </Grid>
        </Grid>
      )
    }
  }
  return null
}
