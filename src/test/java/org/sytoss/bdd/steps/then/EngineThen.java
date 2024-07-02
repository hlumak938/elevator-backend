package org.sytoss.bdd.steps.then;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.sytoss.bdd.configuration.SpringIntegrationTest;
import org.sytoss.bdd.steps.common.TestContext;
import org.sytoss.bom.EngineStatus;
import org.sytoss.bom.Lift;
import org.sytoss.dto.LiftDTO;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class EngineThen extends SpringIntegrationTest {

    @Then("the engine of the free lift was {string}")
    public void theEngineOfTheFreeLiftWas (String engineDirection) {
        verify(getLiftService(), times(1)).startEngine(any(Lift.class), eq(EngineStatus.valueOf(engineDirection)));
    }

    @Then("engine should be {string} {int} times")
    public void engine (String engineDirection, int times) {
        int liftId = TestContext.getInstance().getLiftId();

        Lift lift;
        Optional<LiftDTO> liftDTO = getLiftConnector().findById(liftId);
        if (liftDTO.isPresent()) {
            lift = getLiftConvertor().fromDTO(liftDTO.get());
            verify(getLiftService(), times(times)).stopEngine(any(Lift.class));
            assertEquals(EngineStatus.valueOf(engineDirection), lift.getEngine().getStatus());
        }
    }

    @And("engine was {string} or {string} {int} times")
    public void engineWasOr (String runningUp, String runningDown, int invocationTimes) {
        int liftId = TestContext.getInstance().getLiftId();
        Optional<LiftDTO> liftDTO = getLiftConnector().findById(liftId);
        if (liftDTO.isPresent()) {
            verify(getLiftService(), times(invocationTimes)).startEngine(any(Lift.class), argThat((status) -> status.equals(EngineStatus.valueOf(runningUp)) || status.equals(EngineStatus.valueOf(runningDown))));
        }

    }
    @And("engine should be {string}")
    public void engine (String engineDirection) {
        int liftId = TestContext.getInstance().getLiftId();

        Lift lift;
        Optional<LiftDTO> liftDTO = getLiftConnector().findById(liftId);
        if (liftDTO.isPresent()) {
            lift = getLiftConvertor().fromDTO(liftDTO.get());
            verify(getLiftService(), times(1)).stopEngine(any(Lift.class));
            assertEquals(EngineStatus.valueOf(engineDirection), lift.getEngine().getStatus());
        }
    }
}
