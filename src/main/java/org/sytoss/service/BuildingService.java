package org.sytoss.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.sytoss.bom.Address;
import org.sytoss.bom.Building;
import org.sytoss.bom.FreeLiftNotFoundException;
import org.sytoss.bom.Lift;
import org.sytoss.connector.AddressConnector;
import org.sytoss.connector.BuildingConnector;
import org.sytoss.connector.LiftConnector;
import org.sytoss.convertor.BuildingConvertor;
import org.sytoss.convertor.LiftConvertor;
import org.sytoss.dto.AddressDTO;
import org.sytoss.dto.BuildingDTO;
import org.sytoss.dto.LiftDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BuildingService {

    private final LiftConvertor liftConvertor;

    private final BuildingConnector buildingConnector;

    private final BuildingConvertor buildingConverter;

    private final AddressConnector addressConnector;

    private final LiftConnector liftConnector;

    private final LiftService liftService;

    @Transactional
    public Building register(Building building) {
        building.validate();

        if (buildingConnector.findByAddress(building.getAddress().getCity(), building.getAddress().getStreet(), building.getAddress().getBuildingNumber()) != null) {
            throw new EntityExistsException("Building already exists!");
        }
        BuildingDTO buildingDTO = buildingConverter.toDTO(building);

        Address address = building.getAddress();
        AddressDTO existAddressDTO = addressConnector.findByCityAndStreet(address.getCity(), address.getStreet());
        if (existAddressDTO != null) {
            buildingDTO.setAddress(existAddressDTO);
        }

        Building registeredBuilding = buildingConverter.fromDTO(buildingConnector.save(buildingDTO));
        registeredBuilding.initFloors();

        return registeredBuilding;
    }

    @Transactional
    public Lift register(int buildingId, Lift lift) {
        if (lift == null) {
            throw new IllegalArgumentException("Lift must not be null");
        }
        var optionalBuilding = buildingConnector.findById(buildingId);
        if (optionalBuilding.isEmpty()) {
            throw new EntityNotFoundException("Building not found!");
        } else {
            BuildingDTO buildingDTO = optionalBuilding.get();
            Building building = buildingConverter.fromDTO(buildingDTO);
            building.registerLift(lift);
            LiftDTO liftConvertorDto = liftConvertor.toDto(lift, buildingId);
            buildingDTO.getLifts().add(liftConvertorDto);
            liftConvertorDto.setBuilding(buildingDTO);
            LiftDTO liftDTO = liftConnector.save(liftConvertorDto);
            return liftConvertor.fromDTO(liftDTO);
        }
    }

    public Building getById(int buildingId) {
        BuildingDTO buildingDTO = buildingConnector.findById(buildingId)
                .orElseThrow(() -> new EntityNotFoundException("Building not found!"));
        Building result = buildingConverter.fromDTO(buildingDTO);
        result.initFloors();
        return result;
    }

    public void callCabin(int buildingId, int floorNumber) {
        if (buildingId <= 0 || floorNumber <= 0) {
            throw new IllegalArgumentException("Building id or floorNumber must be more then 0");
        }

        Optional<BuildingDTO> buildingDTO = buildingConnector.findById(buildingId);

        if (buildingDTO.isEmpty()) {
            throw new EntityNotFoundException("Building doesn't exist");
        }

        Building building = buildingConverter.fromDTO(buildingDTO.get());
        building.initFloors();
        boolean isHasFloor = building.hasFloor(floorNumber);

        if (!isHasFloor) {
            throw new IllegalArgumentException("Floor not found!");
        }

        Lift freeLift = building.getFreeNearestLift(floorNumber);

        if (freeLift == null) {
            freeLift = building.getOneLift();
            if (freeLift == null) {
                throw new FreeLiftNotFoundException("Free lift not found!");
            }
        }

        liftService.deliverCabinToCorrespondFloor(freeLift, floorNumber);
    }

    public void deliverCabinToCorrespondFloor(int liftId, int floorNumber) {
        Optional<LiftDTO> liftDTO = liftConnector.findById(liftId);
        if (liftDTO.isPresent()) {
            Lift lift = liftConvertor.fromDTO(liftDTO.get());
            liftService.deliverCabinToCorrespondFloor(lift, floorNumber);
        } else {
            throw new EntityExistsException("Lift not found!");
        }
    }

    public List<Building> getAll() {
        List<Building> buildings = new ArrayList<>();
        for(BuildingDTO buildingDTO : buildingConnector.findAll()) {
            buildings.add(buildingConverter.fromDTO(buildingDTO));
        }
        return buildings;
    }
}
