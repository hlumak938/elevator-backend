package org.sytoss.bdd.configuration;

import io.cucumber.java.DataTableType;
import org.apache.commons.lang3.StringUtils;
import org.sytoss.bom.*;

import java.util.Map;

public class DataTableTransformer {

    @DataTableType
    public Lift defineLift (Map<String, String> entry) {
        Lift lift = new Lift();
        if (entry.containsKey("id")) {
            lift.setId(stringIdToInteger(entry.get("id")));
        }
        if (entry.containsKey("cabinPosition")) {
            lift.setCabinPosition(Integer.parseInt(entry.get("cabinPosition")));
        }
        lift.setCabin(new Cabin());
        if (entry.containsKey("cabin serialNumber")) {
            lift.getCabin().setId(entry.get("cabin serialNumber"));
        }
        if (entry.containsKey("cabin type")) {
            lift.getCabin().setType(entry.get("cabin type"));
        }
        if (entry.containsKey("cabinPosition"))
            lift.setCabinPosition(Integer.parseInt(entry.get("cabinPosition")));
        if (entry.containsKey("doorStatus")) {
            if (entry.get("doorStatus").equals("CLOSED")) {
                lift.getCabin().getDoor().close();
            } else if (entry.get("doorStatus").equals("OPEN")) {
                lift.getCabin().getDoor().open();
            }
        }
        lift.setEngine(new Engine());
        if (entry.containsKey("engine serialNumber")) {
            lift.getEngine().setId(entry.get("engine serialNumber"));
        }
        if (entry.containsKey("engine type")) {
            lift.getEngine().setType(entry.get("engine type"));
        }
        if (entry.containsKey("engineStatus")) {
            lift.getEngine().setStatus(EngineStatus.valueOf(entry.get("engineStatus")));
        }
        if (entry.get("button template") != null) {
            lift.setButtonTemplate(FloorButtonTemplate.valueOf(entry.get("button template")));
        }
        if (entry.containsKey("buildingId")) {
            DataTableTransformer dataTableTransformer = new DataTableTransformer();
            int buildingId = dataTableTransformer.stringIdToInteger(entry.get("buildingId"));
            lift.setBuildingId(buildingId);
        }
        if (entry.containsKey("liftStatus")) {
            lift.setStatus(LiftStatus.valueOf(entry.get("liftStatus")));
        }
        return lift;
    }

    @DataTableType
    public Building defineBuilding (Map<String, String> entry) {
        Address address = new Address();
        address.setCity(entry.get("city"));
        address.setStreet(entry.get("street"));
        Building building = new Building();
        if (entry.containsKey("id")) {
            building.setId(stringIdToInteger(entry.get("id")));
        }
        building.setAddress(address);
        String strFloorCountValue = entry.get("floorCount");
        if (!StringUtils.isEmpty(strFloorCountValue)) {
            building.setFloorCount(Integer.parseInt(strFloorCountValue));
        }
        String strBuildingNumberValue = entry.get("buildingNumber");
        if (!StringUtils.isEmpty(strBuildingNumberValue)) {
            address.setBuildingNumber(Integer.parseInt(strBuildingNumberValue));
        }
        return building;
    }

    private Integer stringIdToInteger (String id) {
        return Integer.parseInt(id.replaceAll("\\*", ""));
    }

}
