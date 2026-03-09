@compatibility
Feature: Verify Browser Compatibility for Zolvit 360

  As a QA engineer
  I want to verify that Zolvit 360 works across different browsers
  So that users have a consistent experience regardless of their browser choice

  @TC-Z60-099
  Scenario Outline: Access Zolvit 360 in <browser> browser
    Given the user launches the "<browser>" browser
    When the user navigates to the Zolvit 360 application
    Then the Zolvit 360 should be accessible in the "<browser>" browser

    Examples:
      | browser |
      | Chrome  |
      | Firefox |
      | Edge    |
      | Safari  |
