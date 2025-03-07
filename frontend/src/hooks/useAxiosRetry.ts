import axios from "axios"
import axiosRetry from "axios-retry"
import React from "react"
import { useLocation, useNavigate } from "react-router-dom"

export const useAxiosRetry = () => {
  const navigate = useNavigate()
  const location = useLocation()

  const [isRetryingRequest, setIsRetryingRequest] = React.useState<boolean>(false)

  React.useEffect(() => {
    const { requestInterceptorId, responseInterceptorId } = axiosRetry(axios, {
      retries: 20,
      retryDelay: () => 3000,
      retryCondition: (error) => error.response === undefined && location.pathname !== "/error", // when there is no response, the backend is not reachable
      onRetry: () => setIsRetryingRequest(true),
      onMaxRetryTimesExceeded: () => {
        setIsRetryingRequest(false)
        navigate("/error")
      },
    })

    const responseSuccessInterceptorId = axios.interceptors.response.use((response) => {
      setIsRetryingRequest(false)
      return response
    })

    return () => {
      axios.interceptors.request.eject(requestInterceptorId)
      axios.interceptors.response.eject(responseInterceptorId)
      axios.interceptors.response.eject(responseSuccessInterceptorId)
    }
  }, [location, navigate])

  return {
    isRetryingRequest,
  }
}
