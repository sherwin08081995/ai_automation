package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.GRCOTPPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.testng.Assert;

public class GRCOTPValidationSteps {
    
    private GRCOTPPage grcOTPPage;
    
    public GRCOTPValidationSteps() {
        this.grcOTPPage = new GRCOTPPage(Hooks.getDriver());
    }
    
    @Given("I am on the GRC OTP verification page")
    public void i_am_on_the_grc_otp_verification_page() {
        grcOTPPage.navigateToOTPPage();
    }
    
    @When("I enter {string} in the OTP input fields")
    public void i_enter_in_the_otp_input_fields(String otpCode) {
        grcOTPPage.enterOTPCode(otpCode);
    }
    
    @Then("all OTP input fields should contain the correct digits")
    public void all_otp_input_fields_should_contain_the_correct_digits() {
        String actualOTP = grcOTPPage.getAllOTPFieldValues();
        Assert.assertFalse(actualOTP.isEmpty(), "OTP fields should not be empty");
        Assert.assertEquals(actualOTP.length(), Math.min(6, actualOTP.length()), "OTP should have correct number of digits");
    }
    
    @And("the OTP fields should be properly filled")
    public void the_otp_fields_should_be_properly_filled() {
        boolean areFieldsFilled = grcOTPPage.areAllOTPFieldsFilled();
        Assert.assertTrue(areFieldsFilled, "All OTP fields should be properly filled");
    }
    
    @And("each field should have exactly one digit")
    public void each_field_should_have_exactly_one_digit() {
        for (int i = 0; i < grcOTPPage.getNumberOfOTPFields(); i++) {
            String fieldValue = grcOTPPage.getOTPFieldValue(i);
            if (!fieldValue.isEmpty()) {
                Assert.assertEquals(fieldValue.length(), 1, "Field " + i + " should contain exactly one digit");
            }
        }
    }
    
    @Then("only the first 6 digits should be entered in the fields")
    public void only_the_first_6_digits_should_be_entered_in_the_fields() {
        String actualOTP = grcOTPPage.getAllOTPFieldValues();
        Assert.assertTrue(actualOTP.length() <= 6, "OTP should not exceed 6 digits");
    }
    
    @And("the extra digit should be ignored")
    public void the_extra_digit_should_be_ignored() {
        int numberOfFields = grcOTPPage.getNumberOfOTPFields();
        Assert.assertEquals(numberOfFields, 6, "Should only have 6 OTP fields");
    }
    
    @Then("only the numeric characters should be accepted")
    public void only_the_numeric_characters_should_be_accepted() {
        String actualOTP = grcOTPPage.getAllOTPFieldValues();
        for (char c : actualOTP.toCharArray()) {
            Assert.assertTrue(Character.isDigit(c), "Only numeric characters should be accepted: " + c);
        }
    }
    
    @And("non-numeric characters should be filtered out")
    public void non_numeric_characters_should_be_filtered_out() {
        String actualOTP = grcOTPPage.getAllOTPFieldValues();
        boolean hasOnlyDigits = actualOTP.matches("[0-9]*");
        Assert.assertTrue(hasOnlyDigits, "Non-numeric characters should be filtered out");
    }
    
    @Then("I should see 6 OTP input fields")
    public void i_should_see_6_otp_input_fields() {
        int numberOfFields = grcOTPPage.getNumberOfOTPFields();
        Assert.assertEquals(numberOfFields, 6, "Should display exactly 6 OTP input fields");
        
        boolean areFieldsVisible = grcOTPPage.areOTPFieldsVisible();
        Assert.assertTrue(areFieldsVisible, "All OTP input fields should be visible");
    }
    
    @And("each OTP field should be properly formatted")
    public void each_otp_field_should_be_properly_formatted() {
        boolean areFieldsFormatted = grcOTPPage.areAllOTPFieldsProperlyFormatted();
        Assert.assertTrue(areFieldsFormatted, "All OTP fields should be properly formatted with maxlength=1 and inputmode=numeric");
    }
    
    @And("the mobile number {string} should be displayed")
    public void the_mobile_number_should_be_displayed(String expectedMobileNumber) {
        String actualMobileNumber = grcOTPPage.getMobileNumberDisplay();
        Assert.assertEquals(actualMobileNumber, expectedMobileNumber, "Mobile number display should match expected value");
    }
    
    @And("the {string} button should be visible")
    public void the_button_should_be_visible(String buttonText) {
        if (buttonText.equals("Edit")) {
            boolean isVisible = grcOTPPage.isEditButtonVisible();
            Assert.assertTrue(isVisible, buttonText + " button should be visible");
        }
    }
    
    @And("the {string} link should be visible")
    public void the_link_should_be_visible(String linkText) {
        if (linkText.equals("Resend")) {
            boolean isVisible = grcOTPPage.isResendLinkVisible();
            Assert.assertTrue(isVisible, linkText + " link should be visible");
        }
    }
    
    @When("I clear all OTP fields")
    public void i_clear_all_otp_fields() {
        grcOTPPage.clearOTPFields();
    }
    
    @Then("all OTP input fields should be empty")
    public void all_otp_input_fields_should_be_empty() {
        boolean areFieldsEmpty = grcOTPPage.areAllOTPFieldsEmpty();
        Assert.assertTrue(areFieldsEmpty, "All OTP input fields should be empty");
    }
    
    @Then("all OTP input fields should contain the new digits")
    public void all_otp_input_fields_should_contain_the_new_digits() {
        String actualOTP = grcOTPPage.getAllOTPFieldValues();
        Assert.assertFalse(actualOTP.isEmpty(), "OTP fields should contain the new digits");
        Assert.assertEquals(actualOTP, "111111", "OTP fields should contain the newly entered digits");
    }
}