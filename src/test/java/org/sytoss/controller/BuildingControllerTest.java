package org.sytoss.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.sytoss.bom.*;
import org.sytoss.service.BuildingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class BuildingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BuildingService buildingService;

    public static String asJsonString (final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldSuccessfulRegistryBuilding () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/buildings").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isOk());

        verify(buildingService).register(any(Building.class));
    }

    @Test
    public void shouldRaiseBadRequestForRegistryBuilding () throws Exception {
        when(buildingService.register(any(Building.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/buildings").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldsucessfullyregisterliftinbuilding () throws Exception {
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
        lift.setButtonTemplate(FloorButtonTemplate.UP_DOWN);
        List<Lift> lifts = new ArrayList<>();
        lifts.add(lift);
        building.setAddress(new Address());
        building.getAddress().setCity("Kharkov");
        building.getAddress().setStreet("Prosperct Grihorieva");
        lift.setCabinPosition(2);
        lift.getCabin().setDoor(new Door());
        lift.getCabin().getDoor().close();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/buildings/{id}/lifts", 1).content(asJsonString(lift)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        verify(buildingService).register(eq(1), argThat(arg -> arg.getId() == 1 && arg.getCabin().getId().equals("CA12345") && arg.getCabin().getType().equals("small") && arg.getEngine().getId().equals("ENG12345") && arg.getEngine().getType().equals("powerful") && arg.getButtonTemplate().equals(FloorButtonTemplate.UP_DOWN)));

    }

    @Test
    public void shouldRaiseNotAcceptableForRegistryBuilding () throws Exception {
        when(buildingService.register(any(Building.class))).thenThrow(new InvalidFloorCountException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/buildings").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isNotAcceptable());
    }

    @Test
    public void shouldRaiseBadRequestForRegistryLiftInBuilding () throws Exception {
        when(buildingService.register(eq(1), any(Lift.class))).thenThrow(new IllegalArgumentException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/buildings/{id}/lifts", 1).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRaiseConflictForRegistryBuilding () throws Exception {
        when(buildingService.register(any(Building.class))).thenThrow(new EntityExistsException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/buildings").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isConflict());
    }

    @Test
    public void shouldSuccessfulGetBuildingById () throws Exception {
        int buildingId = 1;
        Building building = new Building();
        when(buildingService.getById(anyInt())).thenReturn(building);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/buildings/{id}", buildingId).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void shouldRaiseNotFoundForGetBuildingById () throws Exception {
        int nonExistentBuildingId = 999;
        when(buildingService.getById(anyInt())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/buildings/{id}", nonExistentBuildingId).contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void shouldSuccessfulCallCabin () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/buildings/{buildingId}/floors/{floorNumber}", 1, 7).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isOk());

        verify(buildingService).callCabin(1, 7);
    }

    @Test
    public void shouldRaiseBadRequestForCallCabin () throws Exception {
        doThrow(new IllegalArgumentException()).when(buildingService).callCabin(anyInt(), anyInt());
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/buildings/{buildingId}/floors/{floorNumber}",
                0, 0).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldRaiseNotFoundForCallCabin () throws Exception {
        doThrow(new EntityNotFoundException()).when(buildingService).callCabin(anyInt(), anyInt());

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/buildings/{buildingId}/floors/{floorNumber}",
                5, 1).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldRaiseBadRequestForNotFoundFreeLiftInCallCabin () throws Exception {
        doThrow(new FreeLiftNotFoundException()).when(buildingService).callCabin(anyInt(), anyInt());

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/buildings/{buildingId}/floors/{floorNumber}",
                5, 1).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldSuccessfulDeliverCabinToCorrespondFloor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/buildings/lifts/{liftId}/cabins/{floorNumber}", 1, 3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(buildingService).deliverCabinToCorrespondFloor(eq(1), eq(3));
    }

    @Test
    public void shouldRaiseBadRequestForDeliverCabinToCorrespondFloor() throws Exception {
        doThrow(new IllegalArgumentException()).when(buildingService).deliverCabinToCorrespondFloor(123, 5);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/buildings/lifts/{liftId}/cabins/{floorNumber}", 123, 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(buildingService).deliverCabinToCorrespondFloor(123, 5);
    }

    @Test
    public void shouldRaiseNotFoundForDeliverCabinToCorrespondFloor() throws Exception {
        doThrow(new EntityNotFoundException()).when(buildingService).deliverCabinToCorrespondFloor(123, 5);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/buildings/lifts/{liftId}/cabins/{floorNumber}", 123, 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());

        verify(buildingService).deliverCabinToCorrespondFloor(123, 5);
    }

    @Test
    public void shouldGetAllBuildings() throws Exception {
        Building building1 = new Building();
        building1.setId(1);
        building1.setFloorCount(5);

        Building building2 = new Building();
        building2.setId(2);
        building2.setFloorCount(3);

        List<Building> buildings = Arrays.asList(building1, building2);
        when(buildingService.getAll()).thenReturn(buildings);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/buildings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(building1.getId()))
                .andExpect(jsonPath("$[0].floorCount").value(building1.getFloorCount()))
                .andExpect(jsonPath("$[1].id").value(building2.getId()))
                .andExpect(jsonPath("$[1].floorCount").value(building2.getFloorCount()));

        verify(buildingService, times(1)).getAll();
    }
}
