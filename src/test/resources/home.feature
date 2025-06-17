Feature: Home Page Functionality

  Scenario: Validate left side menu
    When the user views the left side menu
    Then the following menu items should be visible:
      | Home        |
      | Compliances |
      | Consult     |
      | Calendar    |
      | Documents   |
      | Reports     |
      | Marketplace |
      | Settings    |
    And the user should be able to access the following menu items:
      | Home        |
      | Compliances |
      | Consult     |
      | Calendar    |
      | Documents   |
      | Reports     |
      | Marketplace |
      | Settings    |

  Scenario: Verify the Overall Compliances count
    When the user views the Overall Compliances section
    Then the Overall Compliances count should be displayed properly
    Then the Overall Compliances should be the sum of:
      | Tab          |
      | Needs action |
      | In progress  |
      | Compliant    |
      | Upcoming     |

  Scenario: Verify each Compliance tab navigates and displays correct record count
    Then the user clicks each Compliance status tab, navigates to the Compliance section, and validates that the counts match for each tab
      | Tab          | Section      |
      | Needs action | Needs Action |
      | In progress  | In progress  |
      | Compliant    | Completed    |
      | Upcoming     | Upcoming     |


