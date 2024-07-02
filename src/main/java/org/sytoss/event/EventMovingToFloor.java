package org.sytoss.event;

import lombok.Getter;
import org.sytoss.bom.Lift;

@Getter
public class EventMovingToFloor {

    private final Lift lift;

    public EventMovingToFloor(Lift lift) {
        this.lift = lift;
    }
}
