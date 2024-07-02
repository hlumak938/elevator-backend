package org.sytoss.convertor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.sytoss.connector.BuildingConnector;
import org.sytoss.dto.BuildingDTO;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LiftConverterHelper {

    private final BuildingConnector buildingConnector;

    BuildingDTO findById (Integer id) {
        Optional<BuildingDTO> buildingDTO = buildingConnector.findById(id);
        return buildingDTO.orElse(null);
    }
}
