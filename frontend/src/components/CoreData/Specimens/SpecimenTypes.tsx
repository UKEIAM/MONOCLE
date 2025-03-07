export function getEnumKeys<T extends string, TEnumValue extends string | number>(enumVariable: {
  [key in T]: TEnumValue
}) {
  return Object.values(enumVariable) as Array<T>
}

export const specimenTypeCodes = [
  { value: "fresh-tissue", label: "Frischgewebe" },
  { value: "cryo-frozen", label: "Cryo-frozen" },
  { value: "FFPE", label: "FFPE" },
  { value: "liquid-biopsy", label: "Liquid Biopsy" },
  { value: "unknown", label: "Unbekannt" },
]
export const specimenLocalizationCodes = [
  { value: "primary-tumor", label: "Primärtumor" },
  { value: "metastasis", label: "Metastase" },
  { value: "unknown", label: "Unbekannt" },
  { value: "local-recurrence", label: "Lokalrezidiv" },
  { value: "regional-lymph-node", label: "Regionäre Lymphknoten" },
  { value: "cellfree-dna", label: "Zellfreie DNA" },
]
export const specimenMethodesCodes = [
  { value: "biopsy", label: "Biopsie" },
  { value: "resection", label: "Resektat" },
  { value: "liquid-biopsy", label: "Liquid Biopsy" },
  { value: "cytology", label: "Zytologie" },
  { value: "unknown", label: "Unbekannt" },
]
