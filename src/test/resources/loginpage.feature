Feature: GRC Service Hub - GST Registration Service

  As a user of the GRC Service Hub
  I want to access GST Registration services
  So that I can register for GST compliance

  Scenario: Navigate to GST Registration and avail service
    Given I am on the GRC Service Hub page
    When I click on the "Licenses/Registrations" tab
    And I click on the "GST Registration" tab
    And I wait for the side panel to open
    And I click on the "Avail Service" CTA button
    Then I should see a successful toast message