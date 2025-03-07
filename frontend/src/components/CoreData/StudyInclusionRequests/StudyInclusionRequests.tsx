import { useEffect, useState } from "react"
import { StudyInclusionRequestsTable } from "./StudyInclusionRequestsTable"
import { StudyInclusionRequest } from "gen/api"
import Session from "hooks/Session"
import { diagnosesToString, ngsReportToString } from "utils/Formats"
import { StudyInclusionRequestsDialog } from "./StudyInclusionRequestsDialog"
import { TabProps } from "components/CoreData/CoreDataFormTabs"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

export function StudyInclusionRequests({ selected }: TabProps) {
  const { NgsReportApi, DiagnoseApi, StudyInclusionRequestApi } = useApi()
  const { showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId()
  const [isOpen, setIsOpen] = useState<boolean>(false)
  const [editElement, setEditElement] = useState<StudyInclusionRequest>()
  const [ngsReportsOptions, setNgsReportsOptions] = useState<{ label: string; value: string }[]>([])
  const [supportingVariantsOptions, setSupportingVariantsOptions] = useState<
    { id: string; values: { label: string; value: string }[] }[]
  >([])
  const [studyInclusionRequests, setStudyInclusionRequests] = useState<StudyInclusionRequest[]>([])
  const [diagnosesMap, setDiagnosesMap] = useState<{ label: string; value: string }[]>([])

  useEffect(() => {
    // On selected
    if (selected) {
      getDiagnoses()
      getStudyInclusionRequestList()
      getNgsReports()
    }
  }, [selected])

  useEffect(() => {
    // On Dialog open/close
    if (isOpen) {
      getDiagnoses()
      getNgsReports()
    } else {
      getStudyInclusionRequestList()
    }
  }, [isOpen])

  const getNgsReports = () => {
    NgsReportApi.getAllNgsReports(episodeId).then((response) => {
      let ngsReportsArray: { label: string; value: string }[] = []
      let supportingVariantsArary: { id: string; values: { label: string; value: string }[] }[] = []
      response.data.forEach((ngsReport) => {
        ngsReportsArray.push({ label: ngsReportToString(ngsReport), value: ngsReport.id! })

        const newSupportingVariants =
          ngsReport.simpleVariants?.map((simpleVariant) => {
            let varientValue = simpleVariant.id ? simpleVariant.id : ""
            let varientLable = simpleVariant.aminoAcidChange?.code
              ? simpleVariant.aminoAcidChange?.code
              : varientValue
            return { label: varientLable, value: varientValue }
          }) ?? []

        let newArray = newSupportingVariants.filter((elem) => elem.label !== "")

        supportingVariantsArary.push({ id: ngsReport.id!, values: newArray })
      })
      setNgsReportsOptions(ngsReportsArray)
      setSupportingVariantsOptions(supportingVariantsArary)
    })
  }

  const getDiagnoses = () => {
    DiagnoseApi.getAllDiagnoses(episodeId).then(({ data: diagnoses }) => {
      setDiagnosesMap(
        diagnoses.map((diagnosis) => ({
          label: diagnosesToString(diagnosis),
          value: diagnosis.id!,
        })),
      )
    })
  }
  const getStudyInclusionRequestList = () => {
    StudyInclusionRequestApi.getAllStudyInclusionRequests(episodeId).then(({ data: resposne }) => {
      setStudyInclusionRequests(resposne)
    })
  }
  const handleEdit = (uuid?: string) => {
    if (uuid) {
      StudyInclusionRequestApi.getStudyInclusionRequest(episodeId, uuid)
        .then(({ data: response }) => {
          setEditElement(response)
          setIsOpen(true)
        })
        .catch(() =>
          showErrorNotification("Die Studien-Einschluss-Empfehlung konnte nicht geladen werden."),
        )
    } else {
      setEditElement(undefined)
      setIsOpen(true)
    }
  }

  const handleClose = () => setIsOpen(false)

  return (
    <>
      <StudyInclusionRequestsTable
        onEdit={handleEdit}
        studyInclusionRequests={studyInclusionRequests}
        setStudyInclusionRequests={setStudyInclusionRequests}
        diagnosesMap={diagnosesMap}
        ngsReportsOptions={ngsReportsOptions}
        supportingVariantsOptions={supportingVariantsOptions}
      ></StudyInclusionRequestsTable>
      <StudyInclusionRequestsDialog
        open={isOpen}
        onClose={handleClose}
        editElement={editElement}
        ngsReportsOptions={ngsReportsOptions}
        supportingVariantsOptions={supportingVariantsOptions}
        diagnosesMap={diagnosesMap}
      ></StudyInclusionRequestsDialog>
    </>
  )
}
