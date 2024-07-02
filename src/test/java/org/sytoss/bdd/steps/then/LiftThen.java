package org.sytoss.bdd.steps.then;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.sytoss.bdd.configuration.SpringIntegrationTest;
import org.sytoss.bdd.steps.common.TestContext;
import org.sytoss.bom.EngineStatus;
import org.sytoss.bom.Lift;
import org.sytoss.bom.LiftStatus;
import org.sytoss.dto.BuildingDTO;
import org.sytoss.dto.LiftDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@Transactional
public class LiftThen extends SpringIntegrationTest {

    @Then("this building contains {int} lift with {string} button templates and {string} engine and {string} cabin")
    public void shouldCheckThatLiftExist (int countLifts, String buttonTemplate, String engineSerialNumber,
            String cabinSerialNumber) {
        BuildingDTO building = getThisBuilding();
        List<LiftDTO> lifts = getLiftConnector().findLiftsByBuildingId(building.getId());
        List<LiftDTO> targetLifts = new ArrayList<>();

        for (LiftDTO lift : lifts) {
            if (lift.getEngine().getId().equals(engineSerialNumber) && lift.getCabin().getId().equals(cabinSerialNumber) && lift.getButtonTemplate().equals(buttonTemplate)) {
                targetLifts.add(lift);
            }
        }
        if (targetLifts.isEmpty()) {
            fail("Lift not found");
        }
        assertEquals(countLifts, targetLifts.size());
    }

    @Then("operation should fail with status code {int}")
    public void operationFailedWithStatusCode (int statusCode) {
        assertEquals(statusCode, TestContext.getInstance().getResponse().getStatusCode().value());
    }

    @Then("the cabin moved to {int}")
    public void theCabinMovedTo (int desirableFloor) {
        int liftId = TestContext.getInstance().getLiftId();
        Lift lift;
        Optional<LiftDTO> liftDTO = getLiftConnector().findById(liftId);
        if (liftDTO.isPresent()) {
            lift = getLiftConvertor().fromDTO(liftDTO.get());
            assertEquals(desirableFloor, lift.getCabinPosition());
        }
    }

    @Then("lift was {string}")
    public void lift(String liftStatus) {
        int liftId = TestContext.getInstance().getLiftId();
        Optional<LiftDTO> liftDTO = getLiftConnector().findById(liftId);
        if (liftDTO.isPresent()) {
            verify(getLiftService(), times(1)).setStatus(argThat((lift) -> lift.getId() == liftId), eq(LiftStatus.valueOf(liftStatus)));
        }
    }

    @And("cabin should be moved to the {listOfIntegers} floor")
    public void cabinShouldBeMovedToTheFloor (List<Integer> listOfFloorNumbers) {
        int liftId = TestContext.getInstance().getLiftId();
        Optional<LiftDTO> liftDTO = getLiftConnector().findById(liftId);
        if (liftDTO.isPresent()) {
            for (int floorNumber : listOfFloorNumbers) {
                verify(getLiftService(), atLeast(1)).setCabinPosition(argThat((lift) -> lift.getId() == liftId), eq(floorNumber));
            }
        }
    }

    @Then("the cabin stays at {int} floor")
    public void theCabinStaysAtCabinPositionFloor(int cabinPosition) {
        int liftId = TestContext.getInstance().getLiftId();
        Optional<LiftDTO> liftDTO = getLiftConnector().findById(liftId);
        if (liftDTO.isPresent()) {
            Lift lift = getLiftConvertor().fromDTO(liftDTO.get());
            assertEquals(cabinPosition, lift.getCabinPosition());
        }
    }

    @Then("the cabin with {int} id should be moved to the {int} floor")
    public void theCabinWithCabinIdIdShouldBeMovedToTheCurrentFloorFloor(int liftId, int floorNumber) {
        Lift lift;
        Optional<LiftDTO> liftDTO = getLiftConnector().findById(TestContext.getInstance().getLiftDtoMap().get(liftId));
        if (liftDTO.isPresent()) {
            lift = getLiftConvertor().fromDTO(liftDTO.get());
            assertEquals(floorNumber, lift.getCabinPosition());
        }
    }
}
