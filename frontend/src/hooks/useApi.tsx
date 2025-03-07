import React from "react"

import {
  AddressbookentryApi,
  AudittrailentryApi,
  BwhcTransferApi,
  CarePlanApi,
  ClaimApi,
  ClaimResponseApi,
  CommentsApi,
  Configuration,
  DiagnoseApi,
  EcogStatusApi,
  EpisodeApi,
  FamilyMemberDiagnosisApi,
  GuidelineTherapyApi,
  HealthinsuranceApi,
  HistologyReevaluationRequestApi,
  HistologyReportApi,
  IhcReportApi,
  KcReportApi,
  LabNumberApi,
  MolecularPathologyFindingApi,
  MolecularTherapyApi,
  MolecularTherapyResponseApi,
  NgsReportApi,
  PatientApi,
  PresentationApi,
  RebiopsyRequestApi,
  RequirementApi,
  SpecimenApi,
  StepsinfoApi,
  StudyInclusionRequestApi,
  TherapyRecommendationApi,
  UploadApi,
  WorkflowApi,
} from "../gen/api"
import { useSafeContext } from "./useSafeContext"
import { BackendNotReachableModal } from "../components/BackendNotReachableModal"
import { useAxiosRetry } from "./useAxiosRetry"
import useAxiosAuthInterceptors from "./useAxiosAuthInterceptors"

type ApiContextType =
  | {
      PatientApi: PatientApi
      WorkflowApi: WorkflowApi
      StepsinfoApi: StepsinfoApi
      CommentsApi: CommentsApi
      AudittrailentryApi: AudittrailentryApi
      AddressbookentryApi: AddressbookentryApi
      HealthinsuranceApi: HealthinsuranceApi
      DiagnoseApi: DiagnoseApi
      HistologyReportApi: HistologyReportApi
      SpecimenApi: SpecimenApi
      FamilyMemberDiagnosisApi: FamilyMemberDiagnosisApi
      MolecularPathologyFindingApi: MolecularPathologyFindingApi
      EcogStatusApi: EcogStatusApi
      LabNumberApi: LabNumberApi
      RebiopsyRequestApi: RebiopsyRequestApi
      HistologyReevaluationRequestApi: HistologyReevaluationRequestApi
      StudyInclusionRequestApi: StudyInclusionRequestApi
      NgsReportApi: NgsReportApi
      MolecularTherapyApi: MolecularTherapyApi
      TherapyRecommendationApi: TherapyRecommendationApi
      ClaimApi: ClaimApi
      ClaimResponseApi: ClaimResponseApi
      CarePlanApi: CarePlanApi
      GuidelineTherapyApi: GuidelineTherapyApi
      MolecularTherapyResponseApi: MolecularTherapyResponseApi
      IhcReportApi: IhcReportApi
      PresentationApi: PresentationApi
      RequirementApi: RequirementApi
      EpisodeApi: EpisodeApi
      BwhcTransferApi: BwhcTransferApi
      UploadApi: UploadApi
      KcReportApi: KcReportApi
    }
  | undefined

const ApiContext = React.createContext<ApiContextType>(undefined)

export const ApiProvider: React.FunctionComponent<{ children: React.ReactNode }> = ({
  children,
}) => {
  useAxiosAuthInterceptors()
  const { isRetryingRequest } = useAxiosRetry()

  const configuration = new Configuration({
    basePath: window.config.MTB_CONTROL_URL,
  })

  const apis = {
    PatientApi: new PatientApi(configuration),
    WorkflowApi: new WorkflowApi(configuration),
    StepsinfoApi: new StepsinfoApi(configuration),
    CommentsApi: new CommentsApi(configuration),
    AudittrailentryApi: new AudittrailentryApi(configuration),
    AddressbookentryApi: new AddressbookentryApi(configuration),
    HealthinsuranceApi: new HealthinsuranceApi(configuration),
    DiagnoseApi: new DiagnoseApi(configuration),
    HistologyReportApi: new HistologyReportApi(configuration),
    SpecimenApi: new SpecimenApi(configuration),
    FamilyMemberDiagnosisApi: new FamilyMemberDiagnosisApi(configuration),
    MolecularPathologyFindingApi: new MolecularPathologyFindingApi(configuration),
    EcogStatusApi: new EcogStatusApi(configuration),
    LabNumberApi: new LabNumberApi(configuration),
    RebiopsyRequestApi: new RebiopsyRequestApi(configuration),
    HistologyReevaluationRequestApi: new HistologyReevaluationRequestApi(configuration),
    StudyInclusionRequestApi: new StudyInclusionRequestApi(configuration),
    NgsReportApi: new NgsReportApi(configuration),
    MolecularTherapyApi: new MolecularTherapyApi(configuration),
    TherapyRecommendationApi: new TherapyRecommendationApi(configuration),
    ClaimApi: new ClaimApi(configuration),
    ClaimResponseApi: new ClaimResponseApi(configuration),
    CarePlanApi: new CarePlanApi(configuration),
    GuidelineTherapyApi: new GuidelineTherapyApi(configuration),
    MolecularTherapyResponseApi: new MolecularTherapyResponseApi(configuration),
    IhcReportApi: new IhcReportApi(configuration),
    PresentationApi: new PresentationApi(configuration),
    RequirementApi: new RequirementApi(configuration),
    EpisodeApi: new EpisodeApi(configuration),
    BwhcTransferApi: new BwhcTransferApi(configuration),
    UploadApi: new UploadApi(configuration),
    KcReportApi: new KcReportApi(configuration),
  }

  return (
    <ApiContext.Provider value={apis}>
      <BackendNotReachableModal isVisible={isRetryingRequest} />
      {children}
    </ApiContext.Provider>
  )
}

export const useApi = () => useSafeContext(ApiContext, "api")
