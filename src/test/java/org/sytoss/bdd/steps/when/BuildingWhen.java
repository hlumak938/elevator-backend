package org.sytoss.bdd.steps.when;

import io.cucumber.java.Transpose;
import io.cucumber.java.en.When;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.sytoss.bdd.configuration.SpringIntegrationTest;
import org.sytoss.bdd.steps.common.TestContext;
import org.sytoss.bom.Building;

import java.util.List;

public class BuildingWhen extends SpringIntegrationTest {

    @When("register building request coming")
    public void registerBuildingRequestComing(@Transpose Building building) {
        ResponseEntity<Building> response = getRestTemplate().postForEntity(getUrl("/api/buildings"),
                building, Building.class);
        TestContext.getInstance().setResponse(response);
    }

    @When("I try to get building by id {string}")
    public void iTryToGetBuildingById(String id) {
        int buildingId = TestContext.getInstance().getBuildingDtoMap().get(Integer.parseInt(id.replaceAll("\\*", "")));
        ResponseEntity<Building> response = getRestTemplate().getForEntity(getUrl("/api/buildings/{id}"), Building.class, buildingId);
        TestContext.getInstance().setResponse(response);
    }

    @When("I try to get building by id {int}")
    public void iTryToGetBuildingById(int buildingId) {
        ResponseEntity<Building> response = getRestTemplate().getForEntity(getUrl("/api/buildings/{id}"), Building.class, buildingId);
        TestContext.getInstance().setResponse(response);
    }

    @When("I try to get all buildings")
    public void iTryToGetAllBuildings() {
        ParameterizedTypeReference<List<Building>> typeRef = new ParameterizedTypeReference<>() {};
        ResponseEntity<List<Building>> response = getRestTemplate().exchange(
                getUrl("/api/buildings"),
                HttpMethod.GET,
                null,
                typeRef
        );
        TestContext.getInstance().setResponse(response);
    }
}
