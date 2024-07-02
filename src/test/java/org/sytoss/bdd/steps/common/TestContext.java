package org.sytoss.bdd.steps.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class TestContext {

    private static final ThreadLocal<TestContext> testContext = new ThreadLocal<>();

    private Map<Integer, Integer> buildingDtoMap = new HashMap<>();

    private Map<Integer, Integer> liftDtoMap = new HashMap<>();

    private Map<Integer, ResponseEntity> responseDtoMap = new HashMap<>();

    private ResponseEntity response;

    private int buildingId;

    private int liftId;

    public static TestContext getInstance () {
        if (testContext.get() == null) {
            testContext.set(new TestContext());
        }
        return testContext.get();
    }

    public static void dropInstance () {
        testContext.set(null);
    }
}