import { levelOfEvidenceType } from "../LevelOfEvidence/LevelOfEvidenceTypes"

/**
 * @deprecated Old version. PLease use Code instead.
 */
type medicationType = {
  code: string
  system: string
  display: string
  version: string
}

/**
 * @deprecated Old Version. Please use TherapyRecommendationPriorityEnum instead.
 */
enum recommandationPriorityEnum {
  ONE = "1",
  TWO = "2",
  THREE = "3",
  FOUR = "4",
}

/**
 * @deprecated Old Version. Please use TherapyRecommendation instead.
 */
export type recommendationType = {
  id: string
  patient: string
  diagnosis: string
  issuedOn: string | undefined
  medication: medicationType[]
  priority: recommandationPriorityEnum | undefined
  levelOfEvidence: levelOfEvidenceType | undefined
  ngsReport: string
  supportingVariants: string[]
}
