Feature: GRC Login Page Navigation and GST Registration Access
  As a GRC user
  I want to navigate through the Service Hub
  So that I can access GST Registration functionality

  Background:
    Given I am on the GRC login page

  @smoke @regression
  Scenario: Navigate to GST Registration through Service Hub
    When I click on the Service Hub tab
    And I wait for the Service Hub page to load
    And I click on the Licenses/Registrations tab
    And I click on GST Registration
    Then the GST side panel should be opened
    And I should see the GST Registration details

  @regression
  Scenario: Verify GST Registration panel accessibility
    When I navigate to Service Hub
    And I access Licenses/Registrations section
    And I select GST Registration option
    Then GST Registration side panel should be displayed
    And the panel should contain relevant GST information

  @smoke
  Scenario: Complete navigation flow validation
    When I perform the complete navigation flow:
      | step | action |
      | 1    | Click Service Hub tab |
      | 2    | Wait for page load |
      | 3    | Click Licenses/Registrations |
      | 4    | Click GST Registration |
    Then all navigation steps should be successful
    And the GST Registration panel should be accessible