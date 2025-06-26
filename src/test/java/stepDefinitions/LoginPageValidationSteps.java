package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import pages.LoginPage;
import utils.ConfigReader;
import utils.ScreenshotUtils;

import java.time.Duration;

/**
 * LoginPageValidationSteps.java
 *
 * Purpose:
 * Step Definitions for validating the Login Page using Cucumber BDD.
 * This class handles:
 *
 * ✅ Navigation to Login Page
 * ✅ Email and OTP entry with validation
 * ✅ Login success verification with redirection to the homepage
 * ✅ Allure screenshot attachment and structured reporting
 *
 * Related Classes:
 * - LoginPage.java (POM)
 * - ScreenshotUtils.java (Screenshot handling)
 * - ConfigReader.java (Configuration properties)
 * - Hooks.java (WebDriver and login setup)
 *
 * @author Sherwin
 * @since 17-06-2025
 *
 */



public class LoginPageValidationSteps {

    WebDriver driver = Hooks.driver;
    LoginPage loginPage;
    WebDriverWait wait;

    public LoginPageValidationSteps() {
        this.driver = Hooks.driver;
        this.loginPage = new LoginPage(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Given("the user is on the Login page")
    public void the_user_is_on_the_login_page() {
        try {
            driver.get(ConfigReader.get("baseUrl"));

            boolean subtitleCorrect = loginPage.isLoginSubtitleCorrect();
            Assert.assertTrue(subtitleCorrect, "Login subtitle is not displayed as expected");

            logStep("✅ Login page subtitle is correct.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginPage");

        } catch (AssertionError ae) {
            logStep("❌ Assertion Failed: " + ae.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginPage_Failed");
            throw ae;
        } catch (Exception e) {
            logStep("❌ Exception occurred: " + e.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginPage_Exception");
            throw e;
        }
    }

    @When("the user enters {string} and click send OTP button")
    public void the_user_enters_and_click_send_otp_button(String email) {
        try {
            boolean isEmailFieldReady = loginPage.isEmailFieldVisibleAndEnabled();
            Assert.assertTrue(isEmailFieldReady, "Email field is not ready");

            loginPage.enterEmail(email);
            logStep("📨 Email entered: " + email);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Email_Entered");

            boolean isSendOtpButtonReady = loginPage.isSendOtpButtonVisibleAndEnabled();
            Assert.assertTrue(isSendOtpButtonReady, "OTP button is not visible/enabled");

            loginPage.clickLoginWithOtpButton();
            logStep("🔘 Clicked Login with OTP button.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Clicked_Login_with_OTP");

            loginPage.clickGetOtpButton();
            logStep("🔘 Clicked Get OTP button.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Clicked_Get_OTP");

        } catch (AssertionError ae) {
            logStep("❌ Assertion Failed: " + ae.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "EmailStep_Failed");
            throw ae;
        } catch (Exception e) {
            logStep("❌ Exception occurred: " + e.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "EmailStep_Exception");
            throw e;
        }
    }

//    @When("the user enters valid {string}")
//    public void the_user_enters_valid(String otp) {
//        try {
//            boolean isOtpFieldReady = loginPage.isOtpFieldVisibleAndEnabled();
//            Assert.assertTrue(isOtpFieldReady, "OTP field is not ready");
//
//            logStep("📥 OTP field is ready.");
//            ScreenshotUtils.attachScreenshotToAllure(driver, "OtpField_Ready");
//            wait.until(driver -> loginPage.isOtpFieldVisibleAndEnabled());
//            loginPage.enterOtp(otp);
//            logStep("🔐 Entered OTP: [REDACTED]");
//            ScreenshotUtils.attachScreenshotToAllure(driver, "Otp_Entered");
//
//        } catch (AssertionError ae) {
//            logStep("❌ Assertion Failed: " + ae.getMessage());
//            ScreenshotUtils.attachScreenshotToAllure(driver, "Otp_Failed");
//            throw ae;
//        } catch (Exception e) {
//            logStep("❌ Exception occurred: " + e.getMessage());
//            ScreenshotUtils.attachScreenshotToAllure(driver, "Otp_Exception");
//            throw e;
//        }
//    }

    @When("the user enters valid {string}")
    public void the_user_enters_valid(String otp) {
        try {
            boolean isOtpFieldReady = loginPage.isOtpFieldVisibleAndEnabled();
            Assert.assertTrue(isOtpFieldReady, "OTP field is not ready");

            logStep("📥 OTP field is ready.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "OtpField_Ready");

            wait.until(driver -> loginPage.isOtpFieldVisibleAndEnabled());

            loginPage.enterOtp(otp);
            logStep("🔐 Entered OTP: [REDACTED]");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Otp_Entered");

            // ✅ Validate each digit is correctly entered in the input fields
            for (int i = 0; i < otp.length(); i++) {
                String expectedValue = String.valueOf(otp.charAt(i));
                String actualValue = loginPage.getOtpInputs().get(i).getAttribute("value");

                Assert.assertEquals(actualValue, expectedValue, "Mismatch at OTP index " + i + ": expected " + expectedValue + ", but found " + actualValue);
            }

            logStep("✅ OTP digits successfully validated in input boxes.");

        } catch (AssertionError ae) {
            logStep("❌ Assertion Failed: " + ae.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "Otp_Failed");
            throw ae;
        } catch (Exception e) {
            logStep("❌ Exception occurred: " + e.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "Otp_Exception");
            throw e;
        }
    }


    @Then("the user should be redirected to the homepage {string}")
    public void the_user_should_be_redirected_to_the_homepage(String expectedAltText) {
        try {
            wait.until(driver -> loginPage.isLoginSuccessful(expectedAltText));
            boolean loginSuccess = loginPage.isLoginSuccessful(expectedAltText);
            Assert.assertTrue(loginSuccess, "Login failed or homepage not loaded");

            logStep("🏠 User redirected to homepage successfully.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Homepage_Redirected");

        } catch (AssertionError ae) {
            logStep("❌ Assertion Failed: " + ae.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "Homepage_Failed");
            throw ae;
        } catch (Exception e) {
            logStep("❌ Exception occurred: " + e.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "Homepage_Exception");
            throw e;
        }
    }

    @Step("{message}")
    public void logStep(String message) {
        // Allure will log this message as a step
    }
}
