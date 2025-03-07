import React, { useEffect, useState } from "react"
import Session from "hooks/Session"
import { GuidelineTherapy } from "gen/api"
import { diagnosesToString, TherapyResponseToString } from "utils/Formats"
import { GuidelineTherapiesTable } from "./GuidelineTherapiesTable"
import { GuidelineTherapiesDialog } from "./GuidelineTherapiesDialog"
import { TabProps } from "../CoreDataFormTabs"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"

export function GuidelineTherapies({ selected }: TabProps) {
  const { GuidelineTherapyApi, MolecularTherapyResponseApi, DiagnoseApi } = useApi()
  const { showErrorNotification } = useNotification()
  const episodeId = Session.getEpisodeId().toString()

  const [editElement, setEditElement] = useState<GuidelineTherapy>()
  const [isOpen, setIsOpen] = useState<boolean>(false)

  const [guidelineTherapies, setGuidelineTherapies] = useState<GuidelineTherapy[]>([])
  const [molecularTherapyResponse, setMolecularTherapyResponse] = useState<
    { label: string; value: string }[]
  >([])
  const [diagnosisOptions, setDiagnosisOptions] = useState<{ label: string; value: string }[]>([])
  useEffect(() => {
    // if selected tab is guideline therapies, fetch all guideline therapies and diagnosis options
    if (selected) {
      getAllGuidelineTherapies()
      getDiagnosisOptions()
      getMolecularTherapyResponse()
    }
  }, [selected])
  useEffect(() => {
    if (!isOpen) return
    // initialize diagnosisOptions
    getDiagnosisOptions()
  }, [isOpen])

  const getAllGuidelineTherapies = () => {
    GuidelineTherapyApi.getAllGuidelineTherapies(episodeId).then(({ data: guidelineTherapies }) => {
      setGuidelineTherapies(guidelineTherapies)
    })
  }

  const getDiagnosisOptions = () => {
    DiagnoseApi.getAllDiagnoses(episodeId)
      .then(({ data: response }) => {
        setDiagnosisOptions(
          response.map((elem) => ({ label: diagnosesToString(elem), value: elem.id! })),
        )
      })
      .catch(() => {
        showErrorNotification("Es ist ein Fehler beim Laden von Diagnosen aufgetreten")
      })
  }

  const getMolecularTherapyResponse = () => {
    MolecularTherapyResponseApi.getAllMolecularTherapyResponses(episodeId).then(({ data }) => {
      const responseMap = data.map((molTherapyResponse) => ({
        value: molTherapyResponse.id!,
        label: TherapyResponseToString(molTherapyResponse),
      }))
      setMolecularTherapyResponse(responseMap)
    })
  }

  const handleEdit = (guidelineTherapieId?: string) => {
    if (guidelineTherapieId) {
      GuidelineTherapyApi.getGuidelineTherapy(episodeId, guidelineTherapieId).then(
        ({ data: response }) => {
          setEditElement(response)
          setIsOpen(true)
        },
      )
    } else {
      setEditElement(undefined)
      setIsOpen(true)
    }
  }

  const handleClose = () => {
    setIsOpen(false)
    getAllGuidelineTherapies()
  }

  return (
    <>
      <GuidelineTherapiesTable
        onEdit={handleEdit}
        guidelineTherapies={guidelineTherapies ?? []}
        getAllGuidelineTherapies={getAllGuidelineTherapies}
        diagnosisOptions={diagnosisOptions}
        molecularTherapyResponse={molecularTherapyResponse}
      />
      <GuidelineTherapiesDialog
        open={isOpen}
        onClose={handleClose}
        editElement={editElement}
        diagnosisOptions={diagnosisOptions}
        getAllGuidelineTherapies={getAllGuidelineTherapies}
        molecularTherapyResponse={molecularTherapyResponse}
      />
    </>
  )
}
