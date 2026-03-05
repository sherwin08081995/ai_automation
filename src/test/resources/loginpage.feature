@smoke @regression
Feature: GRC Home Page Compliance Count Validation
  As a GRC user
  I want to verify compliance counts on the home page
  So that I can ensure data consistency between Overall and Risk Based compliances

  Background:
    Given I navigate to the GRC home page
    And I wait for the page to load completely

  @compliance-count-validation
  Scenario: Validate Overall Compliances count matches Risk Based Compliances count
    When I fetch the Overall Compliances count
    And I fetch the Risk Based Compliances count
    Then the Overall Compliances count should match the Risk Based Compliances count

  @compliance-count-mismatch
  Scenario: Handle compliance count mismatch scenario
    When I fetch the Overall Compliances count
    And I fetch the Risk Based Compliances count
    And the counts do not match
    Then the test should fail with appropriate error message
    And I should capture the actual count values for debugging

  @compliance-data-integrity
  Scenario: Verify compliance data is loaded and displayed
    Then the Overall Compliances section should be visible
    And the Risk Based Compliances section should be visible
    And both compliance counts should be numeric values
    And both compliance counts should be greater than zero