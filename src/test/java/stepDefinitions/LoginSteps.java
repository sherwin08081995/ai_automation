package stepDefinitions;

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

public class LoginSteps {

    WebDriver driver = Hooks.driver;
    LoginPage loginPage;


    @Given("the user is on the Login page")
    public void the_user_is_on_the_login_page() {
        loginPage = new LoginPage(driver);
        driver.get(ConfigReader.get("baseUrl"));
        Assert.assertTrue(loginPage.isLoginSubtitleCorrect(), "Login subtitle is not displayed as expected");
        String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "Login page");
        ExtentTestManager.getTest().log(Status.INFO, "Screenshot to confirm user is on the login page", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
    }


    @When("the user enters {string} and click send OTP button")
    public void the_user_enters_and_click_send_otp_button(String email) {
        loginPage.enterEmail(email);
        loginPage.clickOtpButton();
        String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "User enter email and clicked send OTP button");
        ExtentTestManager.getTest().log(Status.INFO, "Screenshot to confirm user clicked send OTP button", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
    }


    @When("the user enters valid {string}")
    public void the_user_enters_valid(String string) {
        loginPage.enterOtp(string);
        String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "User enter valid OTP");
        ExtentTestManager.getTest().log(Status.INFO, "Screenshot to confirm user enter valid OTP", MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
    }

    @Then("the user should be redirected to the homepage")
    public void the_user_should_be_redirected_to_the_homepage() {

    }
}
