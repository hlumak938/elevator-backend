Feature: Choose particular floor by inner button then lift should deliver me on the correspond floor

  Background:
    Given buildings exist
      | id | floorCount | city    | street    | buildingNumber |
      | *1 | 16         | Kharkov | Sumskaya  | 67             |
    And the building has lift with "STOPPED" engine, cabin with "OPEN" doors

  Scenario Outline: Passenger choose inner button and lift deliver it on the correspond floor
    Given Cabin is on the <cabinFloor> floor
    When I click on the inner button on the <desirableFloor> floor
    Then operation should be successful
    And the engine of the free lift was "<engineStatus>"
    And the cabin moved to <desirableFloor>
    And engine should be "STOPPED"
    And the door should be "OPEN"

    Examples:
      | cabinFloor | desirableFloor | engineStatus |
      | 5          | 10             | RUNNING_UP   |
      | 6          | 3              | RUNNING_DOWN |

  Scenario Outline: Passenger selects an inner button that points to the current floor
    Given Cabin is on the <cabinPosition> floor
    When I click on the inner button on the <cabinPosition> floor
    Then operation should be successful
    And the cabin stays at <cabinPosition> floor

    Examples:
      | cabinPosition |
      | 3             |
      | 4             |