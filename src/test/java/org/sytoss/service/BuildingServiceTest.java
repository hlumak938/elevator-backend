package org.sytoss.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.sytoss.bom.*;
import org.sytoss.connector.AddressConnector;
import org.sytoss.connector.BuildingConnector;
import org.sytoss.connector.LiftConnector;
import org.sytoss.convertor.BuildingConvertor;
import org.sytoss.convertor.LiftConvertor;
import org.sytoss.dto.BuildingDTO;
import org.sytoss.dto.CabinDTO;
import org.sytoss.dto.EngineDTO;
import org.sytoss.dto.LiftDTO;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BuildingServiceTest {

    private final AddressConnector addressConnector = mock(AddressConnector.class);

    private final BuildingConnector buildingConnector = mock(BuildingConnector.class);

    private final BuildingConvertor buildingConvertor = spy(Mappers.getMapper(BuildingConvertor.class));

    private final LiftConnector liftConnector = mock(LiftConnector.class);

    private final LiftConvertor liftConvertor = spy(Mappers.getMapper(LiftConvertor.class));

    @InjectMocks
    private BuildingService buildingService;

    @Autowired
    @Mock
    private LiftService liftService;

    @Test
    public void shouldRegisterBuilding () {
        BuildingDTO savedDto = new BuildingDTO();
        savedDto.setId(1);
        savedDto.setFloorCount(5);
        when(buildingConnector.save(any(BuildingDTO.class))).thenReturn(savedDto);

        Building input = spy(new Building());
        input.setFloorCount(5);
        input.setAddress(new Address());
        input.getAddress().setCity("City");
        input.getAddress().setStreet("Street");
        input.getAddress().setBuildingNumber(22);

        Building testBuilding = buildingService.register(input);

        assertEquals(1, testBuilding.getId());
        assertEquals(5, testBuilding.getFloorCount());
        assertEquals(5, testBuilding.getFloors().length);
        assertEquals(5, testBuilding.getFloorCount());
    }

    @Test
    public void shouldRegisterLiftInBuildings () {
        Lift lift = new Lift();

        lift.setCabin(new Cabin());
        lift.getCabin().setId("CA12345");
        lift.getCabin().setType("small");
        lift.getCabin().getDoor().close();
        lift.setEngine(new Engine());
        lift.getEngine().setId("ENG12345");
        lift.getEngine().setType("powerful");
        lift.setFloorNumbers(new LinkedList<>(List.of(5)));
        lift.setButtonTemplate(FloorButtonTemplate.UP_DOWN);

        LiftDTO savedLiftDTO = new LiftDTO();
        savedLiftDTO.setId(1);
        savedLiftDTO.setCabin(new CabinDTO());
        savedLiftDTO.getCabin().setId("CA12345");
        savedLiftDTO.getCabin().setType("small");
        savedLiftDTO.getCabin().setDoorStatus("CLOSED");
        savedLiftDTO.setEngine(new EngineDTO());
        savedLiftDTO.getEngine().setId("ENG12345");
        savedLiftDTO.getEngine().setType("powerful");
        savedLiftDTO.setButtonTemplate("UP_DOWN");
        savedLiftDTO.setFloorNumbers("5");

        when(liftConnector.save(any(LiftDTO.class))).thenReturn(savedLiftDTO);

        LiftDTO liftDTO = new LiftDTO();

        when(liftConvertor.toDto(any(Lift.class), any())).thenReturn(liftDTO);

        BuildingDTO buildingDTO = new BuildingDTO();
        when(buildingConnector.findById(any(Integer.class))).thenReturn(Optional.of(buildingDTO));

        Lift testLift = buildingService.register(1, lift);

        assertEquals("CA12345", testLift.getCabin().getId());
        assertEquals("small", testLift.getCabin().getType());
        assertEquals("powerful", testLift.getEngine().getType());
        assertEquals("ENG12345", testLift.getEngine().getId());
        assertEquals(FloorButtonTemplate.UP_DOWN, testLift.getButtonTemplate());

        verify(liftConvertor).fromDTO(any());
        verify(liftConvertor, times(1)).toDto(any(), any());

        verify(liftConnector).save(any());
        verify(liftConnector, times(1)).save(any());
    }


    @Test
    public void shouldGetBuildingById () {
        int buildingId = 1;
        BuildingDTO buildingDTO = new BuildingDTO();
        Building expectedBuilding = mock(Building.class);
        when(buildingConnector.findById(buildingId)).thenReturn(Optional.of(buildingDTO));

        doReturn(expectedBuilding).when(buildingConvertor).fromDTO(buildingDTO);
        Building result = buildingService.getById(buildingId);

        assertEquals(expectedBuilding, result);
        verify(expectedBuilding).initFloors();
        verifyNoMoreInteractions(expectedBuilding);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenBuildingNotFound () {
        int nonExistentBuildingId = 999;
        when(buildingConnector.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> buildingService.getById(nonExistentBuildingId));
    }

    @Test
    public void shouldSuccessCallCabinAndMoveToCorrespondFloor () {
        int buildingId = 1;
        int floorNumber = 5;
        BuildingDTO mockBuildingDTO = new BuildingDTO();
        Building mockBuilding = mock(Building.class);
        Lift mockFreeLift = mock(Lift.class);

        when(buildingConnector.findById(buildingId)).thenReturn(Optional.of(mockBuildingDTO));
        when(buildingConvertor.fromDTO(mockBuildingDTO)).thenReturn(mockBuilding);
        when(mockBuilding.hasFloor(floorNumber)).thenReturn(true);
        when(mockBuilding.getFreeNearestLift(floorNumber)).thenReturn(mockFreeLift);

        assertDoesNotThrow(() -> buildingService.callCabin(buildingId, floorNumber));
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenCabinNotFound () {
        int buildingId = 999;
        int floorNumber = 5;
        BuildingConnector mockBuildingConnector = mock(BuildingConnector.class);

        when(mockBuildingConnector.findById(buildingId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> buildingService.callCabin(buildingId, floorNumber));
        assertEquals("Building doesn't exist", exception.getMessage());
    }


    @Test
    public void shouldGetAllBuildings() {
        BuildingDTO buildingDTO1 = new BuildingDTO();
        buildingDTO1.setId(1);
        buildingDTO1.setFloorCount(5);

        BuildingDTO buildingDTO2 = new BuildingDTO();
        buildingDTO2.setId(2);
        buildingDTO2.setFloorCount(3);

        List<BuildingDTO> buildingDTOList = Arrays.asList(buildingDTO1, buildingDTO2);

        Building building1 = new Building();
        building1.setId(1);
        building1.setFloorCount(5);

        Building building2 = new Building();
        building2.setId(2);
        building2.setFloorCount(3);

        when(buildingConnector.findAll()).thenReturn(buildingDTOList);
        when(buildingConvertor.fromDTO(buildingDTO1)).thenReturn(building1);
        when(buildingConvertor.fromDTO(buildingDTO2)).thenReturn(building2);

        List<Building> buildings = buildingService.getAll();

        assertEquals(2, buildings.size());
        assertEquals(building1, buildings.get(0));
        assertEquals(building2, buildings.get(1));

        verify(buildingConnector, times(1)).findAll();
    }
}
