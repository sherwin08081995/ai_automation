package stepDefinitions;


import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.ReportAnIssueSection;
import utils.*;
import org.testng.Assert;

import static utils.AllureLoggerUtils.logToAllure;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sherwin
 * @since 24-08-2025
 */

public class ReportAnIssueValidationSteps {

    WebDriver driver = Hooks.driver;
    ReportAnIssueSection reportAnIssue;
    Logger logger;
    WebDriverWait wait;
    AllureLoggerUtils allureLogging;
    ReusableCommonMethods helperMethods;

    public ReportAnIssueValidationSteps() {
        this.driver = Hooks.driver;
        this.reportAnIssue = new ReportAnIssueSection(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.logger = LoggerUtils.getLogger(getClass());
        this.helperMethods = new ReusableCommonMethods(driver);
    }

    @Step("{message}")
    public void logStep(String message) {
        logger.info(message);
    }

    @Given("the user is on the Report an issue page")
    public void the_user_is_on_the_report_an_issue_page() {
        try {
            logStep("🔍 Navigating and confirming that the user is on the Report an Issue page...");

            // 1) Start timers BEFORE the click (captures click → nav → render)
            Instant navStart = Instant.now();
            NavContext.start("Report an Issue");

            // 2) Click the tab/entry in the UI
            reportAnIssue.clickReportAnIssueTab();

            // 3) Wait up to NAV_FAIL_MS for the page to be ready (config-driven)
            boolean success = reportAnIssue.waitForReportAnIssueLoaded(Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));

            // 4) Stop & log timing via reusable helper (defaults to NAV thresholds 12s/20s)
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs("Report an Issue", navStart);
            double elapsedSec = elapsedMs / 1000.0;

            // 5) Threshold handling for NAV (warn ≥ NAV_WARN_MS, fail ≥ NAV_FAIL_MS)
            if (success) {
                if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                    String failMsg = String.format("Report an Issue took %.2f s — more than %d s. Failing (SLA %ds).", elapsedSec, ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000);
                    logger.error(failMsg);
                    logToAllure("❌ Load Time Failure", failMsg);
                    Assert.fail(failMsg);
                } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                    String warnMsg = String.format("Report an Issue took %.2f s — more than %d s.", elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000);
                    logger.warn(warnMsg);
                    logToAllure("⚠️ Load Time Warning", warnMsg);
                }
            } else {
                String failMsg = String.format("Unable to load Report an Issue within %d s (actual: %.2f s).", ReusableCommonMethods.NAV_FAIL_MS / 1000, elapsedSec);
                logger.error(failMsg);
                logToAllure("❌ Access Failure", failMsg);
                Assert.fail(failMsg);
            }

