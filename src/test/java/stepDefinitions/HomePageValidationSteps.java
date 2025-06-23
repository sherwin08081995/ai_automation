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
    public void the_user_views_the_left_side_menu() {
        boolean isVisible = homePage.isLeftMenuVisible();
        String screenshotPath = ScreenshotUtils.takeScreenshot(driver, isVisible ? "Left_Menu_Visible" : "Left_Menu_Not_Visible");

        test.log(isVisible ? Status.PASS : Status.FAIL, isVisible ? "Left menu is visible." : "Left menu is not visible.", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());

        Assert.assertTrue(isVisible, "Left menu is not visible");
    }

    @Then("the following menu items should be visible:")
    public void the_following_menu_items_should_be_visible(DataTable dataTable) {
        List<String> expectedMenus = dataTable.asList();
        List<String> actualMenus = homePage.getLeftMenuItems();

        for (String menu : expectedMenus) {
            boolean found = actualMenus.contains(menu);
            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, found ? "MenuItem_Visible_" + menu : "MenuItem_Missing_" + menu);
            test.log(found ? Status.PASS : Status.FAIL, found ? "Menu item visible: " + menu : "Missing menu item: " + menu, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            Assert.assertTrue(found, "Menu not found: " + menu);
        }
    }

    @Then("the user should be able to access the following menu items:")
    public void the_user_should_be_able_to_access_the_following_menu_items(DataTable dataTable) {
        List<String> menuItems = dataTable.asList();

        for (String menuItem : menuItems) {
            boolean success = false;
            String screenshotPath = "";

            try {
                homePage.clickLeftMenu(menuItem.trim());
                success = homePage.waitForMenuPageToLoad(menuItem.trim());

                screenshotPath = ScreenshotUtils.takeScreenshot(driver, success ? "Menu_Access_Success_" + menuItem : "Menu_Access_Failure_" + menuItem);

                test.log(success ? Status.PASS : Status.FAIL, success ? "Accessed and loaded menu item: " + menuItem : "Menu item clicked but page did not load: " + menuItem, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());

                if (success) driver.navigate().refresh();

            } catch (Exception e) {
                screenshotPath = ScreenshotUtils.takeScreenshot(driver, "Menu_Access_Exception_" + menuItem);
                test.log(Status.FAIL, "Exception accessing menu item: " + menuItem + "<br>" + e.getMessage(), MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
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
            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "OverallCompliance_Missing");
            test.log(Status.FAIL, "❌ Overall Compliance section is not visible.", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            Assert.fail("Overall Compliance section is not visible. Screenshot: " + screenshotPath);
        }
    }

    @Then("the Overall Compliances count should be displayed properly")
    public void the_Overall_Compliances_count_should_be_displayed_properly() {
        int actualCount = homePage.getOverallCompliancesCount();
        String screenshot = ScreenshotUtils.takeScreenshot(driver, "Overall_Compliances_Count_Check");

        if (actualCount < 0) {
            test.log(Status.FAIL, "❌ Invalid overall compliance count: " + actualCount, MediaEntityBuilder.createScreenCaptureFromPath(screenshot).build());
            Assert.fail("Overall Compliance count is invalid. Screenshot: " + screenshot);
        } else {
            test.log(Status.PASS, "✅ Overall Compliances count displayed: " + actualCount, MediaEntityBuilder.createScreenCaptureFromPath(screenshot).build());
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

        String summaryScreenshot = ScreenshotUtils.takeScreenshot(driver, "Compliance_Tab_Summary");
        test.log(Status.INFO, logMessage.toString()).addScreenCaptureFromPath(summaryScreenshot);

        if (expectedTotal != actualOverall) {
            String mismatchScreenshot = ScreenshotUtils.takeScreenshot(driver, "Compliance_Count_Mismatch");
            test.log(Status.FAIL, "❌ Mismatch in compliance counts!<br>Expected: " + expectedTotal + "<br>Actual: " + actualOverall, MediaEntityBuilder.createScreenCaptureFromPath(mismatchScreenshot).build());
            throw new AssertionError("❌ Compliance count mismatch. Screenshot: " + mismatchScreenshot);
        }

        test.log(Status.PASS, "✅ Compliance counts match as expected.");
    }


    @Then("the user clicks each Compliance status tab, navigates to the Compliance section, and validates that the counts match for each tab")
    public void the_user_clicks_each_Compliance_status_tab_navigates_to_the_Compliance_section_and_validates_that_the_counts_match_for_each_tab(DataTable dataTable) throws InterruptedException {
        List<Map<String, String>> tabSectionPairs = dataTable.asMaps();

        for (Map<String, String> pair : tabSectionPairs) {
            String tabName = pair.get("Tab");
            String sectionName = pair.get("Section");

            //Get displayed tab count
            int displayedTabCount;
            try {
                displayedTabCount = homePage.getDisplayedTabCount(tabName);
                String beforeClickScreenshot = ScreenshotUtils.takeScreenshot(driver, "Before_Click_" + tabName.replace(" ", "_"));
                test.log(Status.INFO, "📥 <b>Displayed Count:</b><br>🔹 Tab: <b>" + tabName + "</b><br>🔢 Count: <b>" + displayedTabCount + "</b>", MediaEntityBuilder.createScreenCaptureFromPath(beforeClickScreenshot).build());
            } catch (Exception e) {
                String errorScreenshot = ScreenshotUtils.takeScreenshot(driver, "Tab_Count_Fail_" + tabName.replace(" ", "_"));
                test.log(Status.FAIL, "❌ <b>Failed to get displayed tab count</b><br>🔹 Tab: <b>" + tabName + "</b><br><pre>" + e.getMessage() + "</pre>", MediaEntityBuilder.createScreenCaptureFromPath(errorScreenshot).build());
                Assert.fail("Displayed tab count fetch failed for: " + tabName);
                return;
            }

            //Click tab and fetch count internally
            int actualTabCount;
            try {
                actualTabCount = homePage.clickComplianceStatusTabAndGetCount(tabName);
                Thread.sleep(3000);
                String clickedTabScreenshot = ScreenshotUtils.takeScreenshot(driver, "Clicked_Tab_" + tabName.replace(" ", "_"));
                test.log(Status.PASS, "✅ <b>Clicked tab:</b> <b>" + tabName + "</b><br>🔢 Fetched Count: <b>" + actualTabCount + "</b>", MediaEntityBuilder.createScreenCaptureFromPath(clickedTabScreenshot).build());
            } catch (Exception e) {
                String failScreenshot = ScreenshotUtils.takeScreenshot(driver, "Click_Tab_Fail_" + tabName.replace(" ", "_"));
                test.log(Status.FAIL, "❌ <b>Failed to click tab or fetch count</b><br>🔹 Tab: <b>" + tabName + "</b><br><pre>" + e.getMessage() + "</pre>", MediaEntityBuilder.createScreenCaptureFromPath(failScreenshot).build());
                Assert.fail("Click or count fetch failed for tab: " + tabName);
                return;
            }

            //Compare displayed vs actual count
            if (displayedTabCount != actualTabCount) {
                test.log(Status.WARNING, "⚠️ <b>Displayed vs. Fetched count mismatch</b><br>🔹 Tab: <b>" + tabName + "</b><br>" + "📥 Displayed: <b>" + displayedTabCount + "</b><br>📤 Fetched: <b>" + actualTabCount + "</b>");
            }
            Assert.assertEquals(actualTabCount, displayedTabCount, "Mismatch in displayed vs actual tab count for: " + tabName);

            //Ensure Compliance screen is visible
            boolean isOnComplianceScreen = homePage.isComplianceScreenVisible();
            String complianceScreenScreenshot = ScreenshotUtils.takeScreenshot(driver, "Compliance_Screen_" + tabName.replace(" ", "_"));

            test.log(isOnComplianceScreen ? Status.PASS : Status.FAIL, isOnComplianceScreen ? "📄 <b>Compliance screen loaded for:</b> <b>" + tabName + "</b>" : "❌ <b>Compliance screen NOT visible</b> for tab: <b>" + tabName + "</b>", MediaEntityBuilder.createScreenCaptureFromPath(complianceScreenScreenshot).build());

            Assert.assertTrue(isOnComplianceScreen, "Compliance screen not visible for tab: " + tabName);

            //Fetch record count from section and validate
            int recordCount = homePage.getRecordCountForSection(sectionName);
            String recordCountScreenshot = ScreenshotUtils.takeScreenshot(driver, "Record_Count_" + sectionName.replace(" ", "_"));

            if (recordCount != actualTabCount) {
                test.log(Status.FAIL, "❌ <b>Record count mismatch</b><br>🔹 Section: <b>" + sectionName + "</b><br>" + "📤 Tab Count: <b>" + actualTabCount + "</b><br>📄 Section Count: <b>" + recordCount + "</b>", MediaEntityBuilder.createScreenCaptureFromPath(recordCountScreenshot).build());
            } else {
                test.log(Status.PASS, "✅ <b style='color:green;'>Counts Matched!</b><br>" + "🔹 Tab: <b>" + tabName + "</b><br>" + "🔹 Section: <b>" + sectionName + "</b><br>" + "📌 Count: <b style='font-size:1.2em;color:green;'>" + actualTabCount + "</b>", MediaEntityBuilder.createScreenCaptureFromPath(recordCountScreenshot).build());
            }

            Assert.assertEquals(recordCount, actualTabCount, "Count mismatch for section: " + sectionName);

            //Navigate back to Home
            boolean navigatedBack = homePage.goBackToHomePage();
            String backScreenshot = ScreenshotUtils.takeScreenshot(driver, "Returned_Home_" + tabName.replace(" ", "_"));

            test.log(navigatedBack ? Status.PASS : Status.FAIL, navigatedBack ? "↩️ <b>Returned to Home after validating tab:</b> <b>" + tabName + "</b>" : "❌ <b>Failed to return to Home</b> after tab: <b>" + tabName + "</b>", MediaEntityBuilder.createScreenCaptureFromPath(backScreenshot).build());

            Assert.assertTrue(navigatedBack, "Failed to return to Home from tab: " + tabName);
        }
    }


}

