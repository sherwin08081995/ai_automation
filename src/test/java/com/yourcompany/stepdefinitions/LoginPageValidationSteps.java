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
    
    @Given("I am on the GRC Service Hub page")
    public void iAmOnTheGrcServiceHubPage() {
        loginPage = new LoginPage(Hooks.getDriver());
        // Navigate to the GRC Service Hub URL
        String baseUrl = System.getProperty("base.url", "https://your-grc-service-hub-url.com");
        Hooks.getDriver().get(baseUrl);
        loginPage.waitForPageToLoad();
    }
    
    @When("I click on the {string} tab")
    public void iClickOnTheTab(String tabName) {
        switch (tabName.toLowerCase()) {
            case "licenses/registrations":
                loginPage.clickLicensesRegistrationsTab();
                break;
            case "gst registration":
                loginPage.clickGstRegistrationTab();
                break;
            default:
                throw new IllegalArgumentException("Unknown tab: " + tabName);
        }
    }
    
    @And("I wait for the side panel to open")
    public void iWaitForTheSidePanelToOpen() {
        loginPage.waitForSidePanelToOpen();
    }
    
    @And("I click on the {string} CTA button")
    public void iClickOnTheCTAButton(String buttonText) {
        if (buttonText.toLowerCase().contains("avail service")) {
            loginPage.clickAvailServiceButton();
        }
    }
    
    @Then("I should be redirected to the external website")
    public void iShouldBeRedirectedToTheExternalWebsite() {
        loginPage.verifyExternalRedirect();
        // Additional verification that we're on a different domain could be added here
    }
    
    @And("the page should load successfully")
    public void thePageShouldLoadSuccessfully() {
        Assert.assertTrue(loginPage.isPageLoadedSuccessfully(), 
            "Page did not load successfully after redirect");
    }
    
    @Then("the Licenses/Registrations section should be displayed")
    public void theLicensesRegistrationsSectionShouldBeDisplayed() {
        Assert.assertTrue(loginPage.isLicensesRegistrationsSectionDisplayed(), 
            "Licenses/Registrations section is not displayed");
    }
    
    @Then("the GST Registration side panel should be displayed")
    public void theGSTRegistrationSidePanelShouldBeDisplayed() {
        Assert.assertTrue(loginPage.isGstRegistrationSidePanelDisplayed(), 
            "GST Registration side panel is not displayed");
    }
    
    @And("the {string} button should be visible")
    public void theButtonShouldBeVisible(String buttonName) {
        if (buttonName.toLowerCase().contains("avail service")) {
            Assert.assertTrue(loginPage.isAvailServiceButtonVisible(), 
                "Avail Service button is not visible");
        }
    }
    
    @And("the side panel should contain relevant GST information")
    public void theSidePanelShouldContainRelevantGSTInformation() {
        Assert.assertTrue(loginPage.sidePanelContainsGstInformation(), 
            "Side panel does not contain relevant GST information");
    }
    
    @Then("the GST Registration tab should be active")
    public void theGSTRegistrationTabShouldBeActive() {
        Assert.assertTrue(loginPage.isGstRegistrationTabActive(), 
            "GST Registration tab is not active");
    }
    
    @And("the side panel should start opening")
    public void theSidePanelShouldStartOpening() {
        // Give some time for animation to start
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // This is a basic check - in real implementation, you might check for CSS transitions
        Assert.assertTrue(loginPage.isGstRegistrationSidePanelDisplayed(), 
            "Side panel is not starting to open");
    }
}