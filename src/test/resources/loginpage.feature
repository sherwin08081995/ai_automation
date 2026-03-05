Feature: LoginPage Service Hub Navigation
  As a user
  I want to navigate to GST registration service
  So that I can access GST registration functionality

  Scenario: Navigate to GST Registration through Service Hub
    Given I am on the login page
    When I click on the service hub tab
    Then I should be redirected to the service hub page
    When I click on the licenses/registrations option
    And I wait for the page to load
    When I click on GST registration
    Then the GST registration side panel should open
    And the panel should contain "Avail service" CTA button