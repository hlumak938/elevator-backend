package org.sytoss.bdd.steps.then;

import io.cucumber.java.en.Then;
import org.springframework.http.ResponseEntity;
import org.sytoss.bdd.configuration.SpringIntegrationTest;
import org.sytoss.bdd.steps.common.TestContext;
import org.sytoss.bom.Address;
import org.sytoss.bom.Building;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildingThen extends SpringIntegrationTest {

    @Then("operation should be successful")
    public void operationIsSuccessful() {
        assertEquals(200, TestContext.getInstance().getResponse().getStatusCode().value());
    }

    @Then("has {int} floors")
    public void hasFloors(int floorCount) {
        Building building = (Building) TestContext.getInstance().getResponse().getBody();
        assertEquals(floorCount, Objects.requireNonNull(building).getFloorCount());
    }

    @Then("operation should fail with {int} error")
    public void operationFailedWithError(int statusCode) {
        assertEquals(statusCode, TestContext.getInstance().getResponse().getStatusCode().value());
    }

    @Then("I should get building")
    public void iShouldGetBuilding(List<Building> expectedBuildings) {
        Building actualBuildings = (Building) Objects.requireNonNull(TestContext.getInstance().getResponse().getBody());
        Integer expectedBuildingID = TestContext.getInstance().getBuildingDtoMap().get(expectedBuildings.getFirst().getId());
        assertEquals(expectedBuildingID, actualBuildings.getId());
        assertEquals(expectedBuildings.getFirst().getFloorCount(), actualBuildings.getFloorCount());

        Address actualAddress = actualBuildings.getAddress();
        Address expectedAddress = expectedBuildings.getFirst().getAddress();
        assertEquals(expectedAddress.getCity(), actualAddress.getCity());
        assertEquals(expectedAddress.getStreet(), actualAddress.getStreet());
        assertEquals(expectedAddress.getBuildingNumber(), actualAddress.getBuildingNumber());
    }

    @Then("operations should be successful")
    public void operationsShouldBeSuccessful() {
        for (ResponseEntity response : TestContext.getInstance().getResponseDtoMap().values()) {
            assertEquals(200, response.getStatusCode().value());
        }
    }

    @Then("I should get buildings")
    public void iShouldGetBuildings(List<Building> expectedBuildings) {
        List<Building> actualBuildings = (List<Building>) Objects.requireNonNull(TestContext.getInstance().getResponse().getBody());

        expectedBuildings.sort(Comparator.comparing(Building::getId));
        actualBuildings.sort(Comparator.comparing(Building::getId));

        for(int i = 0; i < expectedBuildings.size(); i++) {
            Integer expectedBuildingID = TestContext.getInstance().getBuildingDtoMap().get(expectedBuildings.get(i).getId());
            assertEquals(expectedBuildingID, actualBuildings.get(i).getId());
            assertEquals(expectedBuildings.get(i).getFloorCount(), actualBuildings.get(i).getFloorCount());

            Address actualAddress = actualBuildings.get(i).getAddress();
            Address expectedAddress = expectedBuildings.get(i).getAddress();
            assertEquals(expectedAddress.getCity(), actualAddress.getCity());
            assertEquals(expectedAddress.getStreet(), actualAddress.getStreet());
            assertEquals(expectedAddress.getBuildingNumber(), actualAddress.getBuildingNumber());
        }
    }
}
