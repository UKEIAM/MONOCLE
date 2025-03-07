import { EvidenceLevelObject } from "gen/api"

type Props = {
  levelOfEvidence: EvidenceLevelObject | undefined
}

export default function LevelOfEvidenceDisplay({ levelOfEvidence }: Props) {
  if (!levelOfEvidence) {
    return null
  }

  return (
    <>
      {/*{levelOfEvidence.grading?.code && <>{levelOfEvidence.grading?.code} ({levelOfEvidence.grading?.system})</>}*/}
      Einstufung: {levelOfEvidence.grading?.code}
      <>
        {levelOfEvidence.addendums && levelOfEvidence.addendums?.length > 0 && (
          <ul>
            Zusatz:
            {levelOfEvidence.addendums?.map((lOE, index: number) => (
              // <li>{lOE.code ({lOE.system})</li>
              <li key={index}>{lOE.code}</li>
            ))}
          </ul>
        )}
      </>
      <>
        {levelOfEvidence.publications && levelOfEvidence.publications?.length > 0 && (
          <ul>
            Publikationen:
            {levelOfEvidence.publications?.map((pub, index: number) => (
              <li key={index}>
                {pub.pmid} - {pub.doi}
              </li>
            ))}
          </ul>
        )}
      </>
    </>
  )
}
