Feature: OTP Verification Page Validation
  As a user
  I want to enter OTP on the verification page
  So that I can complete the authentication process

  Background:
    Given I navigate to the OTP verification page

  @OTPVerification @positive
  Scenario: Successfully enter OTP digits and verify
    When I enter "0" in the first OTP input box
    And I enter "0" in the second OTP input box
    And I enter "0" in the third OTP input box
    And I enter "0" in the fourth OTP input box
    And I enter "0" in the fifth OTP input box
    And I enter "0" in the sixth OTP input box
    Then all OTP input boxes should contain the entered digits
    And the verify button should be enabled

  @OTPVerification @negative
  Scenario: Attempt to verify with incomplete OTP
    When I enter "1" in the first OTP input box
    And I enter "2" in the second OTP input box
    And I enter "3" in the third OTP input box
    And I leave the remaining OTP boxes empty
    Then the verify button should be disabled
    And I should see validation message for incomplete OTP

  @OTPVerification @edge
  Scenario Outline: Enter different OTP combinations
    When I enter the OTP digits "<otp>"
    Then the OTP should be entered correctly
    And the verify button status should be "<buttonStatus>"

    Examples:
      | otp    | buttonStatus |
      | 000000 | enabled      |
      | 123456 | enabled      |
      | 12345  | disabled     |

  @OTPVerification @ui
  Scenario: Verify OTP page elements are displayed
    Then all six OTP input boxes should be visible
    And the verify OTP button should be visible
    And the resend OTP link should be visible
    And the page should display OTP instruction message