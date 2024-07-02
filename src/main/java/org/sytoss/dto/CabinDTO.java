package org.sytoss.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "CABIN")
@Getter
@Setter
public class CabinDTO {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "DOOR_STATUS")
    private String doorStatus;
}
