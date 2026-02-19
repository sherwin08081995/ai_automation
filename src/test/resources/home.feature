Feature: Home Page Functionality

  As a GRC user
  I want to access and interact with the Home page
  So that I can validate menus, compliance counts, organizations, reports, and widgets effectively

  @TC-Z60-002 @TC-Z60-098
  Scenario: Validate left side menu
    Given the user is on the Home page
    When the user views the left side menu
    Then the following menu items should be visible:
      | Home          |
      | Compliances   |
      | Consult       |
      | Calendar      |
      | Documents     |
      | Reports       |
      | Service Hub   |
      | Users & Roles |
    And the user should be able to access the following menu items:
      | Home          |
      | Compliances   |
      | Consult       |
      | Calendar      |
      | Documents     |
      | Reports       |
      | Service Hub   |
      | Users & Roles |

  @TC-Z60-003
  Scenario: Verify the Overall Compliances count
    Given the user is on the Home page
    When the user views the Overall Compliances section
    Then the Overall Compliances count should be displayed properly
    Then the Overall Compliances should be the sum of:
      | Tab          |
      | Needs action |
      | In progress  |
      | Completed    |
      | Upcoming     |

  @TC-Z60-004 @TC-Z60-005 @TC-Z60-006 @TC-Z60-007
  Scenario: Verify each Compliance tab navigates and displays correct record count
    Given the user is on the Home page
    Then the user clicks each Compliance status tab, navigates to the Compliance section, and validates that the counts match for each tab
      | Tab          | Section      |
      | Needs action | Needs Action |
      | In progress  | In progress  |
      | Completed    | Completed    |
      | Upcoming     | Upcoming     |

  @TC-Z60-009
  Scenario: Verify default Due Date selection after page refresh
    Given the user is on the Home page
    Then after refreshing the page, the Due Date filter should default to "This Month"

  @TC-Z60-008
  Scenario: Verify values listed under the Due Date dropdown
    Given the user is on the Home page
    When the user clicks on the Due Date filter dropdown
    Then the following options should be visible under the dropdown:
      | This Week               |
      | This Month              |
      | Previous Month          |
      | This Quarter            |
      | Previous Quarter        |
      | This Financial Year     |
      | Previous Financial Year |

  @TC-Z60-010 @TC-Z60-011
  Scenario: Verify Due Date filtering and navigation consistency
    Given the user is on the Home page
    When the user applies the following Due Date filters:
      | This Week               |
      | This Month              |
      | Previous Month          |
      | This Quarter            |
      | Previous Quarter        |
      | This Financial Year     |
      | Previous Financial Year |
    Then each Due Date selection should show consistent counts across sections
    And each Due Date selection should show matching count on Compliance page after View All navigation

  @TC-Z60-012
  Scenario: Verify Add Organization button placement
    Given the user is on the Home page
    Then the Add Organization button should be visible in the top right corner
    And the Add Organization button should be visible under the organization dropdown at the top

  @TC-Z60-013
  Scenario: Verify selected organization is reflected on the compliance page
    Given the user is on the Home page
    When the user selects each organization from Home and verifies it is reflected on the Compliance page


  @TC-Z60-014 @TC-Z60-098
  Scenario: User creates a new organization with valid randomized input
    Given the user is on the Home page
    When the user clicks the "Add organizations" button at right corner
    Then the create organization popup should appear

    When the user selects a business type
    And the user enters a company name
    And the user selects a business location
    And the user selects an employee count

    And the user selects an industry type
    And the user selects an annual turnover
    And clicks the "Get Started" button
    Then the mandatory compliances should be displayed
    Then the user clicks view compliance button
    Then the user acknowledges and closes the compliance popup if visible
    Then the dropdown should display the created organization name

  @TC-Z60-015
  Scenario: Verify that Talk to Lawyer and Talk to CA widgets are displayed
    Given the user is on the Home page
    Then the Talk to Lawyer widget should be visible
    And the Talk to CA widget should be visible


  @TC-Z60-020
  Scenario: Verify categories and print colors in the Risk Based Compliances section
    Given the user is on the Home page
    Then the following risk categories should be displayed:
      | High   |
      | Medium |
      | Low    |
    And the color for each risk category should be printed


  @TC-Z60-021
  Scenario: Validate that the total count of Risk Based Compliances is in sync with Overall Compliances
    Given the user is on the Home page
    When the user fetches the total counts of Risk Based Compliances and Overall Compliances
    Then both counts should match
    When the user fetches the counts of high, medium, and low risks under Risk Based Compliances
    Then the sum of high, medium, and low risk counts should match the total Risk Based Compliances count


  @TC-Z60-022
  Scenario: Validate that clicking each risk category (High, Medium, Low) navigates to the Compliance screen and confirms the count mapping
    Given the user is on the Home page
    When the user clicks each risk category one by one and verifies the count mapping on the Compliance screen
    When the user hovers over each risk category one by one and captures bifurcation popup screenshots

  @TC-Z60-025
  Scenario: Verify the components of Compliance Report
    Given the user is on the Home page
    When the user fetches the column headers from the Compliance Report section
    Then the user navigates to the Compliance page and validates that the same column headers appear

  @TC-Z60-026
  Scenario: Verify the View All Button Navigation and Compliance Count Sync
    Given the user is on the Home page
    When the user clicks the View all Compliance button
    Then the user should be navigated to the Compliances screen showing all sections
    And the compliance count should match the Overall compliance count

  @TC-Z60-027
  Scenario: Verify selecting any one of the records from Compliance report section
    When the user selects any one record from the Compliance report section
    Then the system should display detailed information of the selected compliance on the right side


  @TC-Z60-028 @TC-Z60-029 @TC-Z60-030 @TC-Z60-031
  Scenario: Verify Month and Year dropdown components and functionality under Compliance Calendar
    Given the user is on the Home page
    Then Month and Year dropdowns should be displayed
    And Month dropdown should display the current month
    And Year dropdown should display the current year
    And the record count under the Compliance Calendar should be in sync with the Overall Compliances
    When the user selects a different Month and Year from the dropdowns
    Then the due records should match the selected Month and Year, with correct count
    And a random Compliance Calendar record should open its detail panel with correct Due Date





