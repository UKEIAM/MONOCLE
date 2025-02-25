package de.uke.iam.mtb.control.service;

import de.uke.iam.mtb.api.model.LabNumberDto;
import de.uke.iam.mtb.control.exception.ForeignKeyException;
import de.uke.iam.mtb.control.models.LabNumber;
import de.uke.iam.mtb.control.models.mapper.LabNumberMapper;
import de.uke.iam.mtb.control.repository.LabNumberRepository;
import de.uke.iam.mtb.control.service.coredata.SpecimenService;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class LabNumberService {

    private final LabNumberRepository labNumberRepository;
    private final LabNumberMapper labNumberMapper;
    private final SpecimenService specimenService;

    public LabNumberService(LabNumberRepository labNumberRepository, LabNumberMapper labNumberMapper, SpecimenService specimenService) {
        this.labNumberRepository = labNumberRepository;
        this.labNumberMapper = labNumberMapper;
        this.specimenService = specimenService;
    }

    public LabNumberDto addLabNumber(LabNumberDto labNumberDto) throws ForeignKeyException {

        String specimenLabel = labNumberDto.getSpecimenLabelling();

        if (!specimenService.isSpecimenWithLabelExists(specimenLabel)) {
            specimenService.createEmptySpecimenWithLabel(labNumberDto.getEpisodeId(), specimenLabel);
        }

        try {
            String labNumberCapitalized = labNumberDto.getId().toUpperCase().strip();
            LabNumber labNumber = labNumberMapper.toEntity(labNumberDto);
            labNumber.setId(labNumberCapitalized);
            labNumber.setAssigned(false);

            return labNumberMapper.toDto(labNumberRepository.save(labNumber));
        } catch (DataIntegrityViolationException e) {
            throw new ForeignKeyException(
                "Specimen with Label " + labNumberDto.getSpecimenLabelling() + " does not exist");
        }

    }

    public List<LabNumberDto> getAllLabNumbers() {

        return labNumberRepository.findAll().stream().map(labNumberMapper::toDto).toList();

    }

    public List<LabNumberDto> getAllUnassingedLabNumbers() {

        return labNumberRepository.findAllByAssigned(false).stream().map(labNumberMapper::toDto).toList();

    }

    public LabNumberDto getLabNumber(String labNumberId) {

        return labNumberMapper.toDto(labNumberRepository.findById(labNumberId).orElse(null));

    }

    public LabNumberDto updateLabNumber(LabNumberDto updatedLabNumberDto) {

    LabNumber updatedLabNumber = labNumberMapper.toEntity(updatedLabNumberDto);
    return labNumberMapper.toDto(labNumberRepository.save(updatedLabNumber));
  }
  
  public boolean isLabNumberExists(String labNumber) {
    return labNumberRepository.isLabNumberExists(labNumber);
  }
  
  public boolean isLabNumberAlreadyAssigned(String labNumber) {
    return labNumberRepository.isLabNumberAlreadyAssigned(labNumber);
  }
}
