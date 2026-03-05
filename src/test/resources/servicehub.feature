Feature: Service Hub GST Registration Access
  As a user of the application
  I want to navigate to GST Registration through Service Hub
  So that I can access the GST registration functionality

  Background:
    Given the user is logged into the application

  Scenario: Navigate to GST Registration through Service Hub
    When the user opens the Service Hub tab from the left side menu
    And the Service Hub page loads successfully
    And the user clicks on the "Licenses/Registration" tab
    And the Licenses/Registration page loads successfully
    And the user clicks on the "GST Registration" option
    Then the GST Registration panel should open successfully
    And the GST Registration form should be displayed

  Scenario: Verify Service Hub navigation flow
    When the user opens the Service Hub tab from the left side menu
    Then the Service Hub page should be accessible
    And the "Licenses/Registration" tab should be visible
    When the user clicks on the "Licenses/Registration" tab
    Then the "GST Registration" option should be available

  Scenario: Validate GST Registration panel functionality
    Given the user has navigated to Service Hub
    And the user has accessed the Licenses/Registration section
    When the user selects the GST Registration option
    Then the GST Registration panel should display properly
    And all required form fields should be present