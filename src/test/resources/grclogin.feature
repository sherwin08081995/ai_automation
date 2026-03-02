Feature: GRC Login Functionality
  As a user
  I want to login to the GRC system
  So that I can access the application

  Scenario: Successful login with valid phone number and OTP
    Given I am on the GRC login page
    When I enter "8148438075" in the email address field
    And I click the Get OTP button
    Then I should be redirected to the OTP page
    When I enter "000000" in the OTP input boxes
    And I submit the OTP
    Then I should be successfully logged in

  Scenario: Login attempt with invalid phone number
    Given I am on the GRC login page
    When I enter "123" in the email address field
    And I click the Get OTP button
    Then I should see an error message for invalid phone number

  Scenario: Login attempt with incorrect OTP
    Given I am on the GRC login page
    When I enter "8148438075" in the email address field
    And I click the Get OTP button
    Then I should be redirected to the OTP page
    When I enter "111111" in the OTP input boxes
    And I submit the OTP
    Then I should see an error message for invalid OTP