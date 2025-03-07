export type MolecularTherapy = {
  history: [
    {
      id: string
      patient: string
      recordedOn: string // date
      basedOn: string
      note: string
      status: "not-done" | "on-going" | "stopped" | "completed" | ""
      notDoneReason: {
        // system: string // is not being validated by bwhc (valueSet)
        code:
          | "payment-refused"
          | "payment-pending"
          | "no-indication"
          | "medical-reason"
          | "patient-refusal"
          | "patient-death"
          | "other-therapy-chosen"
          | "continued-externally"
          | "lost-to-fu"
          | "other"
          | "unknown"
          | ""
      }
      period: {
        start: string // date
        end: string // date
      }
      medication: {
        code: string
        system: "ATC" | "Unregistered"
        display: string
        version?: string
      }[]
      dosage: ">=50%" | "<50%" | ""
      reasonStopped: {
        // system: "MTB-CDS:MolecularTherapy:StopReason" // is not being validated by bwhc (valueSet)
        code:
          | "remission"
          | "patient-wish"
          | "payment-ended"
          | "medical-reason"
          | "progression"
          | "patient-death"
          | "toxicity"
          | "other-therapy-chosen"
          | "continued-externally"
          | "deterioration"
          | "other"
          | "unknown"
          | ""
        system: "MTB-CDS:MolecularTherapy:StopReason"
      }
    },
  ]
}

type selectOption = {
  label: string
  value: string
}

export const statusOptions: selectOption[] = [
  { label: "Nicht umgesetzt", value: "not-done" },
  { label: "Laufend", value: "on-going" },
  { label: "Abgebrochen", value: "stopped" },
  { label: "Abgeschlossen", value: "completed" },
]

export const notDoneReasonOptions: selectOption[] = [
  { label: "Kostenübernahme abgelehnt", value: "payment-refused" },
  { label: "Kostenübernahme noch ausstehend", value: "payment-pending" },
  { label: "Klinisch keine Indikation", value: "no-indication" },
  { label: "Medizinische Gründe", value: "medical-reason" },
  { label: "Therapie durch Patient abgelehnt", value: "patient-refusal" },
  { label: "Tod", value: "patient-death" },
  { label: "Wahl einer anderen Therapie durch Behandler", value: "other-therapy-chosen" },
  { label: "Weiterbehandlung extern", value: "continued-externally" },
  { label: "Lost to follow-up", value: "lost-to-fu" },
  { label: "Weitere Gründe", value: "other" },
  { label: "Unbekannt", value: "unknown" },
]

export const realisationOptions: selectOption[] = [
  { label: "vollständig", value: "complete" },
  { label: "partiell", value: "partial" },
]

export const dosageOptions: selectOption[] = [
  { label: ">=50%", value: ">=50%" },
  { label: "<50%", value: "<50%" },
]

export const reasonStoppedOptions: selectOption[] = [
  { label: "Anhaltende Remission", value: "remission" },
  { label: "Auf Wunsch des/der Patienten/Patientin", value: "patient-wish" },
  { label: "Ende der Kostenübernahme", value: "payment-ended" },
  { label: "Medizinische Gründe", value: "medical-reason" },
  { label: "Progression", value: "progression" },
  { label: "Tod", value: "patient-death" },
  { label: "Toxizität", value: "toxicity" },
  { label: "Wahl einer anderen Therapie durch Behandler", value: "other-therapy-chosen" },
  { label: "Weiterbehandlung extern", value: "continued-externally" },
  { label: "Zustandsverschlechterung", value: "deterioration" },
  { label: "Weitere Gründe", value: "other" },
  { label: "Unbekannt", value: "unknown" },
]
