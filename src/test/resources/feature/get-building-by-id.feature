Feature: Get building by id

  Scenario: Admin get existing building by id
    Given buildings exist
      | id | floorCount | city    | street    | buildingNumber |
      | *1 | 9          | Kharkov | Sumskaya  | 73             |
      | *2 | 5          | Kiev    | Kievskaya | 25             |
    When I try to get building by id "*1"
    Then operation should be successful
    And I should get building
      | id | floorCount | city    | street   | buildingNumber |
      | *1 | 9          | Kharkov | Sumskaya | 73             |

  Scenario: Admin get non-existing building by id
    Given buildings exist
      | id | floorCount | city    | street    | buildingNumber |
      | *1 | 9          | Kharkov | Sumskaya  | 73             |
      | *2 | 5          | Kiev    | Kievskaya | 25             |
    When I try to get building by id 9999
    Then operation should fail with 404 error