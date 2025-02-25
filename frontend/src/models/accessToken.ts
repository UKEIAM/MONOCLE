import { accessTokenRoles } from "./accessTokenRoles"

export type accessToken = {
  acr: string
  "allowed-origins": string[]
  aud: string[]
  auth_time: number
  azp: string
  email: string
  email_verified: boolean
  exp: number
  family_name: string
  given_name: string
  iat: number
  iss: string
  jti: string
  name: string
  preferred_username: string
  realm_access: accessTokenRoles
  scope: string
  session_state: string
  sid: string
  sub: string
  typ: string
}
