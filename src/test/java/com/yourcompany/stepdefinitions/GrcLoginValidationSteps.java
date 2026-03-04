package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.GrcLoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.testng.Assert;

public class GrcLoginValidationSteps {
    
    private GrcLoginPage grcLoginPage;
    
    public GrcLoginValidationSteps() {
        this.grcLoginPage = new GrcLoginPage(Hooks.getDriver());
    }
    
    @Given("I am on the GRC login page")
    public void i_am_on_the_grc_login_page() {
        grcLoginPage.navigateToLoginPage();
    }
    
    @When("I click on {string} option")
    public void i_click_on_option(String option) {
        if (option.equals("Login with Password")) {
            grcLoginPage.clickLoginWithPassword();
        }
    }
    
    @And("I enter {string} in the email address field")
    public void i_enter_in_the_email_address_field(String email) {
        grcLoginPage.enterEmail(email);
    }
    
    @And("I enter {string} in the password field")
    public void i_enter_in_the_password_field(String password) {
        grcLoginPage.enterPassword(password);
    }
    
    @And("I click on the Log In button")
    public void i_click_on_the_log_in_button() {
        grcLoginPage.clickLoginButton();
    }
    
    @Then("I should be successfully logged in")
    public void i_should_be_successfully_logged_in() {
        Assert.assertTrue(grcLoginPage.isSuccessfullyLoggedIn(), 
            "User should be successfully logged in and redirected away from login page");
    }
    
    @And("the home page should be loaded")
    public void the_home_page_should_be_loaded() {
        Assert.assertTrue(grcLoginPage.isHomePageLoaded(), 
            "Home page should be loaded successfully after login");
    }
    
    @Then("I should see an error message")
    public void i_should_see_an_error_message() {
        Assert.assertTrue(grcLoginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed for invalid credentials");
    }
    
    @And("I should remain on the login page")
    public void i_should_remain_on_the_login_page() {
        Assert.assertTrue(grcLoginPage.isOnLoginPage(), 
            "User should remain on login page after failed login attempt");
    }
    
    @And("I leave the email address field empty")
    public void i_leave_the_email_address_field_empty() {
        grcLoginPage.leaveEmailEmpty();
    }
    
    @And("I leave the password field empty")
    public void i_leave_the_password_field_empty() {
        grcLoginPage.leavePasswordEmpty();
    }
    
    @Then("I should see validation error messages")
    public void i_should_see_validation_error_messages() {
        Assert.assertTrue(grcLoginPage.isValidationErrorDisplayed(), 
            "Validation error messages should be displayed for empty required fields");
    }
}