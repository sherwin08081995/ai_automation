Feature: Compliance Page Functionality

  As a GRC user
  I want to view and manage compliance records
  So that I can track, filter, and act on all compliance activities

  @TC-Z60-037
  Scenario: Verify the presence of Stage sections on Compliance page
    Given the user is on the Compliance page
    Then the user validates that the following Stage sections are displayed:
      | All          |
      | Needs action |
      | In Progress  |
      | Completed    |
      | Upcoming     |


  @TC-Z60-038
  Scenario: Verify that the count of 'All' stage matches the combined total of other stages
    Given the user is on the Compliance page
    Then the user validates that the 'All' stage count equals the sum of Need Action, In Progress, Completed, and Upcoming stage counts

  @TC-Z60-039
  @TC-Z60-040
  @TC-Z60-041
  @TC-Z60-042
  @TC-Z60-043
  Scenario: Validate all stage sections[All, In progress, Completed, Needs action, Upcoming] and their records status-[pending, delayed, overdue, completed, not yet started, in progress]
    Given the user is on the Compliance page
    Then the user validates stage section tabs and their expected statuses


  @TC-Z60-044
  Scenario: Verify that all expected headers are displayed and that specific columns have sorting functionality
    Given the user is on the Compliance page
    Then the Compliance table should display the following headers:
      | Compliance  |
      | Office      |
      | Expert name |
      | Due Date    |
      | Stage       |
      | Status      |
    And the following headers should have sorting enabled:
      | Compliance |
      | Due Date   |
      | Stage      |
    Then the sorting arrows should be clickable for the following headers:
      | Compliance |
      | Due Date   |
      | Stage      |


  @TC-Z60-045 @TC-Z60-046
  Scenario: Verify Archive/Unarchive functionality
    Given the user is on the Compliance page
    Then the checkboxes for each record should be Visible
    And the select all checkbox should select all records on the current page and show the Archive button with count
    And capture the selected record names before archiving
    When the user clicks the Archive button
    Then the system should move the selected records to Archive and display them in Archive list
    When the user Unarchives the records
    Then the records should be moved back to the Original destination


  @TC-Z60-047
  Scenario: Verify that Generate CSV downloads complete and correct data
    Given the user is on the Compliance page
    When the user clicks the "Generate CSV" button and the CSV file is downloaded successfully
    And the CSV file should contain all the available records


  @TC-Z60-048
  Scenario: Verify the components and functionalities of an individual compliance
    Given the user is on the Compliance page
    When the user clicks any compliance record in the list
    Then the compliance details panel should open on the right side of the page
    And the user should see the following fields
      | field    |
      | Status   |
      | Due date |
      | Assignee |
      | Risk     |
    Then the user should be able to access Info, Tasks, and Audit trial tabs and see their content, and Opt with Zolvit action should be present



  @TC-Z60-048
  Scenario: Default to Info for compliances with no activity
    Given the user is on the Compliance page
    When the user clicks any compliance record in the list
    Then the default landing is the info "Info" tab in the active panel

  @TC-Z60-048
  Scenario: Default to Task for compliances with activity(In Progress)
    Given the user is on the Compliance page
    When the user click on In Progress to get active compliance
    And the user clicks any compliance record in the list
    Then the default landing is the task "Tasks" tab in the active panel





  @TC-Z60-032
  Scenario: Verify and print dropdown level filters
    Given the user is on the Compliance page
    Then I should see and print the dropdown filters and their options


  @TC-Z60-032
  Scenario Outline: Department + Category + Due Date -> mapped compliance names are present
    Given the user is on the Compliance page
    When I filter compliances by Department "<Department>", Category "<Category>", and Due Date "<DueDate>"
    Then the results grid reloads
    And the following compliance names should be present:
  """
  <ExpectedItems>
  """



    Examples:
      | Department            | Category | DueDate             | ExpectedItems                                |
      | HR/ Labour Compliance | PT       | This Financial Year | Professional Tax Filings                     |
      | HR/ Labour Compliance | EPF      | This Financial Year | Employees Provident Fund (PF) Return Filings |
      | HR/ Labour Compliance | ESI      | This Financial Year | Employee State Insurance (ESI) Filings       |
      | HR/ Labour Compliance | Payroll  | This Financial Year | Payroll Processing & Salary Disbursement     |
      | Direct Tax            | TDS      | This Financial Year | 24Q TDS Challan Payment; TDS Return Filing   |
      | Direct Tax            | ITR      | This Financial Year | ITR Filing for Companies                     |
      | Indirect Tax          | GSTR     | This Financial Year | GSTR                                         |


#  @noCategory
#  Scenario Outline: Department + Due Date (no category) -> only mapped items are listed
#    Given the user is on the Compliance page
#    When I filter compliances by Department "<Department>", and Due Date "<DueDate>"
#    Then the results grid reloads
#    And the following compliance names should be present:
#  """
#  <ExpectedItems>
#  """
#    Examples:
#      | Department            | DueDate             | ExpectedItems                                                                                                                                                                                                                                       |
##      | Accounts Department  | This Financial Year | Annual Maintenance of Books of Accounts                        |
##      | Legal Department     | This Financial Year | Annual Report of Consumer Grievances                           |
#      | Corporate Secretarial | This Financial Year | Preparation and Finalization of Board Meeting Minutes; LLP Annual Return Filing; DPT; Filing of Financial Statements (AOC-4); Filing of Annual Return (MGT-7A); Filing of Statement of Account & Solvency (Form 8) |





  Scenario: User creates a new compliance with valid randomized input
    Given the user is on the Compliance page
    When the user clicks the "Add new compliance" button
    Then the "Add new compliance" side panel should appear

    When the user enters a randomized valid compliance name
    And the user selects a randomized valid frequency
    And the user selects a randomized valid due date
    And the user selects a randomized valid risk
    And the user sets the "Mandatory" toggle per the randomized value
    And the user selects randomized valid organization
    And the user enters a randomized description
    And the user clicks Create button at the end

    And the user selects the entity associated with the created internal compliances
    And the user clears the Due Date filter
    And the user toggles the Internal filter on
    And print only the created internal compliance across all pages
    And the created internal compliance row should exist across all pages











