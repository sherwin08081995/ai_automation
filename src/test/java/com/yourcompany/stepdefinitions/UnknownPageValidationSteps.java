package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.UnknownPagePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;

public class UnknownPageValidationSteps {
    
    private UnknownPagePage unknownPagePage;
    
    @Given("I am on the unknown page")
    public void i_am_on_the_unknown_page() {
        unknownPagePage = new UnknownPagePage(Hooks.getDriver());
        // Navigate to the page - URL should be provided by test configuration
        String baseUrl = System.getProperty("base.url", "http://localhost:3000");
        Hooks.getDriver().get(baseUrl);
    }
    
    @When("I enter {string} in the email address section")
    public void i_enter_in_the_email_address_section(String phoneNumber) {
        unknownPagePage.enterEmailAddress(phoneNumber);
    }
    
    @When("I click the Get OTP CTA")
    public void i_click_the_get_otp_cta() {
        unknownPagePage.clickGetOtpButton();
    }
    
    @Then("I should be redirected to the OTP page")
    public void i_should_be_redirected_to_the_otp_page() {
        // Wait a moment for page transition
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Assert.assertTrue(unknownPagePage.isOnOtpPage(), "User was not redirected to OTP page");
    }
    
    @When("I enter {string} in the OTP input boxes")
    public void i_enter_in_the_otp_input_boxes(String otp) {
        unknownPagePage.enterOtp(otp);
    }
    
    @Then("the OTP should be entered successfully")
    public void the_otp_should_be_entered_successfully() {
        Assert.assertTrue(unknownPagePage.isOtpEnteredSuccessfully(), "OTP was not entered successfully");
    }
    
    @Then("I should see an error message for invalid phone number")
    public void i_should_see_an_error_message_for_invalid_phone_number() {
        Assert.assertTrue(unknownPagePage.isErrorMessageDisplayed(), "Error message for invalid phone number is not displayed");
        String errorMessage = unknownPagePage.getErrorMessage().toLowerCase();
        Assert.assertTrue(errorMessage.contains("invalid") || errorMessage.contains("error") || 
                         errorMessage.contains("phone") || errorMessage.contains("number"),
                         "Error message does not indicate invalid phone number: " + errorMessage);
    }
    
    @When("I leave the OTP input boxes empty")
    public void i_leave_the_otp_input_boxes_empty() {
        unknownPagePage.leaveOtpFieldsEmpty();
    }
    
    @Then("I should see a validation message for empty OTP")
    public void i_should_see_a_validation_message_for_empty_otp() {
        Assert.assertTrue(unknownPagePage.isValidationMessageDisplayed(), "Validation message for empty OTP is not displayed");
    }
}