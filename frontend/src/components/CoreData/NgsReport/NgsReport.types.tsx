export type MetadataType = {
  kitType: string
  kitManufacturer: string
  sequencer: string
  referenceGenome: string
  pipeline: string
}

export type SimpleVariantType = {
  id: string
  chromosome: "chr1" | "chr22" | "chrX" | "chrY"
  gene: GenCodingType
  startEnd: StartEndType
  refAllele: string
  altAllele: string
  dnaChange: CodeType
  aminoAcidChange: CodeType
  readDepth: number
  allelicFrequency: number
  cosmicId: string
  dbSNPId: string
  interpretation: CodeType
}

export type CnVariantType = {
  id: string
  chromosome: "chr1" | "chr22" | "chrX" | "chrY"
  startRange: StartEndType
  endRange: StartEndType
  totalCopyNumber: number
  relativeCopyNumber: number
  cnA: number
  cnB: number
  reportedAffectedGenes: GenCodingType[]
  reportedFocality: string
  type: "low-level-gain" | "high-level-gain" | "loss"
  copyNumberNeutralLoH: GenCodingType[]
}

export type GenCodingType = {
  ensemblId: string
  hgncId: string
  symbol: string
  name: string
}

export type StartEndType = {
  start: number
  end: number
}

export type CodeType = {
  code: string
  system: string
}

export type DnaFusionType = {
  id: string
  fusionPartner5prime: DnaFusionPartnerType
  fusionPartner3prime: DnaFusionPartnerType
  reportedNumReads: number
}

export type DnaFusionPartnerType = {
  chromosome: "chr1" | "chr22" | "chrX" | "chrY"
  position: number
  gene: GenCodingType
}

export type RnaFusionType = {
  id: string
  fusionPartner5prime: RnaFusionPartnerType
  fusionPartner3prime: RnaFusionPartnerType
  effect: string
  cosmicId: string
  reportedNumReads: number
}

export type RnaFusionPartnerType = {
  gene: GenCodingType
  transcriptId: string
  exon: string
  position: number
  strand: "+" | "-"
}

export type RnaSeqType = {
  id: string
  entrezId: string
  ensemblId: string
  gene: GenCodingType
  transcriptId: string
  fragmentsPerKilobaseMillion: number
  fromNGS: boolean
  tissueCorrectedExpression: boolean
  rawCounts: number
  librarySize: number
  cohortRanking: number
}
