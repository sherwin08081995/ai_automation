Feature: GRC Login Page Navigation
  As a user of the GRC system
  I want to navigate through the service hub and access GST registration
  So that I can manage my licenses and registrations

  Background:
    Given I am on the GRC login page

  @smoke @navigation
  Scenario: Navigate to GST Registration through Service Hub
    When I click the Service Hub tab
    And I wait for the Service Hub page to load
    And I click the Licenses/registrations tab
    And I click GST Registration
    Then the GST side panel should be displayed
    And I should see the GST registration options

  @regression
  Scenario: Verify Service Hub page loads correctly
    When I click the Service Hub tab
    Then the Service Hub page should load successfully
    And the Licenses/registrations tab should be visible

  @regression
  Scenario: Verify GST Registration panel functionality
    Given I have navigated to the Service Hub
    And I have clicked the Licenses/registrations tab
    When I click GST Registration
    Then the GST side panel should open
    And the panel should contain GST registration information