Feature: GRC Login Page Validation
  As a user
  I want to access the GRC login page
  So that I can authenticate and access the application

  Background:
    Given I navigate to the GRC login page

  @smoke @positive
  Scenario: Successfully enter mobile number and click Get OTP
    When I enter "8148438075" in the email address field
    Then the email address field should contain "8148438075"
    When I click on the Get OTP button
    Then the Get OTP button should be clickable

  @negative
  Scenario: Attempt to click Get OTP with empty email field
    When I leave the email address field empty
    And I click on the Get OTP button
    Then I should see an error message "Email or Mobile number is required"

  @edge @validation
  Scenario: Enter invalid email format and verify validation
    When I enter "invalid-email" in the email address field
    And I click on the Get OTP button
    Then the email address field should show validation styling
    And I should see an error message "Email or Mobile number is required"

  @ui @verification
  Scenario: Verify login page elements are displayed
    Then the email address field should be visible
    And the Get OTP button should be visible
    And the "Login with Password" link should be visible
    And the page title should contain "Log into your account"