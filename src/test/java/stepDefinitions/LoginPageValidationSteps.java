package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.LoginPage;
import utils.*;

import java.time.Duration;
import java.time.Instant;

import static utils.AllureLoggerUtils.logToAllure;

/**
 * LoginPageValidationSteps.java
 * <p>
 * Purpose:
 * Step Definitions for validating the Login Page using Cucumber BDD.
 * This class handles:
 * <p>
 * ✅ Navigation to Login Page
 * ✅ Email and OTP entry with validation
 * ✅ Login success verification with redirection to the homepage
 * ✅ Allure screenshot attachment and structured reporting
 * <p>
 * Related Classes:
 * - LoginPage.java (POM)
 * - ScreenshotUtils.java (Screenshot handling)
 * - ConfigReader.java (Configuration properties)
 * - Hooks.java (WebDriver and login setup)
 *
 * @author Sherwin
 * @since 17-06-2025
 */


public class LoginPageValidationSteps {

    WebDriver driver = Hooks.driver;
    LoginPage loginPage;
    Logger logger;
    WebDriverWait wait;
    AllureLoggerUtils allureLogging;
    ReusableCommonMethods helperMethods;
    private Instant loginNavigateStart;
    private Instant getOtpStart;
    private Instant redirectStart;

    public LoginPageValidationSteps() {
        this.driver = Hooks.driver;
        this.loginPage = new LoginPage(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.logger = LoggerUtils.getLogger(getClass());
        this.helperMethods = new ReusableCommonMethods(driver);
    }

    @Step("{message}")
    public void logStep(String message) {
        // Allure will log this message as a step
    }

    @Given("the user is on the Login page")
    public void the_user_is_on_the_login_page() {
        try {
            logStep("🌐 Navigating to the Login page...");

            // start timer BEFORE navigation
            loginNavigateStart = Instant.now();
            NavContext.start("Open Login Page");

            driver.get(ConfigReader.get("baseUrl"));
            logger.info("Navigated to URL: {}", ConfigReader.get("baseUrl"));

            boolean subtitleCorrect = loginPage.isLoginSubtitleCorrect();
            logger.info("Login subtitle status: {}", subtitleCorrect);
            logStep("✅ Login page subtitle is correct: " + subtitleCorrect);
            logToAllure("✅ Subtitle Validation", "Login subtitle is displayed correctly.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginPage");

            // stop & log time with LOGIN thresholds, then enforce them
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs(
                    "Open Login Page",
                    loginNavigateStart,
                    ReusableCommonMethods.LOGIN_WARN_MS,  // e.g., 30_000 from config
                    ReusableCommonMethods.LOGIN_FAIL_MS   // e.g., 40_000 from config
            );

            if (elapsedMs > ReusableCommonMethods.LOGIN_FAIL_MS) {
                String msg = String.format("Open Login Page exceeded %d ms (actual: %.2f s)",
                        ReusableCommonMethods.LOGIN_FAIL_MS, elapsedMs / 1000.0);
                logToAllure("❌ Load Time Failure", msg);
                Assert.fail(msg);
            } else if (elapsedMs > ReusableCommonMethods.LOGIN_WARN_MS) {
                String msg = String.format("⚠️ Open Login Page exceeded %d ms (actual: %.2f s)",
                        ReusableCommonMethods.LOGIN_WARN_MS, elapsedMs / 1000.0);
                logToAllure("⚠️ Load Time Warning", msg);
                logger.warn(msg);
            }

            Assert.assertTrue(subtitleCorrect, "Login subtitle is not displayed as expected");

        } catch (AssertionError ae) {
            String msg = "❌ Assertion Failed: " + ae.getMessage();
            logger.error(msg);
            logStep(msg);
            logToAllure("❌ Assertion Error", msg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginPage_Failed");
            throw ae;

        } catch (Exception e) {
            String msg = "❌ Exception during Login page validation: " + e.getMessage();
            logger.error(msg);
            logStep(msg);
            logToAllure("❌ Exception", msg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginPage_Exception");
            throw e;
        }
    }

    @When("the user enters {string} and click send OTP button")
    public void the_user_enters_and_click_send_otp_button(String email) {
        try {
            logStep("📩 Validating email input field...");
            boolean isEmailFieldReady = loginPage.isEmailFieldVisibleAndEnabled();
            logger.info("Email input field ready: {}", isEmailFieldReady);
            Assert.assertTrue(isEmailFieldReady, "Email field is not ready");

            loginPage.enterEmail(email);
            logger.info("Entered email: {}", email);
            logStep("📨 Email entered: " + email);
            logToAllure("📨 Entered Email", email);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Email_Entered");

            // start timer BEFORE clicking Get OTP
            getOtpStart = Instant.now();
            NavContext.start("Get OTP → OTP field ready");

            loginPage.clickGetOtpButton();
            logStep("🔘 Clicked 'Get OTP' button");
            logger.info("Clicked Get OTP button");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Clicked_Get_OTP");

            // wait until OTP inputs are ready (destination readiness)
            WebDriverWait waitForOtp = new WebDriverWait(
                    driver, java.time.Duration.ofMillis(ReusableCommonMethods.LOGIN_FAIL_MS)
            );
            waitForOtp.until(d -> loginPage.isOtpFieldVisibleAndEnabled());

            // log with LOGIN thresholds
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs(
                    "Get OTP → OTP field ready",
                    getOtpStart,
                    ReusableCommonMethods.LOGIN_WARN_MS,   // e.g., 30_000 ms
                    ReusableCommonMethods.LOGIN_FAIL_MS    // e.g., 40_000 ms
            );
            getOtpStart = null; // reset

            if (elapsedMs > ReusableCommonMethods.LOGIN_FAIL_MS) {
                String msg = String.format(
                        "Get OTP → OTP field ready exceeded %d ms (actual: %.2f s)",
                        ReusableCommonMethods.LOGIN_FAIL_MS, elapsedMs / 1000.0
                );
                logToAllure("❌ Load Time Failure", msg);
                Assert.fail(msg);
            } else if (elapsedMs > ReusableCommonMethods.LOGIN_WARN_MS) {
                String msg = String.format(
                        "⚠️ Get OTP → OTP field ready exceeded %d ms (actual: %.2f s)",
                        ReusableCommonMethods.LOGIN_WARN_MS, elapsedMs / 1000.0
                );
                logToAllure("⚠️ Load Time Warning", msg);
                logger.warn(msg);
            }

        } catch (AssertionError ae) {
            String message = "❌ Assertion Failed: " + ae.getMessage();
            logger.error(message);
            logStep(message);
            logToAllure("❌ Assertion Error", message);
            ScreenshotUtils.attachScreenshotToAllure(driver, "EmailStep_Failed");
            throw ae;

        } catch (Exception e) {
            String message = "❌ Exception occurred during email and OTP step: " + e.getMessage();
            logger.error(message);
            logStep(message);
            logToAllure("❌ Exception", message);
            ScreenshotUtils.attachScreenshotToAllure(driver, "EmailStep_Exception");
            throw e;
        }
    }

    @When("the user enters valid {string}")
    public void the_user_enters_valid(String otp) {
        try {
            logStep("📥 Checking if OTP input field is ready...");
            boolean isOtpFieldReady = loginPage.isOtpFieldVisibleAndEnabled();
            logger.info("OTP input field ready: {}", isOtpFieldReady);
            Assert.assertTrue(isOtpFieldReady, "OTP field is not ready");
            ScreenshotUtils.attachScreenshotToAllure(driver, "OtpField_Ready");

            // ⏱️ Start timing BEFORE entry (enter → digits reflected)
            Instant otpEnterStart = Instant.now();
            NavContext.start("Enter OTP → digits reflected");

            // proceed with entering & validating OTP
            loginPage.enterOtp(otp);
            logStep("🔐 Entered OTP: [REDACTED]");
            logger.info("OTP entered (masked)");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Otp_Entered");

            // verify each digit reflects in the inputs
            for (int i = 0; i < otp.length(); i++) {
                String expectedValue = String.valueOf(otp.charAt(i));
                String actualValue = loginPage.getOtpInputs().get(i).getAttribute("value");
                logger.info("Validating OTP digit at index {}: expected={}, actual={}", i, expectedValue, actualValue);
                Assert.assertEquals(actualValue, expectedValue, "Mismatch at OTP index " + i);
            }

            // ⏱️ Stop & log timing with LOGIN thresholds
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs(
                    "Enter OTP → digits reflected",
                    otpEnterStart,
                    ReusableCommonMethods.LOGIN_WARN_MS,   // e.g., 30_000 ms
                    ReusableCommonMethods.LOGIN_FAIL_MS    // e.g., 40_000 ms
            );

            // Enforce thresholds (warn then fail)
            if (elapsedMs > ReusableCommonMethods.LOGIN_FAIL_MS) {
                String msg = String.format(
                        "Enter OTP → digits reflected exceeded %d ms (actual: %.2f s)",
                        ReusableCommonMethods.LOGIN_FAIL_MS, elapsedMs / 1000.0
                );
                logToAllure("❌ Load Time Failure", msg);
                Assert.fail(msg);
            } else if (elapsedMs > ReusableCommonMethods.LOGIN_WARN_MS) {
                String msg = String.format(
                        "⚠️ Enter OTP → digits reflected exceeded %d ms (actual: %.2f s)",
                        ReusableCommonMethods.LOGIN_WARN_MS, elapsedMs / 1000.0
                );
                logToAllure("⚠️ Load Time Warning", msg);
                logger.warn(msg);
            }

            logStep("✅ OTP digits successfully validated in input boxes.");
            logToAllure("✅ OTP Validation Passed", "All OTP digits matched expected values");

        } catch (AssertionError ae) {
            String msg = "❌ Assertion Failed: " + ae.getMessage();
            logger.error(msg);
            logStep(msg);
            logToAllure("❌ Assertion Error", msg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Otp_Failed");
            throw ae;

        } catch (Exception e) {
            String msg = "❌ Exception during OTP entry: " + e.getMessage();
            logger.error(msg);
            logStep(msg);
            logToAllure("❌ OTP Exception", msg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Otp_Exception");
            throw e;
        }
    }

    @Then("the user selects email and is redirected to the {string} homepage")
    public void the_user_should_be_redirected_to_the_homepage(String expectedAltText) throws InterruptedException {
        try {
            String email = ConfigReader.get("email");
            logStep("📧 Selecting email: " + email);
            logToAllure("🔐 Login Flow Start",
                    "Email: " + email + "\nTarget homepage logo alt: " + expectedAltText);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Start_LoginFlow");

            // ----------------- Choose email -----------------
            NavContext.start("Email Choose → Home");
            if (loginPage.isChooserOpen()) {
                logToAllure("📮 Chooser", "Chooser is visible. Selecting email: " + email);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Chooser_Visible");
                loginPage.selectEmailInChooser(email);
                ScreenshotUtils.attachScreenshotToAllure(driver, "Chooser_Email_Selected");
            } else {
                logger.info("Chooser not open; likely already logged in.");
                logToAllure("📮 Chooser", "Chooser not open; likely already logged in.");
                ScreenshotUtils.attachScreenshotToAllure(driver, "Chooser_NotOpen");
            }

            // ----------------- START TIMING (pre-popup) -----------------
            redirectStart = Instant.now();
            logToAllure("⏱️ Redirect Timing", "Started timing post-login redirect.");

            // We will STOP timing as soon as ANY of these becomes visible:
            //  - Homepage logo (target)
            //  - Profile Incomplete banner
            //  - Festive popup banner
            By homeLogoBy = By.xpath("//img[@alt='" + expectedAltText + "']");
            By profileIncompleteBy = By.xpath("//p[normalize-space()='Action Required: Profile Incomplete']");
            By festiveBannerBy = By.xpath("//p[normalize-space()='Extra 10% Off - Festive Sale!']");

            long maxMs = ReusableCommonMethods.LOGIN_FAIL_MS;
            logToAllure("⏳ First-Signal Wait",
                    "Waiting for first signal of post-login state (OR):\n" +
                            "• Homepage logo\n• Profile Incomplete modal\n• Festive popup\n" +
                            "Timeout(ms): " + maxMs);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Pre_FirstSignal_Wait");

            new WebDriverWait(driver, Duration.ofMillis(maxMs))
                    .until(ExpectedConditions.or(
                            ExpectedConditions.visibilityOfElementLocated(homeLogoBy),
                            ExpectedConditions.visibilityOfElementLocated(profileIncompleteBy),
                            ExpectedConditions.visibilityOfElementLocated(festiveBannerBy)
                    ));

            // ----------------- STOP TIMING (pre-popup) -----------------
            long elapsedMs = helperMethods.logLoadTimeAndReturnMs(
                    "Email Choose → First Signal",
                    redirectStart,
                    ReusableCommonMethods.LOGIN_WARN_MS,
                    ReusableCommonMethods.LOGIN_FAIL_MS
            );
            logToAllure("🛑 Redirect Timing Stopped",
                    String.format("Reached first post-login signal in %.2f s.", elapsedMs / 1000.0));
            ScreenshotUtils.attachScreenshotToAllure(driver, "FirstSignal_Reached");
            redirectStart = null;

            // Threshold handling for the FIRST signal
            if (elapsedMs > ReusableCommonMethods.LOGIN_FAIL_MS) {
                String msg = String.format("First signal exceeded %d ms (actual: %.2f s)",
                        ReusableCommonMethods.LOGIN_FAIL_MS, elapsedMs / 1000.0);
                logToAllure("❌ Load Time Failure (First Signal)", msg + "\nURL: " + safeGetUrl(driver));
                ScreenshotUtils.attachScreenshotToAllure(driver, "FirstSignal_LoadTime_Fail");
                Assert.fail(msg);
            } else if (elapsedMs > ReusableCommonMethods.LOGIN_WARN_MS) {
                String msg = String.format("⚠️ First signal exceeded %d ms (actual: %.2f s)",
                        ReusableCommonMethods.LOGIN_WARN_MS, elapsedMs / 1000.0);
                logToAllure("⚠️ Load Time Warning (First Signal)", msg + "\nURL: " + safeGetUrl(driver));
                ScreenshotUtils.attachScreenshotToAllure(driver, "FirstSignal_LoadTime_Warn");
                logger.warn(msg);
            } else {
                logToAllure("✅ Load Time OK (First Signal)",
                        String.format("First signal within thresholds (%.2f s).", elapsedMs / 1000.0));
                ScreenshotUtils.attachScreenshotToAllure(driver, "FirstSignal_LoadTime_OK");
            }

            // ----------------- BRANCHES AFTER timing is stopped -----------------

            // 1) Handle any generic closeable popup first
            if (loginPage.hasCloseIcon()) {
                long tCloseStart = System.currentTimeMillis();
                logToAllure("🧩 Profile Incomplete Modal",
                        "Detected 'Action Required: Profile Incomplete' modal. Attempting to close.");
                ScreenshotUtils.attachScreenshotToAllure(driver, "ProfileIncomplete_Detected");

                loginPage.closePopupIfPresent();

                long tCloseMs = System.currentTimeMillis() - tCloseStart;
                logToAllure("🧩 Profile Incomplete Modal",
                        "Closed modal. Elapsed: " + tCloseMs + " ms");
                ScreenshotUtils.attachScreenshotToAllure(driver, "ProfileIncomplete_Closed");
            } else {
                logToAllure("🧩 Profile Incomplete Modal",
                        "No 'Action Required: Profile Incomplete' modal detected.");
                ScreenshotUtils.attachScreenshotToAllure(driver, "ProfileIncomplete_NotPresent");
            }

            // 2) If festive popup appears
            if (loginPage.isFestivePopupVisible()) {
                logStep("🎉 Festive popup detected → clicking 'Explore Service Hub'.");
                logToAllure("🎉 Festive Popup",
                        "Detected: 'Extra 10% Off - Festive Sale!'\nAction: Click 'Explore Service Hub' and validate page.");
                ScreenshotUtils.attachScreenshotToAllure(driver, "FestivePopup_Detected");

                long tHubStart = System.currentTimeMillis();
                loginPage.clickExploreServiceHubFromPopup();

                boolean atHub = loginPage.isOnServiceHubPage();
                long tHubMs = System.currentTimeMillis() - tHubStart;

                if (atHub) {
                    logToAllure("✅ Service Hub Navigation",
                            "Service Hub opened successfully.\nMarker: <h1>Service Hub</h1>\nElapsed: " + tHubMs + " ms\nURL: " + safeGetUrl(driver));
                    ScreenshotUtils.attachScreenshotToAllure(driver, "ServiceHub_Landed");
                } else {
                    logToAllure("❌ Service Hub Navigation",
                            "Expected Service Hub but marker not visible.\nElapsed: " + tHubMs + " ms\nURL: " + safeGetUrl(driver));
                    ScreenshotUtils.attachScreenshotToAllure(driver, "ServiceHub_NotLanded");
                }
                Assert.assertTrue(atHub, "Service Hub did not open as expected.");

                // return to dashboard/home to continue main flow
                driver.navigate().back();
                logger.info("Navigated back from Service Hub to dashboard/home.");
                logToAllure("↩️ Return Navigation", "Returned from Service Hub to previous page.\nURL: " + safeGetUrl(driver));
                ScreenshotUtils.attachScreenshotToAllure(driver, "Returned_From_ServiceHub");
            } else {
                logger.info("Festive popup not present; continuing with homepage validation.");
                logToAllure("🎉 Festive Popup", "Not present. Proceeding to homepage verification.");
                ScreenshotUtils.attachScreenshotToAllure(driver, "FestivePopup_NotPresent");
            }

            // ----------------- Final homepage verification (not timed) -----------------
            logStep("🔎 Verifying homepage is visible…");
            logToAllure("🔎 Homepage Verification",
                    "Waiting for logo with alt='" + expectedAltText + "'");
            new WebDriverWait(driver, Duration.ofMillis(ReusableCommonMethods.LOGIN_FAIL_MS))
                    .until(ExpectedConditions.visibilityOfElementLocated(homeLogoBy));
            ScreenshotUtils.attachScreenshotToAllure(driver, "Homepage_Logo_Visible");

            boolean loginSuccess = loginPage.isLoginSuccessful(expectedAltText);
            logToAllure("🔎 Homepage Verification",
                    "Expected alt: " + expectedAltText + "\nisLoginSuccessful: " + loginSuccess);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Homepage_Verification");
            Assert.assertTrue(loginSuccess, "Login failed or homepage not loaded");

            logStep("🏠 User redirected to homepage successfully.");
            logToAllure("✅ Homepage Redirection",
                    "Homepage loaded with expected logo alt text: " + expectedAltText + "\nURL: " + safeGetUrl(driver));
            ScreenshotUtils.attachScreenshotToAllure(driver, "Homepage_Redirected");

        } catch (AssertionError ae) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Assertion_Homepage");
            logToAllure("❌ Assertion Failure",
                    "Context: Homepage Redirection - Assertion\nMessage: " + ae.getMessage());
            loginPage.handleValidationException("Homepage Redirection - Assertion", ae);
            throw ae;

        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(Hooks.driver, "Exception_Homepage");
            logToAllure("💥 Exception",
                    "Context: Homepage Redirection - Exception\nType: " + e.getClass().getSimpleName() + "\nMessage: " + e.getMessage());
            loginPage.handleValidationException("Homepage Redirection - Exception", e);
            throw e;
        }
    }

    /** Small helper to avoid NPEs when logging current URL to Allure. */
    private String safeGetUrl(WebDriver driver) {
        try { return driver.getCurrentUrl(); } catch (Exception e) { return "<unavailable>"; }
    }







}
