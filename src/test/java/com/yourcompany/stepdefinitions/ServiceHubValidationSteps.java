package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.ServiceHubPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.datatable.DataTable;
import org.testng.Assert;
import java.util.List;
import java.util.Map;

public class ServiceHubValidationSteps {
    private ServiceHubPage serviceHubPage;
    
    @Given("I am on the GRC Service Hub page")
    public void iAmOnTheGrcServiceHubPage() {
        serviceHubPage = new ServiceHubPage(Hooks.getDriver());
        // Navigate to the GRC Service Hub page URL
        Hooks.getDriver().get("https://your-grc-app-url.com/servicehub");
    }
    
    @When("I click on the Service Hub tab")
    public void iClickOnTheServiceHubTab() {
        Assert.assertTrue(serviceHubPage.isServiceHubTabVisible(), "Service Hub tab should be visible");
        serviceHubPage.clickServiceHubTab();
    }
    
    @And("I wait for the Service Hub page to load")
    public void iWaitForTheServiceHubPageToLoad() {
        serviceHubPage.waitForServiceHubPageToLoad();
    }
    
    @And("I wait for {string} seconds")
    public void iWaitForSeconds(String seconds) {
        int waitTime = Integer.parseInt(seconds);
        serviceHubPage.waitForServiceHubPageToLoadWithTime(waitTime);
    }
    
    @And("I click on the Licenses/Registrations tab")
    public void iClickOnTheLicensesRegistrationsTab() {
        Assert.assertTrue(serviceHubPage.isLicensesRegistrationsTabVisible(), "Licenses/Registrations tab should be visible");
        serviceHubPage.clickLicensesRegistrationsTab();
    }
    
    @And("I click on GST Registration")
    public void iClickOnGstRegistration() {
        Assert.assertTrue(serviceHubPage.isGstRegistrationOptionVisible(), "GST Registration option should be visible");
        serviceHubPage.clickGstRegistration();
    }
    
    @Then("the GST side panel should be opened")
    public void theGstSidePanelShouldBeOpened() {
        Assert.assertTrue(serviceHubPage.isGstSidePanelOpened(), "GST side panel should be opened");
    }
    
    @Then("the GST side panel should be opened within {string} seconds")
    public void theGstSidePanelShouldBeOpenedWithinSeconds(String expectedTime) {
        int timeLimit = Integer.parseInt(expectedTime);
        Assert.assertTrue(serviceHubPage.isGstSidePanelOpenedWithinTime(timeLimit), 
            "GST side panel should be opened within " + expectedTime + " seconds");
    }
    
    @And("I should see the GST Registration details")
    public void iShouldSeeTheGstRegistrationDetails() {
        String panelTitle = serviceHubPage.getGstPanelTitle();
        Assert.assertTrue(panelTitle.contains("GST") || panelTitle.contains("Registration"), 
            "GST Registration panel should contain relevant title");
    }
    
    @When("I navigate to Service Hub")
    public void iNavigateToServiceHub() {
        serviceHubPage.clickServiceHubTab();
        serviceHubPage.waitForServiceHubPageToLoad();
    }
    
    @And("I access Licenses/Registrations section")
    public void iAccessLicensesRegistrationsSection() {
        serviceHubPage.clickLicensesRegistrationsTab();
    }
    
    @And("I select GST Registration option")
    public void iSelectGstRegistrationOption() {
        serviceHubPage.clickGstRegistration();
    }
    
    @Then("GST Registration side panel should be displayed")
    public void gstRegistrationSidePanelShouldBeDisplayed() {
        Assert.assertTrue(serviceHubPage.isGstSidePanelOpened(), "GST Registration side panel should be displayed");
    }
    
    @And("the panel should contain relevant GST information")
    public void thePanelShouldContainRelevantGstInformation() {
        Assert.assertTrue(serviceHubPage.isGstSidePanelOpened(), "Panel should contain GST information");
        String panelTitle = serviceHubPage.getGstPanelTitle();
        Assert.assertFalse(panelTitle.isEmpty(), "Panel should have title or content");
    }
    
    @When("I perform the complete navigation flow:")
    public void iPerformTheCompleteNavigationFlow(DataTable dataTable) {
        List<Map<String, String>> steps = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> step : steps) {
            String action = step.get("action");
            switch (action) {
                case "Click Service Hub tab":
                    serviceHubPage.clickServiceHubTab();
                    break;
                case "Wait for page load":
                    serviceHubPage.waitForServiceHubPageToLoad();
                    break;
                case "Click Licenses/Registrations":
                    serviceHubPage.clickLicensesRegistrationsTab();
                    break;
                case "Click GST Registration":
                    serviceHubPage.clickGstRegistration();
                    break;
                default:
                    Assert.fail("Unknown action: " + action);
            }
        }
    }
    
    @Then("all navigation steps should be successful")
    public void allNavigationStepsShouldBeSuccessful() {
        Assert.assertTrue(serviceHubPage.isNavigationSuccessful(), "All navigation steps should be successful");
    }
    
    @And("the GST Registration panel should be accessible")
    public void theGstRegistrationPanelShouldBeAccessible() {
        Assert.assertTrue(serviceHubPage.isGstSidePanelOpened(), "GST Registration panel should be accessible");
    }
    
    @And("the Service Hub page fails to load")
    public void theServiceHubPageFailsToLoad() {
        // Simulate or check for page load failure
        Assert.assertTrue(serviceHubPage.isServiceHubPageLoadFailed(), "Service Hub page should fail to load for this test scenario");
    }
    
    @Then("I should see an appropriate error message")
    public void iShouldSeeAnAppropriateErrorMessage() {
        Assert.assertTrue(serviceHubPage.hasErrorMessage(), "Error message should be displayed");
        String errorMsg = serviceHubPage.getErrorMessage();
        Assert.assertFalse(errorMsg.isEmpty(), "Error message should not be empty");
    }
    
    @And("the navigation should handle the failure gracefully")
    public void theNavigationShouldHandleTheFailureGracefully() {
        // Verify that the application doesn't crash and shows proper error handling
        Assert.assertTrue(serviceHubPage.hasErrorMessage() || !serviceHubPage.isServiceHubTabVisible(), 
            "Application should handle navigation failure gracefully");
    }
}