Feature: GRC Login Page Validation
  As a user of the GRC application
  I want to be able to log in to my account
  So that I can access the GRC dashboard

  Background:
    Given I am on the GRC login page

  @smoke @regression
  Scenario: Successfully enter valid mobile number and click Get OTP
    When I enter "8148438075" in the email address field
    And I click the Get OTP button
    Then the Get OTP button should be clickable
    And the mobile number field should contain "8148438075"

  @negative
  Scenario: Attempt to click Get OTP without entering any information
    When I click the Get OTP button
    Then I should see an error message "Email or Mobile number is required"
    And the email address field should be highlighted with red border

  @edge-case
  Scenario: Enter invalid mobile number format
    When I enter "invalid123" in the email address field
    And I click the Get OTP button
    Then the system should display validation error
    And the Get OTP button should remain enabled

  @regression
  Scenario: Navigate to Sign Up page
    When I click the "Sign Up" link
    Then I should be redirected to the signup page

  @functional
  Scenario: Switch to Login with Password option
    When I click the "Login with Password" button
    Then the login form should switch to password mode