Feature: Report an issue – components and functionalities

  As a GRC user
  I want to report issues related to different modules
  So that the support team can review and resolve them quickly

  @TC-Z60-080 @TC-Z60-081
  Scenario: Modules dropdown lists expected modules and displays selected value
    Given the user is on the Report an issue page
    When the user opens the Modules dropdown
    Then the user should see the following options in the Modules dropdown:
      | Home          |
      | Compliances   |
      | Billing       |
      | Consult       |
      | Calendar      |
      | Documents     |
      | Reports       |
      | Service Hub   |
      | Users & Roles |
      | Other         |



    When the user selects "Home" from the Modules dropdown
    Then the selected module should be "Home"
    And the user enters random feedback into the feedback field
    Then the Send button should be enabled

    When the user selects "Compliances" from the Modules dropdown
    Then the selected module should be "Compliances"
    And the user enters random feedback into the feedback field
    Then the Send button should be enabled

    When the user selects "Billing" from the Modules dropdown
    Then the selected module should be "Billing"
    And the user enters random feedback into the feedback field
    Then the Send button should be enabled

    When the user selects "Consult" from the Modules dropdown
    Then the selected module should be "Consult"
    And the user enters random feedback into the feedback field
    Then the Send button should be enabled

    When the user selects "Calendar" from the Modules dropdown
    Then the selected module should be "Calendar"
    And the user enters random feedback into the feedback field
    Then the Send button should be enabled

    When the user selects "Documents" from the Modules dropdown
    Then the selected module should be "Documents"
    And the user enters random feedback into the feedback field
    Then the Send button should be enabled

    When the user selects "Reports" from the Modules dropdown
    Then the selected module should be "Reports"
    And the user enters random feedback into the feedback field
    Then the Send button should be enabled

    When the user selects "Service Hub" from the Modules dropdown
    Then the selected module should be "Service Hub"
    And the user enters random feedback into the feedback field
    Then the Send button should be enabled

    When the user selects "Users & Roles" from the Modules dropdown
    Then the selected module should be "Users & Roles"
    And the user enters random feedback into the feedback field
    Then the Send button should be enabled

    When the user selects "Other" from the Modules dropdown
    Then the selected module should be "Other"
    And the user enters random feedback into the feedback field
    Then the Send button should be enabled
    When the user clicks the Send button
    Then the user should see a confirmation message "We've received your issue and will respond within 4 working hours."
    And the user closes the confirmation popup


  @TC-Z60-082
  Scenario: Verify and validate the support email hyperlink
    Given the user is on the Report an issue page
    Then the user should see the hyperlink "support@vakilsearch.com"
    When the user clicks on the "support@vakilsearch.com" hyperlink
    Then the default Mail application should open with "support@vakilsearch.com" in the "To" field
