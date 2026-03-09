package stepDefinitions;

import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pages.HomePage;
import utils.*;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
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
    private String generatedCompanyName;
    private int overallCount;
    private int riskBasedCount;
    private int highCount;
    private int mediumCount;
    private int lowCount;
    private List<String> homePageHeaders;


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

            logStep("Left menu is visible: " + isVisible);
            logger.info("Left menu visible: {}", isVisible);
            logToAllure("📌 Left Menu Visibility", "Left menu is visible: " + isVisible);
            ScreenshotUtils.attachScreenshotToAllure(driver, "LeftMenuVisible");
            Assert.assertTrue(isVisible, "Left menu is not visible");

        } catch (AssertionError ae) {
            logger.error("❌ Assertion failed while checking left menu: {}", ae.getMessage());
            logToAllure("❌ Assertion Error", ae.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "LeftMenu_AssertionError");
            throw ae;
        } catch (Throwable t) {
            homePage.handleValidationException("Left menu visibility", t);
        }
    }

    @Then("the following menu items should be visible:")
    public void the_following_menu_items_should_be_visible(DataTable dataTable) {
        try {
            List<String> expectedMenus = dataTable.asList();
            List<String> actualMenus = homePage.getLeftMenuItems();
            SoftAssert softAssert = new SoftAssert();

            logToAllure("📋 Expected Menu Items", expectedMenus.toString());
            logToAllure("📥 Actual Menu Items Fetched", actualMenus.toString());

            for (String menu : expectedMenus) {
                try {
                    boolean found = false;
                    for (String item : actualMenus) {
                        if (item.equalsIgnoreCase(menu)) {
                            found = true;
                            break;
                        }
                    }

                    String message = "Menu visible: " + menu + " - " + found;
                    logStep(message);
                    logger.info(message);
                    logToAllure("🔎 Menu Visibility Check", message);

                    ScreenshotUtils.attachScreenshotToAllure(driver, "MenuItem_" + menu.replaceAll("\\s+", "_"));
                    softAssert.assertTrue(found, "Expected menu item not found in left navigation: " + menu);

                } catch (Throwable t) {
                    String errorMsg = "Exception while checking menu '" + menu + "': " + t.getMessage();
                    logger.error(errorMsg);
                    logToAllure("❌ Menu Check Error", errorMsg);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "MenuItem_Exception_" + menu.replaceAll("\\s+", "_"));
                    softAssert.fail(errorMsg);
                }
            }

            logToAllure("✅ Menu Validation", "Completed validation for left menu items.");
            logStep("Completed validation for left menu items.");
            softAssert.assertAll();

        } catch (Throwable t) {
            homePage.handleValidationException("Left menu item validation", t);
        }
    }


    @Then("the user should be able to access the following menu items:")
    public void the_user_should_be_able_to_access_the_following_menu_items(DataTable dataTable) {
        try {
            List<String> menuItems = dataTable.asList();
            SoftAssert softAssert = new SoftAssert();

            logToAllure("📋 Menu Items to Access", menuItems.toString());

            for (String menuItem : menuItems) {
                try {
                    String trimmedMenu = menuItem.trim();

                    // 1) Start timers BEFORE the click (captures click → nav → render)
                    Instant navStart = Instant.now();
                    NavContext.start(trimmedMenu);

                    // 2) Click the left menu
                    homePage.clickLeftMenu(trimmedMenu);

                    // 3) Wait up to NAV_FAIL_MS for the target page to be "loaded" (config-driven)
                    boolean success = homePage.waitForMenuPageToLoad(trimmedMenu, Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));

                    // 4) Stop & log timing via reusable helper (defaults to NAV thresholds 12s/20s)
                    long elapsedMs = helperMethods.logLoadTimeAndReturnMs(trimmedMenu, navStart);
                    double elapsedSec = elapsedMs / 1000.0;

                    // 5) Result logging
                    String resultMsg = "Accessed and loaded: " + trimmedMenu + " → " + success;
                    logStep(resultMsg + " in " + String.format("%.2f s", elapsedSec));
                    logger.info("Accessed menu item: {} | {}s", trimmedMenu, String.format("%.2f", elapsedSec));
                    logToAllure("✅ Access Check", resultMsg + " (took " + String.format("%.2f s", elapsedSec) + ")");
                    ScreenshotUtils.attachScreenshotToAllure(driver, "Access_" + trimmedMenu.replaceAll("\\s+", "_"));

                    // 6) Threshold handling for NAV (warn ≥ NAV_WARN_MS, fail ≥ NAV_FAIL_MS)
                    if (success) {
                        if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                            String failMsg = String.format("%s took %.2f s — more than %d s. Failing (SLA %ds).", trimmedMenu, elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                            logger.error(failMsg);
                            logToAllure("❌ Load Time Failure", failMsg);
                            softAssert.fail(failMsg);
                        } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                            String warnMsg = String.format("%s took %.2f s — more than %d s.", trimmedMenu, elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                            logger.warn(warnMsg);
                            logToAllure("⚠️ Load Time Warning", warnMsg);
                            // soft warning only
                        }

                        // Optional: refresh between checks
                        driver.navigate().refresh();
                        Thread.sleep(2000);

                    } else {
                        String failMsg = String.format("Unable to access or verify menu item within %d s: %s", ReusableCommonMethods.NAV_FAIL_MS / 1000, trimmedMenu);
                        logger.error(failMsg);
                        logToAllure("❌ Access Failure", failMsg + " (after " + String.format("%.2f s", elapsedSec) + ")");
                        softAssert.fail(failMsg);
                    }

                } catch (Throwable t) {
                    String errorMsg = "Exception for menu '" + menuItem + "': " + t.getMessage();
                    logger.error(errorMsg, t);
                    logToAllure("❌ Access Error", errorMsg);
                    ScreenshotUtils.attachScreenshotToAllure(driver, "AccessException_" + menuItem.replaceAll("\\s+", "_"));
                    softAssert.fail("Exception while accessing menu item: " + menuItem + " - " + t.getMessage());
                }
            }

            logToAllure("✅ Menu Access Validation", "Completed access validation for menu items.");
            logStep("Completed access validation for menu items.");
            softAssert.assertAll();

        } catch (Throwable t) {
            homePage.handleValidationException("Menu access validation", t);
        }
    }


    @When("the user views the Overall Compliances section")
    public void the_user_views_the_Overall_Compliances_section() {
        try {
            boolean isVisible = homePage.isOverallCompliancesSectionVisible();

            String stepMsg = "Overall Compliance section visible: " + isVisible;
            logStep(stepMsg);
            logger.info("Overall Compliances section visibility: {}", isVisible);
            logToAllure("✅ Visibility Check", stepMsg);

            ScreenshotUtils.attachScreenshotToAllure(driver, "OverallComplianceSection");

            SoftAssert softAssert = new SoftAssert();
            softAssert.assertTrue(isVisible, "Overall Compliance section not visible");
            softAssert.assertAll();

        } catch (Throwable t) {
            homePage.handleValidationException("Overall Compliance section visibility", t);
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

        } catch (Throwable t) {
            homePage.handleValidationException("Overall Compliance count validation", t);
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
                    case "completed" -> homePage.getCompliantCount();
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

        } catch (Throwable t) {
            homePage.handleValidationException("Overall Compliance count aggregation", t);
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
                //Get displayed tab count before clicking
                int displayedTabCount = homePage.getDisplayedTabCount(tabName);
                logStep("🟡 Displayed Count for tab '" + tabName + "': " + displayedTabCount);
                logger.info("Displayed Count for '{}': {}", tabName, displayedTabCount);
                logToAllure("📊 Displayed Count", "Tab: " + tabName + " → " + displayedTabCount);
                ScreenshotUtils.attachScreenshotToAllure(driver, "DisplayedCount_" + tabName);

                //Click tab and get actual count in tab screen
                int actualTabCount = homePage.clickComplianceStatusTabAndGetCount(tabName);
                logStep("🟢 Clicked tab '" + tabName + "', Actual count: " + actualTabCount);
                logger.info("Clicked tab '{}' → Actual count: {}", tabName, actualTabCount);
                logToAllure("🟢 After Clicking Tab", "Tab: " + tabName + "\nActual Count: " + actualTabCount);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ClickedTab_" + tabName);

                //Validate displayed vs actual
                softAssert.assertEquals(actualTabCount, displayedTabCount, "Mismatch between displayed and actual count for tab: " + tabName);

                //Check compliance screen visibility
                boolean isOnComplianceScreen = homePage.isComplianceScreenVisible();
                logStep("📺 Compliance screen visible for tab '" + tabName + "': " + isOnComplianceScreen);
                logger.info("Compliance screen visibility for '{}': {}", tabName, isOnComplianceScreen);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ComplianceScreen_" + tabName);
                softAssert.assertTrue(isOnComplianceScreen, "Compliance screen not visible for tab: " + tabName);

                //Validate record count in section
                int recordCount = homePage.getRecordCountForSection(sectionName);
                logStep("📌 Record count in section '" + sectionName + "': " + recordCount);
                logger.info("Section '{}' → Record count: {}", sectionName, recordCount);
                logToAllure("📌 Section Record Count", "Section: " + sectionName + "\nCount: " + recordCount);
                ScreenshotUtils.attachScreenshotToAllure(driver, "SectionRecord_" + sectionName);

                softAssert.assertEquals(recordCount, actualTabCount, "Mismatch between tab count and section count for: " + sectionName);

                //Navigate back
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

        } catch (Throwable t) {
            homePage.handleValidationException("Due Date default selection validation", t);
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

        } catch (Throwable t) {
            homePage.handleValidationException("clicking on Due Date dropdown", t);
        }

    }


    @Then("the following options should be visible under the dropdown:")
    public void the_following_options_should_be_visible_under_the_dropdown(DataTable expectedOptionsTable) {
        try {
            List<String> expectedOptions = expectedOptionsTable.asList();
            List<String> actualOptions = homePage.getDueDateFilterOptions();

            logStep("Verifying Due Date dropdown options");
            logger.info("Expected options: {}", expectedOptions);
            logger.info("Actual options: {}", actualOptions);

            logToAllure("✅ Expected Dropdown Options", expectedOptions);
            logToAllure("📥 Actual Dropdown Options Fetched", actualOptions);

            ScreenshotUtils.attachScreenshotToAllure(driver, "DueDateDropdown_Options");

            SoftAssert softAssert = new SoftAssert();
            for (String option : expectedOptions) {
                softAssert.assertTrue(actualOptions.contains(option), "❌ Missing option: " + option);
            }
            softAssert.assertAll();

        } catch (Throwable t) {
            homePage.handleValidationException("verifying Due Date dropdown options", t);
        }

    }


    @When("the user applies the following Due Date filters:")
    public void the_user_applies_due_dates(DataTable dataTable) {
        dueDateList = dataTable.asList(); // Store for @Then use if needed

        logStep("📥 Applying Due Date filters: " + dueDateList);
        logToAllure("✅ Due Dates to be applied", dueDateList);
        ScreenshotUtils.attachScreenshotToAllure(driver, "DueDateFilter_Input");

        SoftAssert softAssert = new SoftAssert();

        for (String dueDate : dueDateList) {
            try {
                logStep("🔄 Selecting Due Date: " + dueDate);
                homePage.selectDueDateFromDropdown(dueDate);
                ScreenshotUtils.attachScreenshotToAllure(driver, "DueDate_Selected_" + dueDate.replaceAll("\\s+", "_"));

                // Step 2: Fetch compliance counts
                int overall = homePage.getOverallCompliancesCount();
                int riskBased = homePage.getRiskBasedCompliancesCount();

                logger.info("✅ Counts for '{}': Overall={}, Risk Based={}", dueDate, overall, riskBased);
                logToAllure("📊 " + dueDate + " | Overall Compliances Count", String.valueOf(overall));
                logToAllure("📊 " + dueDate + " | Risk Based Compliances Count", String.valueOf(riskBased));

                // Step 3: Stronger validation logic
                softAssert.assertTrue(overall >= riskBased, "❌ Risk Based Compliances count exceeds Overall Compliances count for: " + dueDate);
                if (overall == 0) {
                    logger.info("ℹ️ Overall compliance count is 0 for '{}'", dueDate);
                    logToAllure("ℹ️ Note: Zero Overall Compliances", dueDate);
                }

            } catch (Exception e) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "DueDate_Exception_" + dueDate.replaceAll("\\s+", "_"));
                logger.error("❌ Error while validating Due Date '{}': {}", dueDate, e.getMessage());
                softAssert.fail("💥 Exception during due date validation for: " + dueDate);
            }
        }
        softAssert.assertAll();
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

                logger.info("🔍 Re-check '{}' → Overall Compliances: {}, Risk Based Compliances: {}", dueDate, overall, riskBased);

                logToAllure("📊 Recheck " + dueDate + " | Overall Compliances", String.valueOf(overall));
                logToAllure("📊 Recheck " + dueDate + " | Risk Based Compliances", String.valueOf(riskBased));
                ScreenshotUtils.attachScreenshotToAllure(driver, "Recheck_" + dueDate);

                softAssert.assertEquals(riskBased, overall, "❌ Risk Based Compliances ≠ Overall Compliances for: " + dueDate);

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

                try {
                    Thread.sleep(5_000); // 5 seconds
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

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


    @Given("the user is on the Home page")
    public void the_user_is_on_the_home_page() {
        try {
            logStep("🔍 Navigating and confirming that the user is on the Home page...");

            // 1) Start timers BEFORE the click (captures click → nav → render)
            Instant navStart = Instant.now();
            NavContext.start("Home");

            // 2) Click Home
            homePage.clickHomeTab();

            // 3) Wait up to NAV_FAIL_MS for the Home page to be "loaded" (config-driven)
            boolean success = homePage.waitForHomeLoaded(Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));

            // 4) Stop & log timing via reusable helper (defaults to NAV thresholds 12s/20s)
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Home", navStart);
            double elapsedSec = elapsedMs / 1000.0;

            // 5) Threshold handling for NAV (warn ≥ NAV_WARN_MS, fail ≥ NAV_FAIL_MS)
            if (success) {
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("Home took %.2f s — more than %d s. Failing (SLA %ds).", elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    Assert.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("Home took %.2f s — more than %d s.", elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }
            } else {
                String failMsg = String.format("Unable to load Home within %d s (actual: %.2f s).", ReusableCommonMethods.NAV_FAIL_MS / 1000, elapsedSec);
                logger.error(failMsg);
                logToAllure("❌ Access Failure", failMsg);
                Assert.fail(failMsg);
            }

            // 6) Final assertions + artifacts
            Assert.assertTrue(success, "❌ User is not on the Home page.");
            logToAllure("🏠 Home Page Loaded", String.valueOf(success));
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "HomePage_Confirmation");
            logger.info("✅ Home page successfully confirmed.");

        } catch (Throwable t) {
            homePage.handleValidationException("Home page confirmation", t);
        }
    }


    @Then("the Add Organization button should be visible in the top right corner")
    public void verify_add_org_button_top_right() {
        try {
            logStep("🔍 Checking '+ Add Organization' button in top right...");
            boolean visible = homePage.isAddOrgButtonTopRightVisible();
            logToAllure("Top Right Button Visible", String.valueOf(visible));
            ScreenshotUtils.attachScreenshotToAllure(driver, "AddOrg_TopRight");
            Assert.assertTrue(visible, "❌ '+ Add Organization' not found in top right.");
        } catch (Throwable t) {
            homePage.handleValidationException("checking top right Add Org button", t);
        }

    }


    @Then("the Add Organization button should be visible under the organization dropdown at the top")
    public void verify_add_org_button_under_dropdown() {
        try {
            logStep("🔍 Checking '+ Add Organization' option inside dropdown...");
            boolean visible = homePage.isAddOrgButtonUnderDropdownVisible();
            logToAllure("Dropdown Button Visible", String.valueOf(visible));
            ScreenshotUtils.attachScreenshotToAllure(driver, "AddOrg_UnderDropdown");
            Assert.assertTrue(visible, "❌ '+ Add Organization' not found in dropdown.");
        } catch (Throwable t) {
            homePage.handleValidationException("checking dropdown Add Org button", t);
        }

    }


    @When("the user selects each organization from Home and verifies it is reflected on the Compliance page")
    public void verify_selected_org_is_reflected_on_compliance_page() {
        try {
            logStep("🔁 Iterating over each organization under 'All offices'");

            List<String> orgNames = homePage.getAllOrgNamesFromDropdown();
            logToAllure("Organizations Found", String.join(", ", orgNames));
            ScreenshotUtils.attachScreenshotToAllure(driver, "OrgDropdown_Parsed");

            for (String orgName : orgNames) {
                logger.info("🔁 Starting iteration for organization: {}", orgName);

                // Step 1: Navigate to Home
                Assert.assertTrue(homePage.navigateAndConfirmHomePage(), "❌ Home page not loaded properly before reselecting org.");

                // Step 2: Reset dropdown state
                try {
                    new Actions(driver).sendKeys(Keys.ESCAPE).perform();
                    Thread.sleep(500);
                } catch (Exception esc) {
                    logger.warn("⚠️ Could not reset dropdown using ESC: {}", esc.getMessage());
                }

                // Step 3: Select org from dropdown (moved logic into HomePage)
                logStep("📌 Selecting organization: " + orgName);
                homePage.selectOrganizationFromDropdown(orgName);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Org_Selected_" + orgName.replaceAll("\\s+", "_"));

                // Step 4: Navigate to Compliance
                boolean isCompliancePageLoaded = homePage.goToCompliancePage();
                Assert.assertTrue(isCompliancePageLoaded, "❌ Compliance page not loaded properly.");

                Thread.sleep(1000);

                // Step 5: Validate
                String selectedOrg = homePage.getSelectedOrganizationName();
                boolean isMatch = homePage.areAllOfficeValuesMatchingSelectedOrg(selectedOrg);

                logToAllure("Selected Organization", selectedOrg);
                logToAllure("All Office Values Match", String.valueOf(isMatch));
                ScreenshotUtils.attachScreenshotToAllure(driver, "Org_Office_Match_" + selectedOrg.replaceAll("\\s+", "_"));

                Assert.assertTrue(isMatch, "❌ One or more office entries do not match the selected organization: " + selectedOrg);

                // Step 6: Return to Home
                logger.info("↩️ Returning to Home for next organization...");
                Assert.assertTrue(homePage.navigateAndConfirmHomePage(), "❌ Failed to return to Home after Compliance verification.");
            }
        } catch (Throwable t) {
            homePage.handleValidationException("verifying organization reflection on Compliance page", t);
        }

    }


    @When("the user clicks the {string} button at right corner")
    public void user_clicks_button_at_right_corner(String buttonText) {
        try {
            logStep("🖱️ Clicking '" + buttonText + "' button at the right corner...");
            homePage.clickAddOrganisationButton(buttonText);
            logToAllure("Clicked Button", buttonText);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Clicked_" + buttonText.replace(" ", "_"));
        } catch (Throwable t) {
            homePage.handleValidationException("Clicking '" + buttonText + "' button at right corner", t);
        }

    }


    @Then("the create organization popup should appear")
    public void create_organization_popup_should_appear() {
        try {
            logStep("📌 Verifying if 'Create Organization' popup is visible...");
            boolean isVisible = homePage.verifyCreateOrganizationPopupIsVisible();
            logToAllure("Create Org Popup Visible", String.valueOf(isVisible));
            ScreenshotUtils.attachScreenshotToAllure(driver, "CreateOrg_Popup");
            Assert.assertTrue(isVisible, "❌ 'Create Organization' popup not visible.");
        } catch (Throwable t) {
            homePage.handleValidationException("Create Organization popup verification", t);
        }

    }


    @When("the user selects a business type")
    public void user_selects_business_type() {
        try {
            String businessType = TestDataGenerator.getRandomBusinessType();
            logStep("📂 Selecting business type: " + businessType);
            homePage.selectBusinessType(businessType);
            logToAllure("Selected Business Type", businessType);
            ScreenshotUtils.attachScreenshotToAllure(driver, "BusinessType_Selected");
        } catch (Throwable t) {
            homePage.handleValidationException("Business type selection", t);
        }

    }


    @When("the user enters a company name")
    public void user_enters_company_name() {
        try {
            generatedCompanyName = TestDataGenerator.getRandomCompanyName();
            logStep("🏢 Entering company name: " + generatedCompanyName);
            homePage.enterCompanyName(generatedCompanyName);
            logToAllure("Entered Company Name", generatedCompanyName);
            ScreenshotUtils.attachScreenshotToAllure(driver, "CompanyName_Entered");
        } catch (Throwable t) {
            homePage.handleValidationException("Company name entry", t);
        }

    }


    @When("the user selects a business location")
    public void user_selects_business_location() {
        try {
            String location = TestDataGenerator.getRandomLocation();
            logStep("📍 Selecting business location (state): " + location);
            homePage.selectState(location);
            logToAllure("Selected Location", location);
            ScreenshotUtils.attachScreenshotToAllure(driver, "BusinessLocation_Selected");
        } catch (Throwable t) {
            homePage.handleValidationException("Business location selection", t);
        }
    }


    @When("the user selects an employee count")
    public void user_selects_employee_count() {
        try {
            String empCount = TestDataGenerator.getRandomEmployeeCount();
            logStep("👥 Selecting employee count: " + empCount);
            homePage.selectEmployeeSize(empCount);
            logToAllure("Selected Employee Count", empCount);
            ScreenshotUtils.attachScreenshotToAllure(driver, "EmployeeCount_Selected");
        } catch (Throwable t) {
            homePage.handleValidationException("Employee count selection", t);
        }

    }


    @When("the user selects an industry type")
    public void user_selects_industry_type() {
        try {
            String industry = TestDataGenerator.getRandomIndustry();
            logStep("🏭 Selecting industry type: " + industry);
            homePage.selectIndustryType(industry);
            logToAllure("Selected Industry Type", industry);
            ScreenshotUtils.attachScreenshotToAllure(driver, "IndustryType_Selected");
        } catch (Throwable t) {
            homePage.handleValidationException("Industry type selection", t);
        }

    }


    @When("the user selects an annual turnover")
    public void user_selects_annual_turnover() {
        try {
            String turnover = TestDataGenerator.getRandomTurnover();
            logStep("💰 Selecting annual turnover: " + turnover);
            homePage.selectAnnualTurnover(turnover);
            logToAllure("Selected Annual Turnover", turnover);
            ScreenshotUtils.attachScreenshotToAllure(driver, "AnnualTurnover_Selected");
        } catch (Throwable t) {
            homePage.handleValidationException("Annual turnover selection", t);
        }

    }

