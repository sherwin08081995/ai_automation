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
    
    @Given("I am on the GRC login page")
    public void i_am_on_the_grc_login_page() {
        loginPage = new LoginPage(Hooks.getDriver());
        // Navigate to GRC login page - URL to be configured
        Hooks.getDriver().get("https://your-grc-login-url.com");
    }
    
    @When("I click the Service Hub tab")
    public void i_click_the_service_hub_tab() {
        loginPage.clickServiceHubTab();
    }
    
    @When("I wait for the Service Hub page to load")
    @And("I wait for the Service Hub page to load")
    public void i_wait_for_the_service_hub_page_to_load() {
        loginPage.waitForServiceHubPageToLoad();
    }
    
    @When("I click the Licenses/registrations tab")
    @And("I click the Licenses/registrations tab")
    public void i_click_the_licenses_registrations_tab() {
        loginPage.clickLicensesRegistrationsTab();
    }
    
    @When("I click GST Registration")
    @And("I click GST Registration")
    public void i_click_gst_registration() {
        loginPage.clickGstRegistration();
    }
    
    @Then("the GST side panel should be displayed")
    public void the_gst_side_panel_should_be_displayed() {
        Assert.assertTrue(loginPage.isGstSidePanelDisplayed(), 
            "GST side panel should be visible but was not found");
    }
    
    @Then("I should see the GST registration options")
    @And("I should see the GST registration options")
    public void i_should_see_the_gst_registration_options() {
        Assert.assertTrue(loginPage.hasGstRegistrationOptions(), 
            "GST registration options should be available");
    }
    
    @Then("the Service Hub page should load successfully")
    public void the_service_hub_page_should_load_successfully() {
        Assert.assertTrue(loginPage.isServiceHubPageLoaded(), 
            "Service Hub page should load successfully");
    }
    
    @Then("the Licenses/registrations tab should be visible")
    @And("the Licenses/registrations tab should be visible")
    public void the_licenses_registrations_tab_should_be_visible() {
        Assert.assertTrue(loginPage.isLicensesRegistrationsTabVisible(), 
            "Licenses/registrations tab should be visible");
    }
    
    @Given("I have navigated to the Service Hub")
    public void i_have_navigated_to_the_service_hub() {
        loginPage.clickServiceHubTab();
        loginPage.waitForServiceHubPageToLoad();
    }
    
    @Given("I have clicked the Licenses/registrations tab")
    @And("I have clicked the Licenses/registrations tab")
    public void i_have_clicked_the_licenses_registrations_tab() {
        loginPage.clickLicensesRegistrationsTab();
    }
    
    @Then("the GST side panel should open")
    public void the_gst_side_panel_should_open() {
        Assert.assertTrue(loginPage.isGstSidePanelDisplayed(), 
            "GST side panel should open after clicking GST Registration");
    }
    
    @Then("the panel should contain GST registration information")
    @And("the panel should contain GST registration information")
    public void the_panel_should_contain_gst_registration_information() {
        Assert.assertTrue(loginPage.hasGstRegistrationOptions(), 
            "GST side panel should contain registration information");
    }
}