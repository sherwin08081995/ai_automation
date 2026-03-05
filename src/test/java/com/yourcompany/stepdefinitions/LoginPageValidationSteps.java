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
    
    @Given("I am on the GRC Service Hub page")
    public void i_am_on_the_grc_service_hub_page() {
        loginPage.navigateToGRCServiceHub();
    }
    
    @When("I click on the {string} tab")
    public void i_click_on_the_tab(String tabName) {
        switch (tabName) {
            case "Licenses/Registrations":
                loginPage.clickLicensesRegistrationsTab();
                break;
            case "GST Registration":
                loginPage.clickGSTRegistrationTab();
                break;
            default:
                throw new IllegalArgumentException("Unknown tab: " + tabName);
        }
    }
    
    @And("I wait for the side panel to open")
    public void i_wait_for_the_side_panel_to_open() {
        loginPage.waitForSidePanelToOpen();
    }
    
    @And("I click on the {string} CTA button")
    public void i_click_on_the_cta_button(String buttonName) {
        if ("Avail Service".equals(buttonName)) {
            loginPage.clickAvailServiceButton();
        } else {
            throw new IllegalArgumentException("Unknown button: " + buttonName);
        }
    }
    
    @Then("I should see a successful toast message")
    public void i_should_see_a_successful_toast_message() {
        Assert.assertTrue(loginPage.isSuccessToastDisplayed(), 
            "Success toast message was not displayed");
        
        String toastText = loginPage.getToastMessageText();
        System.out.println("Toast message displayed: " + toastText);
    }
}