//    @When("clicks the {string} button")
//    public void user_clicks_button(String buttonLabel) {
//        try {
//            logStep("🖱️ Clicking Get Started button: " + buttonLabel);
//            if (buttonLabel.equalsIgnoreCase("Get Started")) {
//                homePage.clickGetStarted();
//                logToAllure("Clicked Get Started Button", buttonLabel);
//                ScreenshotUtils.attachScreenshotToAllure(driver, "Clicked_" + buttonLabel.replace(" ", "_"));
//            } else {
//                throw new IllegalArgumentException("Unsupported button: " + buttonLabel);
//            }
//        } catch (Throwable t) {
//            homePage.handleValidationException("clicking Get Started button: " + buttonLabel, t);
//        }
//
//    }
//
//    @Then("the mandatory compliances should be displayed")
//    public void verify_mandatory_compliances() {
//        try {
//            logStep("📋 Fetching mandatory compliances count (and logging timing to Allure)...");
//            int count = homePage.getMandatoryComplianceCount();
//
//            // Pull timing from POM
//            double secs = homePage.getLastComplianceLoadSeconds();
//            String formatted = homePage.getLastComplianceLoadFormatted();
//            boolean over1min = homePage.isLastComplianceOverOneMinute();
//
//            // Allure-only logging here
//            logToAllure("Mandatory Compliance Count", String.valueOf(count));
//            logToAllure("Time Taken (formatted)", formatted);
//            logToAllure("Time Taken (seconds)", String.format("%.2f", secs));
//            logToAllure("Exceeded 1 minute?", String.valueOf(over1min));
//
//             Allure.step(
//                 String.format("Compliance load timing: %s (%.2f sec); count = %d%s",
//                     formatted, secs, count, over1min ? " [SLOW > 60s]" : "")
//             );
//
//            ScreenshotUtils.attachScreenshotToAllure(driver, "MandatoryCompliances_Displayed");
//
//            if (count == 0) {
//                logToAllure("Info", "No mandatory compliances found (count = 0).");
//            } else {
//                logToAllure("Info", "Mandatory compliances found: " + count);
//            }
//
//        } catch (Throwable t) {
//            homePage.handleValidationException("Fetching mandatory compliances", t);
//        }
//    }

    // In your step-definition class
    private Instant getStartedStartInstant; // holds click→destination start time

    @When("clicks the {string} button")
    public void user_clicks_button(String buttonLabel) {
        try {
            logStep("🖱️ Clicking button: " + buttonLabel);

            if (buttonLabel.equalsIgnoreCase("Get Started")) {
                // 1) Start timers BEFORE the click (click → nav → render)
                getStartedStartInstant = Instant.now();
                NavContext.start("Get Started → Mandatory Compliances");

                // 2) Click
                homePage.clickGetStarted();

                // 3) Log + screenshot
                logToAllure("Clicked Get Started Button", buttonLabel);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Clicked_" + buttonLabel.replace(" ", "_"));
            } else {
                throw new IllegalArgumentException("Unsupported button: " + buttonLabel);
            }

        } catch (Throwable t) {
            homePage.handleValidationException("clicking " + buttonLabel + " button", t);
        }
    }

