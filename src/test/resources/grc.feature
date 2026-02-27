Feature: GRC Upload Document Navigation
  As a user of the GRC system
  I want to be able to navigate to the upload document page
  So that I can upload required documents for ongoing services

  Background:
    Given I am on the GRC dashboard page

  @smoke @positive
  Scenario: Successfully navigate to upload document page via Upload CTA
    When I click on the Upload button from ongoing services
    Then I should be redirected to the upload document page
    And the page title should contain "Upload"
    And I should see the document upload interface

  @negative
  Scenario: Verify Upload button is not available when no ongoing services exist
    Given there are no ongoing services available
    When I look for the Upload button
    Then the Upload button should not be visible
    And I should see a message indicating no ongoing services

  @edge-case
  Scenario: Navigate to upload page and verify back navigation works
    When I click on the Upload button from ongoing services
    And I should be redirected to the upload document page
    When I navigate back to the previous page
    Then I should be back on the GRC dashboard page
    And the Upload button should still be visible