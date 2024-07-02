package org.sytoss.convertor;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.sytoss.bom.*;
import org.sytoss.dto.CabinDTO;
import org.sytoss.dto.EngineDTO;
import org.sytoss.dto.LiftDTO;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LiftConvertorTest {

    @Autowired
    private LiftConvertor liftConvertor;

    @Test
    public void shouldConvertToDto () {
        Building building = new Building();
        building.setId(1);

        Lift lift = new Lift();

        lift.setId(1);
        lift.setCabin(new Cabin());
        lift.getCabin().setId("CA12345");
        lift.getCabin().setType("small");
        lift.setEngine(new Engine());
        lift.getEngine().setId("ENG12345");
        lift.getEngine().setType("powerful");
        lift.getEngine().stop();
        lift.setButtonTemplate(FloorButtonTemplate.UP_DOWN);

        List<Lift> lifts = new ArrayList<>();
        lifts.add(lift);

        building.setAddress(new Address());
        building.getAddress().setCity("Kharkov");
        building.getAddress().setStreet("Prosperct Grihorieva");
        lift.setCabinPosition(2);
        lift.getCabin().setDoor(new Door());
        lift.getCabin().getDoor().close();

        LiftDTO liftDTO = liftConvertor.toDto(lift, 1);

        assertEquals(1, liftDTO.getId());
        assertEquals("CA12345", liftDTO.getCabin().getId());
        assertEquals("small", liftDTO.getCabin().getType());
        assertEquals("ENG12345", liftDTO.getEngine().getId());
        assertEquals("powerful", liftDTO.getEngine().getType());
        assertEquals(EngineStatus.STOPPED.name(), liftDTO.getEngine().getStatus());
        assertEquals(FloorButtonTemplate.UP_DOWN.name(), liftDTO.getButtonTemplate());
        assertEquals(2, liftDTO.getCabinPosition());
        assertEquals("CA12345", liftDTO.getCabin().getId());
        assertEquals(DoorStatus.CLOSED.name(), liftDTO.getCabin().getDoorStatus());
    }

    @Test
    public void shouldConvertFromDTO () {
        LiftDTO liftDTO = new LiftDTO();
        liftDTO.setId(1);
        liftDTO.setCabinPosition(2);
        liftDTO.setCabin(new CabinDTO());
        liftDTO.getCabin().setId("CA12345");
        liftDTO.getCabin().setType("small");
        liftDTO.getCabin().setDoorStatus("CLOSED");
        liftDTO.setEngine(new EngineDTO());
        liftDTO.getEngine().setId("ENG12345");
        liftDTO.getEngine().setType("powerful");
        liftDTO.setButtonTemplate(FloorButtonTemplate.UP_DOWN.name());
        liftDTO.setFloorNumbers("1");
        Lift lift = Mappers.getMapper(LiftConvertor.class).fromDTO(liftDTO);
        assertEquals(1, lift.getId());
        assertEquals("CA12345", lift.getCabin().getId());
        assertEquals("small", lift.getCabin().getType());
        assertEquals("ENG12345", lift.getEngine().getId());
        assertEquals("powerful", lift.getEngine().getType());
        assertEquals(FloorButtonTemplate.UP_DOWN.name().trim(), lift.getButtonTemplate().name().trim());
        assertEquals(2, lift.getCabinPosition());
        assertEquals("CA12345", lift.getCabin().getId());
        assertEquals(DoorStatus.CLOSED.name().trim(), lift.getCabin().getDoor().getDoorStatus().toString().trim());
    }
}