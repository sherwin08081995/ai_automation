Feature:Login Page Functionality

  As a GRC user
  I want to log in using valid credentials and OTP
  So that I can securely access the Vakilsearch application

  @TC-Z60-001 @TC-Z60-098
  Scenario Outline: Login with valid user credentials and OTP
    Given the user is on the Login page
    When the user enters "<mobNumber>" and click send OTP button
    And the user enters valid "<otp>"
    Then the user selects email and is redirected to the "<Vakilsearch>" homepage

    Examples:
      | mobNumber  | otp    | Vakilsearch |
      | 8148438075 | 000000 | Vakilsearch |

