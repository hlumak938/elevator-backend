Feature: Get the nearest available lift

  Background:
    Given buildings exist
      | id | floorCount | city    | street   | buildingNumber |
      | *1 | 21         | Kharkov | Sumskaya | 26             |

  Scenario Outline: Passenger click on outer button and get nearest available lift
    Given the building has following lifts
      | id | cabinPosition | button template | cabin serialNumber | doorStatus | cabin type | engine serialNumber | engine type | engineStatus | liftStatus |
      | *1 | 1             | UP_DOWN         | CA213B1            | CLOSED     | small      | BP125B1             | common      | STOPPED      | FREE       |
      | *2 | 4             | UP_DOWN         | BA123C1            | CLOSED     | small      | PH134B1             | common      | STOPPED      | FREE       |
      | *3 | 8             | UP_DOWN         | BA723B1            | CLOSED     | small      | PP134B1             | common      | STOPPED      | FREE       |
      | *4 | 12            | UP_DOWN         | B11451             | CLOSED     | small      | P7634B1             | common      | RUNNING_UP   | BUSY       |
    When I click on the outer button on the <currentFloor> floor
    Then operation should be successful
    And the cabin with <liftId> id should be moved to the <currentFloor> floor
    And engine should be "STOPPED"
    And the door should be "OPEN"
    Examples:
      | currentFloor | liftId |
      | 5            | 2      |
      | 7            | 3      |




