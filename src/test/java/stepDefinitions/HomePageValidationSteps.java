package stepDefinitions;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.HomePage;
import utils.ExtentTestManager;
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
    ExtentTest test;

    public HomePageValidationSteps() {
        this.driver = Hooks.driver;
        this.homePage = new HomePage(driver);
        this.test = ExtentTestManager.getTest();
    }

    @When("the user views the left side menu")
    public void the_user_views_the_left_side_menu() throws InterruptedException {
        boolean isVisible = homePage.isLeftMenuVisible();
        Thread.sleep(2000);
        test.log(isVisible ? Status.PASS : Status.FAIL, isVisible ? "Left menu is visible." : "Left menu is not visible.", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
        test.log(Status.INFO, "<img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
        Assert.assertTrue(isVisible, "Left menu is not visible");
    }

    @Then("the following menu items should be visible:")
    public void the_following_menu_items_should_be_visible(DataTable dataTable) {
        List<String> expectedMenus = dataTable.asList();
        List<String> actualMenus = homePage.getLeftMenuItems();

        for (String menu : expectedMenus) {
            boolean found = actualMenus.contains(menu);
            test.log(found ? Status.PASS : Status.FAIL, found ? "Menu item visible: " + menu : "Missing menu item: " + menu, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
            test.log(Status.INFO, "<img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
            Assert.assertTrue(found, "Menu not found: " + menu);
        }
    }

    @Then("the user should be able to access the following menu items:")
    public void the_user_should_be_able_to_access_the_following_menu_items(DataTable dataTable) {
        List<String> menuItems = dataTable.asList();

        for (String menuItem : menuItems) {
            boolean success;

            try {
                homePage.clickLeftMenu(menuItem.trim());
                success = homePage.waitForMenuPageToLoad(menuItem.trim());

                test.log(success ? Status.PASS : Status.FAIL, success ? "Accessed and loaded menu item: " + menuItem : "Page did not load: " + menuItem, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
                test.log(Status.INFO, "<img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");

                if (success) driver.navigate().refresh();
            } catch (Exception e) {
                test.log(Status.FAIL, "Exception accessing menu item: " + menuItem + "\n" + e.getMessage(), MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
                test.log(Status.INFO, "<img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
                Assert.fail("Unable to access or verify menu item: " + menuItem);
                return;
            }

            Assert.assertTrue(success, "Unable to access or verify menu item: " + menuItem);
        }
    }

    @When("the user views the Overall Compliances section")
    public void the_user_views_the_Overall_Compliances_section() {
        try {
            Thread.sleep(2000);
            boolean isVisible = driver.findElement(By.xpath("//p[contains(normalize-space(),'Overall Compliances')]/span")).isDisplayed();
            Assert.assertTrue(isVisible, "Overall Compliance section not visible");
        } catch (Exception e) {
            test.log(Status.FAIL, "❌ Overall Compliance section is not visible.", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
            test.log(Status.INFO, "<img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
            Assert.fail("Overall Compliance section is not visible.");
        }
    }

    // The rest of the method already updated in canvas remains unchanged.


    @Then("the Overall Compliances count should be displayed properly")
    public void the_Overall_Compliances_count_should_be_displayed_properly() {
        int actualCount = homePage.getOverallCompliancesCount();

        if (actualCount < 0) {
            test.log(Status.FAIL, "❌ Invalid overall compliance count: " + actualCount, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
            test.log(Status.INFO, "<img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
            Assert.fail("Overall Compliance count is invalid.");
        } else {
            test.log(Status.PASS, "✅ Overall Compliances count displayed: " + actualCount, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
            test.log(Status.INFO, "<img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
        }
    }

    @Then("the Overall Compliances should be the sum of:")
    public void the_Overall_Compliances_should_be_the_sum_of(DataTable table) {
        List<String> allRows = table.asList();
        List<String> tabs = allRows.subList(1, allRows.size());
        int expectedTotal = 0;

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("🔍 <b>Tab-wise Compliance Counts:</b><br>");

        for (String tab : tabs) {
            int tabCount = switch (tab.toLowerCase()) {
                case "needs action" -> homePage.getNeedsActionCount();
                case "in progress" -> homePage.getInProgressCount();
                case "compliant" -> homePage.getCompliantCount();
                case "upcoming" -> homePage.getUpcomingCount();
                default -> throw new IllegalArgumentException("❌ Unrecognized tab: " + tab);
            };

            expectedTotal += tabCount;
            logMessage.append("📁 ").append(tab).append(" : ").append(tabCount).append("<br>");
        }

        int actualOverall = homePage.getOverallCompliancesCount();
        logMessage.append("<br>📊 <b>Expected Total from Tabs</b> : ").append(expectedTotal).append("<br>");
        logMessage.append("📋 <b>Actual Overall Compliance</b> : ").append(actualOverall).append("<br>");

        test.log(Status.INFO, logMessage.toString());
        test.log(Status.INFO, "<img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");

        if (expectedTotal != actualOverall) {
            test.log(Status.FAIL, "❌ Mismatch in compliance counts!<br>Expected: " + expectedTotal + "<br>Actual: " + actualOverall, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
            test.log(Status.INFO, "<img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
            throw new AssertionError("❌ Compliance count mismatch.");
        }

        test.log(Status.PASS, "✅ Compliance counts match as expected.", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
        test.log(Status.INFO, "<img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
    }


    @Then("the user clicks each Compliance status tab, navigates to the Compliance section, and validates that the counts match for each tab")
    public void the_user_clicks_each_Compliance_status_tab_navigates_to_the_Compliance_section_and_validates_that_the_counts_match_for_each_tab(DataTable dataTable) throws InterruptedException {
        List<Map<String, String>> tabSectionPairs = dataTable.asMaps();

        for (Map<String, String> pair : tabSectionPairs) {
            String tabName = pair.get("Tab");
            String sectionName = pair.get("Section");

            int displayedTabCount;
            try {
                displayedTabCount = homePage.getDisplayedTabCount(tabName);
                test.log(Status.INFO, "📥 <b>Displayed Count:</b><br>🔹 Tab: <b>" + tabName + "</b><br>🔢 Count: <b>" + displayedTabCount + "</b>", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
                test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
            } catch (Exception e) {
                test.log(Status.FAIL, "❌ Failed to get displayed tab count<br>🔹 Tab: <b>" + tabName + "</b><br><pre>" + e.getMessage() + "</pre>", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
                Assert.fail("Displayed tab count fetch failed for: " + tabName);
                return;
            }

            int actualTabCount;
            try {
                actualTabCount = homePage.clickComplianceStatusTabAndGetCount(tabName);
                Thread.sleep(3000);
                test.log(Status.PASS, "✅ <b>Clicked tab:</b> <b>" + tabName + "</b><br>🔢 Fetched Count: <b>" + actualTabCount + "</b>", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
                test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
            } catch (Exception e) {
                test.log(Status.FAIL, "❌ Failed to click tab or fetch count<br>🔹 Tab: <b>" + tabName + "</b><br><pre>" + e.getMessage() + "</pre>", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
                Assert.fail("Click or count fetch failed for tab: " + tabName);
                return;
            }

            if (displayedTabCount != actualTabCount) {
                test.log(Status.WARNING, "⚠️ Displayed vs. Fetched count mismatch<br>🔹 Tab: <b>" + tabName + "</b><br>📥 Displayed: <b>" + displayedTabCount + "</b><br>📤 Fetched: <b>" + actualTabCount + "</b>");
            }
            Assert.assertEquals(actualTabCount, displayedTabCount, "Mismatch in displayed vs actual tab count for: " + tabName);

            boolean isOnComplianceScreen = homePage.isComplianceScreenVisible();
            test.log(isOnComplianceScreen ? Status.PASS : Status.FAIL, isOnComplianceScreen ? "📄 Compliance screen loaded for: <b>" + tabName + "</b>" : "❌ Compliance screen NOT visible for tab: <b>" + tabName + "</b>", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
            test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");

            Assert.assertTrue(isOnComplianceScreen, "Compliance screen not visible for tab: " + tabName);

            int recordCount = homePage.getRecordCountForSection(sectionName);
            if (recordCount != actualTabCount) {
                test.log(Status.FAIL, "❌ Record count mismatch<br>🔹 Section: <b>" + sectionName + "</b><br>📤 Tab Count: <b>" + actualTabCount + "</b><br>📄 Section Count: <b>" + recordCount + "</b>", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
            } else {
                test.log(Status.PASS, "✅ Counts Matched!<br>🔹 Tab: <b>" + tabName + "</b><br>🔹 Section: <b>" + sectionName + "</b><br>📌 Count: <b style='color:green;'>" + actualTabCount + "</b>", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
            }
            test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
            Assert.assertEquals(recordCount, actualTabCount, "Count mismatch for section: " + sectionName);

            boolean navigatedBack = homePage.goBackToHomePage();
            test.log(navigatedBack ? Status.PASS : Status.FAIL, navigatedBack ? "↩️ Returned to Home after validating tab: <b>" + tabName + "</b>" : "❌ Failed to return to Home after tab: <b>" + tabName + "</b>", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());
            test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
            Assert.assertTrue(navigatedBack, "Failed to return to Home from tab: " + tabName);
        }
    }


}

