import { AuthState } from "react-oidc-context"

/**
 * Extracts the initials of a given name.
 * "Doe, John" becomes "DJ"
 * @param name
 */
export const nameToInitials = (name: string) => `${name.split(" ")[0][0]}${name.split(" ")[1][0]}`

type UserProfile = NonNullable<AuthState["user"]>["profile"]

/**
 * Extracts the fullname of a user in the form of "Doe, John"
 * @param profile
 */
export const userProfileToFullname = (profile: UserProfile) =>
  `${profile.family_name}, ${profile.given_name}`
