#Feature: User Registration, Onboarding, and Compliance Workflow in Zolvit GRC Portal
#  As a newly onboarded business user on the Zolvit GRC platform
#  I want to seamlessly register, complete onboarding, and explore service offerings
#  So that I can start managing my compliance needs effectively


#  @noAutoLogin
#  Scenario: End-to-end verification of user signup, login, onboarding, and dashboard actions
#    Given the user is currently on the Login page
#    When the user clicks on the "Sign Up" link on the login page
#    Then the user should redirect to signup page
#    And the Sign Up form should contain fields:
#      | Full name        |
#      | Email address    |
#      | Phone number     |
#      | Password         |
#      | Confirm password |
#    And the "Sign Up" button should be enabled by default
#    And the user enters random valid full name in Full name field
#    And the user enters random valid email in Email address field
#    And the user enters random valid phone number in Phone number field
#    And the user enters random valid password in Password field
#    And the user enters the same password in Confirm password field
#    And the user clicks on the "Sign Up" button
#    Then the account should be created successfully
#    And the user should be redirected to the login page
#    And the Login page header should be "Log into your account"
#    When the user clicks on the Login with Password link on the login page
#    And the user enters the registered email in Login email field
#    And the user enters the registered password in Login password field
#    And the user clicks on the Log In button
#    Then the user should be logged in successfully
#    And the complete profile popup should be visible with correct content
#    When the user clicks on the complete profile popup CTA
#    Then the welcome usage selection page should be displayed
#    When the user clicks on skip to dashboard on the welcome page
#    Then the dashboard home with festive offer popup should be displayed
#    And the festive offer popup should show "Explore Service Hub" CTA
#    When the user clicks on the "Explore Service Hub" CTA on the festive popup
#    Then the Service Hub page should be displayed with URL containing "/grc/marketplace"
#    Then the user navigate back to Home page
#    When the user clicks on the "Start Your Business" CTA on the dashboard
#    Then the Company Registration page should load successfully
#    Then the user navigates back to the previous page which is home page
#    When the user clicks on the Add Business link on the dashboard
#    Then the welcome usage selection page should be displayed
#    When the user clicks on skip to dashboard on the welcome page
#
#
#  @noAutoLogin
#  Scenario: Verify CTA's, icons and flow for Business Needs from the sign-up flow
#
#    Given the user is currently on the Login page
#    When the user clicks on the "Sign Up" link on the login page
#    Then the user should redirect to signup page
#    And the user enters random valid full name in Full name field
#    And the user enters random valid email in Email address field
#    And the user enters random valid phone number in Phone number field
#    And the user enters random valid password in Password field
#    And the user enters the same password in Confirm password field
#    And the user clicks on the "Sign Up" button
#    Then the account should be created successfully
#    And the user should be redirected to the login page
#    And the Login page header should be "Log into your account"
#    When the user clicks on the Login with Password link on the login page
#    And the user enters the registered email in Login email field
#    And the user enters the registered password in Login password field
#    And the user clicks on the Log In button
#    Then the user should be logged in successfully
#    And the complete profile popup should be visible with correct content
#    When the user clicks on the complete profile popup CTA
#    Then the welcome usage selection page should be displayed
#    When the user clicks on skip to dashboard on the welcome page
#    Then the dashboard home with festive offer popup should be displayed
#    And the festive offer popup should show "Explore Service Hub" CTA
#    When the user clicks on the "Explore Service Hub" CTA on the festive popup
#    Then the Service Hub page should be displayed with URL containing "/grc/marketplace"
#    Then the user navigate back to Home page
#    When the user clicks on the Get Started widget on the dashboard
#    Then the Get Started widget should open with correct header content
#    When the user clicks Continue on the Get Started widget
#    Then the welcome usage selection page should be displayed
#    When the user selects Business needs usage and clicks Next on the welcome page
#    Then the Full Name step should be displayed and the user enters a random full name and clicks Next
#    Then the Designation step should be displayed and the user selects a designation from the dropdown
#    Then the Entity Type step should be displayed and the user selects a random entity type and clicks Next
#    Then the CIN step should be displayed and the user clicks Skip and Next without entering CIN
#    Then the Company Details step should be displayed and the user fills all details and clicks Next
#    Then the Team Size step should be displayed and the user selects any Team Size and clicks Next
#    Then the Annual Turnover step should be displayed and the user selects any turnover and clicks Get Started
#    Then the compliance analysis screen should be displayed with compliances count and user clicks View my Compliances
#    Then the "Stay on Top of Your Compliances" popup should be visible
#    And the user clicks "Next" on the product tour
#    Then the "Upload Documents with Ease" popup should be visible
#    And the user clicks "Next" on the product tour
#    Then the "Your Legal Documents Hub" popup should be visible
#    Then the final step of product tour should be visible
#    And the user clicks "Got it" to finish the tour
#    Then the new subscriber offer popup should be visible and user explores Annual Compliance plans
#    Then user clicks Explore Plans and verifies pricing page is loaded
#    Then the exit popup should appear with message "Wait! Before you go"
#    Then the user randomly selects an exit reason and closes the thanks popup
#    When User clicks on Notification icon
#    Then Notification popup should be visible
#    When User clicks on Cart icon
#    Then Cart popup should be visible
#    When the user clicks Report an issue option
#    When the user clicks the "Profile" icon
#    Then the Customer Profile panel should open
#    When the user selects "My Subscriptions" from the Customer profile panel
#    Then the user should redirect to Zero state Subscription page
#    Then the user click Explore Plans & Claim Offer CTA
#    Then the Pricing page should be displayed after clicking the Explore Plans CTA
#    When the user navigates to the previous page
#    Then the user should redirect to Zero state Subscription page
#
##
#  @noAutoLogin
#  Scenario: Verify that the Onboarding Compliance Analysis screen count matches the Compliances Dashboard count (Business Needs)
#
#    Given the user is currently on the Login page
#    When the user clicks on the "Sign Up" link on the login page
#    Then the user should redirect to signup page
#    And the user enters random valid full name in Full name field
#    And the user enters random valid email in Email address field
#    And the user enters random valid phone number in Phone number field
#    And the user enters random valid password in Password field
#    And the user enters the same password in Confirm password field
#    And the user clicks on the "Sign Up" button
#    Then the account should be created successfully
#    And the user should be redirected to the login page
#    And the Login page header should be "Log into your account"
#    When the user clicks on the Login with Password link on the login page
#    And the user enters the registered email in Login email field
#    And the user enters the registered password in Login password field
#    And the user clicks on the Log In button
#    Then the user should be logged in successfully
#    And the complete profile popup should be visible with correct content
#    When the user clicks on the complete profile popup CTA
#    Then the welcome usage selection page should be displayed
#    When the user clicks on skip to dashboard on the welcome page
#    Then the dashboard home with festive offer popup should be displayed
#    And the festive offer popup should show "Explore Service Hub" CTA
#    When the user clicks on the "Explore Service Hub" CTA on the festive popup
#    Then the Service Hub page should be displayed with URL containing "/grc/marketplace"
#    Then the user navigate back to Home page
#    When the user clicks on the Get Started widget on the dashboard
#    Then the Get Started widget should open with correct header content
#    When the user clicks Continue on the Get Started widget
#    Then the welcome usage selection page should be displayed
#    When the user selects Business needs usage and clicks Next on the welcome page
#    Then the Full Name step should be displayed and the user enters a random full name and clicks Next
#    Then the Designation step should be displayed and the user selects a designation from the dropdown
#    Then the Entity Type step should be displayed and the user selects a random entity type and clicks Next
#    Then the CIN step should be displayed and the user clicks Skip and Next without entering CIN
#    Then the Company Details step should be displayed and the user fills all details and clicks Next
#    Then the Team Size step should be displayed and the user selects any Team Size and clicks Next
#    Then the Annual Turnover step should be displayed and the user selects any turnover and clicks Get Started
#    Then the compliance analysis screen should be displayed with compliances count and user clicks View my Compliances
#    Then the "Stay on Top of Your Compliances" popup should be visible
#    And the user clicks "Next" on the product tour
#    Then the "Upload Documents with Ease" popup should be visible
#    And the user clicks "Next" on the product tour
#    Then the "Your Legal Documents Hub" popup should be visible
#    Then the final step of product tour should be visible
#    And the user clicks "Got it" to finish the tour
#    Then the new subscriber offer popup should be visible and user explores Annual Compliance plans
#    Then user clicks Explore Plans and verifies pricing page is loaded
#    Then the exit popup should appear with message "Wait! Before you go"
#    Then the user randomly selects an exit reason and closes the thanks popup
#    Then the user navigates back to the previous page
#    Then the compliances list All count should match analysis count and Due Date should be This Financial Year






