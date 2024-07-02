package org.sytoss.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.sytoss.bom.DoorStatus;
import org.sytoss.bom.EngineStatus;
import org.sytoss.bom.Lift;
import org.sytoss.bom.LiftStatus;
import org.sytoss.connector.LiftConnector;
import org.sytoss.convertor.LiftConvertor;
import org.sytoss.event.EventComingToFloor;
import org.sytoss.event.EventMovingToFloor;

import java.util.Queue;

@Service
@Slf4j
@RequiredArgsConstructor
public class LiftService implements LiftListener {

    private final LiftConnector liftConnector;

    private final LiftConvertor liftConvertor;

    private final FloorService floorService;

    public void deliverCabinToCorrespondFloor (Lift lift, int floorNumber) {
        LiftStatus liftStatus = lift.getStatus();
        log.info("Floor #{}, liftId {}", floorNumber, lift.getId());
        if (liftStatus == LiftStatus.FREE) {
            setStatus(lift, LiftStatus.BUSY);
            saveLift(lift);
            log.info("LiftService is busied");
        }
        Queue<Integer> floors = floorService.getCurrentFloorsNative(lift);
        lift.setFloorNumbers(floors);
        if (!lift.getFloorNumbers().contains(floorNumber)) {
            lift.addFloorNumberToQueue(floorNumber);
        }
        if (liftStatus == LiftStatus.BUSY) {
            floorService.updateFloorNumbers(lift);
            return;
        }
        saveLift(lift);
        lift.addLiftListener(this);
        lift.callCabin();
        lift.removeLiftListener(this);
    }

    public void openDoor(Lift lift) {
        lift.getCabin().getDoor().open();
    }

    public void closeDoor(Lift lift) {
        lift.getCabin().getDoor().close();
    }

    public void startEngine(Lift lift, EngineStatus engineStatus) {
        lift.getEngine().start(engineStatus);
    }

    public void stopEngine(Lift lift) {
        lift.getEngine().stop();
    }

    public void setStatus(Lift lift, LiftStatus liftStatus)  {
        lift.setStatus(liftStatus);
    }

    public void setCabinPosition(Lift lift, int floorNumber) {
        lift.setCabinPosition(floorNumber);
    }

    @Override
    public void handlingMovingToNextFloor(EventMovingToFloor eventMovingToFloor) {
        Lift lift = eventMovingToFloor.getLift();
        int nextFloor = 0;
        EngineStatus direction = lift.getEngine().getStatus();

        Queue<Integer> floors = floorService.getCurrentFloorsNative(lift);
        lift.setFloorNumbers(floors);

        if (lift.getCabinPosition() > lift.getFloorNumbers().peek()) {
            nextFloor = lift.getCabinPosition() - 1;
            direction = EngineStatus.RUNNING_DOWN;

        } else if (lift.getCabinPosition() < lift.getFloorNumbers().peek()) {
            nextFloor = lift.getCabinPosition() + 1;
            direction = EngineStatus.RUNNING_UP;
        }
        log.info("Lift {} will be moving to next floor {} with status {}", lift.getId(), nextFloor, lift.getStatus());
        log.info("Next floor: {}", nextFloor);

        if (lift.getEngine().getStatus() == EngineStatus.STOPPED) {
            if (lift.getCabin().getDoor().getDoorStatus() == DoorStatus.OPEN) {
                closeDoor(lift);
                log.info("Lift {} Door closed", lift.getId());
            }
            startEngine(lift, direction);
            log.info("Lift {} Start engine with status {}", lift.getId(), lift.getEngine().getStatus());
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        setCabinPosition(lift, nextFloor);
    }

    @Override
    public void handlingComingToFloor (EventComingToFloor eventComingToFloor) {
        log.info("Lift is on: {}", eventComingToFloor.getLift().getCabinPosition());
        Lift lift = eventComingToFloor.getLift();

        Queue<Integer> floors = floorService.getCurrentFloorsNative(lift);
        lift.setFloorNumbers(floors);
        saveLift(lift);

        if (lift.getFloorNumbers().contains(lift.getCabinPosition())) {

            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (lift.getEngine().getStatus() != EngineStatus.STOPPED) {
                stopEngine(lift);
                log.info("Stop is engine");
            }
            openDoor(lift);
            log.info("Open door");
            setStatus(lift, LiftStatus.FREE);

            floors = floorService.getCurrentFloorsNative(lift);
            lift.setFloorNumbers(floors);
            lift.removeFloorNumberFromQueue(lift.getCabinPosition());
            saveLift(lift);
        }

        if (!lift.getFloorNumbers().isEmpty()) {
            lift.moveToNextFloor();
        }
    }

    protected void saveLift (Lift lift) {
        liftConnector.save(liftConvertor.toDto(lift, lift.getBuildingId()));
    }
}
