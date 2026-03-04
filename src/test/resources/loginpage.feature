Feature: GRC Login Page Validation
  As a user of the GRC system
  I want to be able to login with my credentials
  So that I can access the application

  Background:
    Given I navigate to the GRC login page

  Scenario: Successful login with valid credentials
    When I click Login with Password option
    And I enter "sherwinzolvit360@yopmail.com" in Email address section
    And I enter "Vakil@1234" in Password section
    And I click Log In CTA
    Then I should confirm that Home page loaded successfully

  Scenario: Login attempt with invalid email format
    When I click Login with Password option
    And I enter "invalid-email" in Email address section
    And I enter "Vakil@1234" in Password section
    And I click Log In CTA
    Then I should see an error message for invalid email

  Scenario: Login attempt with empty credentials
    When I click Login with Password option
    And I leave Email address section empty
    And I leave Password section empty
    And I click Log In CTA
    Then I should see validation errors for required fields