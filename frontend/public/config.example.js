window.config = {
  KEYCLOAK_AUTH_URL: "https://devauth.iam-extern.de/auth/realms/master",
  KEYCLOAK_CLIENT_ID: "mtb",
  KEYCLOAK_REDIRECT_URI: "localhost:3000",
  KEYCLOAK_CLIENT_SECRET: "your-secret",
  MTB_CONTROL_URL: "http://localhost:8080/api",
  KC_URL: "https://kcdemo.iam-extern.de/kc/",
  BWHC_URL: "https://iam-bwhc.gwis.uke.de/",
  // If multiple fileendings are allowed, separate with comma
  ALLOWED_BIOINF_FILEENDINGS: ".zip",
  ALLOWED_KC_REPORT_FILEENDINGS: ".pdf",
  PUBLIC_URL: "/",
  // Versions
  MEDICATION_ATC_VERSIONS: ["2020", "2021", "2022", "2023"],
  TUMOR_MORPHOLOGY_VALID_VERSIONS: ["Erste Revision", "Zweite Revision", "2014", "2019"],
  LATEST_ICD_10_VERSION: "2025",
  LATEST_ICD_O_3_T_VERSION: "Zweite Revision",
  LATEST_WHO_GRADE_VERSION: "2021",
}
