package org.sytoss.convertor;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.sytoss.bom.Address;
import org.sytoss.bom.Building;
import org.sytoss.dto.AddressDTO;
import org.sytoss.dto.BuildingDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildingConvertorTest {

    @Test
    public void shouldConvertToDto () {
        Building source = new Building();
        source.setId(1);
        source.setFloorCount(5);
        source.initFloors();

        Address address = new Address();
        address.setCity("San Francisco");
        address.setStreet("Street");
        address.setBuildingNumber(3);
        source.setAddress(address);

        BuildingDTO target = Mappers.getMapper(BuildingConvertor.class).toDTO(source);
        assertEquals(1, target.getId());
        assertEquals(5, target.getFloorCount());
        assertEquals(3, target.getBuildingNumber());
        assertEquals("San Francisco", target.getAddress().getCity());
        assertEquals("Street", target.getAddress().getStreet());
    }


    @Test
    public void shouldConvertFromDto () {
        BuildingDTO source = new BuildingDTO();
        source.setId(1);
        source.setFloorCount(5);
        source.setBuildingNumber(3);

        AddressDTO address = new AddressDTO();
        address.setCity("San Francisco");
        address.setStreet("Street");
        source.setAddress(address);

        Building target = Mappers.getMapper(BuildingConvertor.class).fromDTO(source);
        assertEquals(1, target.getId());
        assertEquals(5, target.getFloorCount());
        assertEquals(3, target.getAddress().getBuildingNumber());
        assertEquals("San Francisco", target.getAddress().getCity());
        assertEquals("Street", target.getAddress().getStreet());
    }
}
