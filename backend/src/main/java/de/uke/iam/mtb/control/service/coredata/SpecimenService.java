package de.uke.iam.mtb.control.service.coredata;

import de.uke.iam.mtb.api.model.SpecimenDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.coredata.Specimen;
import de.uke.iam.mtb.control.models.mapper.coredata.SpecimenMapper;
import de.uke.iam.mtb.control.repository.coredata.HistologyReevaluationRequestRepository;
import de.uke.iam.mtb.control.repository.coredata.HistologyReportRepository;
import de.uke.iam.mtb.control.repository.coredata.IhcReportRepository;
import de.uke.iam.mtb.control.repository.coredata.MolecularPathologyFindingRepository;
import de.uke.iam.mtb.control.repository.coredata.NgsReportRepository;
import de.uke.iam.mtb.control.repository.coredata.RebiopsyRequestRepository;
import de.uke.iam.mtb.control.repository.coredata.SpecimenRepository;
import de.uke.iam.mtb.control.service.EpisodeService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class SpecimenService {

    private final SpecimenRepository specimenRepository;
    private final SpecimenMapper specimenMapper;
    private final EpisodeService episodeService;

    private final HistologyReevaluationRequestRepository histologyReevaluationRequestRepository;
    private final MolecularPathologyFindingRepository molecularPathologyFindingRepository;
    private final NgsReportRepository ngsReportRepository;
    private final RebiopsyRequestRepository rebiopsyRequestRepository;
    private final HistologyReportRepository histologyReportRepository;
    private final IhcReportRepository ihcReportRepository;

    public SpecimenDto addSpecimen(SpecimenDto specimenDto) throws ForeignKeyException {
        if (!episodeService.isEpisodeExist(specimenDto.getEpisodeId())) {
            throw new ForeignKeyException("Episode with ID " + specimenDto.getEpisodeId() + " does not exist");
        }

        Specimen specimen = specimenMapper.toEntity(specimenDto);
        Specimen savedSpecimen = specimenRepository.save(specimen);

        return specimenMapper.toDto(savedSpecimen);
    }

    public boolean isSpecimenExist(UUID specimenId) {
        return specimenRepository.existsById(specimenId);
    }

    public boolean isSpecimenWithLabelExists(String specimenLabel) {
        return specimenRepository.isSpecimenWithLabelExists(specimenLabel);
    }

    public SpecimenDto getSpecimen(UUID specimenId) {

        Specimen savedSpecimen = specimenRepository.getReferenceById(specimenId);

        return specimenMapper.toDto(savedSpecimen);

    }

    public Specimen getSpecimenReference(UUID specimenId) {

        return specimenRepository.getReferenceById(specimenId);
    }

    public List<SpecimenDto> getAllSpecimens(UUID episodeId) throws ForeignKeyException {

        if (!episodeService.isEpisodeExist(episodeId)) {
            throw new ForeignKeyException("Episode with ID " + episodeId + " does not exist");
        }

        List<Specimen> savedSpecimen = specimenRepository.getAllByEpisodeId(episodeId);

        return savedSpecimen.stream().map(specimenMapper::toDto).toList();
    }

    public SpecimenDto updateSpecimen(UUID specimenId, SpecimenDto specimenDto) {

        Specimen specimen = specimenMapper.toEntity(specimenDto);
        Specimen updatedSpecimen = specimenRepository.save(specimen);

        return specimenMapper.toDto(updatedSpecimen);
    }

    public void deleteSpecimen(UUID specimenId) throws IllegalStateException {

        if (
            histologyReevaluationRequestRepository.existsBySpecimenId(specimenId) ||
                histologyReportRepository.existsBySpecimenId(specimenId) ||
                molecularPathologyFindingRepository.existsBySpecimenId(specimenId) ||
                ngsReportRepository.existsBySpecimenId(specimenId) ||
                rebiopsyRequestRepository.existsBySpecimenId(specimenId) ||
                ihcReportRepository.existsBySpecimenId(specimenId)

        ) {
            throw new IllegalStateException("The specimen cannot be deleted as it is referenced in other tables.");
        }

        specimenRepository.deleteById(specimenId);

    }

    public List<SpecimenDto> getAllByLabelling(String specimanLabel) {
        List<Specimen> savedSpecimen = specimenRepository.getAllByLabelling(specimanLabel);

        return savedSpecimen.stream().map(specimenMapper::toDto).toList();
    }

    public SpecimenDto createEmptySpecimenWithLabel(UUID episodeId, String specimenLabel) {

        if (!Objects.equals(episodeId.toString(), "")) {
            Specimen emptySpecimen = new Specimen();

            emptySpecimen.setEpisode(episodeService.getEpisodeReference(episodeId));
            emptySpecimen.setLabelling(specimenLabel);

            Specimen savedSpecimen = specimenRepository.save(emptySpecimen);
            return specimenMapper.toDto(savedSpecimen);
        }
        return null;
    }

}
