package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.FamilyMemberDiagnosisDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.coredata.FamilyMemberDiagnosis;
import de.uke.iam.mtb.control.models.mapper.coredata.FamilyMemberDiagnosisMapper;
import de.uke.iam.mtb.control.repository.coredata.FamilyMemberDiagnosisRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FamilyMemberDiagnosisService {

    private FamilyMemberDiagnosisRepository familyMemberDiagnosisRepository;
    private FamilyMemberDiagnosisMapper familyMemberDiagnosisMapper;
    private EpisodeService episodeService;

    public FamilyMemberDiagnosisService(FamilyMemberDiagnosisRepository familyMemberDiagnosisRepository, FamilyMemberDiagnosisMapper familyMemberDiagnosisMapper, EpisodeService episodeService) {
        this.familyMemberDiagnosisRepository = familyMemberDiagnosisRepository;
        this.familyMemberDiagnosisMapper = familyMemberDiagnosisMapper;
        this.episodeService = episodeService;
    }

    public FamilyMemberDiagnosisDto addFamilyMemberDiagnosis(FamilyMemberDiagnosisDto familyMemberDiagnosisDto) throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(familyMemberDiagnosisDto.getEpisodeId())) {
            throw new ForeignKeyException("Episode with ID " + familyMemberDiagnosisDto.getEpisodeId() + " does not exist");
        }

        FamilyMemberDiagnosis familyMemberDiagnosis = familyMemberDiagnosisMapper.toEntity(familyMemberDiagnosisDto);
        FamilyMemberDiagnosis savedFamilyMemberDiagnosis = familyMemberDiagnosisRepository.save(familyMemberDiagnosis);

        return familyMemberDiagnosisMapper.toDto(savedFamilyMemberDiagnosis);
    }

    public boolean isFamilyMemberDiagnosisExist(UUID familyMemberDiagnosisId) {
        return familyMemberDiagnosisRepository.existsById(familyMemberDiagnosisId);
    }

    public FamilyMemberDiagnosisDto getFamilyMemberDiagnosis(UUID familyMemberDiagnosisId) {

        FamilyMemberDiagnosis savedFamilyMemberDiagnosis = familyMemberDiagnosisRepository.getById(familyMemberDiagnosisId);

        return familyMemberDiagnosisMapper.toDto(savedFamilyMemberDiagnosis);

    }

    public FamilyMemberDiagnosis getFamilyMemberDiagnosisReference(UUID familyMemberDiagnosisId) {

        return familyMemberDiagnosisRepository.getReferenceById(familyMemberDiagnosisId);
    }

    public List<FamilyMemberDiagnosisDto> getAllFamilyMemberDiagnosis(UUID episodeId) throws ForeignKeyException {

        if (!episodeService.isEpisodeExist(episodeId)) {
            throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
        }

        List<FamilyMemberDiagnosis> savedFamilyMemberDiagnosis = familyMemberDiagnosisRepository.getAllByEpisodeId(episodeId);

        return savedFamilyMemberDiagnosis.stream().map(familyMemberDiagnosisMapper::toDto).toList();
    }

    public FamilyMemberDiagnosisDto updateFamilyMemberDiagnosis(UUID familyMemberDiagnosisId, FamilyMemberDiagnosisDto familyMemberDiagnosisDto) {

        FamilyMemberDiagnosis familyMemberDiagnosis = familyMemberDiagnosisMapper.toEntity(familyMemberDiagnosisDto);
        FamilyMemberDiagnosis updatedFamilyMemberDiagnosis = familyMemberDiagnosisRepository.save(familyMemberDiagnosis);

        return familyMemberDiagnosisMapper.toDto(updatedFamilyMemberDiagnosis);
    }

    public void deleteFamilyMemberDiagnosis(UUID familyMemberDiagnosisId) {

        familyMemberDiagnosisRepository.deleteById(familyMemberDiagnosisId);

    }


}