            // 6) Final artifacts
            logToAllure("📋 Report an Issue Page Loaded", String.valueOf(success));
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "ReportAnIssuePage_Confirmation");
            logger.info("✅ Report an Issue page successfully confirmed.");

        } catch (Throwable t) {
            reportAnIssue.handleValidationException("Report an Issue page confirmation", t);
        }
    }


    @When("the user opens the Modules dropdown")
    public void the_user_opens_the_modules_dropdown() {
        try {
            logStep("📂 Attempting to open the Modules dropdown...");
            logToAllure("📋 Step", "Opening Modules dropdown");

            reportAnIssue.openModulesDropdown();

            logToAllure("📋 Modules Dropdown Action", "Modules dropdown clicked.");
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "ModulesDropdown_Clicked");

            logger.info("✅ Modules dropdown click action performed.");
        } catch (Throwable t) {
            reportAnIssue.handleValidationException("Modules dropdown opening", t);
        }
    }

    @Then("the user should see the following options in the Modules dropdown:")
    public void the_user_should_see_the_following_options_in_the_modules_dropdown(DataTable dataTable) {
        try {
            logStep("🔎 Validating Modules dropdown options (order ignored)…");

            List<String> expected = dataTable.asList(String.class);
            if (expected == null || expected.isEmpty()) {
                throw new IllegalArgumentException("Expected options cannot be null or empty");
            }

            List<String> actual = reportAnIssue.getModulesOptionTexts();
            if (actual == null || actual.isEmpty()) {
                throw new AssertionError("Modules dropdown returned no options");
            }

            logToAllure("📋 Expected Modules Options", expected.toString());
            logToAllure("🔍 Actual Modules Options", actual.toString());
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "ModulesDropdown_Options");

            // order-agnostic, set-based comparison
            Set<String> expectedSet = new HashSet<>(expected);
            Set<String> actualSet = new HashSet<>(actual);

            Set<String> missing = new HashSet<>(expectedSet);
            missing.removeAll(actualSet);
            Set<String> unexpected = new HashSet<>(actualSet);
            unexpected.removeAll(expectedSet);

            Assert.assertTrue(missing.isEmpty() && unexpected.isEmpty(), "❌ Dropdown options mismatch.\nMissing: " + missing + "\nUnexpected: " + unexpected);

            logger.info("✅ Modules dropdown options match the expected set (order ignored).");
        } catch (Throwable t) {
            reportAnIssue.handleValidationException("Modules dropdown options validation", t);
        }
    }


    @When("the user selects {string} from the Modules dropdown")
    public void the_user_selects_from_the_modules_dropdown(String moduleName) {
        try {
            if (moduleName == null || moduleName.trim().isEmpty()) {
                logger.error("❌ Module name is null or empty, cannot select");
                throw new IllegalArgumentException("Module name cannot be null or empty");
            }

            logStep("🖱️ Selecting module: " + moduleName + " from the dropdown...");
            logToAllure("📋 Step", "Selecting module: " + moduleName);

            reportAnIssue.selectModuleByName(moduleName);

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "ModulesDropdown_Selected_" + moduleName);
            logger.info("✅ Selected module: {}", moduleName);
        } catch (Throwable t) {
            reportAnIssue.handleValidationException("Select module '" + moduleName + "'", t);
        }
    }

    @Then("the selected module should be {string}")
    public void the_selected_module_should_be(String expected) {
        try {
            if (expected == null || expected.trim().isEmpty()) {
                logger.error("❌ Expected module value is null or empty");
                throw new IllegalArgumentException("Expected module cannot be null or empty");
            }

            logStep("🔎 Verifying selected module is: " + expected);
            logToAllure("📋 Step", "Verifying selected module is: " + expected);

            String actual = reportAnIssue.getSelectedModule();
            if (actual == null || actual.trim().isEmpty()) {
                logger.error("❌ No module was selected (actual is null or empty)");
                throw new AssertionError("No module selected in UI");
            }

            logToAllure("📋 Selected Module (Actual)", actual);
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "ModulesDropdown_SelectedValue");

            Assert.assertEquals(actual, expected, "❌ Wrong module selected!");
            logger.info("✅ Module verification passed. Expected={}, Actual={}", expected, actual);
        } catch (Throwable t) {
            reportAnIssue.handleValidationException("Verify selected module equals '" + expected + "'", t);
        }
    }


    @And("the user enters random feedback into the feedback field")
    public void the_user_enters_random_feedback_into_the_feedback_field() {
        try {
            logStep("🖊️ Entering random feedback into the textarea...");

            String feedback = reportAnIssue.enterRandomFeedback();

            if (feedback == null || feedback.trim().isEmpty()) {
                logger.error("❌ Feedback value generated is null or empty");
                throw new IllegalArgumentException("Random feedback cannot be null or empty");
            }

            logToAllure("📋 Random Feedback Used", feedback);
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "FeedbackField_Filled");
            String actualFeedback = reportAnIssue.getFeedbackFieldValue();
            if (actualFeedback == null || actualFeedback.trim().isEmpty()) {
                logger.error("❌ Feedback field is empty after entering feedback");
                throw new AssertionError("Feedback field did not capture the input");
            }

            Assert.assertEquals(actualFeedback, feedback, "❌ Feedback entered does not match value in the field!");
            logger.info("✅ Feedback entered and verified successfully. Value={}", actualFeedback);

        } catch (Throwable t) {
            reportAnIssue.handleValidationException("Enter and verify random feedback", t);
        }
    }


    @Then("the Send button should be enabled")
    public void the_send_button_should_be_enabled() {
        try {
            logStep("🔎 Validating that the Send button is enabled...");
            boolean isEnabled = reportAnIssue.verifySendButtonEnabled();
            Assert.assertTrue(isEnabled, "❌ Send button is not enabled!");
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "SendButton_Enabled");
            logToAllure("📋 Validation", "Send button is visible and enabled.");
            logger.info("✅ Send button validation passed.");
        } catch (Throwable t) {
            reportAnIssue.handleValidationException("Verify Send button enabled", t);
        }
    }


    @When("the user clicks the Send button")
    public void the_user_clicks_the_send_button() {
        try {
            logStep("🖱️ Clicking the Send button...");
            boolean ready = reportAnIssue.verifySendButtonEnabled();
            Assert.assertTrue(ready, "❌ Send button is not ready (not visible or not enabled).");
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "SendButton_BeforeClick");
            logToAllure("📋 Precondition", "Send button is visible and enabled.");
            reportAnIssue.clickSendButton();
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "SendButton_AfterClick");
            logToAllure("📋 Action", "Send button clicked.");
            logger.info("✅ Send button click performed.");
        } catch (Throwable t) {
            reportAnIssue.handleValidationException("Click Send button", t);
        }
    }


    @Then("the user should see a confirmation message {string}")
    public void the_user_should_see_a_confirmation_message(String expectedMessage) {
        try {
            logStep("🔎 Validating confirmation message: " + expectedMessage);
            reportAnIssue.verifyConfirmationMessage(expectedMessage);
            logToAllure("📋 Confirmation (Expected)", expectedMessage);
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "ConfirmationMessage_Visible");

            logger.info("✅ Confirmation message validation passed.");
        } catch (Throwable t) {
            reportAnIssue.handleValidationException("Verify confirmation message", t);
        }
    }


    @And("the user closes the confirmation popup")
    public void the_user_closes_the_confirmation_popup() {
        try {
            logStep("🖱️ Closing the confirmation popup...");
            reportAnIssue.closeConfirmationPopup();
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "ConfirmationPopup_Closed");
            logToAllure("📋 Action", "Closed confirmation popup");
            logger.info("✅ Confirmation popup closed.");
        } catch (Throwable t) {
            reportAnIssue.handleValidationException("Close confirmation popup", t);
        }
    }


    @Then("the user should see the hyperlink {string}")
    public void the_user_should_see_the_hyperlink(String expectedEmail) {
        try {
            logStep("🔎 Verifying the support email hyperlink: " + expectedEmail);

            // Delegate validation to POM (no screenshots in POM by your preference)
            reportAnIssue.verifySupportEmailLink(expectedEmail);

            // Evidence & reporting remain in the step layer
            logToAllure("📋 Expected Support Email", expectedEmail);
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "SupportEmailLink_Visible");

            logger.info("✅ Support email hyperlink present and correct: {}", expectedEmail);
        } catch (Throwable t) {
            reportAnIssue.handleValidationException("Verify support email hyperlink", t);
        }
    }


    @When("the user clicks on the {string} hyperlink")
    public void the_user_clicks_on_the_hyperlink(String expectedEmail) {
        try {
            logStep("🖱️ Clicking the support email hyperlink: " + expectedEmail);

            // POM action
            reportAnIssue.clickSupportEmailLink();

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "SupportEmailLink_Clicked");
            logToAllure("📋 Action", "Clicked on support email hyperlink: " + expectedEmail);

            logger.info("✅ Support email hyperlink clicked: {}", expectedEmail);
        } catch (Throwable t) {
            reportAnIssue.handleValidationException("Click support email hyperlink", t);
        }
    }

    @Then("the default Mail application should open with {string} in the {string} field")
    public void the_default_mail_application_should_open_with_in_the_field(String expectedEmail, String fieldName) {
        try {
            logStep("🔎 Validating mailto launch for: " + expectedEmail);

            String expectedHref = "mailto:" + expectedEmail;
            String actualHref = reportAnIssue.getSupportEmailLinkHref();
            Assert.assertEquals(actualHref, expectedHref, "❌ Expected mailto='" + expectedHref + "' but got '" + actualHref + "'");

            // Capture handles BEFORE click
            Set<String> beforeHandles = Hooks.driver.getWindowHandles();

            // Click
            reportAnIssue.clickSupportEmailLink();

            // small wait for a possible new window/tab
            try {
                Thread.sleep(1200);
            } catch (InterruptedException ignored) {
            }

            Set<String> afterHandles = Hooks.driver.getWindowHandles();
            boolean newWindowOpened = afterHandles.size() > beforeHandles.size();

            if (newWindowOpened) {
                afterHandles.removeAll(beforeHandles);
                String newHandle = afterHandles.iterator().next();
                Hooks.driver.switchTo().window(newHandle);

                String openedUrl = Hooks.driver.getCurrentUrl();
                logToAllure("📎 Opened URL", openedUrl == null ? "(null)" : openedUrl);
                ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "MailHandler_BrowserWindow");

                boolean looksLikeMailHandler = openedUrl != null && (openedUrl.startsWith("mailto:") || openedUrl.toLowerCase().contains("outlook") || openedUrl.toLowerCase().contains("office") || openedUrl.toLowerCase().contains("live.com") || openedUrl.toLowerCase().contains("gmail") || openedUrl.toLowerCase().contains("google"));

                Assert.assertTrue(looksLikeMailHandler, "❌ A new window opened but the URL doesn't look like a mail handler: " + openedUrl);

                logger.info("✅ Mailto opened in browser handler: {}", openedUrl);
            } else {
                // Native client (Outlook) case → attach a DESKTOP screenshot to Allure
                AllureLoggerUtils.attachDesktopScreenshot("Desktop evidence (Outlook)");
                logToAllure("🖼️ Desktop evidence", "Attached desktop screenshot for native mail client.");
                logger.info("✅ Mailto likely handed off to native client; desktop evidence attached.");
            }

            logger.info("ℹ️ Requested field label: {}", fieldName);

        } catch (Throwable t) {
            reportAnIssue.handleValidationException("Validate mailto launch", t);
        }
    }


}
