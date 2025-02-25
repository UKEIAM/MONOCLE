import { useAuth } from "react-oidc-context"
import React, { useEffect, useState } from "react"
import jwt_decode from "jwt-decode"
import { accessToken } from "models/accessToken"
import { Button, Grid, Tooltip } from "@mui/material"
import { Link } from "react-router-dom"
import HomeIcon from "@mui/icons-material/Home"
import PersonAddIcon from "@mui/icons-material/PersonAdd"
import ContactsIcon from "@mui/icons-material/Contacts"
import LaunchIcon from "@mui/icons-material/Launch"
import AddLinkIcon from "@mui/icons-material/AddLink"
import UploadFileIcon from "@mui/icons-material/UploadFile"

export default function NavButtonGroup() {
  const auth = useAuth()
  const [roles, setRoles] = useState<string[]>([])
  useEffect(() => {
    setRoles(
      auth.user?.access_token
        ? jwt_decode<accessToken>(auth.user?.access_token).realm_access.roles
        : [],
    )
  }, [auth.user])

  const isDoc = roles.includes("MTBDOCTOR")
  const isPatho = roles.includes("MTBPATHOLOGIST")
  const isBioInf = roles.includes("MTBBIOINF")

  return (
    <Grid
      container
      sx={{ position: "relative", display: "flex", alignItems: "center", width: "100%" }}
      columnSpacing={2}
    >
      {isDoc ? (
        <>
          <Grid item>
            <Link to="/patients" style={{ textDecoration: "none" }}>
              <Tooltip title={"Patient:innen Übersicht"}>
                <Button style={{ minWidth: 0 }}>
                  <HomeIcon />
                </Button>
              </Tooltip>
            </Link>
          </Grid>
          <Grid item>
            <Link to="/new-patient" style={{ textDecoration: "none" }}>
              <Tooltip title={"Patient:in Anlegen"}>
                <Button style={{ minWidth: 0 }}>
                  <PersonAddIcon />
                </Button>
              </Tooltip>
            </Link>
          </Grid>
          <Grid item>
            <Link to="../addressbook" style={{ textDecoration: "none" }}>
              <Tooltip title={"Adressbuch Verwalten"}>
                <Button style={{ minWidth: 0 }}>
                  <ContactsIcon />
                </Button>
              </Tooltip>
            </Link>
          </Grid>
          <Grid item>
            <a
              href={window.config.KC_URL || "https://kcdemo.iam-extern.de/kc/"}
              target={"_blank"}
              style={{ textDecoration: "none" }}
              rel="noreferrer"
            >
              <Tooltip title={"KC Öffnen"}>
                <Button style={{ minWidth: 0, display: "flex", alignItems: "center" }}>
                  <LaunchIcon />
                  <span>KC</span>
                </Button>
              </Tooltip>
            </a>
          </Grid>
          <Grid item>
            <a
              href={window.config.BWHC_URL || "https://iam-bwhc.gwis.uke.de/bwhc/"}
              target={"_blank"}
              style={{ textDecoration: "none" }}
              rel="noreferrer"
            >
              <Tooltip title={"bwHC Öffnen"}>
                <Button style={{ minWidth: 0, display: "flex", alignItems: "center" }}>
                  <LaunchIcon />
                  <span>bwHC</span>
                </Button>
              </Tooltip>
            </a>
          </Grid>
        </>
      ) : null}
      {isPatho ? (
        <Grid item>
          <Link to="/patient-labnumber" style={{ textDecoration: "none" }}>
            <Tooltip title={"Labor Nummern hinzufügen"}>
              <Button style={{ minWidth: 0 }}>
                <AddLinkIcon />
              </Button>
            </Tooltip>
          </Link>
        </Grid>
      ) : null}
      {isBioInf ? (
        <Grid item>
          <Link to="/bioinf-upload" style={{ textDecoration: "none" }}>
            <Tooltip title={"Genetische Daten hochladen"}>
              <Button style={{ minWidth: 0 }}>
                <UploadFileIcon />
              </Button>
            </Tooltip>
          </Link>
        </Grid>
      ) : null}
    </Grid>
  )
}
