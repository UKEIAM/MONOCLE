import React, { useEffect } from "react"
import { Requirement } from "../../gen/api"
import { FormProvider, useForm } from "react-hook-form"
import FormRadioGroup from "../FormFields/FormRadioGroup"
import {
  initialRequirementFormValues,
  RequirementNgsTypeEnumOptions,
  RequirementOthersTypeEnumOptions,
  RequirementStandardTypeEnumOptions,
  trueFalseRadioGroupOptions,
} from "./RequirementFormConfig"
import FormCheckbox from "../FormFields/FormCheckbox"
import { Button, Card, TextareaAutosize, Typography, useTheme } from "@mui/material"
import Session from "../../hooks/Session"
import { useApi } from "../../hooks/useApi"
import { useNotification } from "../../hooks/useNotification"

export default function RequirementCard() {
  const theme = useTheme()
  const { RequirementApi, EpisodeApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const methods = useForm<Requirement>()
  const episodeId = Session.getEpisodeId()

  useEffect(() => {
    EpisodeApi.getEpisode(episodeId).then((response) => {
      const { requirementId } = response.data

      if (requirementId === undefined || requirementId === null) {
        methods.reset({ ...initialRequirementFormValues, episodeId })
        return
      }

      RequirementApi.getRequirement(episodeId, requirementId)
        .then((requirementResponse) => {
          methods.reset(requirementResponse.data)
        })
        .catch(() => {
          showErrorNotification(
            "Die MTB-Anforderungsempfehlung konnten nicht geladen werden. Bitte versuchen Sie es später erneut.",
          )
        })
    })
  }, [EpisodeApi, RequirementApi, episodeId, methods, showErrorNotification])

  const handleSubmit = (requirementForm: Requirement) => {
    const updateOrAddPromise = requirementForm.id
      ? RequirementApi.updateRequirement(episodeId, requirementForm.id, requirementForm)
      : RequirementApi.addRequirement(episodeId, requirementForm)
    const successMessage = requirementForm.id
      ? "Anforderungsempfehlung wurde erfolgreich aktualisiert"
      : "Anforderungsempfehlung wurde erfolgreich gespeichert"

    updateOrAddPromise
      .then((response) => {
        showSuccessNotification(successMessage)
        // reset form after submit
        methods.reset(response.data)
      })
      .catch(() => {
        showErrorNotification(
          "Die Änderungen konnten nicht gespeichert werden. Bitte versuchen Sie es später erneut.",
        )
      })
  }
  return (
    <div
      style={{
        padding: "2rem",
        backgroundColor: theme.palette.primary.light,
        display: "flex",
        justifyContent: "center",
        minHeight: "100vh",
      }}
    >
      {/* MTB Requirements Form */}
      <FormProvider {...methods}>
        <form
          onSubmit={methods.handleSubmit(handleSubmit)}
          style={{ width: "100%", maxWidth: "1200px" }}
        >
          {/* MTB Requirements Yes/No */}
          <Typography
            variant="h5"
            gutterBottom
            style={{ fontWeight: 600, color: theme.palette.text.primary }}
          >
            MTB-Anforderungsempfehlung
          </Typography>
          <FormRadioGroup name={"recommended"} options={trueFalseRadioGroupOptions} />
          {/* If MTB Requirements is Recommended */}
          <Card style={{ padding: "1rem", margin: "1rem 0", borderRadius: "8px" }}>
            <Typography variant="h6" gutterBottom>
              Molekulare Diagnostik
            </Typography>

            {/* Molecular Diagnostic Yes/No */}
            <FormRadioGroup name="moleculareDiagnostic" options={trueFalseRadioGroupOptions} />

            <Card
              style={{
                backgroundColor: "white",
                border: "1px solid #ccc",
                padding: "10px",
                margin: "10px",
              }}
            >
              <Typography variant="h6" gutterBottom>
                Intern/Extern Diagnostik
              </Typography>
              {/* Intern/Extern Diagnostik Yes/No */}
              <FormRadioGroup name="internDiagnostic" options={trueFalseRadioGroupOptions} />
              {/*    /!* Molecular Analytics *!/*/}
              <Typography>molekulare Analytik</Typography>
              <FormCheckbox name={"ngs"} label={"NGS"} />
              <br />
              {methods.watch("ngs") && (
                <FormRadioGroup
                  name={"ngsType"}
                  label={""}
                  options={RequirementNgsTypeEnumOptions}
                />
              )}
              <br />
              <FormCheckbox name={"standard"} label={"Standardmethodik"} />
              <br />
              {methods.watch("standard") && (
                <FormRadioGroup
                  name={"standardType"}
                  options={RequirementStandardTypeEnumOptions}
                />
              )}
              <br />
              <FormCheckbox name={"others"} label={"Weitere fakultative Methoden"} />
              <br />
              {methods.watch("others") && (
                <FormRadioGroup name={"othersType"} options={RequirementOthersTypeEnumOptions} />
              )}
            </Card>
          </Card>
          {/* Comment of the requirement */}
          <Card style={{ padding: "1rem", margin: "1rem 0", borderRadius: "8px" }}>
            <Typography variant="h6" gutterBottom>
              Kommentare zur Anforderung
            </Typography>
            <TextareaAutosize
              {...methods.register("comment")}
              minRows={20}
              placeholder="Ihre Kommentare hier..."
              style={{
                width: "100%",
                borderRadius: "8px",
                border: "none",
                boxShadow: "0px 4px 6px rgba(0, 0, 0, 0.1)",
                backgroundColor: "#f9f9f9",
              }}
            />
          </Card>
          {/* Submit Button */}
          <Button variant="contained" color="primary" type={"submit"}>
            {methods.watch("id") ? "Aktualisieren" : "Speichern"}
          </Button>
        </form>
      </FormProvider>
    </div>
  )
}
