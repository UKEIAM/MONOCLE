import axios, { AxiosError } from "axios"
import React from "react"
import { useAuth } from "react-oidc-context"

const useAxiosAuthInterceptors = () => {
  const { user, signoutSilent } = useAuth()

  React.useEffect(() => {
    const requestInterceptor = axios.interceptors.request.use((request) => {
      const token = user?.access_token

      if (token && request.headers) {
        request.headers.Authorization = `Bearer ${token}`
      }

      return request
    })

    const responseInterceptor = axios.interceptors.response.use(null, (error: AxiosError) => {
      if (error.response?.status === 401) {
        // when the backed responds with an unauthorized status code redirect to the loginpage
        signoutSilent()
      }

      return Promise.reject(error)
    })

    return () => {
      axios.interceptors.request.eject(requestInterceptor)
      axios.interceptors.response.eject(responseInterceptor)
    }
  }, [signoutSilent, user])
}

export default useAxiosAuthInterceptors
