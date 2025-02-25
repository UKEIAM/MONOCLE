import { useEffect } from "react"
import { FormProvider, useForm } from "react-hook-form"
import { NgsReport } from "gen/api"
import { Grid } from "@mui/material"
import { TextField } from "components/FormFields/TextField"
import DatePicker from "components/FormFields/DatePicker"
import { TumorCellContent } from "./TumorCellContent"
import { MetadataTable } from "./MetadataTable"
import { SimpleVariantsTable } from "./SimpleVariantsTable"
import { CnVariantsTable } from "./CnVariantsTable"
import { DnaFusionTable } from "./DnaFusionTable"
import { RnaFusionTable } from "./RnaFusionTable"
import { RnaSequencesTable } from "./RnaSequencesTable"
import { useApi } from "hooks/useApi"

export function NgsReportTab({ ngsReport }: { ngsReport: NgsReport }) {
  const { SpecimenApi } = useApi()
  const methods = useForm<NgsReport>()
  useEffect(() => {
    if (ngsReport.episodeId && ngsReport.specimen) {
      SpecimenApi.getSpecimen(ngsReport.episodeId, ngsReport.specimen).then((response) => {
        // Change useless internal Specimen UUID with Specimen Label
        ngsReport.specimen = response.data.labelling

        methods.reset(ngsReport)
      })
    }
  }, [ngsReport])

  return (
    <FormProvider {...methods}>
      <form onSubmit={methods.handleSubmit(() => {})}>
        <Grid container spacing={2} padding={"2rem"}>
          <Grid item xs={12}>
            <TextField
              name={"specimen"}
              label={"Tumorproben"}
              disabled
              isRequired
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid item xs={12}>
            <DatePicker name={"issueDate"} label={"Erstellungsdatum"} disabled isRequired />
          </Grid>
          <Grid item xs={12}>
            <TextField
              name={"sequencingType"}
              label={"Sequenzierungsart"}
              disabled
              isRequired
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid item xs={12}>
            <MetadataTable listOfMetadata={ngsReport.metadata ?? []} />
          </Grid>
          <Grid item xs={12}>
            <TumorCellContent readonly={true} />
          </Grid>
          <Grid item xs={12}>
            <TextField
              name={"tmb.value"}
              label={"Tumor Mutational Burden (TMB)"}
              disabled
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              name={"brcaness.value"}
              label={"BRCAness"}
              disabled
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              name={"msi.value"}
              label={"Micro-satellite Instabilities (MSI)"}
              disabled
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              name={"hrdScore.value"}
              label={"HRD Score"}
              disabled
              InputLabelProps={{ shrink: true }}
            />
          </Grid>
          <Grid item xs={12}>
            <SimpleVariantsTable simpleVariants={ngsReport.simpleVariants ?? []} />
          </Grid>
          <Grid item xs={12}>
            <CnVariantsTable cnVariants={ngsReport.copyNumberVariants ?? []} />
          </Grid>
          <Grid item xs={12}>
            <DnaFusionTable dnaFusions={ngsReport.dnaFusions ?? []} />
          </Grid>
          <Grid item xs={12}>
            <RnaFusionTable rnaFusions={ngsReport.rnaFusions ?? []} />
          </Grid>
          <Grid item xs={12}>
            <RnaSequencesTable rnaSequences={ngsReport.rnaSeqs ?? []} />
          </Grid>
        </Grid>
      </form>
    </FormProvider>
  )
}
