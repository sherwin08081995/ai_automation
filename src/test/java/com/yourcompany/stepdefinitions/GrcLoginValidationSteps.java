package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.GrcLoginPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class GrcLoginValidationSteps {
    
    private GrcLoginPage grcLoginPage;
    private final String BASE_URL = System.getProperty("base.url", "https://your-grc-app.com/login");
    
    @Given("I am on the GRC login page")
    public void iAmOnTheGrcLoginPage() {
        grcLoginPage = new GrcLoginPage(Hooks.getDriver());
        grcLoginPage.navigateToLoginPage(BASE_URL);
    }
    
    @When("I enter {string} in the email address field")
    public void iEnterInTheEmailAddressField(String phoneNumber) {
        grcLoginPage.enterEmailAddress(phoneNumber);
    }
    
    @And("I click the Get OTP button")
    public void iClickTheGetOtpButton() {
        grcLoginPage.clickGetOtpButton();
    }
    
    @Then("I should be redirected to the OTP verification page")
    public void iShouldBeRedirectedToTheOtpVerificationPage() {
        Assert.assertTrue(grcLoginPage.isOtpPageDisplayed(), 
            "OTP verification page should be displayed");
    }
    
    @When("I enter {string} in the OTP input boxes")
    public void iEnterInTheOtpInputBoxes(String otp) {
        grcLoginPage.enterOtp(otp);
    }
    
    @Then("a popup should appear with available email addresses")
    public void aPopupShouldAppearWithAvailableEmailAddresses() {
        Assert.assertTrue(grcLoginPage.isEmailSelectionPopupDisplayed(), 
            "Email selection popup should be displayed");
    }
    
    @When("I select {string} from the popup")
    public void iSelectFromThePopup(String email) {
        grcLoginPage.selectEmailFromPopup(email);
    }
    
    @Then("I should be redirected to the GRC Home page")
    public void iShouldBeRedirectedToTheGrcHomePage() {
        Assert.assertTrue(grcLoginPage.isHomePageDisplayed(), 
            "GRC Home page should be displayed");
    }
    
    @And("I should confirm successful login to Home page")
    public void iShouldConfirmSuccessfulLoginToHomePage() {
        Assert.assertTrue(grcLoginPage.confirmSuccessfulLogin(), 
            "Should successfully login and reach home page");
    }
    
    @Then("I should see an error message for invalid phone number")
    public void iShouldSeeAnErrorMessageForInvalidPhoneNumber() {
        Assert.assertTrue(grcLoginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed for invalid phone number");
        String errorText = grcLoginPage.getErrorMessage();
        Assert.assertTrue(errorText.toLowerCase().contains("invalid") || 
                         errorText.toLowerCase().contains("error"), 
            "Error message should indicate invalid phone number");
    }
    
    @Then("I should see an error message for invalid OTP")
    public void iShouldSeeAnErrorMessageForInvalidOtp() {
        Assert.assertTrue(grcLoginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed for invalid OTP");
        String errorText = grcLoginPage.getErrorMessage();
        Assert.assertTrue(errorText.toLowerCase().contains("otp") || 
                         errorText.toLowerCase().contains("invalid"), 
            "Error message should indicate invalid OTP");
    }
}