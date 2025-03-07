import { CarePlansTable } from "./CarePlansTable"
import { CarePlansDialog } from "./CarePlansDialog"
import React, { useEffect, useState } from "react"
import Session from "hooks/Session"
import {
  diagnosesToString,
  rebiopsyRequestToString,
  studyInclusionRequestToString,
  therapyRecommendationToString,
} from "utils/Formats"
import { TabProps } from "../CoreDataFormTabs"
import { useApi } from "hooks/useApi"
import { CarePlan } from "gen/api"

export function CarePlans({ selected }: TabProps) {
  const {
    CarePlanApi,
    StudyInclusionRequestApi,
    DiagnoseApi,
    RebiopsyRequestApi,
    TherapyRecommendationApi,
  } = useApi()
  const [isOpen, setIsOpen] = useState<boolean>(false)
  const [diagnosesMap, setDiagnosesMap] = useState<{ [key: string]: string }>({})
  const [studyInclusionRequestMap, setStudyInclusionRequestMap] = useState<{
    [key: string]: string
  }>({})
  const [rebiopsyRequestMap, setRebiopsyRequestMap] = useState<{ [key: string]: string }>({})
  const [recommendationsMap, setRecommendationsMap] = useState<{ [key: string]: string }>({})
  const [carePlans, setCarePlans] = useState<CarePlan[]>([])
  const [editElement, setEditElement] = useState<CarePlan>()
  const episodeId = Session.getEpisodeId()

  useEffect(() => {
    // On selected
    if (selected) {
      fillMaps()
    }
  }, [selected])

  useEffect(() => {
    if (!isOpen) getCarePlans()
  }, [isOpen])

  const getCarePlans = () => {
    CarePlanApi.getAllCarePlans(episodeId).then((responseCarePlans) => {
      setCarePlans(responseCarePlans.data)
    })
  }

  const fillMaps = () => {
    DiagnoseApi.getAllDiagnoses(episodeId).then(({ data: response }) => {
      let tempMap: { [key: string]: string } = {}
      response.map((diagnosis) => {
        tempMap[diagnosis.id!] = diagnosesToString(diagnosis)
      })
      setDiagnosesMap(tempMap)
    })

    StudyInclusionRequestApi.getAllStudyInclusionRequests(episodeId).then(({ data: response }) => {
      let tempMap: { [key: string]: string } = {}
      response.map((studyInclusionRequest) => {
        tempMap[studyInclusionRequest.id!] = studyInclusionRequestToString(studyInclusionRequest)
      })
      setStudyInclusionRequestMap(tempMap)
    })

    RebiopsyRequestApi.getAllRebiopsyRequests(episodeId).then(({ data: response }) => {
      let tempMap: { [key: string]: string } = {}
      response.map((rebiopsyRequest) => {
        tempMap[rebiopsyRequest.id!] = rebiopsyRequestToString(rebiopsyRequest)
      })
      setRebiopsyRequestMap(tempMap)
    })

    TherapyRecommendationApi.getAllTherapyRecommendations(episodeId).then(({ data: response }) => {
      let tempMap: { [key: string]: string } = {}
      response.map((therapyRecommendation) => {
        tempMap[therapyRecommendation.id!] = therapyRecommendationToString(therapyRecommendation)
      })
      setRecommendationsMap(tempMap)
    })
  }
  const handleEdit = (uuid?: string) => {
    if (uuid) {
      CarePlanApi.getCarePlan(episodeId, uuid).then(({ data: carePlan }) => {
        setEditElement(carePlan)
        setIsOpen(true)
      })
    } else {
      setEditElement(undefined)
      setIsOpen(true)
    }
  }

  return (
    <>
      <CarePlansTable
        onEdit={handleEdit}
        carePlans={carePlans}
        updateTable={() => getCarePlans()}
        diagnosesMap={diagnosesMap}
        rebiopsyMap={rebiopsyRequestMap}
        studyInclusionMap={studyInclusionRequestMap}
        therapyRecommendationMap={recommendationsMap}
      />
      <CarePlansDialog
        open={isOpen}
        editElement={editElement}
        onClose={() => setIsOpen(false)}
        diagnosesMap={diagnosesMap}
        rebiopsyMap={rebiopsyRequestMap}
        studyInclusionMap={studyInclusionRequestMap}
        therapyRecommendationMap={recommendationsMap}
      />
    </>
  )
}
