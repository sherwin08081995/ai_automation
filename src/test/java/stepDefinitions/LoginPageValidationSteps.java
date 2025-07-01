package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.LoginPage;
import utils.AllureLoggerUtils;
import utils.ConfigReader;
import utils.LoggerUtils;
import utils.ScreenshotUtils;

import java.time.Duration;

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


    public LoginPageValidationSteps() {
        this.driver = Hooks.driver;
        this.loginPage = new LoginPage(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.logger = LoggerUtils.getLogger(getClass());
    }

    @Step("{message}")
    public void logStep(String message) {
        // Allure will log this message as a step
    }


    @Given("the user is on the Login page")
    public void the_user_is_on_the_login_page() {
        try {
            logStep("🌐 Navigating to the Login page...");
            driver.get(ConfigReader.get("baseUrl"));
            logger.info("Navigated to URL: {}", ConfigReader.get("baseUrl"));

            boolean subtitleCorrect = loginPage.isLoginSubtitleCorrect();

            logger.info("Login subtitle status: {}", subtitleCorrect);
            logStep("✅ Login page subtitle is correct: " + subtitleCorrect);
            logToAllure("✅ Subtitle Validation", "Login subtitle is displayed correctly.");

            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginPage");

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

//            logStep("🔍 Checking Send OTP button...");
//            boolean isSendOtpButtonReady = loginPage.isSendOtpButtonVisibleAndEnabled();
//            logger.info("Send OTP button ready: {}", isSendOtpButtonReady);
//            Assert.assertTrue(isSendOtpButtonReady, "OTP button is not visible/enabled");

//            loginPage.clickLoginWithOtpButton();
//            logStep("🔘 Clicked 'Login with OTP' button");
//            logger.info("Clicked Login with OTP button");
//            ScreenshotUtils.attachScreenshotToAllure(driver, "Clicked_Login_with_OTP");

            loginPage.clickGetOtpButton();
            logStep("🔘 Clicked 'Get OTP' button");
            logger.info("Clicked Get OTP button");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Clicked_Get_OTP");

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

            // Optional: Additional wait if needed before entering OTP
            wait.until(driver -> loginPage.isOtpFieldVisibleAndEnabled());

            loginPage.enterOtp(otp);
            logStep("🔐 Entered OTP: [REDACTED]");
            logger.info("OTP entered (masked)");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Otp_Entered");

            // ✅ Validate each digit is correctly entered in input fields
            for (int i = 0; i < otp.length(); i++) {
                String expectedValue = String.valueOf(otp.charAt(i));
                String actualValue = loginPage.getOtpInputs().get(i).getAttribute("value");
                logger.info("Validating OTP digit at index {}: expected={}, actual={}", i, expectedValue, actualValue);
                Assert.assertEquals(actualValue, expectedValue, "Mismatch at OTP index " + i);
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


    @Then("the user should be redirected to the homepage {string}")
    public void the_user_should_be_redirected_to_the_homepage(String expectedAltText) {
        try {
            logStep("⏳ Waiting for homepage redirection...");
            logger.info("Waiting for homepage with alt text: {}", expectedAltText);

            wait.until(driver -> loginPage.isLoginSuccessful(expectedAltText));
            boolean loginSuccess = loginPage.isLoginSuccessful(expectedAltText);
            logger.info("Homepage redirection status: {}", loginSuccess);

            Assert.assertTrue(loginSuccess, "Login failed or homepage not loaded");

            logStep("🏠 User redirected to homepage successfully.");
            logToAllure("✅ Homepage Redirection", "Homepage loaded with expected logo alt text: " + expectedAltText);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Homepage_Redirected");

        } catch (AssertionError ae) {
            String msg = "❌ Assertion Failed during homepage redirection: " + ae.getMessage();
            logger.error(msg);
            logStep(msg);
            logToAllure("❌ Homepage Assertion Failed", msg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Homepage_Failed");
            throw ae;

        } catch (Exception e) {
            String msg = "❌ Exception occurred while checking homepage redirection: " + e.getMessage();
            logger.error(msg);
            logStep(msg);
            logToAllure("❌ Homepage Redirection Exception", msg);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Homepage_Exception");
            throw e;
        }
    }

}
