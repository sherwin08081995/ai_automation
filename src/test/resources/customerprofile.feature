Feature: Customer Profile panel navigation & accessibility

  As a GRC user
  I want to access and navigate the Customer Profile panel
  So that I can view my account details and use profile-related options

  @TC-Z60-084 @TC-Z60-096 @TC-Z60-097 @TC-Z60-098 @TC-Z60-101
  Scenario: Verify all components displayed inside the Customer Profile panel
    When the user clicks the "Profile" icon
    Then the Customer Profile panel should open
    And the following identity details should be displayed:
      | Name          |
      | Mobile Number |
      | Email Address |
    Then the identity details should match the login credentials
    And the following menu items should be visible in order:
      | My Services      |
      | My Subscriptions |
      | My Business      |
      | My Quotations    |
      | Help             |
      | FAQs             |
      | Log out          |

  Scenario: Accessing Customer Profile panel options
    When the user clicks the "Profile" icon
    Then the Customer Profile panel should open
    When the user selects "My Services" from the Customer profile panel
    Then the user should redirect to "My Services" page

    When the user clicks the "Profile" icon
    Then the Customer Profile panel should open
    When the user selects "My Subscriptions" from the Customer profile panel
    Then the user should redirect to "My Subscriptions" page

    When the user clicks the "Profile" icon
    Then the Customer Profile panel should open
    When the user selects "My Business" from the Customer profile panel
    Then the user should redirect to "My Business" page

    When the user clicks the "Profile" icon
    Then the Customer Profile panel should open
    When the user selects "My Quotations" from the Customer profile panel
    Then the user should redirect to "My Quotations" page

    When the user clicks the "Profile" icon
    Then the Customer Profile panel should open
    When the user selects "Help" from the Customer profile panel
    Then the user should redirect to "Help" page

    When the user clicks the "Profile" icon
    Then the Customer Profile panel should open
    When the user selects "FAQs" from the Customer profile panel
    Then the user should redirect to "FAQs" page

    When the user clicks the "Profile" icon
    Then the Customer Profile panel should open
    When the user selects "View Profile" from the Customer profile panel
    Then the user should redirect to "My Profile" page

    When the user clicks the "Profile" icon
    Then the Customer Profile panel should open
    When the user selects "Log out" from the Customer profile panel
    Then the application should logout and redirect to "Log into your account" page

