Feature: Testing Create new list & add items feature -  Data Driven


  Scenario Outline: Create a New List and add items
    Given I am on the HomeScreen
    When I click on Add New List link
    And I enter <ListName> and click OK
    Then <ListName> is created and is displayed
    When I add few <items>
    Then <items> are added to the list.
    
    Examples:
    | ListName | items |
    |  stationary   |  books:A4-sheets:pens  |
    |  vegetables   |  carrots:brocolli:tomatoes  |

