package org.sytoss.connector;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.sytoss.dto.LiftDTO;

import java.util.List;

@Repository
public interface LiftConnector extends JpaRepository<LiftDTO, Integer> {

    List<LiftDTO> findAllByCabinId(String cabin_id);

    List<LiftDTO> findAllByBuilding_Id(int building_id);

    default List<LiftDTO> findLiftsByBuildingId(int building_id) {
        return findAllByBuilding_Id(building_id);
    }

    @Modifying
    @Query("UPDATE LIFT l SET l.floorNumbers = :floorNumbers WHERE l.id = :id")
    void updateFloorNumbersById(@Param("id") Integer id, @Param("floorNumbers") String floorNumbers);

    @Query(value = "SELECT floor_numbers FROM LIFT WHERE id = :id", nativeQuery = true)
    String findFloorNumbersById(@Param("id") Integer id);
}
