package org.sytoss.bom;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@NoArgsConstructor
public class Cabin {

    @Setter(AccessLevel.NONE)
    private String serialNumber;

    private Door door = new Door();

    private String type;

    public String getId() {
        return this.serialNumber;
    }

    public void setId(String serialNumber) {
        this.serialNumber = serialNumber;
        door.setId(serialNumber);
    }

    public void validate() {
        if (StringUtils.isEmpty(serialNumber)) {
            throw new IllegalArgumentException("Serial number cannot be empty");
        } else if (door == null) {
            throw new IllegalArgumentException("Door cannot be null");
        }
        door.validate();
        if (StringUtils.isEmpty(type)) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
    }
}
