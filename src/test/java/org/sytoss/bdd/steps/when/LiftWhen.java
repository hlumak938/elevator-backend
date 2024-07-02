package org.sytoss.bdd.steps.when;

import io.cucumber.java.Transpose;
import io.cucumber.java.en.When;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.sytoss.bdd.configuration.SpringIntegrationTest;
import org.sytoss.bdd.steps.common.TestContext;
import org.sytoss.bom.Lift;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class LiftWhen extends SpringIntegrationTest {

    @Transactional
    @When("I try to register lift in this building")
    public void shouldRegisterLift (@Transpose Lift lift) {
        ResponseEntity<Lift> response = getRestTemplate().postForEntity(getUrl("/api/buildings/{id}/lifts"), lift, Lift.class, TestContext.getInstance().getBuildingId());
        TestContext.getInstance().setResponse(response);
    }

    @When("I click on the outer button on the {int} floor")
    public void iClickOnTheOuterButtonOnTheFloor (int desirableFloor) {
        ResponseEntity<Void> response = getRestTemplate().exchange(getUrl("/api/buildings/{buildingId}/floors/{floorNumber}"), HttpMethod.PATCH, new HttpEntity<>(Void.class), Void.class, TestContext.getInstance().getBuildingDtoMap().get(1), desirableFloor);
        TestContext.getInstance().setResponse(response);
    }

    @When("I click on the inner button on the {int} floor")
    public void iClickOnTheInnerButtonOnTheDesirableFloorFloor(int desirableFloor) {
        ResponseEntity<Void> response = getRestTemplate().exchange(
                getUrl("/api/buildings/lifts/{liftId}/cabins/{floorNumber}"),
                HttpMethod.PATCH,
                new HttpEntity<>(Void.class),
                Void.class,
                TestContext.getInstance().getLiftId(),
                desirableFloor
        );
        TestContext.getInstance().setResponse(response);
    }

    @When("I click on the inner button that points to the {listOfIntegers} floor")
    public void iClickOnTheInnerButtonThatPointsToTheFloor (List<Integer> list) {
        List<Callable<ResponseEntity<Void>>> threads = new ArrayList<>();
        for (int floorNumber : list) {
            log.info("Sent floor was {}", floorNumber);
            Integer buildingID = TestContext.getInstance().getBuildingDtoMap().get(1);
            Callable<ResponseEntity<Void>> collable = () -> {
                log.info("Floor number is {} and buildingID is {}", floorNumber, buildingID);
                return getRestTemplate().exchange(getUrl("/api/buildings/{buildingId}/floors/{floorNumber}"), HttpMethod.PATCH, new HttpEntity<>(Void.class), Void.class, buildingID, floorNumber);
            };
            threads.add(collable);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(threads.size());

        try {
            List<Future<ResponseEntity<Void>>> futures = new ArrayList<>();
            for (Callable<ResponseEntity<Void>> thread : threads) {
                Thread.sleep(1000);
                futures.add(executorService.submit(thread));
            }

            List<ResponseEntity<Void>> results = new ArrayList<>();
            for (Future<ResponseEntity<Void>> future : futures) {
                results.add(future.get());
            }


            int countOfResponse = 0;
            for (ResponseEntity<Void> result : results) {
                TestContext.getInstance().getResponseDtoMap().put(countOfResponse++, result);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }
}
