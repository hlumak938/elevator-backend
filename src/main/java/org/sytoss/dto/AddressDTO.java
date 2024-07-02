package org.sytoss.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ADDRESS")
@Getter
@Setter
public class AddressDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADDRESS_SEQ_ID")
    @SequenceGenerator(name = "ADDRESS_SEQ_ID", sequenceName = "ADDRESS_SEQ_ID", allocationSize = 1)
    @Column(name = "ID")
    private int id;

    @Column(name = "CITY")
    private String city;

    @Column(name = "STREET")
    private String street;
}
