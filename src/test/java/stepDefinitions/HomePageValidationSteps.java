package stepDefinitions;

import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pages.HomePage;
import utils.*;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static utils.AllureLoggerUtils.logToAllure;


/**
 * HomePageValidationSteps.java
 * <p>
 * Purpose:
 * Step Definitions for validating the Home Page UI and functionality using Cucumber BDD.
 * This class handles:
 * <p>
 * ✅ Visibility and validation of the left side menu
 * ✅ Accessibility checks for menu items
 * ✅ Verification of the Overall Compliances section and its count
 * ✅ Sum validation of Overall Compliances based on status tabs
 * ✅ Cross-verification of tab counts and corresponding section records
 * ✅ Screenshot attachment for each validation step (Allure Reporting)
 * <p>
 * Related Classes:
 * - HomePage.java (Page Object Model for Home Page)
 * - ScreenshotUtils.java (Handles Allure screenshot capture)
 * - LoggerUtils.java (Log4j2 logger utility)
 * - Hooks.java (Manages WebDriver setup and teardown)
 * <p>
 * Author:
 *
 * @author Sherwin
 * @since 26-06-2025
 */


public class HomePageValidationSteps {

    WebDriver driver = Hooks.driver;
    HomePage homePage;
    Logger logger;
    WebDriverWait wait;
    AllureLoggerUtils allureLogging;
    ReusableCommonMethods helperMethods;
    private List<String> dueDateList;


