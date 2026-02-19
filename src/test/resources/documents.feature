Feature: Documents screen sections and folders

  As a GRC user
  I want to view, organize, and manage my legal documents
  So that I can quickly create folders, upload files, and access generated templates

  Background:
    Given the user is on the Home page
    When the user navigates to the Documents page

  @TC-Z60-068 @TC-Z60-069 @TC-Z60-070 @TC-Z60-071 @TC-Z60-072 @TC-Z60-073
  Scenario: Verify the sections available on the Documents page
    Then the page should show the following sections
      | Documents           |
      | Legal Doc generator |

  Scenario: Verify the Documents section is active by default
    Then the "Documents" section is active by default

  Scenario: Verify the folders displayed inside the Documents section
    Then the following folders are displayed in the Documents section
      | Zolvit Documents |
      | My Documents     |
      | Legal Documents  |


  Scenario: Navigate to Legal doc generator and verify different types of Legal document widgets are available

    When the user opens the "Legal doc generator" tab and the page is ready

    Then the user should see multiple legal document widgets
    And each legal document widget should have a visible button labeled "Create document"


  Scenario: Open a random legal document, fill in all sections, and submit to generate the document.
    When the user opens the "Legal doc generator" tab and the page is ready
    When the user clicks any legal document widget
    Then the opened form title matches the Selected document
    When the user fills all sections and submits the form
    When the user navigates to the Documents page
    Then the generated document should be present in the Documents section
    Then the generated document can be opened and downloaded


  Scenario: Verify My Documents section and verify the user is able to create a folder and upload a file
    When the user opens the "My documents" tab and the page is ready

    When the user clicks the "+ New" button and sees options:
      | Add new folder |
      | Upload files   |

    When the user selects "Add new folder"
    Then the create folder pop-up should appear
    When the user creates a folder with random name
    Then the user should be inside the created folder
    When the upload dropzone is clickable












