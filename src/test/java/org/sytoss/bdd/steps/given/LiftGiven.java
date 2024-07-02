package org.sytoss.bdd.steps.given;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.sytoss.bdd.configuration.SpringIntegrationTest;
import org.sytoss.bdd.steps.common.TestContext;
import org.sytoss.bom.*;
import org.sytoss.dto.BuildingDTO;
import org.sytoss.dto.LiftDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class LiftGiven extends SpringIntegrationTest {

    @Given("this building has no lifts")
    public void thisBuildingHasNoLifts () {
        BuildingDTO buildingDTO = getThisBuilding();

        List<LiftDTO> liftDTOs = getLiftConnector().findLiftsByBuildingId(buildingDTO.getId());

        if (liftDTOs.isEmpty()) {
            log.info("no lifts found for building id {}", buildingDTO.getId());
        } else {
            log.info("there are {} lifts, must be removed", liftDTOs.size());
            getLiftConnector().deleteAll(liftDTOs);
        }
    }

    @Transactional
    @Given("the building has lift with {string} engine, cabin with {string} doors")
    public void theBuildingHasLiftWithEngineCabinWithDoors (String engineStatus, String doorStatus) {
        int buildingId = TestContext.getInstance().getBuildingDtoMap().get(1);
        Building building = getBuildingConvertor().fromDTO(getBuildingConnector().findById(buildingId).get());

        if (building.getLifts().isEmpty()) {
            if (getBuildingConnector().findById(buildingId).isEmpty()) {
                fail("Building with id " + buildingId + " not found");
            }

            Lift lift = new Lift();
            lift.setButtonTemplate(FloorButtonTemplate.UP_DOWN);
            lift.setCabin(new Cabin());
            lift.getCabin().setId(UUID.randomUUID().toString());
            lift.getCabin().setType("small");
            lift.getCabin().setDoor(new Door());
            lift.getCabin().getDoor().setDoorStatus(DoorStatus.valueOf(doorStatus.toUpperCase()));
            lift.setEngine(new Engine());
            lift.getEngine().setStatus(EngineStatus.valueOf(engineStatus.toUpperCase()));
            lift.getEngine().setId(UUID.randomUUID().toString());
            lift.getEngine().setType("powerful");

            LiftDTO liftDTO = getLiftConvertor().toDto(lift, buildingId);

            log.info("Old id {}", liftDTO.getId());
            getLiftConnector().save(liftDTO);
            log.info("New id {}", liftDTO.getId());
            TestContext.getInstance().setLiftId(liftDTO.getId());
        } else {
            TestContext.getInstance().setLiftId(building.getLifts().getFirst().getId());
        }
    }

    @Given("Cabin is on the {int} floor")
    public void cabinIsOnTheFloor (int floorNumber) {
        int liftId = TestContext.getInstance().getLiftId();
        Optional<LiftDTO> liftDTOOptional = getLiftConnector().findById(liftId);

        if (liftDTOOptional.isEmpty()) {
            fail("Lift with id " + liftId + " not found");
        }

        LiftDTO liftDTO = liftDTOOptional.get();
        liftDTO.setCabinPosition(floorNumber);
        getLiftConnector().save(liftDTO);
    }

    @Given("the building has following lift")
    public void theBuildingHasFollowingLift (Lift lift) {
        int liftId = lift.getId();
        Optional<LiftDTO> liftDTOOptional = getLiftConnector().findById(liftId);

        if (liftDTOOptional.isEmpty()) {
            LiftDTO liftDTO = getLiftConvertor().toDto(lift, TestContext.getInstance().getBuildingDtoMap().get(lift.getBuildingId()));
            getLiftConnector().save(liftDTO);
            liftId = liftDTO.getId();
        }

        TestContext.getInstance().setLiftId(liftId);
    }

    @Given("Door is {string}")
    public void doorIsClosed(String doorStatus) {
        int liftId = TestContext.getInstance().getLiftId();
        Optional<LiftDTO> liftDTOOptional = getLiftConnector().findById(liftId);

        if (liftDTOOptional.isEmpty()) {
            fail("Lift with id " + liftId + " not found");
        }

        LiftDTO liftDTO = liftDTOOptional.get();
        liftDTO.getCabin().setDoorStatus(doorStatus);
        getLiftConnector().save(liftDTO);
    }
}
