package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.GRCPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.testng.Assert;

/**
 * Step definitions for GRC upload document navigation scenarios.
 * Contains all step implementations matching the feature file.
 */
public class GRCValidationSteps {
    
    private GRCPage grcPage;
    
    public GRCValidationSteps() {
        this.grcPage = new GRCPage(Hooks.getDriver());
    }
    
    @Given("I am on the GRC dashboard page")
    public void iAmOnTheGRCDashboardPage() {
        grcPage.waitForPageLoad();
        Assert.assertTrue(grcPage.isOnGRCDashboard(), "User should be on GRC dashboard page");
    }
    
    @When("I click on the Upload button from ongoing services")
    public void iClickOnTheUploadButtonFromOngoingServices() {
        Assert.assertTrue(grcPage.isUploadButtonVisible(), "Upload button should be visible");
        grcPage.clickUploadButton();
    }
    
    @Then("I should be redirected to the upload document page")
    public void iShouldBeRedirectedToTheUploadDocumentPage() {
        Assert.assertTrue(grcPage.isOnUploadDocumentPage(), "Should be redirected to upload document page");
    }
    
    @And("the page title should contain {string}")
    public void thePageTitleShouldContain(String expectedTitleText) {
        String actualTitle = grcPage.getCurrentPageTitle();
        Assert.assertTrue(actualTitle.toLowerCase().contains(expectedTitleText.toLowerCase()), 
            "Page title '" + actualTitle + "' should contain '" + expectedTitleText + "'");
    }
    
    @And("I should see the document upload interface")
    public void iShouldSeeTheDocumentUploadInterface() {
        Assert.assertTrue(grcPage.isDocumentUploadInterfaceVisible(), 
            "Document upload interface should be visible");
    }
    
    @Given("there are no ongoing services available")
    public void thereAreNoOngoingServicesAvailable() {
        // This step assumes we're testing a state where no services exist
        // In a real scenario, this might involve setting up test data or mocking
        System.out.println("Testing scenario with no ongoing services");
    }
    
    @When("I look for the Upload button")
    public void iLookForTheUploadButton() {
        // Simply check for the button without any action
        System.out.println("Looking for Upload button visibility");
    }
    
    @Then("the Upload button should not be visible")
    public void theUploadButtonShouldNotBeVisible() {
        Assert.assertFalse(grcPage.isUploadButtonVisible(), 
            "Upload button should not be visible when no ongoing services exist");
    }
    
    @And("I should see a message indicating no ongoing services")
    public void iShouldSeeAMessageIndicatingNoOngoingServices() {
        Assert.assertTrue(grcPage.isNoServicesMessageDisplayed(), 
            "Message indicating no ongoing services should be displayed");
    }
    
    @When("I navigate back to the previous page")
    public void iNavigateBackToThePreviousPage() {
        grcPage.navigateBack();
        grcPage.waitForPageLoad();
    }
    
    @Then("I should be back on the GRC dashboard page")
    public void iShouldBeBackOnTheGRCDashboardPage() {
        Assert.assertTrue(grcPage.isOnGRCDashboard(), "Should be back on GRC dashboard page");
    }
    
    @And("the Upload button should still be visible")
    public void theUploadButtonShouldStillBeVisible() {
        Assert.assertTrue(grcPage.isUploadButtonVisible(), 
            "Upload button should still be visible after navigating back");
    }
}