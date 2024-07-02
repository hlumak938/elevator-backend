package org.sytoss.bom;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidFloorCountException extends IllegalArgumentException {
    public InvalidFloorCountException(String msg) {
        super(msg);
    }
}
