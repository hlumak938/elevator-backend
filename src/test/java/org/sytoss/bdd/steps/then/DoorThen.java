package org.sytoss.bdd.steps.then;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.sytoss.bdd.configuration.SpringIntegrationTest;
import org.sytoss.bdd.steps.common.TestContext;
import org.sytoss.bom.DoorStatus;
import org.sytoss.bom.Lift;
import org.sytoss.dto.LiftDTO;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DoorThen extends SpringIntegrationTest {

    @Then("the door should be {string} {int} times")
    public void theDoor (String doorStatus, int times) {
        int liftId = TestContext.getInstance().getLiftId();
        Optional<LiftDTO> liftDTO = getLiftConnector().findById(liftId);
        if (liftDTO.isPresent()) {
            Lift lift = getLiftConvertor().fromDTO(liftDTO.get());
            verify(getLiftService(), times(times)).openDoor(any(Lift.class));
            assertEquals(DoorStatus.valueOf(doorStatus), lift.getCabin().getDoor().getDoorStatus());
        }
    }

    @And("door was {string} {int} times")
    public void doorWas (String doorStatus, int closedTimes) {
        int liftId = TestContext.getInstance().getLiftId();
        Optional<LiftDTO> liftDTO = getLiftConnector().findById(liftId);
        if (liftDTO.isPresent()) {
            if (DoorStatus.valueOf(doorStatus) == DoorStatus.OPEN) {
                verify(getLiftService(), times(closedTimes)).openDoor(argThat((lift) -> lift.getId() == liftId));
            } else if (DoorStatus.valueOf(doorStatus) == DoorStatus.CLOSED) {
                verify(getLiftService(), times(closedTimes)).closeDoor(argThat((lift) -> lift.getId() == liftId));
            }
        }
    }
    @And("the door should be {string}")
    public void theDoor(String doorStatus) {
        int liftId = TestContext.getInstance().getLiftId();
        Optional<LiftDTO> liftDTO = getLiftConnector().findById(liftId);
        if (liftDTO.isPresent()) {
            Lift lift = getLiftConvertor().fromDTO(liftDTO.get());
            verify(getLiftService(), times(1)).openDoor(any(Lift.class));
            assertEquals(DoorStatus.valueOf(doorStatus), lift.getCabin().getDoor().getDoorStatus());
        }
    }
}
