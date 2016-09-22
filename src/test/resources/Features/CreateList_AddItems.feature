Feature: Testing Create new list & add items feature - parameterized Data

  Background: 
    Given OIShoppingList TestData is available for <testcaseid>
      | TestCaseID1 |

  Scenario: Create a New List
    Given I am on the HomeScreen
    When I click on Add New List link
    And I enter Listname and click on OK
    Then New List is created and displayed

  Scenario: Add Items
    Given I have the Newly created List open
    When I add new items
    Then Items are added to the list
