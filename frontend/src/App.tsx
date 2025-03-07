import { useEffect, useState } from "react"
import { Navigate, Route, Routes } from "react-router-dom"
import PatientOverview from "pages/PatientOverview/PatientOverview"
import { ThemeProvider } from "@mui/material"
import Header from "components/Header/Header"
import { mainTheme } from "themes/MainTheme"
import { RequireLogin } from "components/Login/RequireLogin"
import styles from "App.module.scss"
import { NewPatient } from "pages/NewPatient/NewPatient"
import StepPage from "pages/StepPage"
import Dashboard from "pages/PatientDashboard/Dashboard"
import AuditTrail from "pages/AuditTrail"
import PatientLabNumber from "pages/PatientLabNumber/PatientLabNumber"
import Addressbook from "pages/Addressbook/Addressbook"
import { accessToken } from "models/accessToken"
import jwt_decode from "jwt-decode"
import { useAuth } from "react-oidc-context"
import BioInfUpload from "./pages/BioInf/BioInfUpload"
import { RestrictByRole } from "./utils/RestrictByRole"
import { ApiProvider } from "hooks/useApi"
import { NotificationProvider } from "hooks/useNotification"
import TestPage from "pages/TestPage/TestPage"

export default function App() {
  const [width, setWidth] = useState(window.innerWidth)

  const setMainWidth = () => {
    setWidth(window.innerWidth)
  }
  window.addEventListener("resize", setMainWidth)

  const auth = useAuth()
  const [roles, setRoles] = useState<string[]>([])
  useEffect(() => {
    setRoles(
      auth.user?.access_token
        ? jwt_decode<accessToken>(auth.user?.access_token).realm_access.roles
        : [],
    )
  }, [auth.user])

  const homeElement = () => {
    if (roles.includes("MTBDOCTOR")) return <Navigate to="/patients" />
    if (roles.includes("MTBPATHOLOGIST")) return <Navigate to="/patient-labnumber" />
    if (roles.includes("MTBBIOINF")) return <Navigate to="/bioinf-upload" />
    return null
  }

  return (
    <>
      <div className={styles["layout-container"]}>
        <ThemeProvider theme={mainTheme}>
          <Header />
          <main className={styles["layout-content"]}>
            <div style={{ width: width <= 1480 ? width - 80 : "1400px" }}>
              <ApiProvider>
                <RequireLogin>
                  <NotificationProvider>
                    <Routes>
                      <Route path="/" element={homeElement()} />
                      <Route
                        path="/patients"
                        element={
                          <RestrictByRole
                            allowedRole={["MTBDOCTOR"]}
                            restrictedComponent={<PatientOverview />}
                          />
                        }
                      />
                      <Route
                        path="/new-patient"
                        element={
                          <RestrictByRole
                            allowedRole={["MTBDOCTOR"]}
                            restrictedComponent={<NewPatient navigateTo={"/patients"} />}
                          />
                        }
                      />
                      <Route
                        path="/patients/:patientId/step/:stepId"
                        element={
                          <RestrictByRole
                            allowedRole={["MTBDOCTOR"]}
                            restrictedComponent={<StepPage />}
                          />
                        }
                      />
                      <Route
                        path="/audit-trail"
                        element={
                          <RestrictByRole
                            allowedRole={["MTBDOCTOR"]}
                            restrictedComponent={<AuditTrail />}
                          />
                        }
                      />
                      <Route
                        path="/patients/:patientId"
                        element={
                          <RestrictByRole
                            allowedRole={["MTBDOCTOR"]}
                            restrictedComponent={<Dashboard />}
                          />
                        }
                      />
                      <Route
                        path="/addressbook"
                        element={
                          <RestrictByRole
                            allowedRole={["MTBDOCTOR"]}
                            restrictedComponent={<Addressbook />}
                          />
                        }
                      />
                      <Route
                        path="/patient-labnumber"
                        element={
                          <RestrictByRole
                            allowedRole={["MTBPATHOLOGIST"]}
                            restrictedComponent={<PatientLabNumber />}
                          />
                        }
                      />
                      <Route
                        path="/bioinf-upload"
                        element={
                          <RestrictByRole
                            allowedRole={["MTBBIOINF"]}
                            restrictedComponent={<BioInfUpload />}
                          />
                        }
                      />
                      <Route
                        path="/test-page"
                        element={
                          <RestrictByRole
                            allowedRole={["MTBADMIN"]}
                            restrictedComponent={<TestPage />}
                          />
                        }
                      />
                    </Routes>
                  </NotificationProvider>
                </RequireLogin>
              </ApiProvider>
            </div>
          </main>
        </ThemeProvider>
      </div>
    </>
  )
}
