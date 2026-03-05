Feature: GRC Service Hub - GST Registration Navigation
  As a user of the GRC Service Hub
  I want to navigate to GST Registration services
  So that I can avail GST registration services

  @smoke @regression
  Scenario: Navigate to GST Registration and avail service
    Given I am on the GRC Service Hub page
    When I click on the "Licenses/Registrations" tab
    And I click on the "GST Registration" tab
    And I wait for the side panel to open
    And I click on the "Avail Service" CTA button
    Then I should be redirected to the external website
    And the page should load successfully

  @regression
  Scenario: Verify GST Registration side panel content
    Given I am on the GRC Service Hub page
    When I click on the "Licenses/Registrations" tab
    And I click on the "GST Registration" tab
    And I wait for the side panel to open
    Then the GST Registration side panel should be displayed
    And the "Avail Service" button should be visible
    And the side panel should contain relevant GST information

  @smoke
  Scenario: Verify navigation flow without external redirect
    Given I am on the GRC Service Hub page
    When I click on the "Licenses/Registrations" tab
    Then the Licenses/Registrations section should be displayed
    When I click on the "GST Registration" tab
    Then the GST Registration tab should be active
    And the side panel should start opening