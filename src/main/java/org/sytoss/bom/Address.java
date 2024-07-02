package org.sytoss.bom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {

    private String city;

    private String street;

    private int buildingNumber;

    public void validate() {
        if (city == null || city.isEmpty()) {
            throw new IllegalArgumentException("City is null or empty");
        }
        if (street == null || street.isEmpty()) {
            throw new IllegalArgumentException("Street is null or empty");
        }
        if (buildingNumber <= 0) {
            throw new IllegalArgumentException("Building number must be greater than 0");
        }
    }
}
