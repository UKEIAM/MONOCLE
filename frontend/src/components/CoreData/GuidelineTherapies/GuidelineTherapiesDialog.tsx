import { Dialog, DialogActions, DialogContent, DialogTitle, Grid, Typography } from "@mui/material"
import { FormProvider, useForm } from "react-hook-form"
import "dayjs/locale/de"
import { useEffect } from "react"
import Button from "@mui/material/Button"
import { MedicationComponent } from "components/FormFields/MedicationComponent"
import FormSelect from "components/FormFields/FormSelect"
import FormDatePicker from "components/FormFields/FormDatePicker"
import FormNumberField from "components/FormFields/FormNumberField"
import { GuidelineTherapy } from "gen/api"
import Session from "hooks/Session"
import { reasonStoppedOptions } from "./GuidelineTherapiesTypes"
import { useApi } from "hooks/useApi"
import { useNotification } from "hooks/useNotification"
import dayjs from "dayjs"

type PropType = {
  open: boolean
  onClose: () => void
  editElement?: GuidelineTherapy
  diagnosisOptions: { label: string; value: string }[]
  getAllGuidelineTherapies: () => void
  molecularTherapyResponse: { label: string; value: string }[]
}

export function GuidelineTherapiesDialog({
  open,
  onClose,
  editElement,
  diagnosisOptions,
  getAllGuidelineTherapies,
  molecularTherapyResponse,
}: PropType) {
  const episodeId = Session.getEpisodeId()
  const { GuidelineTherapyApi } = useApi()
  const { showSuccessNotification, showErrorNotification } = useNotification()
  const methods = useForm<GuidelineTherapy>({ mode: "all" })
  useEffect(() => {
    if (!open) return
    // initialize form
    methods.reset({
      ...editElement,
      episodeId: episodeId,
      reasonStopped: {
        ...editElement?.reasonStopped,
        system: "dnpm-dip/therapy/status-reason",
      },
    })
  }, [open])

  const handleSubmit = (formData: GuidelineTherapy) => {
    // push changes
    const addOrUpdatePromise = formData.id
      ? GuidelineTherapyApi.updateGuidelineTherapy(episodeId, formData.id, formData)
      : GuidelineTherapyApi.addGuidelineTherapy(episodeId, formData)

    addOrUpdatePromise
      .then(() => {
        showSuccessNotification("Die Leitlinien-Therapie wurden erfolgreich gespeichert")
        getAllGuidelineTherapies()
        onClose()
      })
      .catch(() => {
        showErrorNotification("Beim Speichern der Leitlinien-Therapie ist ein Fehler aufgetreten.")
      })
  }

  return (
    <>
      <Dialog disableEnforceFocus open={open}>
        <FormProvider {...methods}>
          <form onSubmit={methods.handleSubmit(handleSubmit)}>
            <DialogTitle>Leitlinien-Therapie</DialogTitle>
            <DialogContent>
              <Grid container spacing={2} sx={{ marginTop: 2 }}>
                <Grid item xs={12}>
                  <FormSelect
                    name={"diagnosis"}
                    label={"Diagnose"}
                    validationRules={{ required: true }}
                    options={diagnosisOptions}
                  />
                </Grid>
                <Grid item xs={12}>
                  <FormNumberField
                    name={"therapyLine"}
                    label={"Therapielinie"}
                    validationRules={{
                      min: 0,
                      max: 9,
                      warning: !Boolean(methods.watch("therapyLine")),
                    }}
                  />
                </Grid>

                {/* Start date*/}
                <Grid item xs={6}>
                  <FormDatePicker
                    name={"period.start"}
                    label={"Startdatum"}
                    maxdate={dayjs(methods.getValues("period.end"))}
                    validationRules={{ required: true }}
                  />
                </Grid>

                {/* End date*/}
                <Grid item xs={6}>
                  <FormDatePicker
                    name={"period.end"}
                    label={"Enddatum"}
                    mindate={dayjs(methods.getValues("period.start"))}
                    maxdate={dayjs(new Date())}
                    validationRules={{ warning: true }}
                  />
                </Grid>

                {/* Reason Stopped */}
                <Grid item xs={12}>
                  <FormSelect
                    name={"reasonStopped.code"}
                    label={"Grund für Therapieende"}
                    options={reasonStoppedOptions}
                    validationRules={{ warning: true }}
                  />
                </Grid>
                <MedicationComponent />
                {/*TODO : wait until it is clear which code will be used in the following components*/}
                {/*                <Grid container item xs={12} spacing={2}>
                  <Grid item xs={12}>
                    <Typography style={{ fontWeight: "bold" }}>Prozedur</Typography>
                  </Grid>
                  <Grid item xs={12}>
                    <FormTextField
                      name={"procedure.code"}
                      label={
                        "Prozedur (Typ/Kategorie) z.B. OPS-Code oder surgery, radio-therapy, nuclear-medicine"
                      }
                    ></FormTextField>
                  </Grid>
                  <Grid item xs={12}>
                    <FormTextField
                      name={"procedure.display"}
                      label={
                        "Anzeigename (OPS-Name oder OP, Strahlentherapie, Nuklearmedizinische Therapie)"
                      }
                    ></FormTextField>
                  </Grid>
                  <Grid item xs={12}>
                    <FormTextField
                      name={"procedure.system"}
                      label={"System (OPS oder dnpm-dip/mtb/procedure/type)"}
                    ></FormTextField>
                  </Grid>
                  <Grid item xs={12}>
                    <FormTextField name={"procedure.version"} label={"Version"}></FormTextField>
                  </Grid>
                </Grid>
                <Grid item xs={12}>
                  procedurePosition TODO to be checked if correct
                  <FormSelect
                    name={"procedurePosition"}
                    label={"Stellung zur OP"}
                    options={procedurePositionOptions}
                  />
                </Grid>
                <Grid item xs={12}>
                  intention TODO to be checked if correct
                  <FormSelect name={"intention"} label={"Intention"} options={intensionOptions} />
                </Grid>*/}
                {/*<Grid item xs={12}>*/}
                {/*  /!*progressDate TODO to be checked if correct *!/*/}
                {/*  <FormDatePicker*/}
                {/*    name={"progressDate"}*/}
                {/*    label={"Datum Progress"}*/}
                {/*  />*/}
                {/*</Grid>*/}
                {/*<Grid item xs={12}>*/}
                {/*  /!*molecularTherapyResponse TODO to be checked if correct *!/*/}
                {/*  <FormSelect*/}
                {/*    name={"molecularTherapyResponse"}*/}
                {/*    label={"Therapieansprechen (Molekular-Therapie-Befund)"}*/}
                {/*    options={molecularTherapyResponse}*/}
                {/*  />*/}
                {/*</Grid>*/}
                <Grid item xs={12}>
                  <Typography>* Pflichtfelder</Typography>
                </Grid>
              </Grid>
            </DialogContent>
            <DialogActions>
              <Button variant="contained" type={"submit"}>
                {editElement !== undefined ? "aktualisieren" : "hinzufügen"}
              </Button>
              <Button variant="contained" onClick={onClose}>
                abbrechen
              </Button>
            </DialogActions>
          </form>
        </FormProvider>
      </Dialog>
    </>
  )
}
