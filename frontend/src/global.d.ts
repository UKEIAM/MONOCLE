// Define the ConfigContextType interface
interface ConfigType {
  KEYCLOAK_AUTH_URL: string
  KEYCLOAK_CLIENT_ID: string
  KEYCLOAK_REDIRECT_URI: string
  KEYCLOAK_CLIENT_SECRET: string
  MTB_CONTROL_URL: string
  KC_URL: string
  BWHC_URL: string
  // If multiple fileendings are allowed, separate with comma
  ALLOWED_BIOINF_FILEENDINGS: string
  ALLOWED_KC_REPORT_FILEENDINGS: string
  PUBLIC_URL: string
  MEDICATION_ATC_VERSIONS: string[]
  TUMOR_MORPHOLOGY_VALID_VERSIONS: string[]
  LATEST_ICD_10_VERSION: string
  LATEST_ICD_O_3_T_VERSION: string
  LATEST_WHO_GRADE_VERSION: string
}

// Extend the Window interface to include the config property
interface Window {
  config: ConfigType
}
