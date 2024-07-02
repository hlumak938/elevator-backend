package org.sytoss.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.sytoss.bom.Building;
import org.sytoss.bom.Lift;
import org.sytoss.service.BuildingService;

import java.util.List;
import java.util.concurrent.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class BuildingController {

    private final BuildingService buildingService;

    @PostMapping("/buildings")
    public Building register(@RequestBody Building building) {
        return buildingService.register(building);
    }

    @PostMapping("/buildings/{id}/lifts")
    public Lift register(@PathVariable(name = "id") Integer buildingId, @RequestBody Lift lift) {
        return buildingService.register(buildingId, lift);
    }

    @PatchMapping("/buildings/{buildingId}/floors/{floorNumber}")
    public void callCabin(@PathVariable int buildingId, @PathVariable int floorNumber) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<Void> task = () -> {
            buildingService.callCabin(buildingId, floorNumber);
            return null;
        };

        Future<Void> future = executorService.submit(task);
        try {
            future.get();
        } finally {
            executorService.shutdown();
        }
    }
    @GetMapping("/buildings/{id}")
    public Building getById(@PathVariable(name = "id") int buildingId) {
        return buildingService.getById(buildingId);
    }

    @PatchMapping("/buildings/lifts/{liftId}/cabins/{floorNumber}")
    public void deliverCabinToCorrespondFloor(@PathVariable int liftId, @PathVariable int floorNumber) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        Callable<Void> task = () -> {
            buildingService.deliverCabinToCorrespondFloor(liftId, floorNumber);
            return null;
        };

        Future<Void> future = executorService.submit(task);
        try {
            future.get();
        } finally {
            executorService.shutdown();
        }
//        buildingService.deliverCabinToCorrespondFloor(liftId, floorNumber);
    }

    @GetMapping("/buildings")
    public List<Building> getAll() {
        return buildingService.getAll();
    }

}