//    @Then("the mandatory compliances should be displayed")
//    public void verify_mandatory_compliances() {
//        try {
//            logStep("📋 Fetching mandatory compliances count (and logging timing to Allure)...");
//
//            // ---- thresholds for this flow ----
//            final long WARN1_MS = 60_000L;   // 1 minute
//            final long WARN2_MS = 90_000L;   // 1 min 30 sec
//            final long FAIL_MS  = 150_000L;  // 2 min 30 sec
//
//            // 1) Get the count (your POM already waits and measures its own time)
//            int count = homePage.getMandatoryComplianceCount();
//
//            // 2) Step-level timing: click → mandatory compliances visible
//            Instant start = (getStartedStartInstant != null) ? getStartedStartInstant : Instant.now();
//
//            // Use helper to log raw time + primary warn/fail thresholds (warn@60s, fail@150s)
//            long elapsedMs = helperMethods.logLoadTimeAndReturnMs(
//                    "Get Started → Mandatory Compliances",
//                    start,
//                    WARN1_MS, FAIL_MS
//            );
//            double elapsedSec = elapsedMs / 1000.0;
//
//            // Extra mid-warning at 90s
//            if (elapsedMs >= FAIL_MS) {
//                String msg = String.format(
//                        "Get Started → Mandatory Compliances took %.2f s — more than %d s. Failing (SLA %ds).",
//                        elapsedSec, FAIL_MS / 1000, FAIL_MS / 1000
//                );
//                logToAllure("❌ Load Time Failure", msg);
//                Assert.fail(msg);
//            } else if (elapsedMs >= WARN2_MS) {
//                String msg = String.format(
//                        "Get Started → Mandatory Compliances took %.2f s — more than %d s.",
//                        elapsedSec, WARN2_MS / 1000
//                );
//                logger.warn(msg);
//                logToAllure("⚠️ Load Time Warning (90s)", msg);
//            } else if (elapsedMs >= WARN1_MS) {
//                String msg = String.format(
//                        "Get Started → Mandatory Compliances took %.2f s — more than %d s.",
//                        elapsedSec, WARN1_MS / 1000
//                );
//                logger.warn(msg);
//                logToAllure("⚠️ Load Time Warning (60s)", msg);
//            }
//
//            // 3) POM-derived timing (you already had this; keeping it)
//            double secsPOM = homePage.getLastComplianceLoadSeconds();
//            String formatted = homePage.getLastComplianceLoadFormatted();
//            boolean over1min = homePage.isLastComplianceOverOneMinute();
//
//            logToAllure("Mandatory Compliance Count", String.valueOf(count));
//            logToAllure("Time Taken (formatted)", formatted);
//            logToAllure("Time Taken (seconds)", String.format("%.2f", secsPOM));
//            logToAllure("Exceeded 1 minute?", String.valueOf(over1min));
//
//            Allure.step(String.format(
//                    "Compliance load timing: %s (%.2f sec); count = %d%s",
//                    formatted, secsPOM, count, over1min ? " [SLOW > 60s]" : ""
//            ));
//
//            ScreenshotUtils.attachScreenshotToAllure(driver, "MandatoryCompliances_Displayed");
//
//            if (count == 0) {
//                logToAllure("Info", "No mandatory compliances found (count = 0).");
//            } else {
//                logToAllure("Info", "Mandatory compliances found: " + count);
//            }
//
//            // 4) reset for cleanliness
//            getStartedStartInstant = null;
//
//        } catch (Throwable t) {
//            homePage.handleValidationException("Fetching mandatory compliances", t);
//        }
//    }

    @Then("the mandatory compliances should be displayed")
    public void verify_mandatory_compliances() {
        try {
            logStep("📋 Fetching mandatory compliances count (and logging timing to Allure)...");

            // 1) Get the count (POM waits internally)
            int count = homePage.getMandatoryComplianceCount();

            // 2) Measure click → count visible using config thresholds
            Instant start = (getStartedStartInstant != null) ? getStartedStartInstant : Instant.now();
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Get Started → Mandatory Compliances", start, ReusableCommonMethods.GETSTARTED_WARN1_MS,   // 60s warn1 from config
                    ReusableCommonMethods.GETSTARTED_FAIL_MS     // 150s fail from config
            );
            double elapsedSec = elapsedMs / 1000.0;

            // extra mid-warning at 90s (config)
            if (elapsedMs >= ReusableCommonMethods.GETSTARTED_FAIL_MS) {
                String msg = String.format("Get Started → Mandatory Compliances took %.2f s — more than %d s. Failing (SLA %ds).", elapsedSec, ReusableCommonMethods.GETSTARTED_FAIL_MS / 1000, ReusableCommonMethods.GETSTARTED_FAIL_MS / 1000);
                logToAllure("❌ Load Time Failure", msg);
                Assert.fail(msg);

            } else if (elapsedMs >= ReusableCommonMethods.GETSTARTED_WARN2_MS) {
                String msg = String.format("Get Started → Mandatory Compliances took %.2f s — more than %d s.", elapsedSec, ReusableCommonMethods.GETSTARTED_WARN2_MS / 1000);
                logger.warn(msg);
                logToAllure("⚠️ Load Time Warning (90s)", msg);

            } else if (elapsedMs >= ReusableCommonMethods.GETSTARTED_WARN1_MS) {
                String msg = String.format("Get Started → Mandatory Compliances took %.2f s — more than %d s.", elapsedSec, ReusableCommonMethods.GETSTARTED_WARN1_MS / 1000);
                logger.warn(msg);
                logToAllure("⚠️ Load Time Warning (60s)", msg);
            }

            // 3) (optional) also surface POM’s internal timing you already compute
            double secsPOM = homePage.getLastComplianceLoadSeconds();
            String formatted = homePage.getLastComplianceLoadFormatted();
            boolean over1min = homePage.isLastComplianceOverOneMinute();

            logToAllure("Mandatory Compliance Count", String.valueOf(count));
            logToAllure("Time Taken (formatted)", formatted);
            logToAllure("Time Taken (seconds)", String.format("%.2f", secsPOM));
            logToAllure("Exceeded 1 minute?", String.valueOf(over1min));

            Allure.step(String.format("Compliance load timing: %s (%.2f sec); count = %d%s", formatted, secsPOM, count, over1min ? " [SLOW > 60s]" : ""));

            ScreenshotUtils.attachScreenshotToAllure(driver, "MandatoryCompliances_Displayed");

            if (count == 0) {
                logToAllure("Info", "No mandatory compliances found (count = 0).");
            } else {
                logToAllure("Info", "Mandatory compliances found: " + count);
            }

            // reset for cleanliness
            getStartedStartInstant = null;

        } catch (Throwable t) {
            homePage.handleValidationException("Fetching mandatory compliances", t);
        }
    }


    @When("the user clicks view compliance button")
    public void user_clicks_view_compliance_button() {
        try {
            logStep("🔍 Clicking 'View Compliance' button...");
            homePage.clickViewComplianceButton();
            logToAllure("Clicked", "View Compliance Button");
            ScreenshotUtils.attachScreenshotToAllure(driver, "ViewCompliance_Clicked");
        } catch (Throwable t) {
            homePage.handleValidationException("Clicking 'View Compliance' button", t);
        }

    }


    @Then("the user acknowledges and closes the compliance popup if visible")
    public void acknowledge_and_close_if_visible() {
        try {
            logStep("🔔 Checking if compliance popup is visible...");
            boolean isVisible = homePage.isCompliancePopupVisible();
            logToAllure("Compliance Popup Visible", String.valueOf(isVisible));
            ScreenshotUtils.attachScreenshotToAllure(driver, "CompliancePopup_VisibilityCheck");

            if (isVisible) {
                logStep("👉 Clicking 'Got it' button on compliance popup...");
                homePage.clickGotItButton();
                logToAllure("Action", "'Got it' button clicked");
                ScreenshotUtils.attachScreenshotToAllure(driver, "CompliancePopup_GotItClicked");

                logStep("❌ Closing the guidance popup...");
                homePage.closeGuidancePopup();
                logToAllure("Action", "Guidance popup closed");
                ScreenshotUtils.attachScreenshotToAllure(driver, "CompliancePopup_Closed");

            } else {
                logStep("✅ Compliance popup is not visible. No action needed.");
                logToAllure("Compliance Popup", "Not visible; skipped closing.");
                ScreenshotUtils.attachScreenshotToAllure(driver, "CompliancePopup_NotVisible");
            }

        } catch (Throwable t) {
            homePage.handleValidationException("Compliance popup handling", t);
        }

    }


    @Then("the dropdown should display the created organization name")
    public void verify_dropdown_organization_name() {
        try {
            logStep("🏷️ Verifying created organization name in dropdown...");
            String expected = generatedCompanyName;
            String actualFull = homePage.getSelectedOrganizationName();

            String actual = actualFull.contains(" - ") ? actualFull.split(" - ")[0].trim() : actualFull.trim();

            logToAllure("Expected Org Name", expected);
            logToAllure("Actual Org Name", actual);
            ScreenshotUtils.attachScreenshotToAllure(driver, "OrgName_Dropdown");

            Assert.assertEquals(actual, expected, "❌ Organization name in dropdown does not match the created name.");
        } catch (Throwable t) {
            homePage.handleValidationException("Organization name dropdown verification", t);
        }

    }


    @Then("the Talk to Lawyer widget should be visible")
    public void talk_to_lawyer_widget_should_be_visible() {
        try {
            logStep("🔍 Checking 'Talk to Lawyer' widget visibility on home screen...");
            boolean visible = homePage.isTalkToLawyerVisible();
            logToAllure("Talk to Lawyer Widget Visible", String.valueOf(visible));
            ScreenshotUtils.attachScreenshotToAllure(driver, "TalkToLawyer_Widget");
            Assert.assertTrue(visible, "❌ 'Talk to Lawyer' widget is not visible on the home screen.");
        } catch (Throwable t) {
            homePage.handleValidationException("Talk to Lawyer widget visibility check", t);
        }

    }

    @Then("the Talk to CA widget should be visible")
    public void talk_to_ca_widget_should_be_visible() {
        try {
            logStep("🔍 Checking 'Talk to CA' widget visibility on home screen...");
            boolean visible = homePage.isTalkToCAVisible();
            logToAllure("Talk to CA Widget Visible", String.valueOf(visible));
            ScreenshotUtils.attachScreenshotToAllure(driver, "TalkToCA_Widget");
            Assert.assertTrue(visible, "❌ 'Talk to CA' widget is not visible on the home screen.");
        } catch (Throwable t) {
            homePage.handleValidationException("Talk to CA widget visibility check", t);
        }

    }



    @Then("the following risk categories should be displayed:")
    public void the_following_risk_categories_should_be_displayed(DataTable dataTable) {
        try {
            List<String> expectedCategories = dataTable.asList();

            Map<String, List<String>> result = homePage.validateAndFetchRiskCategories(expectedCategories);
            List<String> missing = result.get("missing");
            List<String> found = result.get("found");

            logToAllure("Expected Risk Categories", expectedCategories.toString());
            logToAllure("✅ Found Categories", found.toString());

            // Print all expected categories with status
            for (String category : expectedCategories) {
                if (found.contains(category)) {
                    System.out.println("✅ Category Found: " + category);
                    logToAllure("✅ Category Found", category);
                } else {
                    System.out.println("❌ Category Missing: " + category);
                    logToAllure("❌ Category Missing", category);
                }
            }

            ScreenshotUtils.attachScreenshotToAllure(driver, "RiskCategories_Displayed");

            if (!missing.isEmpty()) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "RiskCategories_Missing");
                throw new AssertionError("❌ Missing Risk Categories: " + missing + "\nExpected: " + expectedCategories);
            }

        } catch (Throwable t) {
            homePage.handleValidationException("Risk category visibility validation", t);
        }
    }


    @Then("the color for each risk category should be printed")
    public void the_color_for_each_risk_category_should_be_printed() {
        try {
            List<String> categories = List.of("High", "Medium", "Low");
            Map<String, String> colors = homePage.getRiskCategoryColors(categories);

            List<String> notFound = new ArrayList<>();

            for (Map.Entry<String, String> entry : colors.entrySet()) {
                String category = entry.getKey();
                String color = entry.getValue();

                if ("Not Found".equalsIgnoreCase(color) || "Not Visible".equalsIgnoreCase(color)) {
                    notFound.add(category);
                    logToAllure("❌ Color Block Missing or Hidden", category + " → " + color);
                    System.out.println("❌ Color not found or hidden for: " + category);
                } else {
                    String colorName = ColorUtils.getColorName(color);
                    if ("Unknown Color".equals(colorName)) {
                        colorName = ColorUtils.resolveColorName(color);  // fallback for RGB
                    }

                    logToAllure("Color - " + category, color + " (" + colorName + ")");
                    System.out.println("🖍 " + category + " => " + color + " (" + colorName + ")");
                }
            }

            ScreenshotUtils.attachScreenshotToAllure(driver, "RiskCategory_ColorsLogged");

            if (!notFound.isEmpty()) {
                throw new AssertionError("❌ Color blocks not found or hidden for categories: " + notFound);
            }

        } catch (Throwable t) {
            homePage.handleValidationException("Risk category color block validation", t);
        }
    }


    @When("the user fetches the total counts of Risk Based Compliances and Overall Compliances")
    public void fetch_total_counts() {
        try {
            logStep("📊 Fetching Risk Based and Overall Compliances counts from Home page...");
            logger.info("Fetching Risk Based and Overall Compliances counts");

            riskBasedCount = homePage.getRiskBasedCompliancesCount();
            overallCount = homePage.getOverallCompliancesCount();

            logger.info("📊 Risk Based Compliances Count: {}", riskBasedCount);
            logger.info("📊 Overall Compliances Count: {}", overallCount);

            logToAllure("Fetched Risk Based Compliances Count", String.valueOf(riskBasedCount));
            logToAllure("Fetched Overall Compliances Count", String.valueOf(overallCount));

            ScreenshotUtils.attachScreenshotToAllure(driver, "Compliances_Counts_Fetched");

        } catch (Throwable t) {
            homePage.handleValidationException("fetching total compliance counts", t);
        }

    }

    @Then("both counts should match")
    public void verify_counts_match() {
        try {
            logStep("🔍 Verifying Risk Based Compliances count matches Overall Compliances count...");
            logger.info("Verifying: Risk Based [{}] vs Overall [{}]", riskBasedCount, overallCount);

            logToAllure("Risk Based Compliances Count", String.valueOf(riskBasedCount));
            logToAllure("Overall Compliances Count", String.valueOf(overallCount));

            ScreenshotUtils.attachScreenshotToAllure(driver, "Compliances_Count_Comparison");

            Assert.assertEquals(riskBasedCount, overallCount, "❌ Mismatch between Risk Based Compliances and Overall Compliances count");

            logger.info("✅ Counts match successfully");

        } catch (Throwable t) {
            homePage.handleValidationException("Risk Based vs Overall Compliances count comparison", t);
        }
    }

    @When("the user fetches the counts of high, medium, and low risks under Risk Based Compliances")
    public void fetch_risk_levels() {
        try {
            logStep("📊 Fetching High, Medium, and Low Risk counts...");
            logger.info("Fetching risk level counts under Risk Based Compliances");

            highCount = homePage.getHighRiskCount();
            mediumCount = homePage.getMediumRiskCount();
            lowCount = homePage.getLowRiskCount();

            logger.info("📊 High Risk Count: {}", highCount);
            logger.info("📊 Medium Risk Count: {}", mediumCount);
            logger.info("📊 Low Risk Count: {}", lowCount);

            logToAllure("High Risk Count", String.valueOf(highCount));
            logToAllure("Medium Risk Count", String.valueOf(mediumCount));
            logToAllure("Low Risk Count", String.valueOf(lowCount));

            ScreenshotUtils.attachScreenshotToAllure(driver, "Risk_Level_Counts");

        } catch (Throwable t) {
            homePage.handleValidationException("Fetch high/medium/low risk counts", t);
        }

    }


    @Then("the sum of high, medium, and low risk counts should match the total Risk Based Compliances count")
    public void verify_sum_matches_risk_based() {
        try {
            logStep("✅ Validating High + Medium + Low == Risk Based Compliances Total");
            logger.info("🔢 High: {}, Medium: {}, Low: {}", highCount, mediumCount, lowCount);

            int sum = highCount + mediumCount + lowCount;

            logger.info("🧮 Calculated Risk levels Sum: {}", sum);
            logger.info("📊 Risk Based Compliances Count: {}", riskBasedCount);

            logToAllure("Sum of Risk Levels", String.valueOf(sum));
            logToAllure("Risk Based Compliances Count", String.valueOf(riskBasedCount));

            ScreenshotUtils.attachScreenshotToAllure(driver, "Risk_Sum_Validation");

            Assert.assertEquals(sum, riskBasedCount, "❌ Sum of High, Medium, and Low Risk counts does not match Risk Based Compliances count");

            logger.info("✅ Risk level sum matches Risk Based Compliances count");

        } catch (Throwable t) {
            homePage.handleValidationException("Risk level sum validation", t);
        }

    }


    @When("the user clicks each risk category one by one and verifies the count mapping on the Compliance screen")
    public void click_each_category_and_verify_count() {
        String[] categories = {"High", "Medium", "Low"};

        for (String category : categories) {
            try {
                logStep("Starting verification for category: " + category);

                int expectedCount = 0;

                // Step 1: Fetch count with screenshot
                logger.info("📊 Fetching [{}] Risk count from Home page...", category);
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, category + "_RiskCount_HomePage");

                switch (category.toLowerCase()) {
                    case "high":
                        expectedCount = homePage.getHighRiskCount();
                        break;
                    case "medium":
                        expectedCount = homePage.getMediumRiskCount();
                        break;
                    case "low":
                        expectedCount = homePage.getLowRiskCount();
                        break;
                }

                logger.info("👉 [{}] Risk Expected Count: {}", category, expectedCount);
                io.qameta.allure.Allure.addAttachment(category + " Risk Expected Count", String.valueOf(expectedCount));

                // Step 2: Click category with screenshot
                logger.info("🖱️ Clicking on [{}] category...", category);
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, category + "_BeforeClick");

                switch (category.toLowerCase()) {
                    case "high":
                        homePage.clickHighCategory();
                        break;
                    case "medium":
                        homePage.clickMediumCategory();
                        break;
                    case "low":
                        homePage.clickLowCategory();
                        break;
                }

                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, category + ": After Clicking " + category + " ,Navigating to Compliance page");

                // Step 3: Validate count on Compliance page with screenshot
                logger.info("🔍 Validating [{}] Risk count on Compliance screen...", category);
                int actualCount = homePage.getAllTabComplianceCountFromCompliancePage();

                logger.info("✅ [{}] Risk Compliance Page Count Fetched: {}", category, actualCount);
                io.qameta.allure.Allure.addAttachment(category + " Risk Compliance Page Count", String.valueOf(actualCount));
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, category + "_CompliancePageCount");

                Assert.assertEquals(actualCount, expectedCount, "❌ Mismatch in " + category + " risk count on Compliance screen");

                String matchMessage = "✅ " + category + " Risk count matched successfully. Expected: " + expectedCount + ", Actual: " + actualCount;
                logger.info(matchMessage);
                io.qameta.allure.Allure.addAttachment(category + " Risk Count Validation Result", matchMessage);

                // Step 4: Navigate back to home page with screenshot
                logger.info("↩️ Navigating back to Home page after verifying [{}]...", category);
                boolean isHome = homePage.navigateAndConfirmHomePage();
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, category + "_HomePage_AfterReturn");

                Assert.assertTrue(isHome, "❌ Failed to navigate back to Home page after " + category);
                logger.info("✅ Successfully returned to Home page after [{}].", category);

            } catch (Throwable t) {
                homePage.handleValidationException("Click and verify for category: " + category, t);
            }

        }
    }


    @When("the user hovers over each risk category one by one and captures bifurcation popup screenshots")
    public void hover_and_capture_bifurcation_screenshots() {
        String[] categories = {"High", "Medium", "Low"};

        for (String category : categories) {
            try {
                logger.info("🔄 Hovering over category: {}", category);
                logToAllure("Hovering Category", category);

                homePage.hoverOverRiskCategory(category);

                Thread.sleep(1500);

                ScreenshotUtils.attachScreenshotToAllure(driver, category + "_BifurcationTooltip");
                logger.info("📸 Screenshot captured for category: {}", category);
                logToAllure(category + " Tooltip Screenshot", "Captured");

            } catch (Throwable t) {
                homePage.handleValidationException("Bifurcation popup for category: " + category, t);
            }

        }
    }


    @When("the user fetches the column headers from the Compliance Report section")
    public void fetch_column_headers_from_home_page() {
        try {
            logStep("✅ Fetching column headers from the Compliance Report section on Home page");

            homePageHeaders = homePage.getComplianceReportColumnHeaders(); // Scroll included inside this method now

            logger.info("📥 Fetched Home Page Compliance Report headers: {}", homePageHeaders);
            logToAllure("Home Page Compliance Report Headers", homePageHeaders.toString());

            ScreenshotUtils.attachScreenshotToAllure(driver, "Home_Page_Headers_Fetch");

        } catch (Throwable t) {
            homePage.handleValidationException("Home page column headers fetch", t);
        }

    }


    @Then("the user navigates to the Compliance page and validates that the same column headers appear")
    public void validate_column_headers_on_compliance_page() {
        try {
            logStep("✅ Navigating to Compliance page and validating column headers");

            Assert.assertTrue(homePage.goToCompliancePage(), "❌ Compliance page not loaded!");

            List<String> compliancePageHeaders = homePage.getCompliancePageColumnHeaders();

            List<String> lowerHomeHeaders = new ArrayList<String>();
            for (int i = 0; i < homePageHeaders.size(); i++) {
                lowerHomeHeaders.add(homePageHeaders.get(i).trim().toLowerCase());
            }

            List<String> lowerComplianceHeaders = new ArrayList<String>();
            for (int i = 0; i < compliancePageHeaders.size(); i++) {
                lowerComplianceHeaders.add(compliancePageHeaders.get(i).trim().toLowerCase());
            }

            Collections.sort(lowerHomeHeaders);
            Collections.sort(lowerComplianceHeaders);

            logger.info("------ Comparing Home Page Headers vs Compliance Page Headers (Unordered, Case-Insensitive) ------");
            logger.info("Home Page Headers : {}", lowerHomeHeaders);
            logger.info("Compliance Page Headers : {}", lowerComplianceHeaders);

            logToAllure("Home Page Headers ", lowerHomeHeaders.toString());
            logToAllure("Compliance Page Headers ", lowerComplianceHeaders.toString());

            if (lowerHomeHeaders.size() != lowerComplianceHeaders.size()) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "Compliance_Header_Size_Mismatch");
                Assert.fail("❌ Header list sizes do not match!");
            }

            for (int i = 0; i < lowerHomeHeaders.size(); i++) {
                logger.info("Position {} -> Home Page: '{}' | Compliance Page: '{}'", (i + 1), lowerHomeHeaders.get(i), lowerComplianceHeaders.get(i));

                if (!lowerHomeHeaders.get(i).equals(lowerComplianceHeaders.get(i))) {
                    ScreenshotUtils.attachScreenshotToAllure(driver, "Compliance_Header_Content_Mismatch");
                    Assert.fail("❌ Mismatch in header (ignoring case) at position: " + (i + 1));
                }
            }

            logger.info("✅ Compliance page headers content matches Home page headers content (ignoring order and case)");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Compliance_Header_Validation_Success");

        } catch (Throwable t) {
            homePage.handleValidationException("Compliance page header validation", t);
        }

    }

    @When("the user clicks the View all Compliance button")
    public void click_view_all_compliance_button() {
        try {
            logStep("📊 Scrolling to Compliance Report section...");
            homePage.scrollToComplianceReportSection();
            ScreenshotUtils.attachScreenshotToAllure(driver, "Compliance_Report_Section_Scrolled");

            logStep("📊 Fetching Overall Compliance Count from Home page...");
            overallCount = homePage.getOverallCompliancesCount();
            logger.info("📊 Overall Compliance Count displayed in home page: {}", overallCount);
            logToAllure("Fetched Overall Compliance Count from home page", String.valueOf(overallCount));
            ScreenshotUtils.attachScreenshotToAllure(driver, "Overall_Compliance_Count_Fetched");

            logStep("📥 Clicking the 'View all Compliance' button...");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Clicking_View_All_Compliance");

            homePage.clickViewAllCompliance();

            // Start timer AFTER the click
            NavContext.start("Compliances");

            logger.info("✅ Successfully clicked 'View all Compliance' button");
            logToAllure("Clicked View all Compliance Button", "Successfully clicked 'View all Compliance' button");
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Clicking_View_All_Compliance");


        } catch (Throwable t) {
            homePage.handleValidationException("Overall count fetch or 'View all Compliance' click", t);
        }
    }

    @Then("the user should be navigated to the Compliances screen showing all sections")
    public void verify_user_is_on_compliances_screen_and_should_see_all_sections() {
        try {
            logStep("✅ Verifying user is navigated to the Compliances screen showing all sections...");
            Instant fallbackStart = Instant.now();
            boolean ready = homePage.isComplianceScreenVisible(Duration.ofSeconds(20));

            // Stop click→ready timer
            java.time.Duration nav = utils.NavContext.stopDuration();
            long elapsedMs = (nav.toMillis() > 0) ? nav.toMillis() : java.time.Duration.between(fallbackStart, Instant.now()).toMillis();
            double elapsedSec = elapsedMs / 1000.0;

            // Same timing log style you use elsewhere
            logToAllure("⏱️ Load Time (Compliances)", String.format("%.2f seconds", elapsedSec));

            // Threshold handling (warn >10s, fail >20s) and ready flag
            if (!ready) {
                String msg = "❌ Compliance page not ready within 20s (click→ready: " + String.format("%.2f s", elapsedSec) + ")";
                logToAllure("❌ Destination Failure", msg);
                throw new AssertionError(msg);
            }
            if (elapsedMs > 20_000) {
                String msg = "❌ Compliances exceeded 20s (actual: " + String.format("%.2f s", elapsedSec) + ")";
                logToAllure("❌ Load Time Failure", msg);
                throw new AssertionError(msg);
            } else if (elapsedMs > 10_000) {
                String msg = "⚠️ Compliances exceeded 10s (actual: " + String.format("%.2f s", elapsedSec) + ")";
                logger.warn(msg);
                logToAllure("⚠️ Load Time Warning", msg);
            }

            if (!homePage.isComplianceScreenVisible()) {
                throw new AssertionError("❌ Compliance Page header is not visible or the expected element is missing.");
            }

            logger.info("✅ Compliance Page header is visible.");
            logToAllure("Compliance Page Navigation", "User successfully navigated to the Compliance Page and header is visible.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Compliance_Page_Navigation_Verified");

        } catch (Throwable t) {
            homePage.handleValidationException("Compliance Page navigation verification", t);
        }
    }


    @Then("the compliance count should match the Overall compliance count")
    public void verify_compliance_count_matches_overall_count() {
        try {
            logStep("✅ Verifying Compliance count matches Overall Compliance count...");

            int allTabCount = homePage.getAllTabComplianceCountFromCompliancePage();

            logger.info("📊 Overall Count from Home page: {}", overallCount);
            logger.info("📊 All Tab Count from Compliance page: {}", allTabCount);

            logToAllure("Overall Compliance Count", String.valueOf(overallCount));
            logToAllure("All Tab Compliance Count", String.valueOf(allTabCount));

            ScreenshotUtils.attachScreenshotToAllure(driver, "Compliance_Counts_Comparison");

            if (overallCount != allTabCount) {
                throw new AssertionError("❌ Compliance count mismatch: Overall Count = " + overallCount + ", All Tab Count = " + allTabCount);
            }

            logger.info("✅ Compliance count matches successfully.");
            logToAllure("Compliance Count Match", "Compliance count matches with overall count successfully.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Compliance_Count_Match_Success");

        } catch (Throwable t) {
            homePage.handleValidationException("compliance count match verification", t);
        }

    }

    @When("the user selects any one record from the Compliance report section")
    public void select_any_one_record_from_compliance_report_section() {
        try {
            logStep("📥 Attempting to select any one record from the Compliance Report section...");

            int totalRecords = homePage.getComplianceReportRowCount();
            logger.info("📊 Total Records Found in Compliance Report: {}", totalRecords);
            logToAllure("Compliance Report Total Records", String.valueOf(totalRecords));

            if (totalRecords > 0) {
                homePage.selectAnyComplianceReportRecord();
                logger.info("✅ Successfully clicked on the first record from Compliance Report section.");
                logToAllure("Compliance Report Record Selection", "First record selected successfully.");
                ScreenshotUtils.attachScreenshotToAllure(driver, "Compliance_Report_Record_Selected");
            } else {
                logger.warn("⚠️ No records available to select in Compliance Report section.");
                logToAllure("Compliance Report Record Selection", "No records available to select.");
                ScreenshotUtils.attachScreenshotToAllure(driver, "Compliance_Report_No_Records");
            }

        } catch (Throwable t) {
            homePage.handleValidationException("selecting a record from Compliance Report section", t);
        }

    }


    @Then("the system should display detailed information of the selected compliance on the right side")
    public void verify_selected_compliance_details_displayed() {
        try {
            logStep("✅ Verifying selected compliance details panel visibility...");

            if (homePage.getComplianceReportRowCount() == 0) {
                logger.warn("⚠️ Skipping compliance details panel check as there were no records to select.");
                logToAllure("Compliance Details Panel Verification", "Skipped: No records available.");
                return;
            }

            boolean isDetailsVisible = homePage.isComplianceDetailsPanelVisible();
            logger.info("📊 Compliance Details Panel Visibility Status: {}", isDetailsVisible);
            logToAllure("Compliance Details Panel Visibility", String.valueOf(isDetailsVisible));

            ScreenshotUtils.attachScreenshotToAllure(driver, "Compliance_Details_Panel_Check");

            if (!isDetailsVisible) {
                throw new AssertionError("❌ Compliance details panel is not visible after selecting a record.");
            }

            logger.info("✅ Compliance details panel is visible as expected.");
            logToAllure("Compliance Details Panel Verification", "Compliance details panel displayed successfully.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Compliance_Details_Panel_Verified");
        } catch (Throwable t) {
            homePage.handleValidationException("compliance details panel visibility verification", t);
        }
    }


    @Then("Month and Year dropdowns should be displayed")
    public void month_and_year_dropdowns_should_be_displayed() {
        try {
            logStep("📋 Verifying that Month and Year dropdowns are displayed on the Compliance page...");

            boolean isMonthVisible = homePage.isMonthLabelVisible();
            boolean isYearVisible = homePage.isYearLabelVisible();

            if (isMonthVisible && isYearVisible) {
                logger.info("✅ Both Month and Year dropdowns are visible.");
                logToAllure("Dropdown Visibility", "Month and Year dropdowns are displayed.");
                ScreenshotUtils.attachScreenshotToAllure(driver, "Dropdowns_Visible");
            } else {
                if (!isMonthVisible) {
                    logger.warn("⚠️ Month dropdown is not visible.");
                    logToAllure("Month Dropdown", "Month dropdown not visible.");
                }
                if (!isYearVisible) {
                    logger.warn("⚠️ Year dropdown is not visible.");
                    logToAllure("Year Dropdown", "Year dropdown not visible.");
                }
                ScreenshotUtils.attachScreenshotToAllure(driver, "Dropdowns_Not_Visible");
                Assert.fail("Month or Year dropdown not visible on the Compliance page.");
            }

        } catch (Throwable t) {
            homePage.handleValidationException("verifying Month and Year dropdown visibility", t);
        }
    }


    @Then("Month dropdown should display the current month")
    public void month_dropdown_should_display_the_current_month() {
        try {
            logStep("📅 Verifying that the Month dropdown displays the current month...");

            String expectedMonth = homePage.getCurrentMonthName();
            String actualMonth = homePage.getSelectedMonth();

            logger.info("Expected Month: {}", expectedMonth);
            logger.info("Actual Month: {}", actualMonth);
            logToAllure("Expected Month", expectedMonth);
            logToAllure("Actual Month", actualMonth);

            if (!expectedMonth.equalsIgnoreCase(actualMonth)) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "Incorrect_Month_Dropdown");
                Assert.fail("❌ Month dropdown default value mismatch.");
            }

            logger.info("✅ Month dropdown displays the correct current month.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Correct_Month_Dropdown");

        } catch (Throwable t) {
            homePage.handleValidationException("verifying Month dropdown default value", t);
        }
    }


    @Then("Year dropdown should display the current year")
    public void year_dropdown_should_display_the_current_year() {
        try {
            logStep("📆 Verifying that the Year dropdown displays the current year...");

            String expectedYear = homePage.getCurrentYear();
            String actualYear = homePage.getSelectedYear();

            logger.info("Expected Year: {}", expectedYear);
            logger.info("Actual Year: {}", actualYear);
            logToAllure("Expected Year", expectedYear);
            logToAllure("Actual Year", actualYear);

            if (!expectedYear.equals(actualYear)) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "Incorrect_Year_Dropdown");
                Assert.fail("❌ Year dropdown default value mismatch.");
            }

            logger.info("✅ Year dropdown displays the correct current year.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Correct_Year_Dropdown");

        } catch (Throwable t) {
            homePage.handleValidationException("verifying Year dropdown default value", t);
        }
    }


    @Then("the record count under the Compliance Calendar should be in sync with the Overall Compliances")
    public void verify_record_count_sync_with_overall_compliances() {
        try {
            logStep("🔢 Verifying that the record count under the Compliance Calendar is in sync with the Overall Compliances...");

            int calendarCount = homePage.getComplianceCalendarRecordCount();
            int overallCount = homePage.getOverallCompliancesCount();

            logger.info("📊 Compliance Calendar Record Count: {}", calendarCount);
            logger.info("📊 Overall Compliances Count: {}", overallCount);
            logToAllure("Compliance Calendar Count", String.valueOf(calendarCount));
            logToAllure("Overall Compliances Count", String.valueOf(overallCount));

            if (calendarCount != overallCount) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "Record_Count_Mismatch");
                Assert.fail("❌ Record count under Compliance Calendar does not match with Overall Compliances.");
            }

            logger.info("✅ Record count is correctly synced.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Record_Count_Synced");

        } catch (Throwable t) {
            homePage.handleValidationException("verifying Compliance Calendar and Overall Compliances record count sync", t);
        }
    }


    @When("the user selects a different Month and Year from the dropdowns")
    public void user_selects_different_month_and_year_from_dropdowns() {
        try {
            logStep("🔀 Selecting a different Month and Year from the Compliance Calendar dropdowns...");

            // Get current month and year from UI
            String currentMonth = homePage.getSelectedMonth();
            String currentYear = homePage.getSelectedYear();

            logger.info("Current Month: {}", currentMonth);
            logger.info("Current Year: {}", currentYear);

            // Generate different random values
            String newMonth = TestDataGenerator.getRandomMonthExcluding(currentMonth);
            String newYear = TestDataGenerator.getRandomYearExcluding(currentYear);

            logger.info("Switching to Month: {}", newMonth);
            logger.info("Switching to Year: {}", newYear);
            logToAllure("New Month", newMonth);
            logToAllure("New Year", newYear);

            // Perform selection
            homePage.selectMonthFromDropdown(newMonth);
            homePage.selectYearFromDropdown(newYear);

            logger.info("✅ Successfully selected new Month and Year.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "New_Month_Year_Selected");

        } catch (Throwable t) {
            homePage.handleValidationException("selecting new Month and Year from dropdowns", t);
        }
    }


    @Then("the due records should match the selected Month and Year, with correct count")
    public void verify_records_fetched_by_selected_month_and_year() {
        try {
            logStep("📅 Verifying records reflect selected Month and Year (count only) with year lock...");

            // 1) Read selections and lock the year
            String selectedMonth = homePage.getSelectedMonth();
            String selectedYear = homePage.getSelectedYear();
            String lockedYear = selectedYear;  // lock it

            logger.info("Selected Month: {}", selectedMonth);
            logger.info("Selected Year (Locked): {}", lockedYear);
            logToAllure("📌 Selected Month", selectedMonth);
            logToAllure("📌 Selected Year (Locked)", lockedYear);

            // 2) Wait for filter to apply
            homePage.waitForMonthYearToApply(selectedMonth, lockedYear);

            // 3) Guard: retry if the year flips
            final int maxRetries = 2;
            int attempt = 0;
            while (attempt <= maxRetries) {
                String currentYear = homePage.getSelectedYear();
                if (lockedYear.equals(currentYear)) {
                    break; // year is stable
                }

                String msg = String.format("Year flipped after filter. Locked=%s, Current=%s. Re-selecting...", lockedYear, currentYear);
                logger.warn(msg);
                logToAllure("🔒 Year Flip Detected", msg);

                homePage.selectYearFromDropdown(lockedYear);
                homePage.waitForMonthYearToApply(selectedMonth, lockedYear);
                attempt++;
            }

            // Final check: if still wrong, fail
            String finalYear = homePage.getSelectedYear();
            if (!lockedYear.equals(finalYear)) {
                String err = String.format("❌ Year changed unexpectedly and could not be locked. Locked=%s, Current=%s", lockedYear, finalYear);
                logger.error(err);
                logToAllure("❌ Year Lock Failed", err);
                Assert.fail(err);
            }

            // 4) Fetch counts
            List<String> allDueDates = homePage.getAllDueDatesSmart();
            int listCount = allDueDates.size();
            int uiCount = homePage.getComplianceCalendarRecordCount();

            logger.info("Fetched {} records from list", listCount);
            logger.info("Compliance Calendar (UI) filtered count: {}", uiCount);
            logToAllure("📥 Fetched Record Count (List)", String.valueOf(listCount));
            logToAllure("🧮 UI Filtered Count", String.valueOf(uiCount));

            // 5) Verify counts match (TestNG style)
            Assert.assertEquals(listCount, uiCount, "❌ Filtered record count mismatch!");

            ScreenshotUtils.attachScreenshotToAllure(driver, "Filtered_Records_By_Month_Year_Count_Only");
            logger.info("✅ Count matches for selected Month={} and Year={}", selectedMonth, lockedYear);
            logToAllure("✅ Success", "Count matches and year is locked.");

        } catch (Throwable t) {
            homePage.handleValidationException("validating filtered records by Month/Year with year lock", t);
        }
    }


    @And("a random Compliance Calendar record should open its detail panel with correct Due Date")
    public void verify_random_record_opens_detail_with_correct_due_date() {
        try {
            logStep("🖱️ Clicking a random Compliance Calendar record...");

            String selectedMonth = homePage.getSelectedMonth();  // e.g., "July"
            String selectedYear = homePage.getSelectedYear();    // e.g., "2025"
            String expectedMonthAbbr = TestDataGenerator.getMonthAbbreviation(selectedMonth); // e.g., "Jul"

            String clickedRecord = homePage.clickRandomComplianceRecord();

            if (clickedRecord == null) {
                logger.warn("⚠️ No records available to click.");
                ScreenshotUtils.attachScreenshotToAllure(driver, "No_Record_Available");
                return;
            }

            logger.info("📝 Clicked Record: {}", clickedRecord);
            Thread.sleep(1000); // Allow panel to render

            String dueDate = homePage.getRightPanelDueDate();
            logger.info("📅 Due Date from panel: {}", dueDate);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Right_Panel_Due_Date");

            // Extract month and year from dueDate
            String[] parts = dueDate.split(" ");
            String dueMonth = parts[1];
            String dueYear = parts[2];

            if (!dueMonth.equalsIgnoreCase(expectedMonthAbbr) || !dueYear.equals(selectedYear)) {
                throw new AssertionError(String.format("❌ Mismatch: Expected [%s %s] but found [%s]", expectedMonthAbbr, selectedYear, dueDate));
            }

            logger.info("✅ Right panel due date matches the selected Month and Year.");

        } catch (Throwable t) {
            homePage.handleValidationException("validating detail panel due date for clicked record", t);
        }
    }


}

