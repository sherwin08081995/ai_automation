package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.ServiceHubPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.testng.Assert;

public class ServiceHubValidationSteps {
    private ServiceHubPage serviceHubPage;
    
    public ServiceHubValidationSteps() {
        this.serviceHubPage = new ServiceHubPage(Hooks.getDriver());
    }
    
    @Given("the user is logged into the application")
    public void theUserIsLoggedIntoTheApplication() {
        // Assuming user is already logged in or add login logic here
        System.out.println("User is logged into the application");
    }
    
    @When("the user opens the Service Hub tab from the left side menu")
    public void theUserOpensTheServiceHubTabFromTheLeftSideMenu() {
        serviceHubPage.clickServiceHubTab();
    }
    
    @And("the Service Hub page loads successfully")
    public void theServiceHubPageLoadsSuccessfully() {
        Assert.assertTrue(serviceHubPage.isServiceHubPageLoaded(), 
            "Service Hub page did not load successfully");
    }
    
    @And("the user clicks on the {string} tab")
    public void theUserClicksOnTheTab(String tabName) {
        if (tabName.equals("Licenses/Registration")) {
            serviceHubPage.clickLicensesRegistrationTab();
        }
    }
    
    @And("the Licenses/Registration page loads successfully")
    public void theLicensesRegistrationPageLoadsSuccessfully() {
        Assert.assertTrue(serviceHubPage.isLicensesRegistrationPageLoaded(), 
            "Licenses/Registration page did not load successfully");
    }
    
    @And("the user clicks on the {string} option")
    public void theUserClicksOnTheOption(String optionName) {
        if (optionName.equals("GST Registration")) {
            serviceHubPage.clickGstRegistrationOption();
        }
    }
    
    @Then("the GST Registration panel should open successfully")
    public void theGstRegistrationPanelShouldOpenSuccessfully() {
        Assert.assertTrue(serviceHubPage.isGstRegistrationPanelOpen(), 
            "GST Registration panel did not open successfully");
    }
    
    @And("the GST Registration form should be displayed")
    public void theGstRegistrationFormShouldBeDisplayed() {
        Assert.assertTrue(serviceHubPage.isGstRegistrationFormDisplayed(), 
            "GST Registration form is not displayed");
    }
    
    @Then("the Service Hub page should be accessible")
    public void theServiceHubPageShouldBeAccessible() {
        Assert.assertTrue(serviceHubPage.isServiceHubAccessible(), 
            "Service Hub page is not accessible");
    }
    
    @And("the {string} tab should be visible")
    public void theTabShouldBeVisible(String tabName) {
        if (tabName.equals("Licenses/Registration")) {
            Assert.assertTrue(serviceHubPage.isLicensesRegistrationTabVisible(), 
                "Licenses/Registration tab is not visible");
        }
    }
    
    @Then("the {string} option should be available")
    public void theOptionShouldBeAvailable(String optionName) {
        if (optionName.equals("GST Registration")) {
            Assert.assertTrue(serviceHubPage.isGstRegistrationOptionAvailable(), 
                "GST Registration option is not available");
        }
    }
    
    @Given("the user has navigated to Service Hub")
    public void theUserHasNavigatedToServiceHub() {
        serviceHubPage.clickServiceHubTab();
        Assert.assertTrue(serviceHubPage.isServiceHubPageLoaded(), 
            "Failed to navigate to Service Hub");
    }
    
    @And("the user has accessed the Licenses/Registration section")
    public void theUserHasAccessedTheLicensesRegistrationSection() {
        serviceHubPage.clickLicensesRegistrationTab();
        Assert.assertTrue(serviceHubPage.isLicensesRegistrationPageLoaded(), 
            "Failed to access Licenses/Registration section");
    }
    
    @When("the user selects the GST Registration option")
    public void theUserSelectsTheGstRegistrationOption() {
        serviceHubPage.clickGstRegistrationOption();
    }
    
    @Then("the GST Registration panel should display properly")
    public void theGstRegistrationPanelShouldDisplayProperly() {
        Assert.assertTrue(serviceHubPage.isGstRegistrationPanelOpen(), 
            "GST Registration panel is not displaying properly");
    }
    
    @And("all required form fields should be present")
    public void allRequiredFormFieldsShouldBePresent() {
        Assert.assertTrue(serviceHubPage.isGstRegistrationFormDisplayed(), 
            "Required form fields are not present in GST Registration form");
    }
}