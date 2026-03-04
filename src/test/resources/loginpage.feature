Feature: GRC Login Page Functionality
  As a user
  I want to login to the GRC system
  So that I can access the application

  Scenario: Successful login with valid credentials
    Given I am on the GRC login page
    When I click on Login with Password option
    And I enter "sherwinzolvit360@yopmail.com" in the email address field
    And I enter "Vakil@1234" in the password field
    And I click the Log In button
    Then I should be redirected to the home page
    And the home page should load successfully

  Scenario: Login attempt with invalid email
    Given I am on the GRC login page
    When I click on Login with Password option
    And I enter "invalid@email.com" in the email address field
    And I enter "Vakil@1234" in the password field
    And I click the Log In button
    Then I should see an error message

  Scenario: Login attempt with empty credentials
    Given I am on the GRC login page
    When I click on Login with Password option
    And I click the Log In button
    Then I should see validation error messages for required fields