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

    public LoginPageValidationSteps() {
        this.loginPage = new LoginPage(Hooks.getDriver());
    }

    @Given("I am on the GRC login page")
    public void i_am_on_the_grc_login_page() {
        loginPage.navigateToLoginPage();
    }

    @When("I click on Login with Password option")
    public void i_click_on_login_with_password_option() {
        loginPage.clickLoginWithPassword();
    }

    @And("I enter {string} in the email address field")
    public void i_enter_in_the_email_address_field(String email) {
        loginPage.enterEmail(email);
    }

    @And("I enter {string} in the password field")
    public void i_enter_in_the_password_field(String password) {
        loginPage.enterPassword(password);
    }

    @And("I click the Log In button")
    public void i_click_the_log_in_button() {
        loginPage.clickLoginButton();
    }

    @Then("I should be redirected to the home page")
    public void i_should_be_redirected_to_the_home_page() {
        Assert.assertTrue(loginPage.isHomePageLoaded(), "Home page should be loaded after successful login");
    }

    @And("the home page should load successfully")
    public void the_home_page_should_load_successfully() {
        Assert.assertTrue(loginPage.isHomePageLoaded(), "Home page should load successfully");
    }

    @Then("I should see an error message")
    public void i_should_see_an_error_message() {
        Assert.assertTrue(loginPage.isErrorMessageVisible(), "Error message should be displayed for invalid credentials");
    }

    @Then("I should see validation error messages for required fields")
    public void i_should_see_validation_error_messages_for_required_fields() {
        Assert.assertTrue(loginPage.isValidationErrorVisible(), "Validation error messages should be displayed for empty fields");
    }
}