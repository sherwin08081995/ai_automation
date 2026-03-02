Feature: GRC OTP Entry Validation
  As a user
  I want to enter OTP codes in the verification fields
  So that I can complete the authentication process

  Background:
    Given I am on the GRC OTP verification page

  @smoke @positive
  Scenario: Successfully enter complete OTP code 000000
    When I enter "000000" in the OTP input fields
    Then all OTP input fields should contain the correct digits
    And the OTP fields should be properly filled

  @positive @datadriven
  Scenario Outline: Enter different OTP codes in input fields
    When I enter "<otpCode>" in the OTP input fields
    Then all OTP input fields should contain the correct digits
    And each field should have exactly one digit
    
    Examples:
      | otpCode |
      | 000000  |
      | 123456  |
      | 999888  |

  @negative
  Scenario: Attempt to enter more than 6 digits
    When I enter "0000001" in the OTP input fields
    Then only the first 6 digits should be entered in the fields
    And the extra digit should be ignored

  @edge @validation
  Scenario: Enter non-numeric characters in OTP fields
    When I enter "abc123" in the OTP input fields
    Then only the numeric characters should be accepted
    And non-numeric characters should be filtered out

  @ui @verification
  Scenario: Verify OTP input fields are displayed correctly
    Then I should see 6 OTP input fields
    And each OTP field should be properly formatted
    And the mobile number "8148438075" should be displayed
    And the "Edit" button should be visible
    And the "Resend" link should be visible

  @functional
  Scenario: Clear OTP fields and re-enter
    When I enter "000000" in the OTP input fields
    And I clear all OTP fields
    Then all OTP input fields should be empty
    When I enter "111111" in the OTP input fields
    Then all OTP input fields should contain the new digits