package org.sytoss.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.sytoss.bom.*;
import org.sytoss.connector.LiftConnector;
import org.sytoss.convertor.LiftConvertor;
import org.sytoss.event.EventComingToFloor;
import org.sytoss.event.EventMovingToFloor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LiftServiceTest {

    @Mock
    private LiftConnector liftConnector;

    @Mock
    private LiftConvertor liftConvertor;

    @Mock
    private FloorService floorService;

    @InjectMocks
    private LiftService liftService;

    @Test
    public void shouldDeliverCabinToCorrespondFloorSuccess () {
        Lift mockLift = mock(Lift.class);
        when(mockLift.getFloorNumbers()).thenReturn(new LinkedList<>(List.of(3)));
        int floorNumber = 5;

        assertDoesNotThrow(() -> liftService.deliverCabinToCorrespondFloor(mockLift, floorNumber));

        verify(mockLift).addFloorNumberToQueue(floorNumber);
        verify(mockLift).addLiftListener(any(LiftListener.class));
        verify(mockLift).callCabin();
        verify(mockLift).removeLiftListener(any(LiftListener.class));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenPassNullLift () {
        int floorNumber = 5;

        assertThrows(NullPointerException.class, () -> liftService.deliverCabinToCorrespondFloor(null, floorNumber));
    }

    @Test
    public void shouldOpenDoorSuccess () {
        Lift mockLift = mock(Lift.class);
        Cabin mockCabin = mock(Cabin.class);
        Door mockDoor = mock(Door.class);
        when(mockLift.getCabin()).thenReturn(mockCabin);
        when(mockCabin.getDoor()).thenReturn(mockDoor);

        assertDoesNotThrow(() -> liftService.openDoor(mockLift));

        verify(mockDoor).open();
    }

    @Test
    public void shouldCloseDoorSuccess () {
        Lift mockLift = mock(Lift.class);
        Cabin mockCabin = mock(Cabin.class);
        Door mockDoor = mock(Door.class);
        when(mockLift.getCabin()).thenReturn(mockCabin);
        when(mockCabin.getDoor()).thenReturn(mockDoor);

        assertDoesNotThrow(() -> liftService.closeDoor(mockLift));

        verify(mockDoor).close();
    }

    @Test
    public void shouldStartEngineSuccess () {
        Lift mockLift = mock(Lift.class);
        Engine mockEngine = mock(Engine.class);
        EngineStatus engineStatus = EngineStatus.RUNNING_UP;
        when(mockLift.getEngine()).thenReturn(mockEngine);

        assertDoesNotThrow(() -> liftService.startEngine(mockLift, engineStatus));

        verify(mockEngine).start(engineStatus);
    }

    @Test
    public void shouldStopEngineSuccess () {
        Lift mockLift = mock(Lift.class);
        Engine mockEngine = mock(Engine.class);
        when(mockLift.getEngine()).thenReturn(mockEngine);

        assertDoesNotThrow(() -> liftService.stopEngine(mockLift));

        verify(mockEngine).stop();
    }

    @Test
    public void shouldHandlingComingToFloorSuccess () {
        Lift mockLift = mock(Lift.class);
        Engine mockEngine = mock(Engine.class);
        Cabin mockCabin = mock(Cabin.class);
        Door mockDoor = mock(Door.class);
        when(mockLift.getFloorNumbers()).thenReturn(new LinkedList<>(List.of(3)));

        when(mockLift.getCabin()).thenReturn(mockCabin);
        when(mockLift.getEngine()).thenReturn(mockEngine);
        when(mockCabin.getDoor()).thenReturn(mockDoor);
        when(mockLift.getCabinPosition()).thenReturn(5);
        when(mockLift.getFloorNumbers()).thenReturn(new LinkedList<>(List.of(5)));
        when(mockEngine.getStatus()).thenReturn(EngineStatus.RUNNING_UP);

        assertDoesNotThrow(() -> liftService.handlingComingToFloor(new EventComingToFloor(mockLift)));

        verify(mockEngine).stop();
        verify(mockDoor).open();
        verify(liftConnector,times(2)).save(any());
    }

    @Test
    public void shouldHandlingComingToFloorUnsuccessful () {
        Lift mockLift = mock(Lift.class);
        Engine mockEngine = mock(Engine.class);
        when(mockLift.getEngine()).thenReturn(mockEngine);
        when(mockLift.getCabinPosition()).thenReturn(5);
        when(mockLift.getFloorNumbers()).thenReturn(new LinkedList<>(List.of(3)));
        when(mockEngine.getStatus()).thenReturn(EngineStatus.RUNNING_UP);
        when(mockLift.getFloorNumbers()).thenReturn(new LinkedList<>(List.of()));
        assertDoesNotThrow(() -> liftService.handlingComingToFloor(new EventComingToFloor(mockLift)));
        verify(mockEngine, never()).stop();
    }

    @Test
    public void shouldHandlingMovingToNextFloorSuccess () {
        Lift mockLift = mock(Lift.class);
        Cabin mockCabin = mock(Cabin.class);
        Door mockDoor = mock(Door.class);
        Engine mockEngine = mock(Engine.class);
        when(mockLift.getCabin()).thenReturn(mockCabin);
        when(mockLift.getEngine()).thenReturn(mockEngine);
        when(mockCabin.getDoor()).thenReturn(mockDoor);
        when(mockEngine.getStatus()).thenReturn(EngineStatus.STOPPED);
        when(mockCabin.getDoor().getDoorStatus()).thenReturn(DoorStatus.OPEN);
        when(mockLift.getFloorNumbers()).thenReturn(new LinkedList<>(List.of(3)));

        assertDoesNotThrow(() -> liftService.handlingMovingToNextFloor(new EventMovingToFloor(mockLift)));

        verify(mockDoor).close();
        verify(mockEngine).start(any(EngineStatus.class));
        verify(mockLift).setCabinPosition(anyInt());
    }
    @Test
    public void shouldDeliverCabinToCorrespondFloorSuccessWhenLiftIsFree() {
        Lift mockLift = mock(Lift.class);
        Queue<Integer> mockQueue = new LinkedList<>();
        int floorNumber = 5;

        when(mockLift.getStatus()).thenReturn(LiftStatus.FREE);
        when(floorService.getCurrentFloorsNative(mockLift)).thenReturn(mockQueue);
        when(mockLift.getFloorNumbers()).thenReturn(new LinkedList<>(List.of(3)));

        assertDoesNotThrow(() -> liftService.deliverCabinToCorrespondFloor(mockLift, floorNumber));

        verify(mockLift).setStatus(LiftStatus.BUSY);
        verify(liftConnector,times(2)).save(any());
        verify(mockLift).setFloorNumbers(mockQueue);
        verify(mockLift).addFloorNumberToQueue(floorNumber);
        verify(mockLift).addLiftListener(any(LiftListener.class));
        verify(mockLift).callCabin();
        verify(mockLift).removeLiftListener(any(LiftListener.class));
    }

    @Test
    public void shouldDeliverCabinToCorrespondFloorSuccessWhenLiftIsBusy() {
        Lift mockLift = mock(Lift.class);
        Queue<Integer> mockQueue = new LinkedList<>();
        int floorNumber = 5;

        when(mockLift.getStatus()).thenReturn(LiftStatus.BUSY);
        when(floorService.getCurrentFloorsNative(mockLift)).thenReturn(mockQueue);
        when(mockLift.getFloorNumbers()).thenReturn(new LinkedList<>(List.of(3)));

        assertDoesNotThrow(() -> liftService.deliverCabinToCorrespondFloor(mockLift, floorNumber));

        verify(mockLift, never()).setStatus(LiftStatus.BUSY);
        verify(liftConnector, never()).save(any());
        verify(mockLift).setFloorNumbers(mockQueue);
        verify(mockLift).addFloorNumberToQueue(floorNumber);
        verify(floorService).updateFloorNumbers(mockLift);
        verify(mockLift, never()).addLiftListener(any(LiftListener.class));
        verify(mockLift, never()).callCabin();
        verify(mockLift, never()).removeLiftListener(any(LiftListener.class));
    }

}

