Feature: Product Tour Walkthrough
  As a logged-in user
  I want to go through the product tour
  So that I can see all the guided steps in sequence


  Scenario: User navigates through the product tour using Next button
    Given the user is on the Home page
    When the user clicks "Take product tour" at the bottom
    Then the "Stay on Top of Your Compliances" popup should be visible
    And the user clicks "Next" on the product tour

    Then the "Upload Documents with Ease" popup should be visible
    And the user clicks "Next" on the product tour

    Then the "Your Legal Documents Hub" popup should be visible

    Then the final step of product tour should be visible
    And the user clicks "Got it" to finish the tour



  Scenario: User skips the product tour from the first popup and remains on Compliances
    Given the user is on the Home page
    When the user clicks "Take product tour" at the bottom
    Then the "Stay on Top of Your Compliances" popup should be visible
    And the user clicks "Skip for now" during the product tour
    Then the user should stays in Compliances page
