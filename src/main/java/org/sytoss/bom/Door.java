package org.sytoss.bom;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class Door {

    private String id;

    @Setter(AccessLevel.PUBLIC)
    private DoorStatus doorStatus;

    public void open() {
        this.doorStatus = DoorStatus.OPEN;
    }

    public void close() {
        this.doorStatus = DoorStatus.CLOSED;
    }

    public void validate() {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("Door must have an id");
        }
    }
}
