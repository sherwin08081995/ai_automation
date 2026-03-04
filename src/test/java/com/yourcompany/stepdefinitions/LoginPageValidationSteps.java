package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.testng.Assert;

public class LoginPageValidationSteps {
    private LoginPage loginPage;
    private String initialUrl;

    @Given("I am on the GRC login page")
    public void iAmOnTheGRCLoginPage() {
        loginPage = new LoginPage(Hooks.getDriver());
        initialUrl = loginPage.getCurrentUrl();
        Assert.assertTrue(loginPage.getCurrentUrl().contains("grc360"), "Should be on GRC login page");
    }

    @When("I click on {string} option")
    public void iClickOnOption(String option) {
        if (option.equals("Login with Password")) {
            loginPage.clickLoginWithPassword();
        }
    }

    @When("I enter {string} in the email address field")
    public void iEnterInTheEmailAddressField(String email) {
        loginPage.enterEmailAddress(email);
    }

    @When("I enter {string} in the password field")
    public void iEnterInThePasswordField(String password) {
        loginPage.enterPassword(password);
    }

    @When("I click the Log In button")
    public void iClickTheLogInButton() {
        loginPage.clickLogInButton();
    }

    @When("I leave the email address field empty")
    public void iLeaveTheEmailAddressFieldEmpty() {
        loginPage.enterEmailAddress("");
    }

    @When("I leave the password field empty")
    public void iLeaveThePasswordFieldEmpty() {
        loginPage.enterPassword("");
    }

    @Then("I should be redirected to the home page")
    public void iShouldBeRedirectedToTheHomePage() {
        // Wait for page navigation
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String currentUrl = loginPage.getCurrentUrl();
        Assert.assertFalse(currentUrl.contains("login"), "Should be redirected away from login page");
        Assert.assertTrue(currentUrl.contains("home") || currentUrl.contains("dashboard") || !currentUrl.equals(initialUrl), 
                         "Should be redirected to home/dashboard page");
    }

    @Then("the home page should load successfully")
    public void theHomePageShouldLoadSuccessfully() {
        String pageTitle = loginPage.getPageTitle();
        String currentUrl = loginPage.getCurrentUrl();
        
        Assert.assertTrue(
            pageTitle.toLowerCase().contains("home") || 
            pageTitle.toLowerCase().contains("dashboard") || 
            currentUrl.contains("home") || 
            currentUrl.contains("dashboard"),
            "Home page should load successfully with appropriate title or URL"
        );
    }

    @Then("I should see an error message")
    public void iShouldSeeAnErrorMessage() {
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message should be displayed for invalid credentials");
    }

    @Then("I should remain on the login page")
    public void iShouldRemainOnTheLoginPage() {
        Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Should remain on the login page");
    }

    @Then("I should see validation errors for required fields")
    public void iShouldSeeValidationErrorsForRequiredFields() {
        Assert.assertTrue(loginPage.isValidationErrorDisplayed(), "Validation errors should be displayed for empty required fields");
    }
}