#  @noAutoLogin
#  Scenario: Validate GetStarted Widget for complete profile
#
#    Given the user is currently on the Login page
#    When the user clicks on the "Sign Up" link on the login page
#    Then the user should redirect to signup page
#    And the user enters random valid full name in Full name field
#    And the user enters random valid email in Email address field
#    And the user enters random valid phone number in Phone number field
#    And the user enters random valid password in Password field
#    And the user enters the same password in Confirm password field
#    And the user clicks on the "Sign Up" button
#    Then the account should be created successfully
#    And the user should be redirected to the login page
#    And the Login page header should be "Log into your account"
#    When the user clicks on the Login with Password link on the login page
#    And the user enters the registered email in Login email field
#    And the user enters the registered password in Login password field
#    And the user clicks on the Log In button
#    Then the user should be logged in successfully
#    And the complete profile popup should be visible with correct content
#    When the user clicks on the complete profile popup CTA
#    Then the welcome usage selection page should be displayed
#    When the user selects Business needs usage and clicks Next on the welcome page
#    Then the Full Name step should be displayed and the user enters a random full name and clicks Next
#    Then the Designation step should be displayed and the user selects a designation from the dropdown
#    Then the Entity Type step should be displayed and the user selects a random entity type and clicks Next
#    Then the CIN step should be displayed and the user clicks Skip and Next without entering CIN
#    Then the Company Details step should be displayed and the user fills all details and clicks Next
#    Then the Team Size step should be displayed and the user selects any Team Size and clicks Next
#    Then the Annual Turnover step should be displayed and the user selects any turnover and clicks Get Started
#    Then the compliance analysis screen should be displayed with compliances count and user clicks View my Compliances
#    Then the "Stay on Top of Your Compliances" popup should be visible
#    And the user clicks "Next" on the product tour
#    Then the "Upload Documents with Ease" popup should be visible
#    And the user clicks "Next" on the product tour
#    Then the "Your Legal Documents Hub" popup should be visible
#    Then the final step of product tour should be visible
#    And the user clicks "Got it" to finish the tour
#    Then the new subscriber offer popup should be visible and user explores Annual Compliance plans
#    Then user clicks Explore Plans and verifies pricing page is loaded
#
#    Then the exit popup should appear with message "Wait! Before you go"
#    Then the user randomly selects an exit reason and closes the thanks popup
#
#    Then the user navigate back to previous page which is Compliance page
#    When the user clicks on the Get Started widget on the dashboard










