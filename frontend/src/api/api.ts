import { Configuration, PatientApi, WorkflowApi } from "gen/api"

function getConfiguration() {
  return new Configuration({
    basePath: window.config.MTB_CONTROL_URL,
  })
}

export const patientApi = new PatientApi(getConfiguration())
export const workflowApi = new WorkflowApi(getConfiguration())
