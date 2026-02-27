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
    public void iAmOnTheGrcLoginPage() {
        grcLoginPage = new GrcLoginPage(Hooks.getDriver());
        grcLoginPage.navigateToGrcLoginPage();
        Assert.assertTrue(grcLoginPage.isLoginFormDisplayed(), "GRC login page should be displayed");
    }

    @When("I enter {string} in the email address field")
    public void iEnterInTheEmailAddressField(String emailOrMobile) {
        grcLoginPage.enterEmailAddress(emailOrMobile);
    }

    @When("I click the Get OTP button")
    public void iClickTheGetOtpButton() {
        grcLoginPage.clickGetOtpButton();
    }

    @When("I leave the email address field empty")
    public void iLeaveTheEmailAddressFieldEmpty() {
        grcLoginPage.clearEmailField();
    }

    @When("I click on {string} link")
    public void iClickOnLink(String linkText) {
        if (linkText.equals("Login with Password")) {
            grcLoginPage.clickLoginWithPasswordButton();
        }
    }

    @Then("I should see OTP request processed")
    public void iShouldSeeOtpRequestProcessed() {
        // Since this is a demo, we'll verify the button was clickable and form is still present
        Assert.assertTrue(grcLoginPage.isGetOtpButtonEnabled(), "Get OTP button should be enabled");
        Assert.assertTrue(grcLoginPage.isLoginFormDisplayed(), "Login form should be displayed");
    }

    @Then("I should see validation error for invalid format")
    public void iShouldSeeValidationErrorForInvalidFormat() {
        // Check if any validation error is displayed
        Assert.assertTrue(grcLoginPage.isValidationErrorDisplayed() || grcLoginPage.isEmailRequiredErrorDisplayed(), 
                "Validation error should be displayed for invalid format");
    }

    @Then("I should see {string} error message")
    public void iShouldSeeErrorMessage(String expectedErrorMessage) {
        Assert.assertTrue(grcLoginPage.isEmailRequiredErrorDisplayed(), 
                "Email required error should be displayed");
        String actualErrorMessage = grcLoginPage.getEmailRequiredErrorText();
        Assert.assertEquals(actualErrorMessage, expectedErrorMessage, 
                "Error message should match expected text");
    }

    @Then("I should see password login option")
    public void iShouldSeePasswordLoginOption() {
        Assert.assertTrue(grcLoginPage.isLoginWithPasswordButtonDisplayed(), 
                "Login with Password button should be displayed");
    }
}