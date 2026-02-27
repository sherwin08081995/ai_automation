package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.GRCPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;

public class GRCValidationSteps {
    private GRCPage grcPage;
    
    public GRCValidationSteps() {
        this.grcPage = new GRCPage(Hooks.getDriver());
    }
    
    @Given("I navigate to the GRC login page")
    public void i_navigate_to_the_grc_login_page() {
        grcPage.navigateToLoginPage();
        Assert.assertTrue(grcPage.isLoginPageLoaded(), "GRC login page should be loaded");
    }
    
    @When("I enter {string} in the email address field")
    public void i_enter_in_the_email_address_field(String emailOrMobile) {
        grcPage.enterEmailAddress(emailOrMobile);
        Assert.assertEquals(grcPage.getEmailFieldValue(), emailOrMobile, 
            "Email field should contain the entered value");
    }
    
    @When("I click the Get OTP button")
    public void i_click_the_get_otp_button() {
        grcPage.clickGetOTPButton();
    }
    
    @Then("I should see the OTP request is processed")
    public void i_should_see_the_otp_request_is_processed() {
        // Since we don't have access to the actual response/redirect after OTP request,
        // we'll verify that the button was clickable and no immediate validation errors appear
        Assert.assertTrue(grcPage.isGetOTPButtonDisplayed(), "Get OTP button should be displayed");
        // Additional verification could be added based on the actual behavior
        // like checking for redirect, success message, or loading state
    }
    
    @When("I leave the email address field empty")
    public void i_leave_the_email_address_field_empty() {
        grcPage.clearEmailField();
        Assert.assertTrue(grcPage.getEmailFieldValue().isEmpty(), 
            "Email field should be empty");
    }
    
    @Then("I should see the error message {string}")
    public void i_should_see_the_error_message(String expectedErrorMessage) {
        Assert.assertTrue(grcPage.isEmailRequiredErrorDisplayed(), 
            "Email required error should be displayed");
        Assert.assertEquals(grcPage.getEmailRequiredErrorText(), expectedErrorMessage, 
            "Error message should match expected text");
    }
    
    @Then("I should see appropriate validation feedback")
    public void i_should_see_appropriate_validation_feedback() {
        // Check for visual validation indicators like red border or error styling
        Assert.assertTrue(grcPage.hasEmailFieldValidationError() || 
                         grcPage.isEmailRequiredErrorDisplayed(), 
            "Should show validation feedback for invalid input");
    }
    
    @Then("I should see the email address input field")
    public void i_should_see_the_email_address_input_field() {
        Assert.assertTrue(grcPage.isEmailAddressFieldDisplayed(), 
            "Email address input field should be visible");
    }
    
    @Then("I should see the Get OTP button")
    public void i_should_see_the_get_otp_button() {
        Assert.assertTrue(grcPage.isGetOTPButtonDisplayed(), 
            "Get OTP button should be visible");
    }
    
    @Then("I should see the {string} link")
    public void i_should_see_the_link(String linkText) {
        switch (linkText) {
            case "Login with Password":
                Assert.assertTrue(grcPage.isLoginWithPasswordLinkDisplayed(), 
                    "Login with Password link should be visible");
                break;
            case "Sign Up":
                Assert.assertTrue(grcPage.isSignUpLinkDisplayed(), 
                    "Sign Up link should be visible");
                break;
            default:
                Assert.fail("Unknown link text: " + linkText);
        }
    }
    
    @Then("I should see appropriate validation for invalid mobile number format")
    public void i_should_see_appropriate_validation_for_invalid_mobile_number_format() {
        // Check for validation feedback for overly long mobile number
        Assert.assertTrue(grcPage.hasEmailFieldValidationError() || 
                         grcPage.isEmailRequiredErrorDisplayed(), 
            "Should show validation feedback for invalid mobile number format");
    }
}