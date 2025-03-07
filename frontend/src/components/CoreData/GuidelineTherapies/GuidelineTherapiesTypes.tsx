import { OptionType } from "components/FormFields/types/FormTypes"

export const reasonStoppedOptions: OptionType[] = [
  { value: "patient-wish", label: "Auf Wunsch des/der Patienten/Patientin" },
  { value: "progression", label: "Progression" },
  { value: "toxicity", label: "Toxizität" },
  { value: "deterioration", label: "Zustandsverschlechterung" },
  { value: "chronic-remission", label: "Anhaltende Remission" },
  { value: "other", label: "Weitere Gründe" },
  { value: "unknown", label: "Unbekannt" },
]

export const intensionOptions: OptionType[] = [
  { value: "K", label: "kurativ" },
  { value: "P", label: "palliativ" },
  { value: "S", label: "sonstiges" },
  { value: "X", label: "keine Angabe" },
]

export const procedurePositionOptions: OptionType[] = [
  { value: "O", label: "ohne Bezug zur operativen Therapie" },
  { value: "A", label: "adjuvant" },
  { value: "N", label: "neoadjuvant" },
  { value: "I", label: "intraoperativ" },
  { value: "S", label: "sonstiges" },
]