    public HomePageValidationSteps() {
        this.driver = Hooks.driver;
        this.homePage = new HomePage(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.logger = LoggerUtils.getLogger(getClass());
        this.helperMethods = new ReusableCommonMethods(driver);
    }

    @Step("{message}")
    public void logStep(String message) {
        logger.info(message);
    }

    @When("the user views the left side menu")
    public void the_user_views_the_left_side_menu() {
        try {
            boolean isVisible = homePage.isLeftMenuVisible();

            // Console & Extent logger
            logStep("Left menu is visible: " + isVisible);
            logger.info("Left menu visible: {}", isVisible);

            // Allure log
            logToAllure("📌 Left Menu Visibility", "Left menu is visible: " + isVisible);
            // Screenshot
            ScreenshotUtils.attachScreenshotToAllure(driver, "LeftMenuVisible");
            // Assertion
            Assert.assertTrue(isVisible, "Left menu is not visible");

        } catch (AssertionError ae) {
            logger.error("❌ Assertion failed while checking left menu: {}", ae.getMessage());
            logToAllure("❌ Assertion Error", ae.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "LeftMenu_AssertionError");
            throw ae;
        } catch (Exception e) {
            logger.error("❌ Exception while verifying left menu: {}", e.getMessage());
            logToAllure("❌ Exception", e.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "LeftMenu_Exception");
            throw e;
        }
    }


    @Then("the following menu items should be visible:")
    public void the_following_menu_items_should_be_visible(DataTable dataTable) {
        List<String> expectedMenus = dataTable.asList();
        List<String> actualMenus = homePage.getLeftMenuItems();
        SoftAssert softAssert = new SoftAssert();

        logToAllure("📋 Expected Menu Items", expectedMenus.toString());
        logToAllure("📥 Actual Menu Items Fetched", actualMenus.toString());

        for (String menu : expectedMenus) {
            try {
                boolean found = actualMenus.stream().anyMatch(item -> item.equalsIgnoreCase(menu));
                String message = "Menu visible: " + menu + " - " + found;

                logStep(message);
                logger.info(message);
                logToAllure("🔎 Menu Visibility Check", message);

                ScreenshotUtils.attachScreenshotToAllure(driver, "MenuItem_" + menu);
                softAssert.assertTrue(found, "Expected menu item not found in left navigation: " + menu);

            } catch (Exception e) {
                String errorMsg = "Exception while checking menu '" + menu + "': " + e.getMessage();
                logger.error(errorMsg);
                logToAllure("❌ Menu Check Error", errorMsg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "MenuItem_Exception_" + menu);
                softAssert.fail(errorMsg);
            }
        }

        logToAllure("✅ Menu Validation", "Completed validation for left menu items.");
        logStep("Completed validation for left menu items.");
        softAssert.assertAll();
    }


    @Then("the user should be able to access the following menu items:")
    public void the_user_should_be_able_to_access_the_following_menu_items(DataTable dataTable) {
        List<String> menuItems = dataTable.asList();
        SoftAssert softAssert = new SoftAssert();

        logToAllure("📋 Menu Items to Access", menuItems.toString());

        for (String menuItem : menuItems) {
            try {
                homePage.clickLeftMenu(menuItem.trim());
                boolean success = homePage.waitForMenuPageToLoad(menuItem.trim());

                logStep("Accessed and loaded: " + menuItem);
                logger.info("Accessed menu item: {}", menuItem);
                logToAllure("✅ Access Check", "Accessed: " + menuItem + " | Loaded: " + success);

                ScreenshotUtils.attachScreenshotToAllure(driver, "Access_" + menuItem);

                if (success) driver.navigate().refresh();

                softAssert.assertTrue(success, "Unable to access or verify menu item: " + menuItem);

            } catch (Exception e) {
                String errorMsg = "Exception for menu '" + menuItem + "': " + e.getMessage();
                logger.error(errorMsg);
                logToAllure("❌ Access Error", errorMsg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "AccessException_" + menuItem);
                softAssert.fail("Exception while accessing menu item: " + menuItem + " - " + e.getMessage());
            }
        }

        logToAllure("✅ Menu Access Validation", "Completed access validation for menu items.");
        logStep("Completed access validation for menu items.");
        softAssert.assertAll();
    }


    @When("the user views the Overall Compliances section")
    public void the_user_views_the_Overall_Compliances_section() {
        try {
            boolean isVisible = driver.findElement(By.xpath("//p[contains(normalize-space(),'Overall Compliances')]/span")).isDisplayed();

            String stepMsg = "Overall Compliance section visible: " + isVisible;
            logStep(stepMsg);
            logger.info("Overall Compliances section visibility: {}", isVisible);
            logToAllure("✅ Visibility Check", stepMsg);

            ScreenshotUtils.attachScreenshotToAllure(driver, "OverallComplianceSection");

            SoftAssert softAssert = new SoftAssert();
            softAssert.assertTrue(isVisible, "Overall Compliance section not visible");
            softAssert.assertAll();

        } catch (Exception e) {
            String errorMsg = "Exception viewing Overall Compliances section: " + e.getMessage();
            logger.error(errorMsg);
            logToAllure("❌ Exception", errorMsg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "OverallCompliance_Exception");
            throw new AssertionError("Overall Compliance section is not visible.");
        }
    }


    @Then("the Overall Compliances count should be displayed properly")
    public void the_Overall_Compliances_count_should_be_displayed_properly() {
        try {
            int actualCount = homePage.getOverallCompliancesCount();

            String stepMsg = "Overall Compliances count: " + actualCount;
            logStep(stepMsg);
            logger.info("Actual compliance count: {}", actualCount);
            logToAllure("📊 Overall Compliances Count", stepMsg);

            ScreenshotUtils.attachScreenshotToAllure(driver, "OverallCount");

            SoftAssert softAssert = new SoftAssert();
            softAssert.assertTrue(actualCount >= 0, "Overall Compliance count is invalid.");
            softAssert.assertAll();
        } catch (Exception e) {
            String errorMsg = "Error reading overall compliance count: " + e.getMessage();
            logger.error(errorMsg);
            logToAllure("❌ Exception", errorMsg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "OverallCount_Exception");
            throw e;
        }
    }


    @Then("the Overall Compliances should be the sum of:")
    public void the_Overall_Compliances_should_be_the_sum_of(DataTable table) {
        List<String> allRows = table.asList();
        List<String> tabs = allRows.subList(1, allRows.size());
        int expectedTotal = 0;
        SoftAssert softAssert = new SoftAssert();

        try {
            for (String tab : tabs) {
                int tabCount = switch (tab.toLowerCase()) {
                    case "needs action" -> homePage.getNeedsActionCount();
                    case "in progress" -> homePage.getInProgressCount();
                    case "compliant" -> homePage.getCompliantCount();
                    case "upcoming" -> homePage.getUpcomingCount();
                    default -> throw new IllegalArgumentException("Unknown tab: " + tab);
                };
                logStep("Count for tab '" + tab + "': " + tabCount);
                logger.info("Tab: {}, Count: {}", tab, tabCount);
                logToAllure("📌 Tab Count", "Tab: " + tab + " → Count: " + tabCount);
                expectedTotal += tabCount;
            }

            int actualTotal = homePage.getOverallCompliancesCount();

            logStep("Expected total: " + expectedTotal + ", Actual total: " + actualTotal);
            logger.info("Expected: {}, Actual: {}", expectedTotal, actualTotal);
            logToAllure("📊 Final Validation", "Expected Overall Compliance Count: " + expectedTotal + "\nActual Count from UI: " + actualTotal);

            ScreenshotUtils.attachScreenshotToAllure(driver, "OverallComplianceMatch");

            softAssert.assertEquals(actualTotal, expectedTotal, "Mismatch in compliance count");
            softAssert.assertAll();
        } catch (Exception e) {
            String errorMsg = "Compliance sum mismatch or exception: " + e.getMessage();
            logger.error(errorMsg);
            logToAllure("❌ Exception", errorMsg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "ComplianceTotal_Exception");
            throw e;
        }
    }


    @Then("the user clicks each Compliance status tab, navigates to the Compliance section, and validates that the counts match for each tab")
    public void the_user_clicks_each_Compliance_status_tab_navigates_to_the_Compliance_section_and_validates_that_the_counts_match_for_each_tab(DataTable dataTable) {
        List<Map<String, String>> tabSectionPairs = dataTable.asMaps();
        SoftAssert softAssert = new SoftAssert();

        for (Map<String, String> pair : tabSectionPairs) {
            String tabName = pair.get("Tab");
            String sectionName = pair.get("Section");

            try {
                // Step 1: Get displayed tab count before clicking
                int displayedTabCount = homePage.getDisplayedTabCount(tabName);
                logStep("🟡 Displayed Count for tab '" + tabName + "': " + displayedTabCount);
                logger.info("Displayed Count for '{}': {}", tabName, displayedTabCount);
                logToAllure("📊 Displayed Count", "Tab: " + tabName + " → " + displayedTabCount);
                ScreenshotUtils.attachScreenshotToAllure(driver, "DisplayedCount_" + tabName);

                // Step 2: Click tab and get actual count in tab screen
                int actualTabCount = homePage.clickComplianceStatusTabAndGetCount(tabName);
                logStep("🟢 Clicked tab '" + tabName + "', Actual count: " + actualTabCount);
                logger.info("Clicked tab '{}' → Actual count: {}", tabName, actualTabCount);
                logToAllure("🟢 After Clicking Tab", "Tab: " + tabName + "\nActual Count: " + actualTabCount);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ClickedTab_" + tabName);

                // Step 3: Validate displayed vs actual
                softAssert.assertEquals(actualTabCount, displayedTabCount, "Mismatch between displayed and actual count for tab: " + tabName);

                // Step 4: Check compliance screen visibility
                boolean isOnComplianceScreen = homePage.isComplianceScreenVisible();
                logStep("📺 Compliance screen visible for tab '" + tabName + "': " + isOnComplianceScreen);
                logger.info("Compliance screen visibility for '{}': {}", tabName, isOnComplianceScreen);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ComplianceScreen_" + tabName);
                softAssert.assertTrue(isOnComplianceScreen, "Compliance screen not visible for tab: " + tabName);

                // Step 5: Validate record count in section
                int recordCount = homePage.getRecordCountForSection(sectionName);
                logStep("📌 Record count in section '" + sectionName + "': " + recordCount);
                logger.info("Section '{}' → Record count: {}", sectionName, recordCount);
                logToAllure("📌 Section Record Count", "Section: " + sectionName + "\nCount: " + recordCount);
                ScreenshotUtils.attachScreenshotToAllure(driver, "SectionRecord_" + sectionName);

                softAssert.assertEquals(recordCount, actualTabCount, "Mismatch between tab count and section count for: " + sectionName);

                // Step 6: Navigate back
                boolean navigatedBack = homePage.goBackToHomePage();
                logStep("↩ Returned to Home from tab '" + tabName + "': " + navigatedBack);
                logger.info("Returned to Home from tab '{}': {}", tabName, navigatedBack);
                ScreenshotUtils.attachScreenshotToAllure(driver, "BackToHome_" + tabName);
                softAssert.assertTrue(navigatedBack, "Failed to return to Home from tab: " + tabName);

            } catch (Exception e) {
                String errorMsg = "❌ Exception during validation for tab '" + tabName + "': " + e.getMessage();
                logger.error(errorMsg);
                logToAllure("❌ Exception for Tab", errorMsg);
                ScreenshotUtils.attachScreenshotToAllure(driver, "TabValidation_Exception_" + tabName);
                softAssert.fail("Exception during tab validation: " + tabName + " - " + e.getMessage());
            }
        }

        softAssert.assertAll();
    }


    @Then("after refreshing the page, the Due Date filter should default to {string}")
    public void after_refreshing_the_page_the_due_date_filter_should_default_to(String expectedDefaultDueDate) {
        try {
            logStep("🔄 Refreshing the page and verifying default Due Date filter selection...");
            driver.navigate().refresh();
            logger.info("🔄 Page refreshed successfully.");

            String actualSelectedDueDate = homePage.getSelectedDueDateFilter();

            logger.info("📌 Expected Default: '{}', Actual Selected: '{}'", expectedDefaultDueDate, actualSelectedDueDate);
            logToAllure("📌 Expected vs Actual Due Date", "Expected: " + expectedDefaultDueDate + "\nActual: " + actualSelectedDueDate);

            ScreenshotUtils.attachScreenshotToAllure(driver, "DueDate_Default");

            Assert.assertEquals(actualSelectedDueDate, expectedDefaultDueDate, "❌ Mismatch in default Due Date filter selection.");

        } catch (Exception e) {
            String msg = "❌ Error during Due Date default selection validation: " + e.getMessage();
            logger.error(msg);
            logToAllure("❌ Exception", msg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "DueDate_Default_Exception");
            throw e;
        }
    }


    @When("the user clicks on the Due Date filter dropdown")
    public void the_user_clicks_on_the_Due_Date_filter_dropdown() {
        try {
            logStep("🖱️ Clicking on the Due Date filter dropdown...");
            homePage.clickDueDateDropdown();
            logger.info("✅ Clicked on the Due Date filter dropdown successfully.");

            logToAllure("🖱️ Dropdown Click", "Clicked on the Due Date filter dropdown.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "DueDateDropdown_Clicked");

        } catch (Exception e) {
            String msg = "❌ Error while clicking on Due Date dropdown: " + e.getMessage();
            logger.error(msg);
            logToAllure("❌ Exception", msg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "DueDateDropdown_Exception");
            throw e;
        }
    }


    @Then("the following options should be visible under the dropdown:")
    public void the_following_options_should_be_visible_under_the_dropdown(DataTable expectedOptionsTable) {
        try {
            List<String> expectedOptions = expectedOptionsTable.asList();
            List<String> actualOptions = homePage.getDueDateFilterOptions();

            logStep("Verifying Due Date dropdown options");

            // Log in console
            logger.info("Expected options: {}", expectedOptions);
            logger.info("Actual options: {}", actualOptions);

            // Log in Allure
            logToAllure("✅ Expected Dropdown Options", expectedOptions);
            logToAllure("📥 Actual Dropdown Options Fetched", actualOptions);

            ScreenshotUtils.attachScreenshotToAllure(driver, "DueDateDropdown_Options");

            SoftAssert softAssert = new SoftAssert();
            for (String option : expectedOptions) {
                softAssert.assertTrue(actualOptions.contains(option), "❌ Missing option: " + option);
            }
            softAssert.assertAll();

        } catch (Exception e) {
            logger.error("Error verifying Due Date dropdown options: {}", e.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "DueDateDropdownOptions_Exception");
            throw e;
        }
    }


    @When("the user applies the following Due Date filters:")
    public void the_user_applies_due_dates(DataTable dataTable) {
        dueDateList = dataTable.asList(); // Store for @Then use

        logStep("📥 Applying Due Date filters: " + dueDateList);
        logToAllure("✅ Due Dates to be applied", dueDateList);

        ScreenshotUtils.attachScreenshotToAllure(driver, "DueDateFilter_Input");

        for (String dueDate : dueDateList) {
            logStep("🔄 Selecting Due Date: " + dueDate);
            homePage.selectDueDateFromDropdown(dueDate);

            ScreenshotUtils.attachScreenshotToAllure(driver, "DueDate_Selected_" + dueDate);

            int overall = homePage.getOverallCompliancesCount();
            int riskBased = homePage.getRiskBasedCompliancesCount();

            logger.info("✅ Counts for '{}': Overall={}, Risk Based={}", dueDate, overall, riskBased);
            logToAllure("📊 " + dueDate + " | Overall Count", String.valueOf(overall));
            logToAllure("📊 " + dueDate + " | Risk Based Count", String.valueOf(riskBased));

            SoftAssert softAssert = new SoftAssert();
            softAssert.assertEquals(riskBased, overall, "❌ Mismatch in Risk Based vs Overall for: " + dueDate);
            softAssert.assertAll();
        }
    }


    @Then("each Due Date selection should show consistent counts across sections")
    public void each_due_date_should_show_consistent_counts() {
        logStep("🔁 Verifying consistency across sections for each Due Date");
        SoftAssert softAssert = new SoftAssert();

        for (String dueDate : dueDateList) {
            try {
                logStep("🔄 Re-validating Due Date: " + dueDate);
                homePage.selectDueDateFromDropdown(dueDate);

                int overall = homePage.getOverallCompliancesCount();
                int riskBased = homePage.getRiskBasedCompliancesCount();

                logger.info("🔍 Re-check '{}' → Overall: {}, Risk Based: {}", dueDate, overall, riskBased);

                logToAllure("📊 Recheck " + dueDate + " | Overall", String.valueOf(overall));
                logToAllure("📊 Recheck " + dueDate + " | Risk Based", String.valueOf(riskBased));
                ScreenshotUtils.attachScreenshotToAllure(driver, "Recheck_" + dueDate);

                softAssert.assertEquals(riskBased, overall, "❌ Risk Based ≠ Overall for: " + dueDate);

            } catch (Exception e) {
                logger.error("❌ Error during re-validation for '{}': {}", dueDate, e.getMessage());
                ScreenshotUtils.attachScreenshotToAllure(driver, "Error_" + dueDate);
                softAssert.fail("Exception for Due Date '" + dueDate + "': " + e.getMessage());
            }
        }

        softAssert.assertAll();
    }


    @Then("each Due Date selection should show matching count on Compliance page after View All navigation")
    public void verify_count_consistency_across_pages() {
        logStep("🔁 Starting full Due Date consistency check between Home and Compliance pages");

        SoftAssert softAssert = new SoftAssert();

        for (String dueDate : dueDateList) {
            try {
                logStep("📌 Processing Due Date: " + dueDate);

                // 1. Select on Home Page
                homePage.selectDueDateFromDropdown(dueDate);
                int expectedCount = homePage.getOverallCompliancesCount(); // Source of truth
                logger.info("🏠 Home Page [{}] - Overall Count: {}", dueDate, expectedCount);

                logToAllure("🏠 Home Page Count - " + dueDate, String.valueOf(expectedCount));
                ScreenshotUtils.attachScreenshotToAllure(driver, "HomePage_" + dueDate);

                // 2. Navigate to Compliance Page
                homePage.clickViewAllCompliance();

                // 3. Refresh and reapply Due Date
                helperMethods.refreshAndReapply("Due Date - " + dueDate, () -> homePage.selectDueDateDropdownFromCompliancePage(dueDate));

                // 4. Get All tab count on Compliance Page
                int actualCount = homePage.getAllTabComplianceCountFromCompliancePage();
                logger.info("📋 Compliance Page [{}] - All() Tab Count: {}", dueDate, actualCount);

                logToAllure("📋 Compliance Page Count - " + dueDate, String.valueOf(actualCount));
                ScreenshotUtils.attachScreenshotToAllure(driver, "CompliancePage_" + dueDate);

                // 5. Assertion
                softAssert.assertEquals(actualCount, expectedCount, String.format("❌ Mismatch for '%s' ➤ Home: %d, Compliance: %d", dueDate, expectedCount, actualCount));

                // 6. Go back
                driver.navigate().back();

            } catch (Exception e) {
                logger.error("❌ Exception during verification for '{}': {}", dueDate, e.getMessage());
                ScreenshotUtils.attachScreenshotToAllure(driver, "Error_" + dueDate);
                softAssert.fail("Exception for '" + dueDate + "': " + e.getMessage());
            }
        }

        softAssert.assertAll();
    }


}
