package org.sytoss.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "LIFT")
public class LiftDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LIFT_SEQ")
    @SequenceGenerator(name = "LIFT_SEQ", sequenceName = "LIFT_SEQ_ID", allocationSize = 2)
    @Column(name = "ID")
    private int id;

    @Column(name = "CABIN_POSITION")
    private int cabinPosition;

    @Column(name = "BUTTON_TEMPLATE")
    private String buttonTemplate;

    @Column(name = "STATUS")
    private String status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CABIN_ID")
    private CabinDTO cabin;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ENGINE_ID")
    private EngineDTO engine;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "BUILDING_ID")
    private BuildingDTO building;

    @Column(name = "FLOOR_NUMBERS")
    private String floorNumbers;
}