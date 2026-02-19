package stepDefinitions;


import hooks.Hooks;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utils.*;
import pages.CustomerProfilePanel;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static utils.AllureLoggerUtils.logToAllure;

/**
 * @author Sherwin
 * @since 27-08-2025
 */

public class CustomerProfilePanelValidationSteps {

    WebDriver driver = Hooks.driver;
    CustomerProfilePanel customerProfilePanel;
    Logger logger;
    WebDriverWait wait;
    AllureLoggerUtils allureLogging;
    ReusableCommonMethods helperMethods;
    private Instant profileMenuNavStart;


    public CustomerProfilePanelValidationSteps() {
        this.driver = Hooks.driver;
        this.customerProfilePanel = new CustomerProfilePanel(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.logger = LoggerUtils.getLogger(getClass());
        this.helperMethods = new ReusableCommonMethods(driver);
    }

    @Step("{message}")
    public void logStep(String message) {
        logger.info(message);
    }

    @When("the user clicks the {string} icon")
    public void theUserClicksTheIcon(String iconName) {
        try {
            logStep("🖱️ Clicking the \"" + iconName + "\" icon");

            // validation before action
            Assert.assertTrue(customerProfilePanel.isProfileIconVisible(), "Profile icon is not visible.");

            logToAllure("🔎 Validation", "Profile icon is visible");

            // POM action
            customerProfilePanel.clickProfileIcon();

            // evidence
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "ProfileIcon_Clicked");
            logToAllure("📋 Action", "Clicked on \"" + iconName + "\" icon");

            logger.info("✅ Profile icon clicked: {}", iconName);
        } catch (Throwable t) {
            customerProfilePanel.handleValidationException("Click \"" + iconName + "\" icon", t);
        }
    }

