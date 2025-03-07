package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.control.models.Requirement;
import de.uke.iam.mtb.control.models.coredata.CarePlan;
import de.uke.iam.mtb.control.models.coredata.Claim;
import de.uke.iam.mtb.control.models.coredata.ClaimResponse;
import de.uke.iam.mtb.control.models.coredata.Diagnose;
import de.uke.iam.mtb.control.models.coredata.EcogStatus;
import de.uke.iam.mtb.control.models.coredata.FamilyMemberDiagnosis;
import de.uke.iam.mtb.control.models.coredata.GeneticCounsellingRequest;
import de.uke.iam.mtb.control.models.coredata.GuidelineTherapy;
import de.uke.iam.mtb.control.models.coredata.HistologyReevaluationRequest;
import de.uke.iam.mtb.control.models.coredata.HistologyReport;
import de.uke.iam.mtb.control.models.coredata.IhcReport;
import de.uke.iam.mtb.control.models.coredata.MolecularPathologyFinding;
import de.uke.iam.mtb.control.models.coredata.MolecularTherapy;
import de.uke.iam.mtb.control.models.coredata.MolecularTherapyResponse;
import de.uke.iam.mtb.control.models.coredata.NgsReport;
import de.uke.iam.mtb.control.models.coredata.RebiopsyRequest;
import de.uke.iam.mtb.control.models.coredata.Specimen;
import de.uke.iam.mtb.control.models.coredata.StudyInclusionRequest;
import de.uke.iam.mtb.control.models.coredata.TherapyRecommendation;
import de.uke.iam.mtb.control.models.coredata.TumorCellContent;
import de.uke.iam.mtb.control.models.coredata.TumorMorphology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CloningMapper {

  CloningMapper INSTANCE = Mappers.getMapper(CloningMapper.class);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  Requirement clone(Requirement requirement);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  CarePlan clone(CarePlan carePlan);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  Specimen clone(Specimen specimen);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  RebiopsyRequest clone(RebiopsyRequest rebiopsyRequest);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  EcogStatus clone(EcogStatus ecogStatus);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  FamilyMemberDiagnosis clone(FamilyMemberDiagnosis familyMemberDiagnosis);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  HistologyReevaluationRequest clone(HistologyReevaluationRequest histologyReevaluationRequest);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  GeneticCounsellingRequest clone(GeneticCounsellingRequest geneticCounsellingRequest);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  HistologyReport clone(HistologyReport histologyReport);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  TumorCellContent clone(TumorCellContent tumorCellContent);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episodeId", ignore = true)
  TumorMorphology clone(TumorMorphology tumorMorphology);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  MolecularPathologyFinding clone(MolecularPathologyFinding molecularPathologyFinding);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  NgsReport clone(NgsReport ngsReport);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  Diagnose clone(Diagnose diagnose);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  GuidelineTherapy clone(GuidelineTherapy guidelineTherapy);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  StudyInclusionRequest clone(StudyInclusionRequest studyInclusionRequest);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  TherapyRecommendation clone(TherapyRecommendation therapyRecommendation);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  MolecularTherapy clone(MolecularTherapy molecularTherapy);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  MolecularTherapyResponse clone(MolecularTherapyResponse molecularTherapyResponse);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  Claim clone(Claim claim);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  ClaimResponse clone(ClaimResponse claimResponse);

  @DeepClone
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "episode", ignore = true)
  IhcReport clone(IhcReport ihcReport);
}
