import React from "react"
import ReactDOM from "react-dom/client"
import "index.css"
import App from "App"
import reportWebVitals from "reportWebVitals"

import { BrowserRouter } from "react-router-dom"
import { AuthProvider, AuthProviderProps } from "react-oidc-context"

export const oidcConfig: AuthProviderProps = {
  authority: window.config.KEYCLOAK_AUTH_URL!,
  client_id: window.config.KEYCLOAK_CLIENT_ID!,
  accessTokenExpiringNotificationTimeInSeconds: 5,
  redirect_uri: window.location.origin,
  onSigninCallback: () => {
    window.location.href = "/"
  },
}

const root = ReactDOM.createRoot(document.getElementById("root") as HTMLElement)

root.render(
  <AuthProvider {...oidcConfig}>
    <React.StrictMode>
      <BrowserRouter basename={window.config.PUBLIC_URL}>
        <App />
      </BrowserRouter>
    </React.StrictMode>
  </AuthProvider>,
)

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals()
