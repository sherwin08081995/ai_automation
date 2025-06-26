package stepDefinitions;

import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import pages.HomePage;
import utils.LoggerUtils;
import utils.ScreenshotUtils;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;


/**
 * HomePageValidationSteps.java
 *
 * Purpose:
 * Step Definitions for validating the Home Page UI and functionality using Cucumber BDD.
 * This class handles:
 *
 * ✅ Visibility and validation of the left side menu
 * ✅ Accessibility checks for menu items
 * ✅ Verification of the Overall Compliances section and its count
 * ✅ Sum validation of Overall Compliances based on status tabs
 * ✅ Cross-verification of tab counts and corresponding section records
 * ✅ Screenshot attachment for each validation step (Allure Reporting)
 *
 * Related Classes:
 * - HomePage.java (Page Object Model for Home Page)
 * - ScreenshotUtils.java (Handles Allure screenshot capture)
 * - LoggerUtils.java (Log4j2 logger utility)
 * - Hooks.java (Manages WebDriver setup and teardown)
 *
 * Author:
 * @author Sherwin
 * @since 26-06-2025
 *
 */


public class HomePageValidationSteps {

    WebDriver driver = Hooks.driver;
    HomePage homePage;
    Logger logger;

    public HomePageValidationSteps() {
        this.driver = Hooks.driver;
        this.homePage = new HomePage(driver);
        this.logger = LoggerUtils.getLogger(getClass());
    }

    @When("the user views the left side menu")
    public void the_user_views_the_left_side_menu() {
        try {
            boolean isVisible = homePage.isLeftMenuVisible();
            logStep("Left menu is visible: " + isVisible);
            logger.info("Left menu visible: {}", isVisible);
            ScreenshotUtils.attachScreenshotToAllure(driver, "LeftMenuVisible");

            Assert.assertTrue(isVisible, "Left menu is not visible");

        } catch (AssertionError ae) {
            logger.error("Assertion failed while checking left menu: {}", ae.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "LeftMenu_AssertionError");
            throw ae;
        } catch (Exception e) {
            logger.error("Exception while verifying left menu: {}", e.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "LeftMenu_Exception");
            throw e;
        }
    }

    @Then("the following menu items should be visible:")
    public void the_following_menu_items_should_be_visible(DataTable dataTable) {
        List<String> expectedMenus = dataTable.asList();
        List<String> actualMenus = homePage.getLeftMenuItems();
        SoftAssert softAssert = new SoftAssert();

        for (String menu : expectedMenus) {
            try {
                // 🔁 SUGGESTION: Use case-insensitive matching
                boolean found = actualMenus.stream().anyMatch(item -> item.equalsIgnoreCase(menu));
                String message = "Menu visible: " + menu + " - " + found;
                logStep(message);
                logger.info(message);
                ScreenshotUtils.attachScreenshotToAllure(driver, "MenuItem_" + menu);
                softAssert.assertTrue(found, "Expected menu item not found in left navigation: " + menu); // 🔁 Message improved
            } catch (Exception e) {
                logger.error("Exception while checking menu '{}': {}", menu, e.getMessage());
                ScreenshotUtils.attachScreenshotToAllure(driver, "MenuItem_Exception_" + menu);
                softAssert.fail("Exception while validating menu item: " + menu + " - " + e.getMessage());
            }
        }

        logStep("Completed validation for left menu items.");
        softAssert.assertAll();
    }

    @Then("the user should be able to access the following menu items:")
    public void the_user_should_be_able_to_access_the_following_menu_items(DataTable dataTable) {
        List<String> menuItems = dataTable.asList();
        SoftAssert softAssert = new SoftAssert();

        for (String menuItem : menuItems) {
            try {
                homePage.clickLeftMenu(menuItem.trim());
                boolean success = homePage.waitForMenuPageToLoad(menuItem.trim());

                logStep("Accessed and loaded: " + menuItem);
                logger.info("Accessed menu item: {}", menuItem);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Access_" + menuItem);

                if (success) driver.navigate().refresh();
                softAssert.assertTrue(success, "Unable to access or verify menu item: " + menuItem);
            } catch (Exception e) {
                logger.error("Exception for menu '{}': {}", menuItem, e.getMessage());
                ScreenshotUtils.attachScreenshotToAllure(driver, "AccessException_" + menuItem);
                softAssert.fail("Exception while accessing menu item: " + menuItem + " - " + e.getMessage());
            }
        }

        logStep("Completed access validation for menu items.");
        softAssert.assertAll();
    }

