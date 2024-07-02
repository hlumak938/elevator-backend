package org.sytoss.bom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddressTest {

    @Test
    public void testValidateWithValidInput() {
        Address address = new Address();
        address.setCity("City");
        address.setStreet("Street");
        address.setBuildingNumber(123);

        assertDoesNotThrow(address::validate);
    }

    @Test
    public void testValidateWithNullCity() {
        Address address = new Address();
        address.setCity(null);
        address.setStreet("Street");
        address.setBuildingNumber(123);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, address::validate);
        assertEquals("City is null or empty", exception.getMessage());
    }

    @Test
    public void testValidateWithEmptyCity() {
        Address address = new Address();
        address.setCity("");
        address.setStreet("Street");
        address.setBuildingNumber(123);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, address::validate);
        assertEquals("City is null or empty", exception.getMessage());
    }

    @Test
    public void testValidateWithNullStreet() {
        Address address = new Address();
        address.setCity("City");
        address.setStreet(null);
        address.setBuildingNumber(123);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, address::validate);
        assertEquals("Street is null or empty", exception.getMessage());
    }

    @Test
    public void testValidateWithEmptyStreet() {
        Address address = new Address();
        address.setCity("City");
        address.setStreet("");
        address.setBuildingNumber(123);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, address::validate);
        assertEquals("Street is null or empty", exception.getMessage());
    }

    @Test
    public void testValidateWithNonPositiveBuildingNumber() {
        Address address = new Address();
        address.setCity("City");
        address.setStreet("Street");
        address.setBuildingNumber(0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, address::validate);
        assertEquals("Building number must be greater than 0", exception.getMessage());
    }
}
