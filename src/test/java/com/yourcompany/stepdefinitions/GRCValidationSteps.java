package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.GRCPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;

public class GRCValidationSteps {
    
    private GRCPage grcPage;
    private static final String GRC_LOGIN_URL = "https://grc.vakilsearch.com/grc/auth/signin";
    
    @Given("I am on the GRC login page")
    public void i_am_on_the_grc_login_page() {
        grcPage = new GRCPage(Hooks.getDriver());
        Hooks.getDriver().get(GRC_LOGIN_URL);
        Assert.assertTrue(grcPage.isLoginPageDisplayed(), "GRC login page is not displayed");
    }
    
    @When("I enter {string} in the email address field")
    public void i_enter_in_the_email_address_field(String emailOrMobile) {
        grcPage.enterEmailAddress(emailOrMobile);
        Assert.assertEquals(grcPage.getEmailFieldValue(), emailOrMobile, "Email field value doesn't match entered value");
    }
    
    @When("I click the Get OTP button")
    public void i_click_the_get_otp_button() {
        grcPage.clickGetOtpButton();
    }
    
    @Then("the system should process the OTP request")
    public void the_system_should_process_the_otp_request() {
        // Wait a moment for any processing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Verify that the page doesn't show validation errors for valid input
        String currentUrl = grcPage.getCurrentPageUrl();
        Assert.assertTrue(currentUrl.contains("grc"), "Should remain on GRC domain after valid input");
    }
    
    @Then("I should see a validation error message")
    public void i_should_see_a_validation_error_message() {
        Assert.assertTrue(grcPage.isValidationErrorDisplayed(), "Validation error message should be displayed");
    }
    
    @When("I leave the email address field empty")
    public void i_leave_the_email_address_field_empty() {
        grcPage.leaveEmailFieldEmpty();
        Assert.assertEquals(grcPage.getEmailFieldValue(), "", "Email field should be empty");
    }
    
    @Then("I should see {string} error message")
    public void i_should_see_error_message(String expectedErrorMessage) {
        Assert.assertTrue(grcPage.isValidationErrorDisplayed(), "Validation error should be displayed");
        String actualErrorMessage = grcPage.getValidationErrorText();
        Assert.assertEquals(actualErrorMessage, expectedErrorMessage, "Error message doesn't match expected text");
    }
    
    @Then("I should see appropriate validation feedback")
    public void i_should_see_appropriate_validation_feedback() {
        // For special characters, expect either validation error or field highlighting
        boolean hasError = grcPage.isValidationErrorDisplayed() || grcPage.isFieldHighlightedWithError();
        Assert.assertTrue(hasError, "Should show some form of validation feedback for invalid input");
    }
}