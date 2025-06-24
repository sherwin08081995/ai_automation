package stepDefinitions;

import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.HomePage;
import utils.ScreenshotUtils;

import java.util.List;
import java.util.Map;

/**
 * Home feature steps will be mapped here
 *
 * @author Sherwin
 * @since 17-06-2025
 */

public class HomePageValidationSteps {
    WebDriver driver = Hooks.driver;
    HomePage homePage;

    public HomePageValidationSteps() {
        this.driver = Hooks.driver;
        this.homePage = new HomePage(driver);
    }

    @When("the user views the left side menu")
    public void the_user_views_the_left_side_menu() throws InterruptedException {
        try {
            boolean isVisible = homePage.isLeftMenuVisible();
            Thread.sleep(2000);
            logStep("Left menu is visible: " + isVisible);
            ScreenshotUtils.attachScreenshotToAllure(driver, "LeftMenuVisible");
            Assert.assertTrue(isVisible, "Left menu is not visible");
        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "LeftMenu_Exception");
            throw e;
        }
    }

    @Then("the following menu items should be visible:")
    public void the_following_menu_items_should_be_visible(DataTable dataTable) {
        List<String> expectedMenus = dataTable.asList();
        List<String> actualMenus = homePage.getLeftMenuItems();

        for (String menu : expectedMenus) {
            boolean found = actualMenus.contains(menu);
            logStep("Menu visible: " + menu + " - " + found);
            ScreenshotUtils.attachScreenshotToAllure(driver, "MenuItem_" + menu);
            Assert.assertTrue(found, "Menu not found: " + menu);
        }
    }

    @Then("the user should be able to access the following menu items:")
    public void the_user_should_be_able_to_access_the_following_menu_items(DataTable dataTable) {
        List<String> menuItems = dataTable.asList();

        for (String menuItem : menuItems) {
            try {
                homePage.clickLeftMenu(menuItem.trim());
                boolean success = homePage.waitForMenuPageToLoad(menuItem.trim());
                logStep("Accessed and loaded: " + menuItem);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Access_" + menuItem);

                if (success) driver.navigate().refresh();
                Assert.assertTrue(success, "Unable to access or verify menu item: " + menuItem);
            } catch (Exception e) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "AccessException_" + menuItem);
                Assert.fail("Unable to access or verify menu item: " + menuItem);
            }
        }
    }

    @When("the user views the Overall Compliances section")
    public void the_user_views_the_Overall_Compliances_section() {
        try {
            Thread.sleep(2000);
            boolean isVisible = driver.findElement(By.xpath("//p[contains(normalize-space(),'Overall Compliances')]/span")).isDisplayed();
            logStep("Overall Compliance section visible: " + isVisible);
            ScreenshotUtils.attachScreenshotToAllure(driver, "OverallComplianceSection");
            Assert.assertTrue(isVisible, "Overall Compliance section not visible");
        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "OverallCompliance_Exception");
            throw new AssertionError("Overall Compliance section is not visible.");
        }
    }

    @Then("the Overall Compliances count should be displayed properly")
    public void the_Overall_Compliances_count_should_be_displayed_properly() {
        int actualCount = homePage.getOverallCompliancesCount();
        logStep("Overall Compliances count: " + actualCount);
        ScreenshotUtils.attachScreenshotToAllure(driver, "OverallCount");
        Assert.assertTrue(actualCount >= 0, "Overall Compliance count is invalid.");
    }

    @Then("the Overall Compliances should be the sum of:")
    public void the_Overall_Compliances_should_be_the_sum_of(DataTable table) {
        List<String> allRows = table.asList();
        List<String> tabs = allRows.subList(1, allRows.size());
        int expectedTotal = 0;

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
        ScreenshotUtils.attachScreenshotToAllure(driver, "OverallComplianceMatch");
        Assert.assertEquals(actualTotal, expectedTotal, "Mismatch in compliance count");
    }

    @Then("the user clicks each Compliance status tab, navigates to the Compliance section, and validates that the counts match for each tab")
    public void the_user_clicks_each_Compliance_status_tab_navigates_to_the_Compliance_section_and_validates_that_the_counts_match_for_each_tab(DataTable dataTable) throws InterruptedException {
        List<Map<String, String>> tabSectionPairs = dataTable.asMaps();

        for (Map<String, String> pair : tabSectionPairs) {
            String tabName = pair.get("Tab");
            String sectionName = pair.get("Section");

            try {
                int displayedTabCount = homePage.getDisplayedTabCount(tabName);
                logStep("Displayed Count for " + tabName + ": " + displayedTabCount);
                ScreenshotUtils.attachScreenshotToAllure(driver, "DisplayedCount_" + tabName);

                int actualTabCount = homePage.clickComplianceStatusTabAndGetCount(tabName);
                Thread.sleep(3000);
                logStep("Clicked tab " + tabName + " and got count: " + actualTabCount);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ClickedTab_" + tabName);

                Assert.assertEquals(actualTabCount, displayedTabCount, "Mismatch in displayed vs actual tab count for: " + tabName);

                boolean isOnComplianceScreen = homePage.isComplianceScreenVisible();
                logStep("Compliance screen visible: " + isOnComplianceScreen);
                ScreenshotUtils.attachScreenshotToAllure(driver, "ComplianceScreen_" + tabName);
                Assert.assertTrue(isOnComplianceScreen, "Compliance screen not visible for tab: " + tabName);

                int recordCount = homePage.getRecordCountForSection(sectionName);
                logStep("Record count for section " + sectionName + ": " + recordCount);
                ScreenshotUtils.attachScreenshotToAllure(driver, "SectionRecord_" + sectionName);
                Assert.assertEquals(recordCount, actualTabCount, "Count mismatch for section: " + sectionName);

                boolean navigatedBack = homePage.goBackToHomePage();
                logStep("Returned to Home from tab: " + tabName + " - " + navigatedBack);
                ScreenshotUtils.attachScreenshotToAllure(driver, "BackToHome_" + tabName);
                Assert.assertTrue(navigatedBack, "Failed to return to Home from tab: " + tabName);
            } catch (Exception e) {
                ScreenshotUtils.attachScreenshotToAllure(driver, "TabValidation_Exception_" + tabName);
                throw e;
            }
        }
    }

    @Step("{message}")
    public void logStep(String message) {
        // Allure log
    }
}
