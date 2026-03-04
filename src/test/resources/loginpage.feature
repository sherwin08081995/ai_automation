@login
Feature: GRC Login Page Validation
  As a user
  I want to login to the GRC application
  So that I can access the home page

  @smoke @regression
  Scenario: Successful login with valid credentials
    Given I am on the GRC login page
    When I click on "Login with Password" option
    And I enter "sherwinzolvit360@yopmail.com" in the email address field
    And I enter "Vakil@1234" in the password field
    And I click the "Log In" button
    Then I should be redirected to the home page
    And I should see the "Home" tab on the left side

  @negative
  Scenario: Login with invalid credentials
    Given I am on the GRC login page
    When I click on "Login with Password" option
    And I enter "invalid@email.com" in the email address field
    And I enter "wrongpassword" in the password field
    And I click the "Log In" button
    Then I should see an error message

  @validation
  Scenario: Verify login form elements are displayed
    Given I am on the GRC login page
    When I click on "Login with Password" option
    Then I should see the email address field
    And I should see the password field
    And I should see the "Log In" button