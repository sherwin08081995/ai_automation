@GRC
Feature: GRC Login Functionality
  As a user of the GRC system
  I want to be able to enter my credentials and get OTP
  So that I can access my account securely

  Background:
    Given I am on the GRC login page

  @ValidLogin
  Scenario: Enter valid mobile number in email field and get OTP
    When I enter "8148438075" in the email address field
    And I click the Get OTP button
    Then the system should process the OTP request

  @InvalidLogin
  Scenario: Enter invalid email format and attempt to get OTP
    When I enter "invalid-email" in the email address field
    And I click the Get OTP button
    Then I should see a validation error message

  @EmptyField
  Scenario: Attempt to get OTP with empty email field
    When I leave the email address field empty
    And I click the Get OTP button
    Then I should see "Email or Mobile number is required" error message

  @EdgeCase
  Scenario: Enter special characters in email field
    When I enter "@#$%^&*()" in the email address field
    And I click the Get OTP button
    Then I should see appropriate validation feedback