package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.GrcLoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;

public class GrcLoginValidationSteps {
    
    private GrcLoginPage grcLoginPage;
    
    @Given("I am on the GRC login page")
    public void i_am_on_the_grc_login_page() {
        grcLoginPage = new GrcLoginPage(Hooks.getDriver());
        grcLoginPage.navigateToLoginPage();
    }
    
    @When("I enter {string} in the email address field")
    public void i_enter_in_the_email_address_field(String email) {
        grcLoginPage.enterEmailAddress(email);
    }
    
    @When("I click the Get OTP button")
    public void i_click_the_get_otp_button() {
        grcLoginPage.clickGetOtpButton();
    }
    
    @Then("I should be redirected to the OTP page")
    public void i_should_be_redirected_to_the_otp_page() {
        Assert.assertTrue(grcLoginPage.isOnOtpPage(), "User should be redirected to OTP page");
    }
    
    @When("I enter {string} in the OTP input boxes")
    public void i_enter_in_the_otp_input_boxes(String otp) {
        grcLoginPage.enterOtpDigits(otp);
    }
    
    @When("I submit the OTP")
    public void i_submit_the_otp() {
        grcLoginPage.submitOtp();
    }
    
    @Then("I should be successfully logged in")
    public void i_should_be_successfully_logged_in() {
        Assert.assertTrue(grcLoginPage.isLoggedInSuccessfully(), "User should be logged in successfully");
    }
    
    @Then("I should see an error message for invalid phone number")
    public void i_should_see_an_error_message_for_invalid_phone_number() {
        Assert.assertTrue(grcLoginPage.isErrorMessageDisplayed(), "Error message should be displayed for invalid phone number");
    }
    
    @Then("I should see an error message for invalid OTP")
    public void i_should_see_an_error_message_for_invalid_otp() {
        Assert.assertTrue(grcLoginPage.isErrorMessageDisplayed(), "Error message should be displayed for invalid OTP");
    }
}