package org.sytoss.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "BUILDING")
@Getter
@Setter
public class BuildingDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BUILDING_SEQ_ID")
    @SequenceGenerator(name = "BUILDING_SEQ_ID", sequenceName = "BUILDING_SEQ_ID", allocationSize = 1)
    @Column(name = "ID")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ADDRESS_ID", nullable = false, referencedColumnName = "ID")
    private AddressDTO address;

    @Column(name = "FLOOR_COUNT")
    private int floorCount;

    @Column(name = "BUILDING_NUMBER")
    private int buildingNumber;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "building", cascade = CascadeType.ALL)
    private List<LiftDTO> lifts = new ArrayList<>();
}
