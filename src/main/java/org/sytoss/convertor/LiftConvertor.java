package org.sytoss.convertor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.sytoss.bom.Lift;
import org.sytoss.dto.LiftDTO;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {LiftConverterHelper.class})
public interface LiftConvertor {

    @Mapping(source = "lift.cabin.serialNumber", target = "cabin.id")
    @Mapping(source = "lift.engine.serialNumber", target = "engine.id")
    @Mapping(source = "lift.cabin.door.doorStatus", target = "cabin.doorStatus")
    @Mapping(source = "lift.engine.status", target = "engine.status")
    @Mapping(source = "buildingId", target = "building")
    LiftDTO toDto (Lift lift, Integer buildingId);

    @Mapping(source = "cabin.id", target = "cabin.id")
    @Mapping(source = "engine.id", target = "engine.id")
    @Mapping(source = "cabin.doorStatus", target = "cabin.door.doorStatus")
    @Mapping(source = "building.id", target = "buildingId")
    Lift fromDTO (LiftDTO liftDTO);

    default String map(Queue<Integer> value) {
        return value.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    default Queue<Integer> map(String value) {
        Queue<Integer> queue = new LinkedList<>();
        if (!value.isEmpty()) {
            String[] split = value.split(",");
            for (String s : split) {
                queue.add(Integer.parseInt(s));
            }
        }
        return queue;
    }
}
