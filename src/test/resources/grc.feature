Feature: GRC Login Page Validation
  As a user of the GRC system
  I want to log in with my mobile number
  So that I can access my account

  Background:
    Given I navigate to the GRC login page

  @smoke @regression
  Scenario: Valid mobile number login request
    When I enter "8148438075" in the email address field
    And I click the Get OTP button
    Then I should see the OTP request is processed

  @negative
  Scenario: Empty email address field validation
    When I leave the email address field empty
    And I click the Get OTP button
    Then I should see the error message "Email or Mobile number is required"

  @negative
  Scenario: Invalid email format validation
    When I enter "invalid-email-format" in the email address field
    And I click the Get OTP button
    Then I should see appropriate validation feedback
    
  @UI
  Scenario: Login page elements visibility
    Then I should see the email address input field
    And I should see the Get OTP button
    And I should see the "Login with Password" link
    And I should see the "Sign Up" link in the header

  @boundary
  Scenario: Very long mobile number input
    When I enter "81484380751234567890" in the email address field
    And I click the Get OTP button
    Then I should see appropriate validation for invalid mobile number format