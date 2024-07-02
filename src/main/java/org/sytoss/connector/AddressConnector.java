package org.sytoss.connector;

import org.springframework.data.jpa.repository.JpaRepository;
import org.sytoss.dto.AddressDTO;

public interface AddressConnector extends JpaRepository<AddressDTO, Integer> {

    AddressDTO findByCityAndStreet(String city, String street);
}
