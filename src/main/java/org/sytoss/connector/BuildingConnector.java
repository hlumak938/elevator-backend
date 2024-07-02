package org.sytoss.connector;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sytoss.dto.BuildingDTO;

public interface BuildingConnector extends JpaRepository<BuildingDTO, Integer> {

    BuildingDTO findByAddress_CityAndAddress_StreetAndBuildingNumber(String city, String street, int buildingNumber);

    default BuildingDTO findByAddress(String city, String street, int buildingNumber) {
        return findByAddress_CityAndAddress_StreetAndBuildingNumber(city, street, buildingNumber);
    }
}
