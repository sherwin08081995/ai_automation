Feature: Verify Reports Page Functionality

  As a user of Zolvit GRC
  I want to view and interact with different report categories
  So that I can verify the reports grouped by Risk, Stage, Organization, and Department

  @TC-Z60-074
  Scenario: Verify landing page components of Reports
    Given the user is on the Reports page
    Then the Reports page should display the following categories:
      | Category Name   |
      | Frequently Used |
      | By Risk         |
      | By Stage        |
      | By Organization |
      | By Department   |

  @FrequentlyUsed
  Scenario: Verify the Frequently Used category
    Given the user is on the Reports page
    When the user views the "Frequently Used" section
    Then the system should display the recently accessed reports
    And the user validates each Frequently Used report redirects correctly and can go back

  @ByRisk
  Scenario: Verify the By Risk category
    Given the user is on the Reports page
    Then the "By Risk" section should list the following risk levels:
      | Risk Level |
      | High       |
      | Medium     |
      | Low        |
    When the user validates each of the listed risk levels shows compliance results

  @ByStage
  Scenario: Verify the By Stage category
    Given the user is on the Reports page
    Then the "By Stage" section should display a list of available stages
    And each stage name should be visible and clickable
    When the user validates each of the available stages shows compliance results

  @ByOrganization
  Scenario: Verify the By Organization category
    Given the user is on the Reports page
    Then the "By Organization" section should display a list of created organizations
    And each organization name should be visible and clickable
    When the user validates each of the available organizations shows compliance results


  @ByDepartment
  Scenario: Verify the By Department category
    Given the user is on the Reports page
    Then the "By Department" section should display a list of departments
    And each department name should be visible and clickable
    When the user validates each of the available departments shows compliance results