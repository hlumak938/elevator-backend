package org.sytoss.bdd.configuration;

import io.cucumber.spring.CucumberContextConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.sytoss.bdd.steps.common.TestContext;
import org.sytoss.connector.AddressConnector;
import org.sytoss.connector.BuildingConnector;
import org.sytoss.connector.LiftConnector;
import org.sytoss.convertor.BuildingConvertor;
import org.sytoss.convertor.LiftConvertor;
import org.sytoss.dto.BuildingDTO;
import org.sytoss.service.LiftService;

@Getter
@Slf4j
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SpringIntegrationTest {

    @Autowired
    private BuildingConnector buildingConnector;

    @Autowired
    private BuildingConvertor buildingConvertor;

    @Autowired
    private AddressConnector addressConnector;

    @Autowired
    private LiftConnector liftConnector;

    @Autowired
    private LiftConvertor liftConvertor;

    @Autowired
    @SpyBean
    private LiftService liftService;

    @LocalServerPort
    private int port;

    private final TestRestTemplate restTemplate = new TestRestTemplate();

    protected String getUrl(String uri) {
        String result = "http://127.0.0.1:" + port + uri;
        log.debug("Final URL is {}", result);
        return result;
    }

    protected BuildingDTO getThisBuilding() {
        int buildingId = TestContext.getInstance().getBuildingId();

        if (buildingId == 0) {
            throw new IllegalArgumentException("Building is not defined");
        }

        return getBuildingConnector().getReferenceById(buildingId);
    }
}
