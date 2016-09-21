Feature: Testing Mercury tours Flight ticket booking feature - parameterized Data

  Background: 
    Given TestData is available for <testcaseid>
      | TestLinkTestCaseID1 |

  Scenario: Login to application
    Given I am on the HomePage
    When I click on SIGN-ON link
    And enter login credentials and sign in
    Then Flight finder page is displayed

  Scenario: Search for flights
    Given I am on the Flight finder page
    When I enter search parameters
    And click on search
    Then flight search results should be displayed

  Scenario: select flights
    Given I am on flight search results page
    And I Select the cheapest flights
    And I click on contniue
    Then Book a flight page should be displayed

  Scenario: book tickets
    Given I am on Book a flight page
    And I enter Passenger and payment information
    And I click on Purchase Tickets button
    Then Flight confirmation page should be displayed with booking confirmation number
