import React from "react"
import {
  AppBar,
  Avatar,
  Button,
  IconButton,
  ListItemIcon,
  Menu,
  MenuItem,
  Toolbar,
  Typography,
  useTheme,
} from "@mui/material"
import LoginIcon from "@mui/icons-material/Login"
import { useAuth } from "react-oidc-context"
import { Logout } from "@mui/icons-material"
import { useNavigate } from "react-router-dom"
import NavButtonGroup from "./NavButtonGroup"

export default function Header() {
  const auth = useAuth()
  const theme = useTheme()
  const isLoggedIn = auth.isAuthenticated
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null)
  const open = Boolean(anchorEl)
  const navigate = useNavigate()

  const stringAvatar = (name: string | undefined) => {
    return {
      children: name ? `${name.split(" ")[0][0]}${name.split(" ")[1][0]}` : "U",
    }
  }

  const openUserMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget)
  }

  const handleClose = () => {
    setAnchorEl(null)
  }

  const handleLogout = () => {
    auth.signoutSilent().then()
  }

  const navigateHome = () => {
    navigate("/")
  }

  return (
    <>
      <AppBar position="sticky" style={{ backgroundColor: "#ffffff" }}>
        <Toolbar>
          <img
            src={process.env.PUBLIC_URL + "/uke-logo.png"}
            alt="Startseite"
            width={48}
            height={48}
            style={{ cursor: "pointer" }}
            onClick={() => navigateHome()}
          />
          <Typography
            variant={"inherit"}
            color={"#000000cc"}
            marginLeft={"8px"}
            marginBottom={"1px"}
            marginRight={"16px"}
            fontSize={"21px"}
            fontWeight={500}
          >
            MONOCLE
          </Typography>
          <NavButtonGroup />
          {!isLoggedIn && (
            <Button
              onClick={() => auth.signinRedirect()}
              startIcon={<LoginIcon />}
              variant="outlined"
            >
              Anmelden
            </Button>
          )}
          {isLoggedIn && (
            <>
              <IconButton
                id="user-button"
                onClick={openUserMenu}
                aria-haspopup="true"
                aria-controls={open ? "account-menu" : undefined}
                aria-expanded={open ? "true" : undefined}
              >
                <Avatar
                  {...stringAvatar(auth.user?.profile.name)}
                  sx={{ backgroundColor: theme.palette.primary.main }}
                />
              </IconButton>

              <Menu
                id="account-menu"
                anchorEl={anchorEl}
                open={open}
                onClose={handleClose}
                MenuListProps={{
                  "aria-labelledby": "user-button",
                }}
              >
                <MenuItem onClick={handleLogout}>
                  <ListItemIcon>
                    <Logout fontSize="small" />
                  </ListItemIcon>
                  Abmelden
                </MenuItem>
              </Menu>
            </>
          )}
        </Toolbar>
      </AppBar>
    </>
  )
}
