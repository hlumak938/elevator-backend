package org.sytoss.bom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BuildingTest {

    @Test
    public void shouldSuccessfulValidate () {
        Building building = new Building();
        building.setId(1);
        building.setFloorCount(5);
        building.initFloors();

        Address address = new Address();
        address.setStreet("Street");
        address.setCity("City");
        address.setBuildingNumber(1);
        building.setAddress(address);

        assertDoesNotThrow(building::validate);
    }

    @Test
    public void testValidateWithNullAddress () {
        Building building = new Building();
        building.setId(1);
        building.setFloorCount(5);
        building.initFloors();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, building::validate);
        assertEquals("Address is null", exception.getMessage());
    }

    @Test
    public void testValidateWithZeroFloorCount () {
        Building building = new Building();
        building.setId(1);
        building.setFloorCount(0);
        building.initFloors();

        Address address = new Address();
        address.setStreet("Street");
        address.setCity("City");
        address.setBuildingNumber(1);
        building.setAddress(address);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, building::validate);
        assertEquals("Floor count must be greater than 0", exception.getMessage());
    }

    @Test
    public void testValidateWithLessThanTwoFloorCount () {
        Building building = new Building();
        building.setId(1);
        building.setFloorCount(1);
        building.initFloors();

        Address address = new Address();
        address.setStreet("Street");
        address.setCity("City");
        address.setBuildingNumber(1);
        building.setAddress(address);

        IllegalArgumentException exception = assertThrows(InvalidFloorCountException.class, building::validate);
        assertEquals("Floor count must be greater than 2", exception.getMessage());
    }

    @Test
    public void testGetFreeNearestLift () {
        Building building = new Building();
        building.setId(1);
        building.setFloorCount(1);
        building.initFloors();

        Address address = new Address();
        address.setStreet("Street");
        address.setCity("City");
        address.setBuildingNumber(1);
        building.setAddress(address);

        Lift lift = new Lift();
        lift.setId(1);
        lift.setCabin(new Cabin());
        lift.setCabinPosition(4);
        lift.setStatus(LiftStatus.FREE);
        lift.getCabin().setId("123");
        lift.getCabin().setType("Small");
        lift.getCabin().setDoor(new Door());
        lift.setEngine(new Engine());
        lift.getEngine().setId("123PH");
        lift.getEngine().setType("Powerful");
        lift.setButtonTemplate(FloorButtonTemplate.UP_DOWN);
        building.setLift(lift);

        Lift liftSecond = new Lift();
        liftSecond.setId(2);
        liftSecond.setCabin(new Cabin());
        liftSecond.setCabinPosition(6);
        liftSecond.setStatus(LiftStatus.BUSY);
        liftSecond.getCabin().setId("123Hh");
        liftSecond.getCabin().setType("Small");
        liftSecond.getCabin().setDoor(new Door());
        liftSecond.setEngine(new Engine());
        liftSecond.getEngine().setId("123PHh");
        liftSecond.getEngine().setType("Powerful");
        liftSecond.setButtonTemplate(FloorButtonTemplate.UP_DOWN);
        building.setLift(liftSecond);

        Lift liftThird = new Lift();
        liftThird.setId(3);
        liftThird.setCabin(new Cabin());
        liftThird.setCabinPosition(8);
        liftThird.setStatus(LiftStatus.FREE);
        liftThird.getCabin().setId("987Hh");
        liftThird.getCabin().setType("Small");
        liftThird.getCabin().setDoor(new Door());
        liftThird.setEngine(new Engine());
        liftThird.getEngine().setId("987PHh");
        liftThird.getEngine().setType("Powerful");
        liftThird.setButtonTemplate(FloorButtonTemplate.UP_DOWN);
        building.setLift(liftThird);
        Lift freeLift = building.getFreeNearestLift(7);

        assertEquals(3, freeLift.getId());
        assertEquals("987Hh", freeLift.getCabin().getId());
        assertEquals("987PHh", freeLift.getEngine().getId());
    }

    @Test
    public void testHasFloor () {
        Building building = new Building();
        building.setId(1);
        building.setFloorCount(10);
        building.initFloors();

        Address address = new Address();
        address.setStreet("Street");
        address.setCity("City");
        address.setBuildingNumber(1);
        building.setAddress(address);

        assertTrue(building.hasFloor(5));
        assertTrue(building.hasFloor(10));
    }

    @Test
    public void testHasNotExistFloor () {
        Building building = new Building();
        building.setId(1);
        building.setFloorCount(10);
        building.initFloors();

        Address address = new Address();
        address.setStreet("Street");
        address.setCity("City");
        address.setBuildingNumber(1);
        building.setAddress(address);

        assertFalse(building.hasFloor(11));
        assertFalse(building.hasFloor(15));
        assertFalse(building.hasFloor(0));
    }
}