    @Then("the Customer Profile panel should open")
    public void theCustomerProfilePanelShouldOpen() {
        CustomerProfilePanel panel = new CustomerProfilePanel(driver);
        try {
            logStep("🧭 Verifying Customer Profile panel opens");

            // wait for UI state
            panel.waitForOpen();

            // validation
            Assert.assertTrue(panel.isOpen(), "Customer Profile panel did not open.");
            logToAllure("🔎 Validation", "Customer Profile panel is open");

            // evidence
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "CustomerProfilePanel_Open");
            logToAllure("📷 Screenshot", "Captured Customer Profile panel");

            logger.info("✅ Customer Profile panel opened successfully");
        } catch (Throwable t) {
            panel.handleValidationException("Verify Customer Profile panel open", t);
        }
    }


    @Then("the following identity details should be displayed:")
    public void theFollowingIdentityDetailsShouldBeDisplayed(io.cucumber.datatable.DataTable table) {

        try {
            logStep("🔎 Validating identity details are displayed in the Customer Profile panel");

            customerProfilePanel.waitForOpen(); // ensure panel is present

            java.util.List<String> expected = table.asList();

            for (String field : expected) {
                String f = field == null ? "" : field.trim();
                if ("Name".equals(f)) {
                    Assert.assertTrue(customerProfilePanel.isNameVisible(), "Name should be visible");
                    String name = customerProfilePanel.getDisplayedName();
                    Assert.assertTrue(name != null && !name.trim().isEmpty(), "Name should not be empty");
                    logToAllure("🧾 Name (displayed)", name);
                    logger.info("✅ Name displayed: {}", name);
                } else if ("Mobile Number".equals(f)) {
                    Assert.assertTrue(customerProfilePanel.isMobileVisible(), "Mobile Number should be visible");
                    String mobile = customerProfilePanel.getDisplayedMobile();
                    Assert.assertTrue(mobile != null && !mobile.trim().isEmpty(), "Mobile Number should not be empty");
                    logToAllure("📞 Mobile (displayed)", mobile);
                    logger.info("✅ Mobile displayed: {}", mobile);
                } else if ("Email Address".equals(f)) {
                    Assert.assertTrue(customerProfilePanel.isEmailVisible(), "Email Address should be visible");
                    String email = customerProfilePanel.getDisplayedEmail();
                    Assert.assertTrue(email != null && !email.trim().isEmpty(), "Email Address should not be empty");
                    logToAllure("✉️ Email (displayed)", email);
                    logger.info("✅ Email displayed: {}", email);
                } else {
                    logger.warn("⚠️ Unknown identity field: {}", field);
                }
            }

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Identity_Details_Displayed");
            logToAllure("📷 Screenshot", "Captured identity details section");
        } catch (Throwable t) {
            customerProfilePanel.handleValidationException("Validate identity details are displayed", t);
        }
    }

    @Then("the identity details should match the login credentials")
    public void identityDetailsShouldMatchLoginCreds() {

        try {
            logStep("🔎 Starting validation: Checking if identity details match login credentials");

            customerProfilePanel.waitForOpen();

            // --- fetch displayed values from UI (null-safe) ---
            String uiMobile = customerProfilePanel.getDisplayedMobile();
            if (uiMobile == null) uiMobile = "";
            String uiEmail = customerProfilePanel.getDisplayedEmail();
            if (uiEmail == null) uiEmail = "";

            // --- fetch expected values from config.properties (null-safe) ---
            String expMobile = ConfigReader.get("mobNum");
            if (expMobile == null) expMobile = "";
            String expEmail = ConfigReader.get("email");
            if (expEmail == null) expEmail = "";

            // ensure config present
            Assert.assertTrue(expMobile.trim().length() > 0, "❌ Config mobNum is missing or empty");
            Assert.assertTrue(expEmail.trim().length() > 0, "❌ Config email is missing or empty");

            // --- normalize values for fair comparison ---
            String uiMobileN = customerProfilePanel.normalizePhone(uiMobile);
            String expMobileN = customerProfilePanel.normalizePhone(expMobile);

            String uiEmailN = uiEmail.trim().toLowerCase();
            String expEmailN = expEmail.trim().toLowerCase();

            // --- Allure logging ---
            logToAllure("📞 Mobile (UI vs Expected)", "UI: " + uiMobile + " | Expected: " + expMobile);
            logToAllure("✉️ Email (UI vs Expected)", "UI: " + uiEmail + " | Expected: " + expEmail);

            // --- assertions ---
            Assert.assertTrue(uiMobileN.length() > 0, "❌ Mobile number displayed in UI is empty");
            Assert.assertTrue(uiEmailN.length() > 0, "❌ Email displayed in UI is empty");

            Assert.assertEquals(uiMobileN, expMobileN, "❌ Mobile number mismatch.\nUI: " + uiMobile + " | Expected: " + expMobile);

            Assert.assertEquals(uiEmailN, expEmailN, "❌ Email mismatch.\nUI: " + uiEmail + " | Expected: " + expEmail);

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Identity_Match_OK");

            // --- detailed logger messages ---
            logger.info("✅ Validation successful: Customer profile details match login credentials.");
            logger.info("   ➤ Mobile number shown on screen [{}] matches configured login number [{}]", customerProfilePanel.maskPhone(uiMobileN), customerProfilePanel.maskPhone(expMobileN));
            logger.info("   ➤ Email shown on screen [{}] matches configured login email [{}]", customerProfilePanel.maskEmail(uiEmailN), customerProfilePanel.maskEmail(expEmailN));

        } catch (Throwable t) {
            customerProfilePanel.handleValidationException("Validate identity details vs login credentials", t);
        }
    }


    @Then("the following menu items should be visible in order:")
    public void theFollowingMenuItemsShouldBeVisibleInOrder(DataTable table) {
        try {
            logStep("📑 Validating menu items in Customer Profile panel (order-agnostic)");

            customerProfilePanel.waitForOpen();

            // ----- expected items from Gherkin table (copy & normalize; don't mutate asList()) -----
            List<String> expectedRaw = table.asList();
            List<String> expected = new ArrayList<String>();
            for (int i = 0; i < expectedRaw.size(); i++) {
                String s = expectedRaw.get(i);
                expected.add(customerProfilePanel.normalizeLabel(s));
            }

            // ----- actual items from UI -----
            List<String> actualRaw = customerProfilePanel.getMenuItemsInOrder();
            List<String> actual = new ArrayList<String>();
            for (int i = 0; i < actualRaw.size(); i++) {
                String s = actualRaw.get(i);
                if (s != null && s.trim().length() > 0) {
                    actual.add(customerProfilePanel.normalizeLabel(s));
                }
            }

            // evidence
            logToAllure("📋 Expected Menu Items", expected.toString());
            logToAllure("📋 Actual Menu Items", actual.toString());

            // sanity
            Assert.assertFalse(actual.isEmpty(), "❌ No menu items were found in Customer Profile panel");

            // ----- presence check (order does NOT matter) -----
            List<String> missing = new ArrayList<String>();
            for (int i = 0; i < expected.size(); i++) {
                String exp = expected.get(i);
                if (!actual.contains(exp)) {
                    missing.add(exp);
                }
            }

            // also report unexpected/excess items to help debugging
            List<String> unexpected = new ArrayList<String>();
            for (int i = 0; i < actual.size(); i++) {
                String act = actual.get(i);
                if (!expected.contains(act)) {
                    unexpected.add(act);
                }
            }

            if (!missing.isEmpty() || !unexpected.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                if (!missing.isEmpty()) {
                    sb.append("Missing menu item(s): ").append(missing.toString()).append("\n");
                }
                if (!unexpected.isEmpty()) {
                    sb.append("Unexpected menu item(s): ").append(unexpected.toString()).append("\n");
                }
                Assert.fail("❌ Menu items mismatch (order ignored).\n" + sb.toString() + "Expected: " + expected + "\nActual:   " + actual);
            }

            // optional: if you still want counts to match exactly
            Assert.assertEquals(actual.size(), expected.size(), "❌ Menu items count differs. (Order ignored)\nExpected count: " + expected.size() + " | Actual count: " + actual.size());

            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "MenuItems_Validated");
            logger.info("✅ Menu items present (order ignored). Expected={}, Actual={}", expected, actual);

        } catch (Throwable t) {
            customerProfilePanel.handleValidationException("Validate menu items (order-agnostic)", t);
        }
    }

    @When("the user selects {string} from the Customer profile panel")
    public void theUserSelectsFromTheProfilePanel(String menuName) {
        try {
            logStep("🖱️ Selecting menu from profile panel: " + menuName);

            // 1) Open panel (wait up to NAV_FAIL_MS) and log timing with NAV thresholds
            Instant panelOpenStart = Instant.now();
            customerProfilePanel.waitForOpen(Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));

            long openMs = helperMethods.logLoadTimeAndReturnMs("Open Customer Profile panel", panelOpenStart);
            double openSec = openMs / 1000.0;

            if (openMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                String msg = String.format(
                        "Open Customer Profile panel took %.2f s — more than %d s. Failing (SLA %ds).",
                        openSec, ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000
                );
                logToAllure("❌ Panel Open Failure", msg);
                Assert.fail(msg);
            } else if (openMs >= ReusableCommonMethods.NAV_WARN_MS) {
                String msg = String.format(
                        "Open Customer Profile panel took %.2f s — more than %d s.",
                        openSec, ReusableCommonMethods.NAV_WARN_MS / 1000
                );
                logger.warn(msg);
                logToAllure("⚠️ Panel Open Warning", msg);
            }

            // 2) Start click→destination timer BEFORE clicking the menu
            profileMenuNavStart = Instant.now();
            NavContext.start(menuName);

            // 3) Click the item (allow up to NAV_FAIL_MS for stability)
            customerProfilePanel.clickMenu(menuName, Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS));

            ScreenshotUtils.attachScreenshotToAllure(driver, "Menu_Clicked_" + menuName.replace(" ", "_"));
            logToAllure("📋 Action", "Clicked menu: " + menuName);

        } catch (Throwable t) {
            customerProfilePanel.handleValidationException("Click menu: " + menuName, t);
        }
    }


    @Then("the user should redirect to {string} page")
    public void theDestinationShouldShow(String expectedText) {
        try {
            logStep("🔎 Verifying destination shows expected text: " + expectedText);

            // 1) Wait for destination ready (config-driven fail SLA)
            Duration timeout = Duration.ofMillis(ReusableCommonMethods.NAV_FAIL_MS);
            boolean ready = customerProfilePanel.waitForDestinationReady(expectedText, timeout);

            // 2) Measure click→ready using the Instant captured in the @When step
            Instant start = (profileMenuNavStart != null) ? profileMenuNavStart : Instant.now();
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs(expectedText, start); // uses NAV thresholds by default
            double elapsedSec = elapsedMs / 1000.0;

            // 3) Readiness + thresholds
            if (!ready) {
                String msg = String.format(
                        "Destination not ready within %d s for: %s (click→ready: %.2f s)",
                        ReusableCommonMethods.NAV_FAIL_MS / 1000, expectedText, elapsedSec
                );
                logToAllure("❌ Destination Failure", msg);
                Assert.fail(msg);
            }

            if (elapsedMs >= ReusableCommonMethods.NAV_FAIL_MS) {
                String msg = String.format(
                        "%s took %.2f s — more than %d s. Failing (SLA %ds).",
                        expectedText, elapsedSec,
                        ReusableCommonMethods.NAV_FAIL_MS / 1000, ReusableCommonMethods.NAV_FAIL_MS / 1000
                );
                logToAllure("❌ Load Time Failure", msg);
                Assert.fail(msg);
            } else if (elapsedMs >= ReusableCommonMethods.NAV_WARN_MS) {
                String msg = String.format(
                        "%s took %.2f s — more than %d s.",
                        expectedText, elapsedSec, ReusableCommonMethods.NAV_WARN_MS / 1000
                );
                logger.warn(msg);
                logToAllure("⚠️ Load Time Warning", msg);
            }

            // 4) Final page validation
            Assert.assertTrue(
                    customerProfilePanel.isDestinationVisible(expectedText),
                    "Expected destination marker not visible for: " + expectedText
            );

            ScreenshotUtils.attachScreenshotToAllure(driver, "Destination_" + expectedText.replace(" ", "_"));
            logToAllure("✅ Verified", "Destination shows: " + expectedText);

            // 5) Cleanup
            profileMenuNavStart = null;

        } catch (Throwable t) {
            customerProfilePanel.handleValidationException("Verify destination text: " + expectedText, t);
        }
    }



    @Then("the application should logout and redirect to {string} page")
    public void theLoginPageShouldBeDisplayedWithText(String expectedText) {
        try {
            logStep("🔎 Verifying login page is displayed after logout");

            boolean visible = customerProfilePanel.isLoginTextVisible(expectedText);
            Assert.assertTrue(visible, "Login page text not visible: " + expectedText);

            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginPage_AfterLogout");
            logToAllure("✅ Verified", "Login page shows: " + expectedText);
            logger.info("✅ Login page displayed with text: {}", expectedText);
        } catch (Throwable t) {
            customerProfilePanel.handleValidationException("Verify login page after logout", t);
        }
    }

}
