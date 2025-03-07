export enum levelOfEvidenceGradingCodeEnum {
  M1A = "m1A",
  M1B = "m1B",
  M1C = "m1C",
  M2A = "m2A",
  M2B = "m2B",
  M2C = "m2C",
  M3 = "m3",
  M4 = "m4",
}

export enum levelOfEvidenceAddendumsCodeEnum {
  IS = "is",
  IV = "iv",
  Z = "Z",
  R = "R",
}

export type levelOfEvidenceGrandingType = {
  code: levelOfEvidenceGradingCodeEnum | undefined
  system: string
}

export type levelOfEvidenceAddendumsType = {
  code: levelOfEvidenceAddendumsCodeEnum | undefined
  system: string
}

export type levelOfEvidenceType = {
  grading: levelOfEvidenceGrandingType
  addendums: levelOfEvidenceAddendumsType[]
}
