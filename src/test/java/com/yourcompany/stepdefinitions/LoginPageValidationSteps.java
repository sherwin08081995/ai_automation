package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.datatable.DataTable;
import org.testng.Assert;
import java.util.List;
import java.util.Map;

public class LoginPageValidationSteps {
    private LoginPage loginPage;
    
    @Given("I am on the GRC login page")
    public void iAmOnTheGrcLoginPage() {
        loginPage = new LoginPage(Hooks.getDriver());
        // Navigate to the GRC login page URL
        Hooks.getDriver().get("https://your-grc-app-url.com/login");
    }
    
    @When("I click on the Service Hub tab")
    public void iClickOnTheServiceHubTab() {
        Assert.assertTrue(loginPage.isServiceHubTabVisible(), "Service Hub tab should be visible");
        loginPage.clickServiceHubTab();
    }
    
    @And("I wait for the Service Hub page to load")
    public void iWaitForTheServiceHubPageToLoad() {
        loginPage.waitForServiceHubPageToLoad();
    }
    
    @And("I click on the Licenses/Registrations tab")
    public void iClickOnTheLicensesRegistrationsTab() {
        Assert.assertTrue(loginPage.isLicensesRegistrationsTabVisible(), "Licenses/Registrations tab should be visible");
        loginPage.clickLicensesRegistrationsTab();
    }
    
    @And("I click on GST Registration")
    public void iClickOnGstRegistration() {
        Assert.assertTrue(loginPage.isGstRegistrationOptionVisible(), "GST Registration option should be visible");
        loginPage.clickGstRegistration();
    }
    
    @Then("the GST side panel should be opened")
    public void theGstSidePanelShouldBeOpened() {
        Assert.assertTrue(loginPage.isGstSidePanelOpened(), "GST side panel should be opened");
    }
    
    @And("I should see the GST Registration details")
    public void iShouldSeeTheGstRegistrationDetails() {
        String panelTitle = loginPage.getGstPanelTitle();
        Assert.assertTrue(panelTitle.contains("GST") || panelTitle.contains("Registration"), 
            "GST Registration panel should contain relevant title");
    }
    
    @When("I navigate to Service Hub")
    public void iNavigateToServiceHub() {
        loginPage.clickServiceHubTab();
        loginPage.waitForServiceHubPageToLoad();
    }
    
    @And("I access Licenses/Registrations section")
    public void iAccessLicensesRegistrationsSection() {
        loginPage.clickLicensesRegistrationsTab();
    }
    
    @And("I select GST Registration option")
    public void iSelectGstRegistrationOption() {
        loginPage.clickGstRegistration();
    }
    
    @Then("GST Registration side panel should be displayed")
    public void gstRegistrationSidePanelShouldBeDisplayed() {
        Assert.assertTrue(loginPage.isGstSidePanelOpened(), "GST Registration side panel should be displayed");
    }
    
    @And("the panel should contain relevant GST information")
    public void thePanelShouldContainRelevantGstInformation() {
        Assert.assertTrue(loginPage.isGstSidePanelOpened(), "Panel should contain GST information");
        String panelTitle = loginPage.getGstPanelTitle();
        Assert.assertFalse(panelTitle.isEmpty(), "Panel should have title or content");
    }
    
    @When("I perform the complete navigation flow:")
    public void iPerformTheCompleteNavigationFlow(DataTable dataTable) {
        List<Map<String, String>> steps = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> step : steps) {
            String action = step.get("action");
            switch (action) {
                case "Click Service Hub tab":
                    loginPage.clickServiceHubTab();
                    break;
                case "Wait for page load":
                    loginPage.waitForServiceHubPageToLoad();
                    break;
                case "Click Licenses/Registrations":
                    loginPage.clickLicensesRegistrationsTab();
                    break;
                case "Click GST Registration":
                    loginPage.clickGstRegistration();
                    break;
                default:
                    Assert.fail("Unknown action: " + action);
            }
        }
    }
    
    @Then("all navigation steps should be successful")
    public void allNavigationStepsShouldBeSuccessful() {
        Assert.assertTrue(loginPage.isNavigationSuccessful(), "All navigation steps should be successful");
    }
    
    @And("the GST Registration panel should be accessible")
    public void theGstRegistrationPanelShouldBeAccessible() {
        Assert.assertTrue(loginPage.isGstSidePanelOpened(), "GST Registration panel should be accessible");
    }
}