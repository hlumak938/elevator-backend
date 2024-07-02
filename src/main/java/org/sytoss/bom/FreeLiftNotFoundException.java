package org.sytoss.bom;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FreeLiftNotFoundException extends IllegalArgumentException {
    public FreeLiftNotFoundException(String msg) {
        super(msg);
    }
}