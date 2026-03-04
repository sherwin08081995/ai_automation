package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.LoginPage;
import io.cucumber.java.en.*;
import org.testng.Assert;

public class LoginPageValidationSteps {
    
    private LoginPage loginPage;
    
    @Given("I am on the GRC login page")
    public void i_am_on_the_grc_login_page() {
        // Navigate to login page - replace with actual URL
        String loginUrl = System.getProperty("base.url", "https://your-grc-app.com/login");
        Hooks.getDriver().get(loginUrl);
        loginPage = new LoginPage(Hooks.getDriver());
    }
    
    @When("I click on {string} option")
    public void i_click_on_option(String optionText) {
        if (optionText.equals("Login with Password")) {
            loginPage.clickLoginWithPasswordOption();
        }
    }
    
    @When("I enter {string} in the email address field")
    public void i_enter_in_the_email_address_field(String email) {
        loginPage.enterEmail(email);
    }
    
    @When("I enter {string} in the password field")
    public void i_enter_in_the_password_field(String password) {
        loginPage.enterPassword(password);
    }
    
    @When("I click the {string} button")
    public void i_click_the_button(String buttonText) {
        if (buttonText.equals("Log In")) {
            loginPage.clickLoginButton();
        }
    }
    
    @Then("I should be redirected to the home page")
    public void i_should_be_redirected_to_the_home_page() {
        // Wait for page to load and verify URL or page elements
        String currentUrl = Hooks.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("home") || currentUrl.contains("dashboard"),
            "User was not redirected to home page. Current URL: " + currentUrl);
    }
    
    @Then("I should see the {string} tab on the left side")
    public void i_should_see_the_tab_on_the_left_side(String tabName) {
        if (tabName.equals("Home")) {
            Assert.assertTrue(loginPage.isHomeTabVisible(),
                "Home tab is not visible on the left side");
        }
    }
    
    @Then("I should see an error message")
    public void i_should_see_an_error_message() {
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
            "Error message is not displayed for invalid credentials");
    }
    
    @Then("I should see the email address field")
    public void i_should_see_the_email_address_field() {
        Assert.assertTrue(loginPage.isEmailFieldDisplayed(),
            "Email address field is not displayed");
    }
    
    @Then("I should see the password field")
    public void i_should_see_the_password_field() {
        Assert.assertTrue(loginPage.isPasswordFieldDisplayed(),
            "Password field is not displayed");
    }
    
    @Then("I should see the {string} button")
    public void i_should_see_the_button(String buttonText) {
        if (buttonText.equals("Log In")) {
            Assert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "Log In button is not displayed");
        }
    }
}