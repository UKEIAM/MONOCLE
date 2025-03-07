import { useAuth } from "react-oidc-context"
import React from "react"
import { Typography } from "@mui/material"
import styles from "./RequireLogin.module.scss"

// @ts-ignore
export const RequireLogin = ({ children }) => {
  const auth = useAuth()

  if (!auth.isAuthenticated) {
    return (
      <div className={styles["app-description"]}>
        <Typography>
          Mit dem Molekularen Tumorboard werden die Kerndatensätze gepflegt...
        </Typography>
        <Typography>
          Bitte loggen Sie sich ein, um die Daten zu den Patient:innen einsehen und bearbeiten zu
          können.
        </Typography>
      </div>
    )
  }

  return children
}
