const proteinOptions = [
  { id: "ALK", value: "ALK", label: "ALK" },
  { id: "BRAF V600E", value: "BRAF V600E", label: "BRAF V600E" },
  { id: "EGFR", value: "EGFR", label: "EGFR" },
  { id: "HER2/neu", value: "HER2/neu", label: "HER2/neu" },
  { id: "AR", value: "AR", label: "AR" },
  { id: "ER", value: "ER", label: "ER" },
  { id: "AR (Androgenrezeptor)", value: "AR (Androgenrezeptor)", label: "AR (Androgenrezeptor)" },
  { id: "ER (Östrogenrezeptor)", value: "ER (Östrogenrezeptor)", label: "ER (Östrogenrezeptor)" },
  {
    id: "PR (Progesteronrezeptor)",
    value: "PR (Progesteronrezeptor)",
    label: "PR (Progesteronrezeptor)",
  },
  { id: "Ki-67/MIB-1", value: "Ki-67/MIB-1", label: "Ki-67/MIB-1" },
  { id: "Nectin-4", value: "Nectin-4", label: "Nectin-4" },
  { id: "PanTRK", value: "PanTRK", label: "PanTRK" },
  { id: "PD-L1", value: "PD-L1", label: "PD-L1" },
  { id: "ROS1", value: "ROS1", label: "ROS1" },
  { id: "Trop-2", value: "Trop-2", label: "Trop-2" },
  { id: "ATRX", value: "ATRX", label: "ATRX" },
  { id: "CDK4", value: "CDK4", label: "CDK4" },
  { id: "CDK6", value: "CDK6", label: "CDK6" },
  { id: "CyclinD1", value: "CyclinD1", label: "CyclinD1" },
  { id: "CyclinD2", value: "CyclinD2", label: "CyclinD2" },
  { id: "CyclinD3", value: "CyclinD3", label: "CyclinD3" },
  { id: "MDM2", value: "MDM2", label: "MDM2" },
  { id: "MET", value: "MET", label: "MET" },
  { id: "p16", value: "p16", label: "p16" },
  { id: "p53", value: "p53", label: "p53" },
  { id: "p-AKT", value: "p-AKT", label: "p-AKT" },
  { id: "p-mTOR", value: "p-mTOR", label: "p-mTOR" },
  { id: "p-p38 MAPK", value: "p-p38 MAPK", label: "p-p38 MAPK" },
  { id: "p-p44/42 MAPK", value: "p-p44/42 MAPK", label: "p-p44/42 MAPK" },
  { id: "p-RB", value: "p-RB", label: "p-RB" },
  { id: "p-S6", value: "p-S6", label: "p-S6" },
  { id: "p-STAT3", value: "p-STAT3", label: "p-STAT3" },
  { id: "p-STAT5", value: "p-STAT5", label: "p-STAT5" },
  { id: "PTEN", value: "PTEN", label: "PTEN" },
  { id: "RB1", value: "RB1", label: "RB1" },
]

const valueOptions = [
  { id: "exp", value: "exp", label: "Exprimiert" },
  { id: "not-exp", value: "not-exp", label: "Nicht exprimiert" },
  { id: "unknown", value: "unknown", label: "Untersucht, kein Ergebnis" },
  { id: "1+", value: "1+", label: "1+" },
  { id: "2+", value: "2+", label: "2+" },
  { id: "3+", value: "3+", label: "3+" },
]

// TPS-Score	Integer

// [0,100]

// CPS-Score	Integer

const icScoreOptions = [
  { id: "0", value: "0", label: "< 1%" },
  { id: "1", value: "1", label: ">= 1%" },
  { id: "2", value: "2", label: ">= 5%" },
  { id: "3", value: "3", label: ">= 10%" },
]
// 0...1

// Observation.component: IC

// Code: TODO

// Value: CodeableConcept

// TC-Score	Coding: Code
const tcScoreOptions = [
  { id: "0", value: "0", label: "< 1%" },
  { id: "1", value: "1", label: ">= 1%" },
  { id: "2", value: "2", label: ">= 5%" },
  { id: "3", value: "3", label: ">= 10%" },
  { id: "4", value: "4", label: ">= 25 %" },
  { id: "5", value: "5", label: ">= 50 %" },
  { id: "6", value: "6", label: ">= 75 %" },
]

export { proteinOptions, valueOptions, icScoreOptions, tcScoreOptions }
