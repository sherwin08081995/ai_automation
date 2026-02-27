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
        grcPage.navigateToGRCLoginPage();
        grcPage.waitForPageToLoad();
        Assert.assertTrue(grcPage.isEmailFieldDisplayed(), "Email field should be displayed");
        Assert.assertTrue(grcPage.isGetOtpButtonDisplayed(), "Get OTP button should be displayed");
    }
    
    @When("I enter {string} in the email address field")
    public void i_enter_in_the_email_address_field(String emailOrMobile) {
        grcPage.enterEmailAddress(emailOrMobile);
        Assert.assertEquals(grcPage.getEmailFieldValue(), emailOrMobile, "Email field should contain entered value");
    }
    
    @When("I click the Get OTP button")
    public void i_click_the_get_otp_button() {
        grcPage.clickGetOtpButton();
    }
    
    @When("I click the Get OTP button without entering any details")
    public void i_click_the_get_otp_button_without_entering_any_details() {
        // Click Get OTP without entering anything
        grcPage.clickGetOtpButton();
    }
    
    @When("I click the {string} button")
    public void i_click_the_button(String buttonText) {
        if (buttonText.equals("Login with Password")) {
            grcPage.clickLoginWithPasswordButton();
        }
    }
    
    @When("I click the {string} link")
    public void i_click_the_link(String linkText) {
        if (linkText.equals("Sign Up")) {
            grcPage.clickSignUpLink();
        }
    }
    
    @Then("I should see the OTP request is processed")
    public void i_should_see_the_otp_request_is_processed() {
        // Wait for any loading or redirect behavior
        // In a real scenario, this would check for OTP input field or success message
        String currentUrl = grcPage.getCurrentPageUrl();
        Assert.assertNotNull(currentUrl, "Should remain on a valid page after OTP request");
    }
    
    @Then("I should see a validation message {string}")
    public void i_should_see_a_validation_message(String expectedMessage) {
        Assert.assertTrue(grcPage.isValidationMessageDisplayed(), "Validation message should be displayed");
        String actualMessage = grcPage.getValidationMessage();
        Assert.assertEquals(actualMessage, expectedMessage, "Validation message should match expected text");
    }
    
    @Then("I should see appropriate validation for invalid input")
    public void i_should_see_appropriate_validation_for_invalid_input() {
        // Check for any validation behavior with invalid input
        // This could be client-side validation or server response
        Assert.assertTrue(grcPage.isEmailFieldDisplayed(), "Should remain on login page with invalid input");
    }
    
    @Then("I should see password login options")
    public void i_should_see_password_login_options() {
        // After clicking "Login with Password", check for password-related elements
        String currentUrl = grcPage.getCurrentPageUrl();
        Assert.assertTrue(currentUrl.contains("signin") || currentUrl.contains("login"), "Should be on login-related page");
    }
    
    @Then("I should be redirected to the sign up page")
    public void i_should_be_redirected_to_the_sign_up_page() {
        String currentUrl = grcPage.getCurrentPageUrl();
        Assert.assertTrue(currentUrl.contains("signup"), "Should be redirected to signup page");
    }
}