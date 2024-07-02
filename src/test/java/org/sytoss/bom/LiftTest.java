package org.sytoss.bom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sytoss.event.EventComingToFloor;
import org.sytoss.event.EventMovingToFloor;
import org.sytoss.service.LiftListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LiftTest {

    @Test
    public void testValidLiftSuccess() {
        Cabin cabin = new Cabin();
        cabin.setId("123");
        cabin.setType("Type A");

        Door door = new Door();
        door.setId("456");
        cabin.setDoor(door);

        Engine engine = new Engine();
        engine.setId("789");
        engine.setType("Engine Type");

        Lift lift = new Lift();
        lift.setCabin(cabin);
        lift.setEngine(engine);
        lift.setButtonTemplate(FloorButtonTemplate.UP_DOWN);

        lift.validate();

        assertEquals("123", lift.getCabin().getId());
        assertEquals("Type A", lift.getCabin().getType());
        assertEquals("456", lift.getCabin().getDoor().getId());
        assertEquals("789", lift.getEngine().getId());
        assertEquals("Engine Type", lift.getEngine().getType());
        assertEquals(FloorButtonTemplate.UP_DOWN, lift.getButtonTemplate());
    }

    @Test
    public void testLiftValidationFailed() {
        Lift lift = new Lift();
        Assertions.assertThrows(IllegalArgumentException.class, lift::validate);
    }

    @Test
    public void testAddRemoveLiftListener() {
        LiftListener listener = mock(LiftListener.class);
        Lift lift = new Lift();
        lift.addLiftListener(listener);
        assertTrue(lift.getLiftListenerList().contains(listener));
        lift.removeLiftListener(listener);
        assertFalse(lift.getLiftListenerList().contains(listener));
    }

    @Test
    public void testNotifyComingToFloor() {
        LiftListener listener = mock(LiftListener.class);
        Lift lift = new Lift();
        lift.addLiftListener(listener);
        lift.setCabinPosition(5);
        verify(listener).handlingComingToFloor(any(EventComingToFloor.class));
    }

    @Test
    public void testNotifyMovingToNextFloor() {
        LiftListener listener = mock(LiftListener.class);
        Lift lift = new Lift();
        lift.addLiftListener(listener);
        lift.moveToNextFloor();
        verify(listener).handlingMovingToNextFloor(any(EventMovingToFloor.class));
    }

    @Test
    public void testCallCabin() {
        LiftListener listener = mock(LiftListener.class);
        Lift lift = new Lift();
        lift.addLiftListener(listener);
        lift.callCabin();
        verify(listener).handlingComingToFloor(any(EventComingToFloor.class));
    }

    @Test
    public void testAddFloorNumberToQueue(){
        Lift lift = new Lift();
        lift.addFloorNumberToQueue(5);
        lift.addFloorNumberToQueue(3);

        assertEquals(2, lift.getFloorNumbers().size(), "Queue should contain 2 floor numbers");
        assertTrue(lift.getFloorNumbers().contains(5), "Queue should contain floor number 5");
        assertTrue(lift.getFloorNumbers().contains(3), "Queue should contain floor number 3");
    }

    @Test
    public void testRemoveFloorNumberFromQueue(){
        Lift lift = new Lift();
        lift.addFloorNumberToQueue(5);
        lift.addFloorNumberToQueue(3);
        lift.removeFloorNumberFromQueue(5);

        assertEquals(1, lift.getFloorNumbers().size(), "Queue should contain 1 floor number");
        assertFalse(lift.getFloorNumbers().contains(5), "Queue should not contain floor number 5");
        assertTrue(lift.getFloorNumbers().contains(3), "Queue should contain floor number 3");
    }
}
