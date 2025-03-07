import React, { useState } from "react"
import { tabsName } from "./TabsData"
import { Specimens } from "./Specimens"
import { HistologyReports } from "./HistologyReports"
import { Diagnoses } from "./Diagnoses"
import { FamilyMemberDiagnoses } from "./FamilyMemberDiagnoses"
import { GuidelineTherapies } from "./GuidelineTherapies"
import { EcogStatusList } from "./EcogStatusList"
import { MolecularPathoFindings } from "./MolecularPathologyFindings"
import { CarePlans } from "./CarePlans"
import { Recommendations } from "./Recommendations"
import { HistologyReevaluationRequests } from "./HistologyReevaluationRequests"
import { RebiopsyRequests } from "./RebiopsyRequests"
import { StudyInclusionRequests } from "./StudyInclusionRequests"
import { Claims } from "./Claims"
import { ClaimResponses } from "./ClaimResponses"
import { MolecularTherapies } from "./MolecularTherapies"
import { TherapyResponses } from "./TherapyResponses"
import { Tab, Tabs, useTheme } from "@mui/material"
import { IHCReports } from "./IHCReports"

export type TabProps = {
  selected: boolean
}

export function CoreDataFormTabs() {
  const theme = useTheme()
  const [selectedTab, setSelectedTab] = useState<string>("diagnoses")
  const tabs = tabsName
  // Handle tab selection
  const handleTabChange = (event: React.SyntheticEvent, newValue: string) => {
    if (newValue !== undefined) {
      setSelectedTab(newValue)
    }
  }

  function renderTabContent(value: string) {
    const selected = selectedTab === value
    switch (value) {
      case "specimens":
        return <Specimens />
      case "histologyReports":
        return <HistologyReports selected={selected} />
      case "diagnoses":
        return <Diagnoses selected={selected} />
      case "familyMemberDiagnoses":
        return <FamilyMemberDiagnoses />
      case "previousGuidelineTherapies":
        return <GuidelineTherapies selected={selected} />
      case "ecogStatus":
        return <EcogStatusList />
      case "molecularPathologyFindings":
        return <MolecularPathoFindings selected={selected} />
      case "carePlans":
        return <CarePlans selected={selected} />
      case "recommendations":
        return <Recommendations selected={selected} />
      case "histologyReevaluationRequests":
        return <HistologyReevaluationRequests selected={selected} />
      case "rebiopsyRequests":
        return <RebiopsyRequests selected={selected} />
      case "studyInclusionRequests":
        return <StudyInclusionRequests selected={selected} />
      case "claims":
        return <Claims selected={selected} />
      case "claimResponses":
        return <ClaimResponses selected={selected} />
      case "molecularTherapies":
        return <MolecularTherapies selected={selected} />
      case "response":
        return <TherapyResponses selected={selected} />
      case "ihcReport":
        return <IHCReports selected={selected} />
      default:
        return null
    }
  }

  return (
    <div
      style={{
        padding: "2rem",
        justifyContent: "center",
        backgroundColor: theme.palette.primary.light,
      }}
    >
      {/*Create Tabs for each element (section) in the formStructure*/}
      <Tabs
        value={selectedTab}
        onChange={handleTabChange}
        variant="scrollable"
        scrollButtons="auto"
        aria-label="scrollable auto tabs example"
        style={{ backgroundColor: "white" }}
      >
        {/*show tabs with the name of element */}
        {tabs.map((tab) => (
          <Tab key={tab.key} label={tab.value} value={tab.key} />
        ))}
      </Tabs>

      {/* Render selected tab content */}
      {tabs.map((tab) => (
        <div key={tab.key} hidden={selectedTab !== tab.key}>
          {renderTabContent(tab.key)}
        </div>
      ))}
    </div>
  )
}
