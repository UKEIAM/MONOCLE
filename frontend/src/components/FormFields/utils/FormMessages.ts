export const formRequiredMessage = "Dieses Feld ist erforderlich"
export const formMinLengthMessage = (value: number) =>
  `Der Text muss mindestens ${value} Zeichen lang sein`
export const formMaxLengthMessage = (value: number) =>
  `Der Text darf maximal ${value} Zeichen lang sein`
export const formMinMessage = (value: number) => `Der Wert muss mindestens ${value} sein`
export const formMaxMessage = (value: number) => `Der Wert darf maximal ${value} sein`
export const formInvalidDate = `Ungültiges Datum`
export const formDateBefore = (date: string) => `Datum soll vor dem ${date} sein`
export const formDateAfter = (date: string) => `Datum soll nach dem ${date} sein`
export const formWarningMessage = "Empfohlender Wert für die Datenqualität"
export const formPatternMessage = "Entspricht nicht dem vorgegebenen Pattern"
