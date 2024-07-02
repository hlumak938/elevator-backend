Feature: Register new lift in building

  Background:
    Given building with address "Kharkov, Sumskaya, 1" with 5 floors exists

  Scenario: Admin register a new lift in empty building

    Given this building has lifts
    When I try to register lift in this building
      | field               | value      |
      | cabin serialNumber  | cabin-123  |
      | cabin type          | my-cabin   |
      | engine serialNumber | engine-123 |
      | engine type         | my-engine  |
      | button template     | UP_DOWN    |
    Then operation should be successful
    And this building contains 1 lift with "UP_DOWN" button templates and "engine-123" engine and "cabin-123" cabin

  Scenario Outline: Admin register a lift with empty required field
    Given this building has lifts
    When I try to register lift in this building
      | field               | value                |
      | cabin serialNumber  | <cabinSerialNumber>  |
      | cabin type          | <cabinType>          |
      | engine serialNumber | <engineSerialNumber> |
      | engine type         | <engineType>         |
      | button template     | <buttonTemplate>     |
    Then operation should fail with status code 400
    Examples:
      | cabinSerialNumber | cabinType | engineSerialNumber | engineType | buttonTemplate |
      |                   | my-cabin  | engine-123         | my-engine  | UP_DOWN        |
      | cabin-123         | my-cabin  |                    | my-engine  | UP_DOWN        |
      |                   |           | engine-123         | my-engine  | UP_DOWN        |
      | cabin-123         | my-cabin  | engine-123         | my-engine  |                |

