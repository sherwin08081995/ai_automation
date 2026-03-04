Feature: GRC Login Functionality
  As a user
  I want to login to the GRC system
  So that I can access the home page

  Background:
    Given I am on the GRC login page

  @smoke @login
  Scenario: Successful login with valid credentials
    When I click on "Login with Password" option
    And I enter "sherwinzolvit360@yopmail.com" in the email address field
    And I enter "Vakil@1234" in the password field
    And I click on the Log In button
    Then I should be successfully logged in
    And the home page should be loaded

  @negative @login
  Scenario: Login attempt with invalid email
    When I click on "Login with Password" option
    And I enter "invalid@email.com" in the email address field
    And I enter "Vakil@1234" in the password field
    And I click on the Log In button
    Then I should see an error message
    And I should remain on the login page

  @negative @login
  Scenario: Login attempt with empty credentials
    When I click on "Login with Password" option
    And I leave the email address field empty
    And I leave the password field empty
    And I click on the Log In button
    Then I should see validation error messages
    And I should remain on the login page