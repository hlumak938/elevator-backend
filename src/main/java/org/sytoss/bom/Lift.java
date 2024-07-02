package org.sytoss.bom;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.sytoss.event.EventComingToFloor;
import org.sytoss.event.EventMovingToFloor;
import org.sytoss.service.LiftListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Getter
@Setter
public class Lift {

    private int id;

    @Setter(AccessLevel.NONE)
    private int cabinPosition;

    private LiftStatus status = LiftStatus.FREE;

    private Cabin cabin;

    private Engine engine;

    private FloorButtonTemplate buttonTemplate;

    private Queue<Integer> floorNumbers = new LinkedList<>();

    private List<LiftListener> liftListenerList = new ArrayList<>();

    private int buildingId;

    public void setCabinPosition(int cabinPosition) {
        this.cabinPosition = cabinPosition;
        notifyComingToFloor();
    }

    public void validate() {
        if (cabin == null || engine == null) {
            throw new IllegalArgumentException("Cabin or engine cannot be null");
        }
        cabin.validate();
        engine.validate();
        if (buttonTemplate == null) {
            throw new IllegalArgumentException("ButtonTemplate cannot be null");
        }
    }

    public void addFloorNumberToQueue (int floorNumber) {
        floorNumbers.add(floorNumber);
    }

    public void removeFloorNumberFromQueue (int floorNumber) {
        floorNumbers.remove(floorNumber);
    }

    public void addLiftListener (LiftListener l) {
        liftListenerList.add(l);
    }

    public void removeLiftListener(LiftListener l) {
        liftListenerList.remove(l);
    }

    public void notifyComingToFloor() {
        EventComingToFloor eventComingToFloor = new EventComingToFloor(this);
        for (LiftListener l : liftListenerList) {
            l.handlingComingToFloor(eventComingToFloor);
        }
    }

    public void notifyMovingToNextFloor() {
        EventMovingToFloor eventMovingToFloor = new EventMovingToFloor(this);
        for (LiftListener l : liftListenerList) {
            l.handlingMovingToNextFloor(eventMovingToFloor);
        }
    }

    public void callCabin() {
        notifyComingToFloor();
    }

    public void moveToNextFloor() {
        notifyMovingToNextFloor();
    }
}
