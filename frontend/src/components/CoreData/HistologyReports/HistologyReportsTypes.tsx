import { tumorCellContentType } from "../NgsReport/TumorCellContent"
import { DifferentiationDegree } from "gen/api"

export type tumorMorphologyValueType = {
  code: string
  display: string
  version: string
  system: string
}

export type tumorMorphologyType = {
  id: string
  patient: string
  specimen: string
  value: tumorMorphologyValueType
  note: string
}

export type histologyReportsType = {
  id: string
  patient: string
  specimen: string
  issuedOn: string | undefined
  tumorMorphology: tumorMorphologyType
  tumorCellContent?: tumorCellContentType
  differentiationDegree: DifferentiationDegree
}

export const differentiationDegreeList = [
  {
    value: "0",
    label:
      "0 - primär erworbene Melanose ohne zelluläre Atypien (nur beim malignen Melanom der Konjunktiva)",
  },
  { value: "1", label: "1 - gut differenziert" },
  { value: "2", label: "2 - mäßig differenziert" },
  { value: "3", label: "3 - schlecht differenziert" },
  { value: "4", label: "4 - undifferenziert" },
  { value: "5", label: "5 - nur für C61, TNM8" },
  { value: "X", label: "X - nicht bestimmbar" },
  { value: "L", label: "L - low grade (G1 oder G2)" },
  { value: "M", label: "M - intermediate grade (G2 oder G3)" },
  { value: "H", label: "H - high grade (G3 oder G4)" },
  { value: "B", label: "B - Borderline" },
  { value: "U", label: "U - unbekannt" },
  { value: "T", label: "T - trifft nicht zu" },
]
