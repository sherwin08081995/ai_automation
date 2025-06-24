package stepDefinitions;

import hooks.Hooks;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.LoginPage;
import utils.ConfigReader;
import utils.ScreenshotUtils;

public class LoginPageValidationSteps {

    WebDriver driver = Hooks.driver;
    LoginPage loginPage;

    public LoginPageValidationSteps() {
        this.driver = Hooks.driver;
        this.loginPage = new LoginPage(driver);
    }

    @Given("the user is on the Login page")
    public void the_user_is_on_the_login_page() {
        try {
            driver.get(ConfigReader.get("baseUrl"));

            boolean subtitleCorrect = loginPage.isLoginSubtitleCorrect();
            Assert.assertTrue(subtitleCorrect, "Login subtitle is not displayed as expected");

            logStep("Login page subtitle is correct.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginPage");

        } catch (AssertionError ae) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "LoginPage_Failed");
            throw ae;
        } catch (Exception e) {
            Allure.addAttachment("Exception", e.getMessage());
            throw e;
        }
    }

    @When("the user enters {string} and click send OTP button")
    public void the_user_enters_and_click_send_otp_button(String email) {
        try {
            boolean isEmailFieldReady = loginPage.isEmailFieldVisibleAndEnabled();
            Assert.assertTrue(isEmailFieldReady, "Email field is not ready");

            loginPage.enterEmail(email);
            logStep("Email entered: " + email);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Email_Entered");

            boolean isSendOtpButtonReady = loginPage.isSendOtpButtonVisibleAndEnabled();
            Assert.assertTrue(isSendOtpButtonReady, "OTP button is not visible/enabled");

            loginPage.clickOtpButton();
            logStep("Clicked Get OTP button.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Clicked_OTP");

        } catch (AssertionError ae) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "EmailStep_Failed");
            throw ae;
        } catch (Exception e) {
            Allure.addAttachment("Exception", e.getMessage());
            throw e;
        }
    }

    @When("the user enters valid {string}")
    public void the_user_enters_valid(String otp) {
        try {
            boolean isOtpFieldReady = loginPage.isOtpFieldVisibleAndEnabled();
            Assert.assertTrue(isOtpFieldReady, "OTP field is not ready");

            logStep("OTP field is ready.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "OtpField_Ready");

            loginPage.enterOtp(otp);
            logStep("Entered OTP: " + otp);
            ScreenshotUtils.attachScreenshotToAllure(driver, "Otp_Entered");

        } catch (AssertionError ae) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Otp_Failed");
            throw ae;
        } catch (Exception e) {
            Allure.addAttachment("Exception", e.getMessage());
            throw e;
        }
    }

    @Then("the user should be redirected to the homepage {string}")
    public void the_user_should_be_redirected_to_the_homepage(String expectedAltText) {
        try {
            boolean loginSuccess = loginPage.isLoginSuccessful(expectedAltText);
            Assert.assertTrue(loginSuccess, "Login failed or homepage not loaded");

            logStep("User redirected to homepage successfully.");
            ScreenshotUtils.attachScreenshotToAllure(driver, "Homepage_Redirected");

        } catch (AssertionError ae) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Homepage_Failed");
            throw ae;
        } catch (Exception e) {
            Allure.addAttachment("Exception", e.getMessage());
            throw e;
        }
    }

    @Step("{message}")
    public void logStep(String message) {
        // This method marks steps in Allure report
    }
}
