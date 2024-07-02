package org.sytoss.event;

import lombok.Getter;
import org.sytoss.bom.Lift;

@Getter
public class EventComingToFloor {

    private final Lift lift;

    public EventComingToFloor(Lift lift) {
        this.lift = lift;
    }
}
