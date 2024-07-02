Feature: Сhoose particular floor by inner button then lift should deliver me on the correspond floor.

  Background:
    Given buildings exist
      | id | floorCount | city    | street   | buildingNumber |
      | *1 | 10         | Kharkov | Sumskaya | 26             |
    And the building has following lift
      | id | cabinPosition | button template | cabin serialNumber | doorStatus | cabin type | engine serialNumber | engine type | engineStatus | buildingId |
      | *1 | 1             | UP_DOWN         | CA123B1            | CLOSED     | small      | BP125B1             | common      | STOPPED      | *1         |

  Scenario Outline:
    Given Cabin is on the <cabinFloor> floor
    And Door is "CLOSED"
    When I click on the inner button that points to the [1, 2, 5, 7] floor
    Then operations should be successful
    And door was "CLOSED" 3 times
    And engine was "RUNNING_UP" or "RUNNING_DOWN" 4 times
    And cabin should be moved to the [1, 2, 5, 7] floor
    And engine should be "STOPPED" 4 times
    And the door should be "OPEN" 4 times

    Examples:
      | cabinFloor |
      | 5          |
      | 7          |