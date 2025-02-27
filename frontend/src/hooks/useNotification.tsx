import React from "react"
import { useSafeContext } from "./useSafeContext"
import AlertMessageForHook from "../components/AlertMessageForHook"

export interface Notification {
  open: boolean
  severity: "error" | "warning" | "info" | "success"
  message: string
}

type NotificationContextType =
  | {
      showErrorNotification: (message: string) => void
      showWarningNotification: (message: string) => void
      showInfoNotification: (message: string) => void
      showSuccessNotification: (message: string) => void
    }
  | undefined

const NotificationContext = React.createContext<NotificationContextType>(undefined)

export const NotificationProvider: React.FunctionComponent<{ children: React.ReactNode }> = ({
  children,
}) => {
  // use an array of notifications to show multiple at once
  const [{ open, severity, message }, setNotification] = React.useState<Notification>({
    open: false,
    severity: "info", // Default to a valid severity type
    message: "",
  })

  const openNotification = (
    severity: "error" | "warning" | "info" | "success",
    message: string,
  ) => {
    setNotification({
      open: true,
      severity,
      message,
    })
  }

  const onClose = () => {
    setNotification({
      open: false,
      severity,
      message: message,
    })
  }

  return (
    <NotificationContext.Provider
      value={{
        showErrorNotification: (message: string) => openNotification("error", message),
        showWarningNotification: (message: string) => openNotification("warning", message),
        showInfoNotification: (message: string) => openNotification("info", message),
        showSuccessNotification: (message: string) => openNotification("success", message),
      }}
    >
      <AlertMessageForHook open={open} severity={severity} message={message} onClose={onClose} />
      {children}
    </NotificationContext.Provider>
  )
}

export const useNotification = () => useSafeContext(NotificationContext, "notification")
