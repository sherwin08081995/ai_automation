Feature: Login Functionality

  Scenario Outline: Login with valid user credentials and OTP
    Given the user is on the Login page
    When the user enters "<email>" and click send OTP button
    And the user enters valid "<otp>"
    Then the user should be redirected to the homepage

    Examples:
      | email                  | otp    |
      | demo3434343@gmail.com  | 000000 |
