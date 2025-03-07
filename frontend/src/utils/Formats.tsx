import dayjs from "dayjs"
import {
  AddressbookEntry,
  CarePlan,
  Claim,
  ClaimResponse,
  Code,
  Diagnose,
  EcogStatus,
  FamilyMemberDiagnosis,
  GuidelineTherapy,
  HistologyReevaluationRequest,
  HistologyReport,
  IhcReport,
  MolecularPathologyFinding,
  MolecularTherapy,
  MolecularTherapyResponse,
  NgsReport,
  RebiopsyRequest,
  Specimen,
  StudyInclusionRequest,
  TherapyRecommendation,
} from "gen/api"
import { statusOptions } from "components/CoreData/MolecularTherapies/MolecularTherapiesTypes"
import { statusOptions as statusOptionsClaims } from "components/CoreData/ClaimResponses/ClaimResponsesTypes"
import { relationshipCodes } from "components/CoreData/FamilyMemberDiagnoses/FamilyMemberDiagnosesTypes"
import { recistOptions } from "components/CoreData/TherapyResponses/TherapyResponsesTypes"

export function toGermanDateFormat(date: string | undefined, altText?: string) {
  return date ? dayjs(date).format("DD.MM.YYYY") : (altText ?? "")
}

// Format date in German format with date and time
export function toGermanDateTimeFormat(dateString: string | undefined) {
  if (dateString === undefined) {
    return ""
  }

  const date = new Date(dateString)
  return new Intl.DateTimeFormat("de-DE", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  }).format(date)
}

export function getEnumValues<T extends string, TEnumValue extends string | number>(enumVariable: {
  [key in T]: TEnumValue
}) {
  return Object.values(enumVariable) as Array<T>
}

export const diagnosesToString = (diagnoses: Diagnose): string => {
  /* Return concatenated Diagnoses String */
  return `${toGermanDateFormat(diagnoses?.recordedOn)} - ${diagnoses?.icd10?.code} - ${diagnoses?.icdO3T?.code}`
}

export const specimenToString = (specimen: Specimen): string => {
  /* concat a String from the given Specimen Object */
  return `${specimen.labelling ? "E/N-Nummer: " + specimen.labelling + " - " : ""} ${specimen.icd10?.code} - ${specimen.type} - ${toGermanDateFormat(specimen.collection?.date)}`
}

export const histologyReportToString = (histologyReport: HistologyReport): string => {
  /* Return concatenated Histology Report String */
  return `Histologie-Bericht vom ${toGermanDateFormat(histologyReport.issuedOn, "Kein Datum gesetzt")} ${histologyReport.tumorMorphology?.note}`
}

export const rebiopsyRequestToString = (rebiopsyRequest: RebiopsyRequest): string => {
  if (rebiopsyRequest.issuedOn)
    return `Rebiopsy vom ${toGermanDateFormat(rebiopsyRequest.issuedOn, "Biopsieauftrag ohne Datum")}`
  else return `Rebiopsie-Auftrag mit Tumorproben ID ${rebiopsyRequest.specimen}`
}

export const studyInclusionRequestToString = (studyInclusionRequest: StudyInclusionRequest) => {
  // FIXME: Maybe reason is not a unique description
  return `${studyInclusionRequest.reason} vom ${toGermanDateFormat(studyInclusionRequest.issuedOn, "Studien-Einschluss-Empfehlung ohne Datum")}`
}

export const therapyRecommendationToString = (
  therapyRecommendation: TherapyRecommendation,
): string => {
  return `Therapie-Empfehlung vom ${toGermanDateFormat(therapyRecommendation.issuedOn, "Kein Datum gesetzt, ID " + therapyRecommendation.id)}`
}

export const medicationToString = (medi: Code): string => {
  return `${medi.code} (${medi.system})`
}

export const medicationsString = (medications: Code[]): string => {
  let result = ""
  for (let medication of medications) {
    result += `${medication.code} (${medication.system}) - ${medication.display}\n`
  }
  return result
}

export const ngsReportToString = (ngs: NgsReport): string => {
  return `NGS Bericht vom ${toGermanDateFormat(ngs.issueDate, "Kein Datum gesetzt")}`
}

