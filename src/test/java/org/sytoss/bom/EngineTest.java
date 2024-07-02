package org.sytoss.bom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EngineTest {

    @Test
    public void testStartWithRunningUp () {
        Engine engine = new Engine();
        engine.start(EngineStatus.RUNNING_UP);
        assertEquals(EngineStatus.RUNNING_UP, engine.getStatus());
    }

    @Test
    public void testStartWithRunningDown () {
        Engine engine = new Engine();
        engine.start(EngineStatus.RUNNING_DOWN);
        assertEquals(EngineStatus.RUNNING_DOWN, engine.getStatus());
    }

    @Test
    public void testStop () {
        Engine engine = new Engine();
        engine.stop();
        assertEquals(EngineStatus.STOPPED, engine.getStatus());
    }
}