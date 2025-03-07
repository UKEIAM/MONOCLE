import RecommendationDialog from "./RecommendationDialog"
import RecommendationTable from "./RecommendationTable"
import { useEffect, useState } from "react"
import { TherapyRecommendation } from "gen/api"
import Session from "hooks/Session"
import { diagnosesToString, ngsReportToString } from "utils/Formats"
import { TabProps } from "../CoreDataFormTabs"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

export default function Recommendations({ selected }: TabProps) {
  const { DiagnoseApi, NgsReportApi, TherapyRecommendationApi } = useApi()
  const { showErrorNotification } = useNotification()
  const [isOpen, setIsOpen] = useState<boolean>(false)
  const [diagnosesOptions, setDiagnosesOptions] = useState<{ label: string; value: string }[]>([])
  const [ngsReportsOptions, setNgsReportsOptions] = useState<{ label: string; value: string }[]>([])
  const [supportingVariantsOptions, setSupportingVariantsOptions] = useState<
    { id: string; values: { label: string; value: string }[] }[]
  >([])
  const [recommendations, setRecommendations] = useState<TherapyRecommendation[]>([])
  const [editElement, setEditElement] = useState<TherapyRecommendation>()
  const episodeId = Session.getEpisodeId()

  useEffect(() => {
    // On selected tab
    if (selected) {
      getDiagnosesList()
      getTherapyRecommendationList()
      getNgsReports()
    }
  }, [selected])

  useEffect(() => {
    // On isOpen
    if (isOpen) {
      getDiagnosesList()
      getNgsReports()
    } else {
      getTherapyRecommendationList()
    }
  }, [isOpen])

  const getDiagnosesList = () => {
    DiagnoseApi.getAllDiagnoses(episodeId)
      .then((response) => {
        setDiagnosesOptions(
          response.data.map((diagnosis) => ({
            label: diagnosesToString(diagnosis),
            value: diagnosis.id!,
          })),
        )
      })
      .catch(() => showErrorNotification("Es ist ein Fehler beim Laden von Diagnosen aufgetreten"))
  }

  const getNgsReports = () => {
    NgsReportApi.getAllNgsReports(episodeId).then((response) => {
      let ngsReportsArray: { label: string; value: string }[] = []
      let supportingVariantsArary: { id: string; values: { label: string; value: string }[] }[] = []
      response.data.forEach((ngsReport) => {
        ngsReportsArray.push({ label: ngsReportToString(ngsReport), value: ngsReport.id! })

        const newSupportingVariants =
          ngsReport.simpleVariants?.map((simpleVariant) => ({
            label: simpleVariant?.aminoAcidChange?.code ?? "",
            value: simpleVariant?.id ?? "",
          })) ?? []
        supportingVariantsArary.push({ id: ngsReport.id!, values: newSupportingVariants })
      })
      setNgsReportsOptions(ngsReportsArray)
      setSupportingVariantsOptions(supportingVariantsArary)
    })
  }

  const getTherapyRecommendationList = () => {
    TherapyRecommendationApi.getAllTherapyRecommendations(episodeId).then((response) => {
      setRecommendations(response.data)
    })
  }

  const handleEdit = (uuid?: string) => {
    if (uuid) {
      TherapyRecommendationApi.getTherapyRecommendation(episodeId, uuid).then((response) => {
        setEditElement(response.data)
        setIsOpen(true)
      })
    } else {
      setEditElement(undefined)
      setIsOpen(true)
    }
  }

  return (
    <>
      <RecommendationDialog
        open={isOpen}
        editElement={editElement}
        onClose={() => setIsOpen(false)}
        diagnosesOptions={diagnosesOptions}
        ngsReportsOptions={ngsReportsOptions}
        supportingVariantsOptions={supportingVariantsOptions}
      />
      <RecommendationTable
        onEdit={handleEdit}
        diagnosesOptions={diagnosesOptions}
        ngsReportsOptions={ngsReportsOptions}
        supportingVariantsOptions={supportingVariantsOptions}
        recommendations={recommendations}
        setRecommendations={setRecommendations}
      />
    </>
  )
}
