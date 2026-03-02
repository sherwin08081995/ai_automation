package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.GRCLoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.testng.Assert;

public class GRCLoginValidationSteps {
    
    private GRCLoginPage grcLoginPage;
    
    public GRCLoginValidationSteps() {
        this.grcLoginPage = new GRCLoginPage(Hooks.getDriver());
    }
    
    @Given("I navigate to the GRC login page")
    public void i_navigate_to_the_grc_login_page() {
        grcLoginPage.navigateToLoginPage();
    }
    
    @When("I enter {string} in the email address field")
    public void i_enter_in_the_email_address_field(String emailOrMobile) {
        grcLoginPage.enterEmailAddress(emailOrMobile);
    }
    
    @Then("the email address field should contain {string}")
    public void the_email_address_field_should_contain(String expectedValue) {
        String actualValue = grcLoginPage.getEmailFieldValue();
        Assert.assertEquals(actualValue, expectedValue, "Email field value does not match expected value");
    }
    
    @When("I click on the Get OTP button")
    public void i_click_on_the_get_otp_button() {
        grcLoginPage.clickGetOTPButton();
    }
    
    @Then("the Get OTP button should be clickable")
    public void the_get_otp_button_should_be_clickable() {
        boolean isClickable = grcLoginPage.isGetOTPButtonClickable();
        Assert.assertTrue(isClickable, "Get OTP button should be clickable");
    }
    
    @When("I leave the email address field empty")
    public void i_leave_the_email_address_field_empty() {
        grcLoginPage.clearEmailField();
    }
    
    @Then("I should see an error message {string}")
    public void i_should_see_an_error_message(String expectedErrorMessage) {
        String actualErrorMessage = grcLoginPage.getErrorMessage();
        Assert.assertEquals(actualErrorMessage, expectedErrorMessage, "Error message does not match expected text");
    }
    
    @Then("the email address field should show validation styling")
    public void the_email_address_field_should_show_validation_styling() {
        boolean hasValidationStyling = grcLoginPage.hasEmailFieldValidationStyling();
        Assert.assertTrue(hasValidationStyling, "Email field should have validation styling");
    }
    
    @Then("the email address field should be visible")
    public void the_email_address_field_should_be_visible() {
        boolean isVisible = grcLoginPage.isEmailFieldVisible();
        Assert.assertTrue(isVisible, "Email address field should be visible");
    }
    
    @And("the Get OTP button should be visible")
    public void the_get_otp_button_should_be_visible() {
        boolean isVisible = grcLoginPage.isGetOTPButtonVisible();
        Assert.assertTrue(isVisible, "Get OTP button should be visible");
    }
    
    @And("the {string} link should be visible")
    public void the_link_should_be_visible(String linkText) {
        boolean isVisible = grcLoginPage.isLoginWithPasswordLinkVisible();
        Assert.assertTrue(isVisible, linkText + " link should be visible");
    }
    
    @And("the page title should contain {string}")
    public void the_page_title_should_contain(String expectedTitle) {
        String actualTitle = grcLoginPage.getPageTitle();
        Assert.assertEquals(actualTitle, expectedTitle, "Page title does not match expected text");
    }
}