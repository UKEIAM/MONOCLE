package de.uke.iam.mtb.control.models.mapper;

import de.uke.iam.mtb.api.model.TransferJobDto;
import de.uke.iam.mtb.control.models.TransferJob;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferJobMapper {

    @Mapping(target = "patientId", source = "patientId")
    @Mapping(target = "episodeId", source = "episodeId")
    TransferJobDto toDto(TransferJob transferJob);

    @Mapping(target = "patientId", source = "patientId")
    @Mapping(target = "episodeId", source = "episodeId")
    TransferJob toEntity(TransferJobDto transferJobDto);

    UUID map(String value);
    String map(UUID value);
}