import {
  Requirement,
  RequirementNgsTypeEnum,
  RequirementOthersTypeEnum,
  RequirementStandardTypeEnum,
} from "../../gen/api"
import { OptionType } from "../FormFields/types/FormTypes"

export const initialRequirementFormValues: Requirement = {
  id: "",
  episodeId: "",
  recommended: undefined,
  moleculareDiagnostic: undefined,
  internDiagnostic: undefined,
  ngs: undefined,
  ngsType: undefined,
  standard: undefined,
  standardType: undefined,
  others: undefined,
  othersType: undefined,
  comment: "",
}

export const RequirementNgsTypeEnumOptions: OptionType[] = [
  { label: RequirementNgsTypeEnum.Exom, value: RequirementNgsTypeEnum.Exom },
  { label: RequirementNgsTypeEnum.Ngs500Panel, value: RequirementNgsTypeEnum.Ngs500Panel },
  { label: RequirementNgsTypeEnum.RnaPanel, value: RequirementNgsTypeEnum.RnaPanel },
  { label: RequirementNgsTypeEnum.Transkriptom, value: RequirementNgsTypeEnum.Transkriptom },
]

export const RequirementStandardTypeEnumOptions: OptionType[] = [
  { label: RequirementStandardTypeEnum.Sanger, value: RequirementStandardTypeEnum.Sanger },
  { label: RequirementStandardTypeEnum.RtPcr, value: RequirementStandardTypeEnum.RtPcr },
  { label: RequirementStandardTypeEnum.QPcr, value: RequirementStandardTypeEnum.QPcr },
]

export const RequirementOthersTypeEnumOptions: OptionType[] = [
  {
    label: RequirementOthersTypeEnum.Methylierungsanalytik,
    value: RequirementOthersTypeEnum.Methylierungsanalytik,
  },
  { label: RequirementOthersTypeEnum.MultiplexIhc, value: RequirementOthersTypeEnum.MultiplexIhc },
]

export const trueFalseRadioGroupOptions: OptionType[] = [
  { label: "Ja", value: "true" },
  { label: "Nein", value: "false" },
]
