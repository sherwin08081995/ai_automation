Feature: GRC Login Authentication Flow
  As a user of the GRC system
  I want to authenticate using phone number and OTP
  So that I can access the GRC Home page

  Background:
    Given I am on the GRC login page

  @smoke
  Scenario: Successful login with valid credentials and email selection
    When I enter "8148438075" in the email address field
    And I click the Get OTP button
    Then I should be redirected to the OTP verification page
    When I enter "000000" in the OTP input boxes
    Then a popup should appear with available email addresses
    When I select "sherwinzolvit360@yopmail.com" from the popup
    Then I should be redirected to the GRC Home page
    And I should confirm successful login to Home page

  @negative
  Scenario: Login attempt with invalid phone number
    When I enter "1234567890" in the email address field
    And I click the Get OTP button
    Then I should see an error message for invalid phone number

  @edge
  Scenario: Login attempt with invalid OTP
    When I enter "8148438075" in the email address field
    And I click the Get OTP button
    Then I should be redirected to the OTP verification page
    When I enter "123456" in the OTP input boxes
    Then I should see an error message for invalid OTP