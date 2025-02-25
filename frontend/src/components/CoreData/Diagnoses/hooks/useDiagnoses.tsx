import { Diagnose } from "gen/api"

/* Provides a Custom Hook to work with Diagnoses */
export const useDiagnoses = () => {
  return {
    /* Provides a Methods to find a Diagnoses by UUID */
    getDiagnosesToIcd10Codes: (diagnoses: Diagnose[]) => {
      return diagnoses?.map((diagnosis: Diagnose) => {
        return { label: diagnosis.icd10?.code ?? "", value: diagnosis.icd10?.code ?? "" }
      })
    },
  }
}
