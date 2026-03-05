Feature: GRC Home Page Needs Action Validation
  As a GRC user
  I want to validate the Needs Action count between Home page and Compliances page
  So that I can ensure data consistency across the application

  Background:
    Given I am on the GRC home page
    And I am logged in as a valid user

  @smoke @regression
  Scenario: Validate Needs Action count matches between Home page and Compliances page
    Given I am on the GRC home page
    When I fetch the count of "Needs Action" from the home page
    And I click on the "Needs Action" section
    Then I should be redirected to the compliances tab
    And the "Needs Action" count on compliances page should match the home page count

  @regression
  Scenario: Verify Needs Action navigation functionality
    Given I am on the GRC home page
    And the "Needs Action" section is visible
    When I click on the "Needs Action" section
    Then I should be redirected to the compliances tab
    And the compliances page should be displayed correctly

  @smoke
  Scenario: Validate Needs Action count is displayed correctly
    Given I am on the GRC home page
    When I view the "Needs Action" section
    Then the "Needs Action" count should be displayed
    And the count should be a valid number