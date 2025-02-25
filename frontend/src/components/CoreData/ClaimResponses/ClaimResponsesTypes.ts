export const statusOptions: { label: string; value: string }[] = [
  { value: "accepted", label: "Akzeptiert" },
  { value: "rejected", label: "Abgelehnt" },
  { value: "unknown", label: "Unbekannt" },
]

export const reasonOptions: { label: string; value: string }[] = [
  { value: "insufficient-evidence", label: "Unzureichende Belege" },
  {
    value: "standard-therapy-not-exhausted",
    label: "Standardtherapie nicht erschöpft (Neuantrag erforderlich)",
  },
  {
    value: "standard-therapy-not-exhausted-no-new-claim",
    label: "Standardtherapie nicht ausgeschöpft (kein Neuantrag erforderlich)",
  },
  { value: "formal-reasons", label: "Inhaltliche Gründe" },
  { value: "other-therapy-recommended", label: "Andere Therapie vorgeschlagen" },
  { value: "inclusion-in-study", label: "Studieneinschluss" },
  { value: "approval-revocation", label: "Rücknahme der Zulassung" },
  { value: "other", label: "Weitere Gründe" },
  { value: "unknown", label: "Unbekannt" },
]
