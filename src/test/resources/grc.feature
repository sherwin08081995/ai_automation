Feature: GRC Login page functionality
  As a user
  I want to access the GRC login page
  So that I can log into my account

  Background:
    Given I navigate to the GRC login page

  @smoke @positive
  Scenario: Successfully enter mobile number and click Get OTP
    When I enter "8148438075" in the email address field
    And I click the Get OTP button
    Then I should see the OTP request is processed

  @negative
  Scenario: Submit empty login form
    When I click the Get OTP button without entering any details
    Then I should see a validation message "Email or Mobile number is required"

  @boundary
  Scenario: Enter invalid mobile number format
    When I enter "abc123" in the email address field
    And I click the Get OTP button
    Then I should see appropriate validation for invalid input

  @positive
  Scenario: Navigate to password login option
    When I click the "Login with Password" button
    Then I should see password login options

  @navigation
  Scenario: Navigate to sign up page
    When I click the "Sign Up" link
    Then I should be redirected to the sign up page