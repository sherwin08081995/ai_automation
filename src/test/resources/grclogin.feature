Feature: GRC Login Page Validation
  As a user
  I want to validate the GRC login functionality
  So that I can access the GRC system with proper credentials

  Background:
    Given I am on the GRC login page

  Scenario: Valid mobile number login - Get OTP
    When I enter "8148438075" in the email address field
    And I click the Get OTP button
    Then I should see OTP request processed

  Scenario: Invalid mobile number format
    When I enter "invalid123" in the email address field
    And I click the Get OTP button
    Then I should see validation error for invalid format

  Scenario: Empty email address field
    When I leave the email address field empty
    And I click the Get OTP button
    Then I should see "Email or Mobile number is required" error message

  Scenario: Valid email address login
    When I enter "test@example.com" in the email address field
    And I click the Get OTP button
    Then I should see OTP request processed

  Scenario: Login with password option
    When I click on "Login with Password" link
    Then I should see password login option