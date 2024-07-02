Feature: Get all buildings

  Scenario: Admin get all buildings
    Given buildings table is clear
    And buildings exist
      | id | floorCount | city    | street    | buildingNumber |
      | *1 | 9          | Kharkov | Sumskaya  | 73             |
      | *2 | 5          | Kiev    | Kievskaya | 25             |
    When I try to get all buildings
    Then operation should be successful
    And I should get buildings
      | id | floorCount | city    | street    | buildingNumber |
      | *2 | 5          | Kiev    | Kievskaya | 25             |
      | *1 | 9          | Kharkov | Sumskaya  | 73             |