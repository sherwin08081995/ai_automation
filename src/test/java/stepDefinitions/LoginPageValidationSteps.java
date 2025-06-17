package stepDefinitions;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.LoginPage;
import utils.ConfigReader;
import utils.ExtentTestManager;
import utils.ScreenshotUtils;

/**
 * Login feature steps will be mapped here
 *
 * @author Sherwin
 * @since 16-06-2025
 */
public class LoginPageValidationSteps {

    WebDriver driver = Hooks.driver;
    LoginPage loginPage;
    ExtentTest test;

    public LoginPageValidationSteps() {
        this.driver = Hooks.driver;
        this.loginPage = new LoginPage(driver);
        this.test = ExtentTestManager.getTest(); // ← Call once here
    }


    @Given("the user is on the Login page")
    public void the_user_is_on_the_login_page() {
        try {
            driver.get(ConfigReader.get("baseUrl"));

            boolean subtitleCorrect = loginPage.isLoginSubtitleCorrect();
            Assert.assertTrue(subtitleCorrect, "Login subtitle is not displayed as expected");

            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "Login_Page_Loaded");
            test.log(Status.PASS, "Login page subtitle is correct.", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());

        } catch (AssertionError ae) {
            String errorShot = ScreenshotUtils.takeScreenshot(driver, "Login_Page_Subtitle_Failure");
            test.log(Status.FAIL, "Login subtitle check failed.", MediaEntityBuilder.createScreenCaptureFromPath(errorShot).build());
            throw ae;
        } catch (Exception e) {
            test.log(Status.FAIL, "Unexpected error on login page: " + e.getMessage());
            throw e;
        }
    }

    @When("the user enters {string} and click send OTP button")
    public void the_user_enters_and_click_send_otp_button(String email) {
        try {
            boolean isEmailFieldReady = loginPage.isEmailFieldVisibleAndEnabled();
            Assert.assertTrue(isEmailFieldReady, "Email field is not ready");
            loginPage.enterEmail(email);
            String emailFieldScreenshot = ScreenshotUtils.takeScreenshot(driver, "Email_Field_Ready");
            test.log(Status.PASS, "Email field ready and email entered: " + email, MediaEntityBuilder.createScreenCaptureFromPath(emailFieldScreenshot).build());

            boolean isSendOtpButtonReady = loginPage.isSendOtpButtonVisibleAndEnabled();
            Assert.assertTrue(isSendOtpButtonReady, "OTP button is not visible/enabled");
            loginPage.clickOtpButton();

            String otpButtonScreenshot = ScreenshotUtils.takeScreenshot(driver, "Clicked_OTP_Button");
            test.log(Status.PASS, "Clicked Get OTP button.", MediaEntityBuilder.createScreenCaptureFromPath(otpButtonScreenshot).build());

        } catch (AssertionError ae) {
            String failScreenshot = ScreenshotUtils.takeScreenshot(driver, "OTP_Email_Failure");
            test.log(Status.FAIL, "Email or OTP button step failed: " + ae.getMessage(), MediaEntityBuilder.createScreenCaptureFromPath(failScreenshot).build());
            throw ae;
        } catch (Exception e) {
            test.log(Status.FAIL, "Exception in entering email or clicking OTP: " + e.getMessage());
            throw e;
        }
    }

    @When("the user enters valid {string}")
    public void the_user_enters_valid(String otp) {
        try {
            boolean isOtpFieldReady = loginPage.isOtpFieldVisibleAndEnabled();
            Assert.assertTrue(isOtpFieldReady, "OTP field is not ready for input");

            String otpReadyScreenshot = ScreenshotUtils.takeScreenshot(driver, "OTP_Field_Ready");
            test.log(Status.PASS, "OTP field is ready.", MediaEntityBuilder.createScreenCaptureFromPath(otpReadyScreenshot).build());

            loginPage.enterOtp(otp);

            String enteredOtpScreenshot = ScreenshotUtils.takeScreenshot(driver, "Entered_OTP_" + otp);
            test.log(Status.INFO, "Entered OTP: " + otp, MediaEntityBuilder.createScreenCaptureFromPath(enteredOtpScreenshot).build());

        } catch (AssertionError ae) {
            String failShot = ScreenshotUtils.takeScreenshot(driver, "OTP_Field_Not_Ready");
            test.log(Status.FAIL, "OTP field validation failed.", MediaEntityBuilder.createScreenCaptureFromPath(failShot).build());
            throw ae;
        } catch (Exception e) {
            test.log(Status.FAIL, "Unexpected error entering OTP: " + e.getMessage());
            throw e;
        }
    }

    @Then("the user should be redirected to the homepage {string}")
    public void the_user_should_be_redirected_to_the_homepage(String expectedAltText) {
        try {
            boolean loginSuccess = loginPage.isLoginSuccessful(expectedAltText);
            Assert.assertTrue(loginSuccess, "Login was not successful or user not redirected properly");

            String successShot = ScreenshotUtils.takeScreenshot(driver, "Login_Success_Homepage");
            test.log(Status.PASS, "User redirected to homepage successfully.", MediaEntityBuilder.createScreenCaptureFromPath(successShot).build());

        } catch (AssertionError ae) {
            String failShot = ScreenshotUtils.takeScreenshot(driver, "Login_Failure_Homepage");
            test.log(Status.FAIL, "Login failed or homepage not loaded properly.", MediaEntityBuilder.createScreenCaptureFromPath(failShot).build());
            throw ae;
        } catch (Exception e) {
            test.log(Status.FAIL, "Error during homepage redirection validation: " + e.getMessage());
            throw e;
        }
    }
}
