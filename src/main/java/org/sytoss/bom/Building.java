package org.sytoss.bom;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Building {

    private Integer id;

    private Address address;

    private int floorCount;

    private Floor[] floors;

    @Setter(AccessLevel.NONE)
    private List<Lift> lifts = new ArrayList<>();

    public void setLift(Lift lift) {
        if (lift != null) {
            lifts.add(lift);
        }
    }

    public void validate() {
        if (address == null) {
            throw new IllegalArgumentException("Address is null");
        }
        address.validate();
        if (floorCount == 0) {
            throw new IllegalArgumentException("Floor count must be greater than 0");
        }
        if (floorCount < 2) {
            throw new InvalidFloorCountException("Floor count must be greater than 2");
        }
    }

    public void initFloors() {
        Floor[] floors = new Floor[getFloorCount()];
        for (int i = 0; i < floors.length; i++) {
            Floor floor = new Floor();
            int floorNumber = (i + 1);
            floor.setId(getId() * 1000 + floorNumber);
            floor.setNumber(floorNumber);
            floors[i] = floor;
        }
        setFloors(floors);
    }

    public void registerLift(Lift lift) {
        lift.setCabinPosition(1);
        lift.getCabin().getDoor().setId(lift.getCabin().getId());
        lift.getCabin().getDoor().close();
        lift.validate();
        lifts.add(lift);
    }

    public boolean hasFloor(int floorNumber) {
        for (Floor floor : getFloors()) {
            if (floor.getNumber() == floorNumber) {
                return true;
            }
        }
        return false;
    }

    public Lift getFreeNearestLift(int floorNumber) {
        Lift freeLift = null;
        int minDifference = Integer.MAX_VALUE;

        for (Lift lift : lifts) {
            if (lift.getStatus() == LiftStatus.FREE) {
                int difference = Math.abs(lift.getCabinPosition() - floorNumber);
                if (difference < minDifference) {
                    minDifference = difference;
                    freeLift = lift;
                }
            }
        }

        return freeLift;
    }

    public Lift getOneLift() {
        if (!lifts.isEmpty()) {
            return lifts.getFirst();
        }
        return null;
    }
}
