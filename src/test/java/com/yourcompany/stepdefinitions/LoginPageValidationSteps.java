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
    
    public LoginPageValidationSteps() {
        this.loginPage = new LoginPage(Hooks.getDriver());
    }

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        // Navigate to the login page - replace with actual URL
        Hooks.getDriver().get("https://example.com/login");
    }

    @When("I click on the service hub tab")
    public void i_click_on_the_service_hub_tab() {
        loginPage.clickServiceHubTab();
    }

    @Then("I should be redirected to the service hub page")
    public void i_should_be_redirected_to_the_service_hub_page() {
        // Verify redirect to service hub page - add proper verification
        String currentUrl = Hooks.getDriver().getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("service-hub") || currentUrl.contains("servicehub"), 
            "Should be redirected to service hub page");
    }

    @When("I click on the licenses/registrations option")
    public void i_click_on_the_licenses_registrations_option() {
        loginPage.clickLicensesRegistrationsOption();
    }

    @And("I wait for the page to load")
    public void i_wait_for_the_page_to_load() {
        loginPage.waitForPageLoad();
    }

    @When("I click on GST registration")
    public void i_click_on_gst_registration() {
        loginPage.clickGstRegistration();
    }

    @Then("the GST registration side panel should open")
    public void the_gst_registration_side_panel_should_open() {
        Assert.assertTrue(loginPage.isGstRegistrationSidePanelDisplayed(), 
            "GST registration side panel should be displayed");
    }

    @And("the panel should contain {string} CTA button")
    public void the_panel_should_contain_cta_button(String expectedButtonText) {
        Assert.assertTrue(loginPage.isAvailServiceButtonDisplayed(), 
            "Avail service button should be displayed");
        String actualButtonText = loginPage.getAvailServiceButtonText();
        Assert.assertEquals(actualButtonText, expectedButtonText, 
            "Button text should match expected text");
    }
}