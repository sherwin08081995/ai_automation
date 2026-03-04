@login
Feature: GRC Login Page Authentication
  As a GRC user
  I want to login to the application
  So that I can access the home page

  Background:
    Given I am on the GRC login page

  @smoke @positive
  Scenario: Successful login with valid credentials
    When I click on "Login with Password" option
    And I enter "sherwinzolvit360@yopmail.com" in the email address field
    And I enter "Vakil@1234" in the password field
    And I click the Log In button
    Then I should be redirected to the home page
    And the home page should load successfully

  @negative
  Scenario: Login attempt with invalid credentials
    When I click on "Login with Password" option
    And I enter "invalid@email.com" in the email address field
    And I enter "wrongpassword" in the password field
    And I click the Log In button
    Then I should see an error message
    And I should remain on the login page

  @negative
  Scenario: Login attempt with empty credentials
    When I click on "Login with Password" option
    And I leave the email address field empty
    And I leave the password field empty
    And I click the Log In button
    Then I should see validation errors for required fields
    And I should remain on the login page