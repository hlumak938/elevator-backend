package org.sytoss.bom;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class Engine {

    @Setter(AccessLevel.NONE)
    private String serialNumber;

    private String type;

    private EngineStatus status = EngineStatus.STOPPED;

    public String getId() { return this.serialNumber; }

    public void setId(String id) { this.serialNumber = id; }

    public void validate() {
        if (StringUtils.isEmpty(serialNumber)) {
            throw new IllegalArgumentException("Serial number cannot be empty");
        } else if (StringUtils.isEmpty(type)) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
    }

    public void start(EngineStatus status) {
        this.status = status;
    }

    public void stop() {
        this.status = EngineStatus.STOPPED;
    }
}
