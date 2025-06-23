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

            test.log(Status.PASS, "Login page subtitle is correct.", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());

            test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");

        } catch (AssertionError ae) {
            test.log(Status.FAIL, "Login subtitle check failed.", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());

            test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
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

            test.log(Status.PASS, "Email field ready and email entered: " + email, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());

            test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");

            boolean isSendOtpButtonReady = loginPage.isSendOtpButtonVisibleAndEnabled();
            Assert.assertTrue(isSendOtpButtonReady, "OTP button is not visible/enabled");
            loginPage.clickOtpButton();

            test.log(Status.PASS, "Clicked Get OTP button.", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());

            test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");

        } catch (AssertionError ae) {
            test.log(Status.FAIL, "Email or OTP button step failed: " + ae.getMessage(), MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());

            test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
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

            test.log(Status.PASS, "OTP field is ready.", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());

            test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");

            loginPage.enterOtp(otp);

            test.log(Status.INFO, "Entered OTP: " + otp, MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());

            test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");

        } catch (AssertionError ae) {
            test.log(Status.FAIL, "OTP field validation failed.", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());

            test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
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

            test.log(Status.PASS, "User redirected to homepage successfully.", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());

            test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");

        } catch (AssertionError ae) {
            test.log(Status.FAIL, "Login failed or homepage not loaded properly.", MediaEntityBuilder.createScreenCaptureFromBase64String(ScreenshotUtils.getBase64Screenshot(driver), "image/png").build());

            test.log(Status.INFO, "Inline Screenshot:<br><img src='data:image/png;base64," + ScreenshotUtils.getBase64Screenshot(driver) + "' height='400' width='600'/>");
            throw ae;
        } catch (Exception e) {
            test.log(Status.FAIL, "Error during homepage redirection validation: " + e.getMessage());
            throw e;
        }
    }
}
