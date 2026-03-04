package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.LoginPagePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.testng.Assert;

public class LoginPageValidationSteps {
    private LoginPagePage loginPage;
    
    public LoginPageValidationSteps() {
        this.loginPage = new LoginPagePage(Hooks.getDriver());
    }
    
    @Given("I navigate to the GRC login page")
    public void i_navigate_to_the_grc_login_page() {
        loginPage.navigateToLoginPage();
    }
    
    @When("I click Login with Password option")
    public void i_click_login_with_password_option() {
        loginPage.clickLoginWithPasswordOption();
    }
    
    @And("I enter {string} in Email address section")
    public void i_enter_in_email_address_section(String email) {
        loginPage.enterEmailAddress(email);
    }
    
    @And("I enter {string} in Password section")
    public void i_enter_in_password_section(String password) {
        loginPage.enterPassword(password);
    }
    
    @And("I click Log In CTA")
    public void i_click_log_in_cta() {
        loginPage.clickLoginButton();
    }
    
    @Then("I should confirm that Home page loaded successfully")
    public void i_should_confirm_that_home_page_loaded_successfully() {
        Assert.assertTrue(loginPage.isHomePageLoaded(), 
            "Home page should be loaded successfully after login");
    }
    
    @Then("I should see an error message for invalid email")
    public void i_should_see_an_error_message_for_invalid_email() {
        Assert.assertTrue(loginPage.isErrorMessageDisplayed() || loginPage.isEmailErrorDisplayed(), 
            "Error message should be displayed for invalid email format");
    }
    
    @And("I leave Email address section empty")
    public void i_leave_email_address_section_empty() {
        loginPage.leaveEmailEmpty();
    }
    
    @And("I leave Password section empty")
    public void i_leave_password_section_empty() {
        loginPage.leavePasswordEmpty();
    }
    
    @Then("I should see validation errors for required fields")
    public void i_should_see_validation_errors_for_required_fields() {
        Assert.assertTrue(loginPage.isEmailErrorDisplayed() || 
                         loginPage.isPasswordErrorDisplayed() || 
                         loginPage.isErrorMessageDisplayed(), 
            "Validation errors should be displayed for empty required fields");
    }
}