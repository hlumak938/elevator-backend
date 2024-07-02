package org.sytoss.convertor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.sytoss.bom.Building;
import org.sytoss.dto.BuildingDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {LiftConvertor.class})
public interface BuildingConvertor {

    @Mapping(target = "address.id", ignore = true)
    @Mapping(source = "address.buildingNumber", target = "buildingNumber")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "lifts", target = "lifts")
    BuildingDTO toDTO(Building building);

    @Mapping(target = "floors", ignore = true)
    @Mapping(source = "buildingNumber", target = "address.buildingNumber")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "lifts", target = "lifts")
    Building fromDTO(BuildingDTO dto);
}

