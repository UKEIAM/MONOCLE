package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.control.models.Episode;
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
import de.uke.iam.mtb.control.models.mapper.CloningMapper;
import de.uke.iam.mtb.control.repository.coredata.CarePlanRepository;
import de.uke.iam.mtb.control.repository.coredata.ClaimRepository;
import de.uke.iam.mtb.control.repository.coredata.ClaimResponseRepository;
import de.uke.iam.mtb.control.repository.coredata.DiagnoseRepository;
import de.uke.iam.mtb.control.repository.coredata.EcogStatusRepository;
import de.uke.iam.mtb.control.repository.coredata.FamilyMemberDiagnosisRepository;
import de.uke.iam.mtb.control.repository.coredata.GeneticCounsellingRequestRepository;
import de.uke.iam.mtb.control.repository.coredata.GuidelineTherapyRepository;
import de.uke.iam.mtb.control.repository.coredata.HistologyReevaluationRequestRepository;
import de.uke.iam.mtb.control.repository.coredata.HistologyReportRepository;
import de.uke.iam.mtb.control.repository.coredata.IhcReportRepository;
import de.uke.iam.mtb.control.repository.coredata.MolecularPathologyFindingRepository;
import de.uke.iam.mtb.control.repository.coredata.MolecularTherapyRepository;
import de.uke.iam.mtb.control.repository.coredata.MolecularTherapyResponseRepository;
import de.uke.iam.mtb.control.repository.coredata.NgsReportRepository;
import de.uke.iam.mtb.control.repository.coredata.RebiopsyRequestRepository;
import de.uke.iam.mtb.control.repository.coredata.SpecimenRepository;
import de.uke.iam.mtb.control.repository.coredata.StudyInclusionRequestRepository;
import de.uke.iam.mtb.control.repository.coredata.TherapyRecommendationRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CoreDataCloneService {

    private static final Logger logger = LoggerFactory.getLogger(CoreDataCloneService.class);
    private final SpecimenRepository specimenRepository;
    private final EcogStatusRepository ecogStatusRepository;
    private final FamilyMemberDiagnosisRepository familyMemberDiagnosisRepository;
    private final GeneticCounsellingRequestRepository geneticCounsellingRequestRepository;
    private final HistologyReportRepository histologyReportRepository;
    private final MolecularPathologyFindingRepository molecularPathologyFindingRepository;
    private final HistologyReevaluationRequestRepository histologyReevaluationRequestRepository;
    private final NgsReportRepository ngsReportRepository;
    private final RebiopsyRequestRepository rebiopsyRequestRepository;
    private final DiagnoseRepository diagnoseRepository;
    private final GuidelineTherapyRepository guidelineTherapyRepository;
    private final StudyInclusionRequestRepository studyInclusionRequestRepository;
    private final TherapyRecommendationRepository therapyRecommendationRepository;
    private final MolecularTherapyRepository molecularTherapyRepository;
    private final MolecularTherapyResponseRepository molecularTherapyResponseRepository;
    private final CarePlanRepository carePlanRepository;
    private final ClaimRepository claimRepository;
    private final ClaimResponseRepository claimResponseRepository;
    private final IhcReportRepository ihcReportRepository;

    public CoreDataCloneService(SpecimenRepository specimenRepository, EcogStatusRepository ecogStatusRepository,
        FamilyMemberDiagnosisRepository familyMemberDiagnosisRepository,
        GeneticCounsellingRequestRepository geneticCounsellingRequestRepository, HistologyReportRepository histologyReportRepository,
        MolecularPathologyFindingRepository molecularPathologyFindingRepository,
        HistologyReevaluationRequestRepository histologyReevaluationRequestRepository, NgsReportRepository ngsReportRepository,
        RebiopsyRequestRepository rebiopsyRequestRepository, DiagnoseRepository diagnoseRepository,
        GuidelineTherapyRepository guidelineTherapyRepository, StudyInclusionRequestRepository studyInclusionRequestRepository,
        TherapyRecommendationRepository therapyRecommendationRepository, MolecularTherapyRepository molecularTherapyRepository,
        MolecularTherapyResponseRepository molecularTherapyResponseRepository, CarePlanRepository carePlanRepository,
        ClaimRepository claimRepository, ClaimResponseRepository claimResponseRepository,
        IhcReportRepository ihcReportRepository) {

        this.specimenRepository = specimenRepository;
        this.ecogStatusRepository = ecogStatusRepository;
        this.familyMemberDiagnosisRepository = familyMemberDiagnosisRepository;
        this.geneticCounsellingRequestRepository = geneticCounsellingRequestRepository;
        this.histologyReportRepository = histologyReportRepository;
        this.molecularPathologyFindingRepository = molecularPathologyFindingRepository;
        this.histologyReevaluationRequestRepository = histologyReevaluationRequestRepository;
        this.ngsReportRepository = ngsReportRepository;
        this.rebiopsyRequestRepository = rebiopsyRequestRepository;
        this.diagnoseRepository = diagnoseRepository;
        this.guidelineTherapyRepository = guidelineTherapyRepository;
        this.studyInclusionRequestRepository = studyInclusionRequestRepository;
        this.therapyRecommendationRepository = therapyRecommendationRepository;
        this.molecularTherapyRepository = molecularTherapyRepository;
        this.molecularTherapyResponseRepository = molecularTherapyResponseRepository;
        this.carePlanRepository = carePlanRepository;
        this.claimRepository = claimRepository;
        this.claimResponseRepository = claimResponseRepository;
        this.ihcReportRepository = ihcReportRepository;
    }

    public void cloneCoreData(UUID oldEpisodeId, Episode newEpisode) {
        logger.info("old Episode: " + oldEpisodeId);
        logger.info("new Episode: " + newEpisode.getId());

        // Map to save old object Id and new object (with new Id)
        HashMap<UUID, Object> idsMap = new HashMap<>();

        List<Specimen> specimens = specimenRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : specimens) {
            Specimen specimen = CloningMapper.INSTANCE.clone(elem);
            specimen.setEpisode(newEpisode);
            Specimen newSpecimen = specimenRepository.save(specimen);
            logger.info("old Specimen: " + elem);
            logger.info("new Specimen: " + newSpecimen);
            idsMap.put(elem.getId(), newSpecimen);
        }

        List<RebiopsyRequest> rebiopsyRequests = rebiopsyRequestRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : rebiopsyRequests) {
            RebiopsyRequest rebiopsyRequest = CloningMapper.INSTANCE.clone(elem);
            rebiopsyRequest.setEpisode(newEpisode);
            rebiopsyRequest.setSpecimen((Specimen) idsMap.get(elem.getSpecimen().getId()));
            RebiopsyRequest newRebiobsy = rebiopsyRequestRepository.save(rebiopsyRequest);
            logger.info("old Rebiopsy: " + elem);
            logger.info("new Rebiopsy: " + newRebiobsy);
            idsMap.put(elem.getId(), newRebiobsy);
        }

        List<EcogStatus> ecogStatuses = ecogStatusRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : ecogStatuses) {
            EcogStatus ecogStatus = CloningMapper.INSTANCE.clone(elem);
            ecogStatus.setEpisode(newEpisode);
            EcogStatus newEcogStatus = ecogStatusRepository.save(ecogStatus);
            logger.info("old EcogStatus: " + elem);
            logger.info("new EcogStatus: " + newEcogStatus);
            // Does not need to be added to the idsMap as the ecog status is not referenced
        }

        List<FamilyMemberDiagnosis> familyMemberDiagnoses = familyMemberDiagnosisRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : familyMemberDiagnoses) {
            FamilyMemberDiagnosis familyMemberDiagnosis = CloningMapper.INSTANCE.clone(elem);
            familyMemberDiagnosis.setEpisode(newEpisode);
            FamilyMemberDiagnosis familyMember = familyMemberDiagnosisRepository.save(familyMemberDiagnosis);
            logger.info("old FamilyMemberDiagnosis: " + elem);
            logger.info("new FamilyMemberDiagnosis: " + familyMember);
            // Does not need to be added to the idsMap as the family member diagnoses is not referenced
        }

        List<GeneticCounsellingRequest> geneticCounsellingRequests = geneticCounsellingRequestRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : geneticCounsellingRequests) {
            GeneticCounsellingRequest geneticCounsellingRequest = CloningMapper.INSTANCE.clone(elem);
            geneticCounsellingRequest.setEpisode(newEpisode);
            GeneticCounsellingRequest newGeneticCounsellingRequest = geneticCounsellingRequestRepository.save(geneticCounsellingRequest);
            logger.info("old GeneticCounsellingRequest: " + elem);
            logger.info("new GeneticCounsellingRequest: " + newGeneticCounsellingRequest);
            idsMap.put(elem.getId(), newGeneticCounsellingRequest);
        }

        List<HistologyReevaluationRequest> histologyReevaluationRequests = histologyReevaluationRequestRepository.getAllByEpisodeId(
            oldEpisodeId);
        for (var elem : histologyReevaluationRequests) {
            HistologyReevaluationRequest histologyReevaluationRequest = CloningMapper.INSTANCE.clone(elem);
            histologyReevaluationRequest.setEpisode(newEpisode);
            histologyReevaluationRequest.setSpecimen((Specimen) idsMap.get(elem.getSpecimen().getId()));
            HistologyReevaluationRequest newHistologyReevaluationRequest = histologyReevaluationRequestRepository.save(
                histologyReevaluationRequest);
            logger.info("old HistologyReevaluationRequest: " + elem);
            logger.info("new HistologyReevaluationRequest: " + newHistologyReevaluationRequest);
            idsMap.put(elem.getId(), newHistologyReevaluationRequest);
        }

        List<HistologyReport> histologyReports = histologyReportRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : histologyReports) {
            HistologyReport histologyReport = CloningMapper.INSTANCE.clone(elem);
            histologyReport.setEpisode(newEpisode);
            histologyReport.setSpecimen((Specimen) idsMap.get(elem.getSpecimen().getId()));

            // TumorCellContent
            if (histologyReport.getTumorCellContent() != null) {
                TumorCellContent tumorCellContent = CloningMapper.INSTANCE.clone(histologyReport.getTumorCellContent());
                tumorCellContent.setSpecimen(((Specimen) idsMap.get(elem.getSpecimen().getId())).getId());
                tumorCellContent.setId(UUID.randomUUID());
                histologyReport.setTumorCellContent(tumorCellContent);
            }

            // TumorMorphology
            if (histologyReport.getTumorMorphology() != null) {
                TumorMorphology tumorMorphology = CloningMapper.INSTANCE.clone(histologyReport.getTumorMorphology());
                tumorMorphology.setSpecimen(((Specimen) idsMap.get(elem.getSpecimen().getId())).getId());
                tumorMorphology.setEpisodeId(newEpisode.getId());
                tumorMorphology.setId(UUID.randomUUID());
                histologyReport.setTumorMorphology(tumorMorphology);
            }

            HistologyReport newHistologyReport = histologyReportRepository.save(histologyReport);
            logger.info("old histologyReports: " + elem);
            logger.info("new histologyReports: " + newHistologyReport);
            idsMap.put(elem.getId(), newHistologyReport);
        }

        List<MolecularPathologyFinding> molecularPathologyFindings = molecularPathologyFindingRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : molecularPathologyFindings) {
            MolecularPathologyFinding molecularPathologyFinding = CloningMapper.INSTANCE.clone(elem);
            molecularPathologyFinding.setEpisode(newEpisode);
            if (elem.getSpecimen() != null) {
                molecularPathologyFinding.setSpecimen((Specimen) idsMap.get(elem.getSpecimen().getId()));
            }
            MolecularPathologyFinding newMolecularPathologyFinding = molecularPathologyFindingRepository.save(molecularPathologyFinding);
            logger.info("old MolecularPathologyFinding: " + elem);
            logger.info("new MolecularPathologyFinding: " + newMolecularPathologyFinding);
            idsMap.put(elem.getId(), newMolecularPathologyFinding);
        }

        List<NgsReport> ngsReports = ngsReportRepository.getAllByEpisodeId(oldEpisodeId);
        if (ngsReports != null) {
            for (var elem : ngsReports) {
                NgsReport ngsReport = CloningMapper.INSTANCE.clone(elem);
                ngsReport.setId(UUID.randomUUID().toString()); // Must be manually assigned before saving, won't be generated.
                ngsReport.setEpisode(newEpisode);

                // TumorCellContent
                if (ngsReport.getTumorCellContent() != null) {
                    TumorCellContent tumorCellContent = CloningMapper.INSTANCE.clone(ngsReport.getTumorCellContent());
                    tumorCellContent.setSpecimen(((Specimen) idsMap.get(elem.getSpecimen().getId())).getId());
                    tumorCellContent.setId(UUID.randomUUID());
                    ngsReport.setTumorCellContent(tumorCellContent);
                }

                if (elem.getSpecimen() != null) ngsReport.setSpecimen((Specimen) idsMap.get((elem.getSpecimen().getId())));
                NgsReport newNgsReport = ngsReportRepository.save(ngsReport);
                logger.info("old ngsReports: " + elem);
                logger.info("new ngsReports: " + newNgsReport);
                idsMap.put(UUID.fromString(elem.getId()), newNgsReport);
            }
        }

        List<Diagnose> diagnoses = diagnoseRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : diagnoses) {
            Diagnose diagnose = CloningMapper.INSTANCE.clone(elem);
            diagnose.setEpisode(newEpisode);

            List<HistologyReport> histList = new ArrayList<>();
            for (var histologyReport : diagnose.getHistologyResults()) {
                histList.add((HistologyReport) idsMap.get(histologyReport.getId()));
            }
            diagnose.setHistologyResults(histList);

            Diagnose newDiagnosis = diagnoseRepository.save(diagnose);
            logger.info("old diagnoses: " + elem);
            logger.info("new diagnoses: " + newDiagnosis);
            idsMap.put(elem.getId(), newDiagnosis);
        }

        List<GuidelineTherapy> guidelineTherapies = guidelineTherapyRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : guidelineTherapies) {
            GuidelineTherapy guidelineTherapy = CloningMapper.INSTANCE.clone(elem);
            guidelineTherapy.setEpisode(newEpisode);
            guidelineTherapy.setDiagnosis((Diagnose) idsMap.get(elem.getDiagnosis().getId()));
            GuidelineTherapy newGuidelineTherapy = guidelineTherapyRepository.save(guidelineTherapy);
            logger.info("old guidelineTherapies: " + elem);
            logger.info("new guidelineTherapies: " + newGuidelineTherapy);
            idsMap.put(elem.getId(), newGuidelineTherapy);
        }

        List<StudyInclusionRequest> studyInclusionRequests = studyInclusionRequestRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : studyInclusionRequests) {
            StudyInclusionRequest studyInclusionRequest = CloningMapper.INSTANCE.clone(elem);
            studyInclusionRequest.setEpisode(newEpisode);
            studyInclusionRequest.setDiagnose((Diagnose) idsMap.get(elem.getDiagnose().getId()));
            StudyInclusionRequest newStudyInclusionRequest = studyInclusionRequestRepository.save(studyInclusionRequest);
            logger.info("old studyInclusionRequests: " + elem);
            logger.info("new studyInclusionRequests: " + newStudyInclusionRequest);
            idsMap.put(elem.getId(), newStudyInclusionRequest);
        }

        List<TherapyRecommendation> therapyRecommendations = therapyRecommendationRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : therapyRecommendations) {
            TherapyRecommendation therapyRecommendation = CloningMapper.INSTANCE.clone(elem);
            therapyRecommendation.setEpisode(newEpisode);
            therapyRecommendation.setDiagnosis((Diagnose) idsMap.get(elem.getDiagnosis().getId()));

            if (elem.getNgsReport() != null) {
                therapyRecommendation.setNgsReport((NgsReport) idsMap.get(UUID.fromString(elem.getNgsReport().getId())));
            }

            TherapyRecommendation newTherapyRecommendation = therapyRecommendationRepository.save(therapyRecommendation);
            logger.info("old therapyRecommendations: " + elem);
            logger.info("new therapyRecommendations: " + newTherapyRecommendation);
            idsMap.put(elem.getId(), newTherapyRecommendation);
        }

        List<MolecularTherapy> molecularTherapies = molecularTherapyRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : molecularTherapies) {
            MolecularTherapy molecularTherapy = CloningMapper.INSTANCE.clone(elem);
            molecularTherapy.setEpisode(newEpisode);

            if (elem.getTherapyRecommendation() != null) { // should never be null because it's required
                molecularTherapy.setTherapyRecommendation((TherapyRecommendation) idsMap.get(elem.getTherapyRecommendation().getId()));
            }

            MolecularTherapy newMolecularTherapy = molecularTherapyRepository.save(molecularTherapy);
            logger.info("old molecularTherapies: " + elem);
            logger.info("new molecularTherapies: " + newMolecularTherapy);
            idsMap.put(elem.getId(), newMolecularTherapy);
        }

        List<MolecularTherapyResponse> molecularTherapyResponses = molecularTherapyResponseRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : molecularTherapyResponses) {
            MolecularTherapyResponse molecularTherapyResponse = CloningMapper.INSTANCE.clone(elem);
            molecularTherapyResponse.setEpisode(newEpisode);
            // should never be null because it's required
            if (elem.getTherapy() != null) {
                molecularTherapyResponse.setTherapy((MolecularTherapy) idsMap.get(elem.getTherapy().getId()));
            }
            MolecularTherapyResponse newMolecularTherapyResponse = molecularTherapyResponseRepository.save(molecularTherapyResponse);
            logger.info("old MolecularTherapyResponse: " + elem);
            logger.info("new MolecularTherapyResponse: " + newMolecularTherapyResponse);
            idsMap.put(elem.getId(), newMolecularTherapyResponse);
        }

        //Claims
        List<Claim> claims = claimRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : claims) {
            Claim claim = CloningMapper.INSTANCE.clone(elem);
            claim.setEpisode(newEpisode);
            // should never be null because it's required
            if (elem.getTherapyRecommendation() != null) {
                claim.setTherapyRecommendation((TherapyRecommendation) idsMap.get(elem.getTherapyRecommendation().getId()));
            }
            Claim newClaim = claimRepository.save(claim);
            logger.info("old Claim: " + elem);
            logger.info("new Claim: " + newClaim);
            idsMap.put(elem.getId(), newClaim);
        }

        List<IhcReport> ihcReports = ihcReportRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : ihcReports) {
            IhcReport ihcReport = CloningMapper.INSTANCE.clone(elem);
            ihcReport.setEpisode(newEpisode);
            // should never be null because it's required
            if (elem.getSpecimen() != null) {
                ihcReport.setSpecimen((Specimen) idsMap.get(elem.getSpecimen().getId()));
            }
            for (var result : ihcReport.getProteinExpressionResults()) {
                result.setId(UUID.randomUUID());
            }
            for (var result : ihcReport.getMsiMmrResults()) {
                result.setId(UUID.randomUUID());
            }
            IhcReport newIhcReport = ihcReportRepository.save(ihcReport);
            logger.info("old IhcReport: " + elem);
            logger.info("new IhcReport: " + newIhcReport);
            idsMap.put(elem.getId(), newIhcReport);
        }

        //claims response
        List<ClaimResponse> claimsResponses = claimResponseRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : claimsResponses) {
            ClaimResponse claimResponse = CloningMapper.INSTANCE.clone(elem);
            claimResponse.setEpisode(newEpisode);
            // should never be null because it's required
            if (elem.getClaim() != null) {
                claimResponse.setClaim((Claim) idsMap.get(elem.getClaim().getId()));
            }
            ClaimResponse newClaimResponse = claimResponseRepository.save(claimResponse);
            logger.info("old ClaimResponse: " + elem);
            logger.info("new ClaimResponse: " + newClaimResponse);
            idsMap.put(elem.getId(), newClaimResponse);
        }

        List<CarePlan> carePlans = carePlanRepository.getAllByEpisodeId(oldEpisodeId);
        for (var elem : carePlans) {
            CarePlan carePlan = CloningMapper.INSTANCE.clone(elem);
            carePlan.setEpisode(newEpisode);

            // References:
            // Diagnosis
            carePlan.setDiagnosis((Diagnose) idsMap.get(elem.getDiagnosis().getId()));
            // GeneticCounsellingRequest
            if (elem.getGeneticCounsellingRequest() != null) {
                carePlan.setGeneticCounsellingRequest((GeneticCounsellingRequest) idsMap.get(elem.getGeneticCounsellingRequest().getId()));
            }
            // TherapyRecommendations
            if (elem.getRecommendations() != null) {
                List<TherapyRecommendation> recommendations = new ArrayList<>();
                for (var recommendation : elem.getRecommendations()) {
                    recommendations.add((TherapyRecommendation) idsMap.get(recommendation.getId()));
                }
                carePlan.setRecommendations(recommendations);
            }
            //RebiopsyRequest
            if (elem.getRebiopsyRequests() != null) {
                List<RebiopsyRequest> rebiopsyRequestList = new ArrayList<>();
                for (var rebiopsy : elem.getRebiopsyRequests()) {
                    rebiopsyRequestList.add((RebiopsyRequest) idsMap.get(rebiopsy.getId()));
                }
                carePlan.setRebiopsyRequests(rebiopsyRequestList);
            }
            //StudyInclusionRequest
            if (elem.getStudyInclusionRequests() != null) {
                List<StudyInclusionRequest> studyInclusionList = new ArrayList<>();
                for (var studyInclusion : elem.getStudyInclusionRequests()) {
                    studyInclusionList.add((StudyInclusionRequest) idsMap.get(studyInclusion.getId()));
                }
                carePlan.setStudyInclusionRequests(studyInclusionList);
            }
            CarePlan newCarePlan = carePlanRepository.save(carePlan);
            logger.info("old CarePlan: " + elem);
            logger.info("new CarePlan: " + newCarePlan);
            idsMap.put(elem.getId(), newCarePlan);
        }
        logger.info("idsMap " + idsMap);
    }

}

