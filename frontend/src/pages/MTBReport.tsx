import React from "react"
import { Patient } from "gen/api"
import Session from "hooks/Session"
import KcReportsTable from "../components/MTBReport/KcReportsTable"
import ReportInput from "../components/MTBReport/ReportInput"

type Props = {
  patient: Patient
}
export default function MTBReport({ patient }: Props) {
  return (
    <>
      <ReportInput patient={patient} />
      <KcReportsTable episodeId={Session.getEpisodeId()} />
    </>
  )
}
