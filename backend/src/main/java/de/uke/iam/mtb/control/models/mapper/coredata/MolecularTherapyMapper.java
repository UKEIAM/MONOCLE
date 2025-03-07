package de.uke.iam.mtb.control.models.mapper.coredata;

import de.uke.iam.mtb.api.model.MolecularTherapyDto;
import de.uke.iam.mtb.control.models.coredata.MolecularTherapy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MolecularTherapyMapper {

  @Mapping(target = "episodeId", source = "episode.id")
  @Mapping(target = "basedOn", source = "therapyRecommendation.id")
  @Mapping(target = "period.start", source = "periodStart")
  @Mapping(target = "period.end", source = "periodEnd")
  MolecularTherapyDto toDto(MolecularTherapy molecularTherapy);

  @Mapping(target = "episode.id", source = "episodeId")
  @Mapping(target = "therapyRecommendation.id", source = "basedOn")
  @Mapping(target = "periodStart", source = "period.start")
  @Mapping(target = "periodEnd", source = "period.end")
  MolecularTherapy toEntity(MolecularTherapyDto molecularTherapyDto);
}
