package org.sytoss.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.sytoss.bom.Lift;
import org.sytoss.connector.LiftConnector;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FloorService {

    private final LiftConnector liftConnector;

    @Transactional
    public void updateFloorNumbers(Lift lift) {
        String floorNumbersString = lift.getFloorNumbers().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        liftConnector.updateFloorNumbersById(lift.getId(), floorNumbersString);
    }

    protected Queue<Integer> getCurrentFloorsNative(Lift lift) {
        String floors = liftConnector.findFloorNumbersById(lift.getId());
        Queue<Integer> result = new LinkedList<>();
        String[] numbers = floors.split(",");
        for (String number : numbers) {
            if (!number.isEmpty()) {
                result.add(Integer.parseInt(number.trim()));
            }

        }
        return result;
    }
}