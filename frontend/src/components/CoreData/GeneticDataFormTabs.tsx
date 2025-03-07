import React, { useEffect, useState } from "react"
import { NgsReport, Patient } from "gen/api"
import Session from "hooks/Session"
import { Alert, Tab, Tabs, useTheme } from "@mui/material"
import { NgsReportTab } from "./NgsReport"
import { useApi } from "hooks/useApi"

export function GeneticDataFormTabs() {
  const theme = useTheme()
  const { NgsReportApi } = useApi()
  const episodeId = Session.getEpisodeId()
  const [selectedTab, setSelectedTab] = useState<String>()

  const [ngsReports, setNgsReports] = useState<NgsReport[]>([])
  useEffect(() => {
    NgsReportApi.getAllNgsReports(episodeId)
      .then((response) => {
        setNgsReports(response.data)
        setSelectedTab(response.data[0]?.id)
      })
      .catch(() => {
        // FIXME handle me
      })
  }, [])

  useEffect(() => {
    console.log(`selected tab: ${selectedTab}`)
  }, [selectedTab])

  const handleTabChange = (event: React.SyntheticEvent, newValue: String) => {
    if (newValue !== undefined) {
      setSelectedTab(newValue)
    }
  }

  return (
    <div style={{ padding: "2rem", backgroundColor: theme.palette.primary.light }}>
      {ngsReports.length === 0 ? (
        <Alert severity="info"> Es liegen noch keine NGS-Berichte vor. </Alert>
      ) : (
        <>
          {/*Create Tabs for each element (section) in the formStructure*/}
          <Tabs
            value={selectedTab}
            onChange={handleTabChange}
            variant="scrollable"
            scrollButtons="auto"
            aria-label="scrollable auto tabs example"
            style={{ backgroundColor: "white" }}
          >
            {/* show tabs with the number of the report */}
            {ngsReports.map((ngsReport, index) => (
              <Tab key={ngsReport.id} label={`NGS-Bericht Nr.${index + 1}`} value={ngsReport.id} />
            ))}
          </Tabs>

          {/* Render selected tab content */}
          {ngsReports.map((ngsReport) => (
            <div key={ngsReport.id} hidden={selectedTab !== ngsReport.id}>
              <NgsReportTab ngsReport={ngsReport} />
            </div>
          ))}
        </>
      )}
    </div>
  )
}
