Feature: Register new building

  Scenario: Admin register a building
    Given building with address "Kharkov, Sumskaya, 1" doesnt exist
    When register building request coming
      | field          | value    |
      | floorCount     | 9        |
      | city           | Kharkov  |
      | street         | Sumskaya |
      | buildingNumber | 1        |
    Then operation should be successful
    And building with address "Kharkov, Sumskaya, 1" exists
    And has 9 floors

  Scenario Outline: Admin register a building with empty required field
    Given building with address "Kharkov, Sumskaya, 1" doesnt exist
    When register building request coming
      | field          | value            |
      | floorCount     | <floorCount>     |
      | city           | <city>           |
      | street         | <street>         |
      | buildingNumber | <buildingNumber> |
    Then operation should fail with 400 error
    Examples:
      | floorCount | city    | street   | buildingNumber |
      |            | Kharkov | Sumskaya | 1              |
      | 0          | Kharkov | Sumskaya | 1              |
      | 10         |         | Sumskaya | 1              |
      | 10         | Kharkov |          | 1              |
      | 10         | Kharkov | Sumskaya |                |
      | 10         | Kharkov | Sumskaya | 0              |

  Scenario Outline: Admin specify wrong amount of floors
    Given building with address "Kharkov, Sumskaya, 1" exists
    And this building has lifts
    When register building request coming
      | field          | value            |
      | floorCount     | <floorCount>     |
      | city           | <city>           |
      | street         | <street>         |
      | buildingNumber | <buildingNumber> |
    Then operation should fail with 406 error

    Examples:
      | floorCount | city    | street   | buildingNumber |
      | -10        | Kharkov | Sumskaya | 1              |
      | 1          | Kharkov | Sumskaya | 1              |

  Scenario: Admin register a building by already exists address
    Given building with address "Kharkov, Sumskaya, 1" exists
    When register building request coming
      | field          | value    |
      | floorCount     | 9        |
      | city           | Kharkov  |
      | street         | Sumskaya |
      | buildingNumber | 1        |
    Then operation should fail with 409 error