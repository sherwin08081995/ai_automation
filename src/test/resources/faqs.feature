Feature: Verify FAQ section components and behavior

  As a GRC user
  I want to view and interact with the FAQs section
  So that I can access answers to common questions across different topics

  @TC-Z60-097 @TC-Z60-098
  Scenario: Verify the FAQ section displays the expected topics and Expanding each question reveals its information content
    When the user clicks the "Profile" icon
    Then the Customer Profile panel should open
    And the user select "FAQs" from the Customer profile panel
    Then the user should redirected to "FAQs" page


    And the FAQ topics should include:
      | Compliance Calendar |
      | Messages            |
      | Payments            |
      | Users and Roles     |


    When the user opens the "Compliance Calendar" topic in FAQs
    Then the topic header "Compliance Calendar" is visible
    And the questions listed under "Compliance Calendar" should be:
      | What are the compliances included?                                               |
      | I don't see few compliance deadlines, is it not applicable for me?               |
      | I see few compliances that I have not availed in the past. What should i do now? |
      | Can I get a customised calendar for my business?                                 |
      | The information here is different from the web.                                  |
    When the user expands every question under "Compliance Calendar"
    Then each expanded question content is visible and not empty
    And each accordion shows a "+" icon when collapsed and changes state when expanded
    When the user clicks "Back"
    Then the FAQ topics list is visible


    When the user opens the "Messages" topic in FAQs
    Then the topic header "Messages" is visible
    And the questions listed under "Messages" should be:
      | What do I find in Messages? |
      | Can I chat live?            |
    When the user expands every question under "Messages"
    Then each expanded question content is visible and not empty
    And each accordion shows a "+" icon when collapsed and changes state when expanded
    When the user clicks "Back"
    Then the FAQ topics list is visible


    When the user opens the "Payments" topic in FAQs
    Then the topic header "Payments" is visible
    And the questions listed under "Payments" should be:
      | What are the payment modes?                                                              |
      | Where can I get/ modify a GST invoice?                                                   |
      | What should I do when the dashboard still shows a payment as pending even after payment? |
      | Where can I apply for a refund?                                                          |
      | What happens after purchasing a new service?                                             |
    When the user expands every question under "Payments"
    Then each expanded question content is visible and not empty
    And each accordion shows a "+" icon when collapsed and changes state when expanded
    When the user clicks "Back"
    Then the FAQ topics list is visible


    When the user opens the "Users and Roles" topic in FAQs
    Then the topic header "Users and Roles" is visible
    And the questions listed under "Users and Roles" should be:
      | Who can be invited to join your business account?                               |
      | The user I wish to invite is not part of Vakilsearch, will I be able to invite? |
      | Will users in a business account be able to view my personal account?           |
      | Will users in a business be able to view my other business accounts?            |
      | Will the user be notified if I remove them?                                     |
    When the user expands every question under "Users and Roles"
    Then each expanded question content is visible and not empty
    And each accordion shows a "+" icon when collapsed and changes state when expanded
    When the user clicks "Back"
    Then the FAQ topics list is visible



