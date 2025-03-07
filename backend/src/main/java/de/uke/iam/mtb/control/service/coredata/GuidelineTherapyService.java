package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.GuidelineTherapyDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.Episode;
import de.uke.iam.mtb.control.models.coredata.Diagnose;
import de.uke.iam.mtb.control.models.coredata.GuidelineTherapy;
import de.uke.iam.mtb.control.models.coredata.MolecularTherapyResponse;
import de.uke.iam.mtb.control.models.mapper.coredata.GuidelineTherapyMapper;
import de.uke.iam.mtb.control.repository.coredata.GuidelineTherapyRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Transactional
public class GuidelineTherapyService {

    private final GuidelineTherapyRepository guidelineTherapyRepository;
    private final GuidelineTherapyMapper guidelineTherapyMapper;
    private final EpisodeService episodeService;
    private final DiagnoseService diagnoseService;
    private final MolecularTherapyResponseService molecularTherapyResponseService;

    public GuidelineTherapyDto addGuidelineTherapy(GuidelineTherapyDto guidelineTherapyDto) throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(guidelineTherapyDto.getEpisodeId())) {
            throw new ForeignKeyException("Episode with ID " + guidelineTherapyDto.getEpisodeId() + " does not exist");
        }

        if (!diagnoseService.isDiagnoseExist(guidelineTherapyDto.getDiagnosis())) {
            throw new ForeignKeyException("Diagnose with ID " + guidelineTherapyDto.getDiagnosis() + " does not exist");
        }

        GuidelineTherapy guidelineTherapy = guidelineTherapyMapper.toEntity(guidelineTherapyDto);
        Episode episodeReference = episodeService.getEpisodeReference(guidelineTherapyDto.getEpisodeId());
        Diagnose diagnoseReference = diagnoseService.getDiagnoseReference(guidelineTherapyDto.getDiagnosis());

        MolecularTherapyResponse molecularTherapyResponseReference = null;
        if (guidelineTherapyDto.getMolecularTherapyResponse() != null) {
            molecularTherapyResponseReference = molecularTherapyResponseService
                .getMolecularTherapyResponseReference(guidelineTherapyDto.getMolecularTherapyResponse());
        }
        guidelineTherapy.setMolecularTherapyResponse(molecularTherapyResponseReference);

        guidelineTherapy.setEpisode(episodeReference);
        guidelineTherapy.setDiagnosis(diagnoseReference);
        GuidelineTherapy savedGuidelineTherapy = guidelineTherapyRepository.save(guidelineTherapy);

        return guidelineTherapyMapper.toDto(savedGuidelineTherapy);
    }

    public boolean isGuideLineTherapyValid(GuidelineTherapyDto guidelineTherapyDto) {
        if (guidelineTherapyDto == null) {
            return false;
        }

        if (guidelineTherapyDto.getEpisodeId() == null ||
            guidelineTherapyDto.getDiagnosis() == null ||
            guidelineTherapyDto.getPeriod() == null ||
            guidelineTherapyDto.getPeriod().getStart() == null) {
            return false;
        }

        Integer therapyLine = guidelineTherapyDto.getTherapyLine();
        if (therapyLine != null && (therapyLine < 0 || therapyLine > 9)) {
            return false;
        }

        return true;
    }

    public boolean isGuidelineTherapyExist(UUID guidelineTherapyId) {
        return guidelineTherapyRepository.existsById(guidelineTherapyId);
    }

    public GuidelineTherapyDto getGuidelineTherapy(UUID guidelineTherapyId) {
        GuidelineTherapy savedGuidelineTherapy = guidelineTherapyRepository.findById(guidelineTherapyId).orElse(null);
        return guidelineTherapyMapper.toDto(savedGuidelineTherapy);
    }

    public GuidelineTherapy getGuidelineTherapyReference(UUID guidelineTherapyId) {

        return guidelineTherapyRepository.getReferenceById(guidelineTherapyId);
    }

    public List<GuidelineTherapyDto> getAllGuidelineTherapies(UUID episodeId) throws ForeignKeyException {

        if (!episodeService.isEpisodeExist(episodeId)) {
            throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
        }

        List<GuidelineTherapy> savedGuidelineTherapy = guidelineTherapyRepository.getAllByEpisodeId(episodeId);

        return savedGuidelineTherapy.stream().map(guidelineTherapyMapper::toDto).toList();
    }

    public GuidelineTherapyDto updateGuidelineTherapy(UUID guidelineTherapyId, GuidelineTherapyDto guidelineTherapyDto)
        throws ForeignKeyException {


        if (!diagnoseService.isDiagnoseExist(guidelineTherapyDto.getDiagnosis())) {
            throw new ForeignKeyException("Diagnose with ID " + guidelineTherapyDto.getDiagnosis() + " does not exist");
        }

        GuidelineTherapy guidelineTherapy = guidelineTherapyMapper.toEntity(guidelineTherapyDto);

        MolecularTherapyResponse molecularTherapyResponseReference = null;
        if (guidelineTherapyDto.getMolecularTherapyResponse() != null) {
            molecularTherapyResponseReference = molecularTherapyResponseService
                .getMolecularTherapyResponseReference(guidelineTherapyDto.getMolecularTherapyResponse());
        }
        guidelineTherapy.setMolecularTherapyResponse(molecularTherapyResponseReference);

        GuidelineTherapy updatedGuidelineTherapy = guidelineTherapyRepository.save(guidelineTherapy);

        return guidelineTherapyMapper.toDto(updatedGuidelineTherapy);
    }

    public void deleteGuidelineTherapy(UUID guidelineTherapyId) {

        guidelineTherapyRepository.deleteById(guidelineTherapyId);

    }

}
