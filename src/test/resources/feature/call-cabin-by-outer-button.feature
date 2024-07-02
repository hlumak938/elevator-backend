Feature: Call cabin on particular floor by outer button

  Background:
    Given buildings exist
      | id | floorCount | city    | street   | buildingNumber |
      | *1 | 10         | Kharkov | Sumskaya | 26             |
    And the building has lift with "STOPPED" engine, cabin with "CLOSED" doors

  Scenario Outline: Passenger clicks on outer button and lift delivers a cabin on the correspond floor
    Given Cabin is on the <cabinFloor> floor
    When I click on the outer button on the <desirableFloor> floor
    Then operation should be successful
    And the engine of the free lift was "<engineDirection>"
    And the cabin moved to <desirableFloor>
    And lift was "BUSY"
    And engine should be "STOPPED" 1 times
    And the door should be "OPEN" 1 times

    Examples:
      | cabinFloor | desirableFloor | engineDirection |
      | 1          | 5              | RUNNING_UP      |
      | 4          | 3              | RUNNING_DOWN    |

  Scenario Outline: Passenger clicks on outer button with invalid params
    Given Cabin is on the <cabinFloor> floor
    When I click on the outer button on the <desirableFloor> floor
    Then operation should fail with 400 error

    Examples:
      | cabinFloor | desirableFloor |
      | 4          | 20             |
      | 2          | 30             |