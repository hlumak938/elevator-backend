package org.sytoss.bdd.steps.given;

import io.cucumber.java.en.Given;
import jakarta.transaction.Transactional;
import org.sytoss.bdd.configuration.SpringIntegrationTest;
import org.sytoss.bdd.steps.common.TestContext;
import org.sytoss.bom.Building;
import org.sytoss.bom.FloorButtonTemplate;
import org.sytoss.bom.Lift;
import org.sytoss.dto.*;

import java.util.List;
import java.util.UUID;

import static org.sytoss.bdd.steps.LiftBddStep.*;

@Transactional
public class BuildingGiven extends SpringIntegrationTest {

    @Given("building with address {string} exists")
    public void buildingWithAddressExists (String address) {
        String[] addressValues = address.split(",");

        String city = addressValues[CITY_INDEX].trim();
        String street = addressValues[STREET_INDEX].trim();
        int buildingNumber = Integer.parseInt(addressValues[BUILDING_NUMBER_INDEX].trim());

        BuildingDTO buildingDTO = getBuildingConnector().findByAddress(city, street, buildingNumber);
        if (buildingDTO == null) {
            BuildingDTO newBuildingDTO = new BuildingDTO();
            newBuildingDTO.setBuildingNumber(buildingNumber);
            newBuildingDTO.setFloorCount(9);

            AddressDTO addressDTO = getAddressConnector().findByCityAndStreet(city, street);
            if (addressDTO == null) {
                addressDTO = new AddressDTO();
                addressDTO.setCity(city);
                addressDTO.setStreet(street);
            }
            newBuildingDTO.setAddress(addressDTO);

            getBuildingConnector().save(newBuildingDTO);
            buildingDTO = newBuildingDTO;
        }
        TestContext.getInstance().setBuildingId(buildingDTO.getId());
    }

    @Given("building with address {string} doesnt exist")
    public void buildingWithAddressDoesntExist (String address) {
        String[] addressValues = address.split(",");

        BuildingDTO buildingDTO = getBuildingConnector().findByAddress(addressValues[CITY_INDEX].trim(), addressValues[STREET_INDEX].trim(), Integer.parseInt(addressValues[BUILDING_NUMBER_INDEX].trim()));
        if (buildingDTO != null) {
            getBuildingConnector().delete(buildingDTO);
        }
    }

    @Given("building with address {string} with {int} floors exists")
    public void buildingWithAddressExistsInSystem (String address, int floorsCount) {
        String[] addressValues = address.split(",");
        String city = addressValues[CITY_INDEX].trim();
        String street = addressValues[STREET_INDEX].trim();

        int buildingNumber = Integer.parseInt(addressValues[BUILDING_NUMBER_INDEX].trim());
        BuildingDTO buildingDTO = getBuildingConnector().findByAddress(city, street, buildingNumber);

        if (buildingDTO == null) {
            BuildingDTO newBuildingDTO = new BuildingDTO();

            newBuildingDTO.setBuildingNumber(buildingNumber);
            newBuildingDTO.setFloorCount(floorsCount);

            AddressDTO addressDTO = getAddressConnector().findByCityAndStreet(city, street);

            if (addressDTO == null) {
                addressDTO = new AddressDTO();
                addressDTO.setCity(city);
                addressDTO.setStreet(street);
            }
            newBuildingDTO.setAddress(addressDTO);
            buildingDTO = getBuildingConnector().save(newBuildingDTO);
        } else if (buildingDTO.getFloorCount() != floorsCount) {
            buildingDTO.setFloorCount(floorsCount);
            buildingDTO = getBuildingConnector().save(buildingDTO);
        }
        TestContext.getInstance().setBuildingId(buildingDTO.getId());
    }

    @Given("this building has lifts")
    public void thisBuildingHasLifts () {
        LiftDTO liftDTO = new LiftDTO();

        liftDTO.setCabinPosition(2);
        liftDTO.setCabin(new CabinDTO());

        liftDTO.getCabin().setId(UUID.randomUUID().toString());
        liftDTO.getCabin().setType("small");
        liftDTO.getCabin().setDoorStatus("CLOSED");
        liftDTO.setEngine(new EngineDTO());
        liftDTO.getEngine().setId(UUID.randomUUID().toString());
        liftDTO.getEngine().setType("powerful");
        liftDTO.setFloorNumbers("5");
        liftDTO.setButtonTemplate(FloorButtonTemplate.UP_DOWN.name());

        BuildingDTO buildingDTO = getThisBuilding();
        liftDTO.setBuilding(buildingDTO);

        buildingDTO.getLifts().add(liftDTO);
        getBuildingConnector().save(buildingDTO);
    }

    @Given("buildings exist")
    public void buildingsExist (List<Building> buildings) {
        for (Building building : buildings) {
            String city = building.getAddress().getCity();
            String street = building.getAddress().getStreet();
            int buildingNumber = building.getAddress().getBuildingNumber();
            BuildingDTO buildingDTO = getBuildingConnector().findByAddress(city, street, buildingNumber);
            if (buildingDTO == null) {
                BuildingDTO dto = getBuildingConvertor().toDTO(building);
                dto.setId(0);

                AddressDTO addressDTO = getAddressConnector().findByCityAndStreet(city, street);
                if (addressDTO == null) {
                    addressDTO = new AddressDTO();
                    addressDTO.setCity(city);
                    addressDTO.setStreet(street);
                }
                dto.setAddress(addressDTO);

                getBuildingConnector().save(dto);
                buildingDTO = dto;
            }
            TestContext.getInstance().getBuildingDtoMap().put(building.getId(), buildingDTO.getId());
        }
    }

    @Given("the building has following lifts")
    public void theBuildingHasFollowingLifts (List<Lift> lifts) {
        for (Lift lift : lifts) {
            List<LiftDTO> liftDTOs = getLiftConnector().findAllByCabinId(lift.getCabin().getSerialNumber());
            LiftDTO dto = getLiftConvertor().toDto(lift, TestContext.getInstance().getBuildingDtoMap().get(1));
            dto.setId(liftDTOs.isEmpty() ? 0 : liftDTOs.getFirst().getId());

            LiftDTO liftDTO = getLiftConnector().save(dto);
            TestContext.getInstance().getLiftDtoMap().put(lift.getId(), liftDTO.getId());
        }
    }

    @Given("buildings table is clear")
    public void buildingsTableIsClear () {
        getBuildingConnector().deleteAll();
    }
}