package org.sytoss.bdd.configuration;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import lombok.extern.slf4j.Slf4j;
import org.sytoss.bdd.steps.common.TestContext;

@Slf4j
public class CucumberHooks {
    @Before
    public void init() {
        log.info("Run CucumberHooks Before init");
    }

    @After
    public void tearDown() {
        TestContext.dropInstance();
        log.info("tearDown completed...");
    }
}
