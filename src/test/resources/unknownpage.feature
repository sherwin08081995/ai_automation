Feature: Unknown Page OTP Flow Validation
  As a user
  I want to enter my phone number and receive OTP
  So that I can verify my identity

  Background:
    Given I am on the unknown page

  Scenario: Successfully enter phone number and navigate to OTP page
    When I enter "8148438075" in the email address section
    And I click the Get OTP CTA
    Then I should be redirected to the OTP page
    When I enter "000000" in the OTP input boxes
    Then the OTP should be entered successfully

  Scenario: Attempt to get OTP with invalid phone number
    When I enter "invalid123" in the email address section
    And I click the Get OTP CTA
    Then I should see an error message for invalid phone number

  Scenario: Enter OTP with empty fields
    When I enter "8148438075" in the email address section
    And I click the Get OTP CTA
    Then I should be redirected to the OTP page
    When I leave the OTP input boxes empty
    Then I should see a validation message for empty OTP