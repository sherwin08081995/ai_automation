package stepDefinitions;

import com.aventstack.extentreports.ExtentTest;
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

public class LoginPageValidationSteps {

    WebDriver driver = Hooks.driver;
    LoginPage loginPage;
    ExtentTest test;

    public LoginPageValidationSteps() {
        this.driver = Hooks.driver;
        this.loginPage = new LoginPage(driver);
        this.test = ExtentTestManager.getTest();
    }

    @Given("the user is on the Login page")
    public void the_user_is_on_the_login_page() {
        try {
            driver.get(ConfigReader.get("baseUrl"));

            boolean subtitleCorrect = loginPage.isLoginSubtitleCorrect();
            Assert.assertTrue(subtitleCorrect, "Login subtitle is not displayed as expected");

            String relPath = ScreenshotUtils.takeScreenshot(driver, "LoginPage");
            test.log(Status.PASS, "Login page subtitle is correct.")
                    .addScreenCaptureFromPath(relPath);

        } catch (AssertionError ae) {
            String relPath = ScreenshotUtils.takeScreenshot(driver, "LoginPage_Failed");
            test.log(Status.FAIL, "Login subtitle check failed.")
                    .addScreenCaptureFromPath(relPath);
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

            String relPath = ScreenshotUtils.takeScreenshot(driver, "Email_Entered");
            test.log(Status.PASS, "Email field ready and email entered: " + email)
                    .addScreenCaptureFromPath(relPath);

            boolean isSendOtpButtonReady = loginPage.isSendOtpButtonVisibleAndEnabled();
            Assert.assertTrue(isSendOtpButtonReady, "OTP button is not visible/enabled");
            loginPage.clickOtpButton();

            relPath = ScreenshotUtils.takeScreenshot(driver, "Clicked_OTP");
            test.log(Status.PASS, "Clicked Get OTP button.")
                    .addScreenCaptureFromPath(relPath);

        } catch (AssertionError ae) {
            String relPath = ScreenshotUtils.takeScreenshot(driver, "EmailStep_Failed");
            test.log(Status.FAIL, "Email or OTP button step failed: " + ae.getMessage())
                    .addScreenCaptureFromPath(relPath);
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

            String relPath = ScreenshotUtils.takeScreenshot(driver, "OtpField_Ready");
            test.log(Status.PASS, "OTP field is ready.")
                    .addScreenCaptureFromPath(relPath);

            loginPage.enterOtp(otp);

            relPath = ScreenshotUtils.takeScreenshot(driver, "Otp_Entered");
            test.log(Status.PASS, "Entered OTP: " + otp)
                    .addScreenCaptureFromPath(relPath);

        } catch (AssertionError ae) {
            String relPath = ScreenshotUtils.takeScreenshot(driver, "Otp_Failed");
            test.log(Status.FAIL, "OTP field validation failed.")
                    .addScreenCaptureFromPath(relPath);
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

            String relPath = ScreenshotUtils.takeScreenshot(driver, "Homepage_Redirected");
            test.log(Status.PASS, "User redirected to homepage successfully.")
                    .addScreenCaptureFromPath(relPath);

        } catch (AssertionError ae) {
            String relPath = ScreenshotUtils.takeScreenshot(driver, "Homepage_Failed");
            test.log(Status.FAIL, "Login failed or homepage not loaded properly.")
                    .addScreenCaptureFromPath(relPath);
            throw ae;
        } catch (Exception e) {
            test.log(Status.FAIL, "Error during homepage redirection validation: " + e.getMessage());
            throw e;
        }
    }
}