    @When("the user views the Overall Compliances section")
    public void the_user_views_the_Overall_Compliances_section() {
        try {
            boolean isVisible = driver.findElement(By.xpath("//p[contains(normalize-space(),'Overall Compliances')]/span")).isDisplayed();
            logStep("Overall Compliance section visible: " + isVisible);
            logger.info("Overall Compliances section visibility: {}", isVisible);
            ScreenshotUtils.attachScreenshotToAllure(driver, "OverallComplianceSection");

            SoftAssert softAssert = new SoftAssert();
            softAssert.assertTrue(isVisible, "Overall Compliance section not visible");
            softAssert.assertAll();
        } catch (Exception e) {
            logger.error("Exception viewing Overall Compliances section: {}", e.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "OverallCompliance_Exception");
            throw new AssertionError("Overall Compliance section is not visible.");
        }
    }

    @Then("the Overall Compliances count should be displayed properly")
    public void the_Overall_Compliances_count_should_be_displayed_properly() {
        try {
            int actualCount = homePage.getOverallCompliancesCount();
            logStep("Overall Compliances count: " + actualCount);
            logger.info("Actual compliance count: {}", actualCount);
            ScreenshotUtils.attachScreenshotToAllure(driver, "OverallCount");

            SoftAssert softAssert = new SoftAssert();
            softAssert.assertTrue(actualCount >= 0, "Overall Compliance count is invalid.");
            softAssert.assertAll();
        } catch (Exception e) {
            logger.error("Error reading overall compliance count: {}", e.getMessage());
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
                expectedTotal += tabCount;
            }

            int actualTotal = homePage.getOverallCompliancesCount();
            logStep("Expected: " + expectedTotal + ", Actual: " + actualTotal);
            logger.info("Expected: {}, Actual: {}", expectedTotal, actualTotal);
            ScreenshotUtils.attachScreenshotToAllure(driver, "OverallComplianceMatch");

            softAssert.assertEquals(actualTotal, expectedTotal, "Mismatch in compliance count");
            softAssert.assertAll();
        } catch (Exception e) {
            logger.error("Compliance sum mismatch or exception: {}", e.getMessage());
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
                int displayedTabCount = homePage.getDisplayedTabCount(tabName);
                logger.info("Displayed Count for {}: {}", tabName, displayedTabCount);
                logStep("Displayed Count for " + tabName + ": " + displayedTabCount);
                ScreenshotUtils.attachScreenshotToAllure(driver, "DisplayedCount_" + tabName);

                int actualTabCount = homePage.clickComplianceStatusTabAndGetCount(tabName);
                logger.info("Clicked tab {} → Actual count: {}", tabName, actualTabCount);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ClickedTab_" + tabName);

                softAssert.assertEquals(actualTabCount, displayedTabCount, "Mismatch in displayed vs actual tab count for: " + tabName);

                boolean isOnComplianceScreen = homePage.isComplianceScreenVisible();
                logStep("Compliance screen visible: " + isOnComplianceScreen);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ComplianceScreen_" + tabName);
                softAssert.assertTrue(isOnComplianceScreen, "Compliance screen not visible for tab: " + tabName);

                int recordCount = homePage.getRecordCountForSection(sectionName);
                logStep("Record count for section " + sectionName + ": " + recordCount);
                logger.info("Section {} → Record count: {}", sectionName, recordCount);
                ScreenshotUtils.attachScreenshotToAllure(driver, "SectionRecord_" + sectionName);

                softAssert.assertEquals(recordCount, actualTabCount, "Count mismatch for section: " + sectionName);

                boolean navigatedBack = homePage.goBackToHomePage();
                logStep("Returned to Home from tab: " + tabName + " - " + navigatedBack);
                ScreenshotUtils.attachScreenshotToAllure(driver, "BackToHome_" + tabName);
                softAssert.assertTrue(navigatedBack, "Failed to return to Home from tab: " + tabName);

            } catch (Exception e) {
                logger.error("Exception during validation for tab {}: {}", tabName, e.getMessage());
                ScreenshotUtils.attachScreenshotToAllure(driver, "TabValidation_Exception_" + tabName);
                softAssert.fail("Exception during tab validation: " + tabName + " - " + e.getMessage());
            }
        }

        softAssert.assertAll();
    }

    @Step("{message}")
    public void logStep(String message) {
        logger.info(message);  // Console + Allure logging
    }
}