export const claimToString = (claim: Claim): string => {
  return `Kostenübernahme Antrag vom ${toGermanDateFormat(claim.issuedOn, "Kein Datum gesetzt, ID " + claim.id)}`
}

export const claimResponseToString = (claimsResponse: ClaimResponse): string => {
  return `Kostenübernahme Antwort vom ${toGermanDateFormat(claimsResponse.issuedOn)}, Status: ${statusOptionsClaims.find((elem) => elem.value === claimsResponse.status)?.label}`
}

export const molecularTherapyToString = (molecularTherapy?: MolecularTherapy): string => {
  if (molecularTherapy === undefined) return "FIXME: non existent molecularTherapy" // FIXME: should be caught before and therefor impossible
  return `${toGermanDateFormat(molecularTherapy.recordedOn)} ${statusOptions.find((e) => e.value === molecularTherapy.status)?.label ?? ""}`
}

export const TherapyResponseToString = (therapyResponse: MolecularTherapyResponse): string => {
  return `Molekular-Therapie-Befund vom ${toGermanDateFormat(therapyResponse.effectiveDate)}, Wert: ${recistOptions.find((e) => e.value === therapyResponse.value?.code)?.label ?? ""}`
}

export const ecogStatusToString = (egocStatus: EcogStatus): string => {
  return `Ecog-Perfomance-Status vom ${toGermanDateFormat(egocStatus.effectiveDate, "Kein Datum gesetzt, ID " + egocStatus.value)}`
}

export const familyMemberDiagnosisToString = (
  familyMemberDiagnosis: FamilyMemberDiagnosis,
): string => {
  return `${relationshipCodes.find((value) => value.value === familyMemberDiagnosis.relationship?.code)?.label ?? ""} ${familyMemberDiagnosis.details}`
}

export const molecularPathologyFindingToString = (
  molecularPathologyFinding: MolecularPathologyFinding,
): string => {
  return `Molekular-Pathologie-Befund vom ${toGermanDateFormat(molecularPathologyFinding.issuedOn)} mit der Notiz ${molecularPathologyFinding.note?.slice(0, 30)}...`
}

export const histologyReevaluationRequestToString = (
  histologyReevaluationRequest: HistologyReevaluationRequest,
): string => {
  return `Histologie-Reevaluations-Auftrag vom ${toGermanDateFormat(histologyReevaluationRequest.issuedOn, "Kein Datum gesetzt")}`
}

export const guidelineTherapyToString = (guidelineTherapy: GuidelineTherapy): string => {
  return `Leitlinien-Therapie mit dem/den Wirkstoff(en) ${guidelineTherapy.medication ? medicationsString(guidelineTherapy.medication) : ""}`
}

export const addressEntryToString = (addressbookEntry: AddressbookEntry): string => {
  return `${addressbookEntry.lastname}, ${addressbookEntry.firstname}`
}

export const carePlanListToString = (carePlan: CarePlan[]): string[] => {
  return carePlan.map((elem) => carePlanToString(elem))
}

export const carePlanToString = (carePlan: CarePlan): string => {
  return `Therapieplan vom ${toGermanDateFormat(carePlan.issuedOn, "Kein Datum gesetzt")} mit dem Protokollauszug: ${carePlan.description?.slice(0, 30)}...`
}

export const guidelineTherapyProcedureToString = (procedure: Code): string => {
  if (procedure) {
    const parts = [
      procedure?.code,
      procedure?.display,
      procedure?.system,
      procedure?.version,
    ].filter((part) => part) // Remove undefined, empty and null values
    return parts.length >= 0 ? `${parts.join(", ")}` : "" // Join parts with ", " if any exist
  } else {
    return ""
  }
}

export const codeToString = (codeObject: Code | undefined) => {
  return (
    <>
      {codeObject?.code && (
        <>
          <div style={{ fontWeight: "bold" }}>{codeObject.code}</div>
          {codeObject.system && codeObject.version
            ? ` (${codeObject.system}, ${codeObject.version}); `
            : codeObject.system || codeObject.version
              ? ` (${codeObject.system || codeObject.version}); `
              : ";"}
        </>
      )}
    </>
  )
}

export const ihcReportToString = (ihcReport: IhcReport): string => {
  return `IHC-Bericht vom ${toGermanDateFormat(ihcReport.date)}`
}
