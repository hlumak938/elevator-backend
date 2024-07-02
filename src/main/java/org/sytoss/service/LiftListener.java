package org.sytoss.service;

import org.sytoss.event.EventComingToFloor;
import org.sytoss.event.EventMovingToFloor;

public interface LiftListener {

    void handlingMovingToNextFloor (EventMovingToFloor eventMovingToFloor);

    void handlingComingToFloor (EventComingToFloor eventComingToFloor);
}
