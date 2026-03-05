package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.GRCHomePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.testng.Assert;

/**
 * Step definitions for GRC Home Page Needs Action validation
 */
public class GRCHomeValidationSteps {
    private GRCHomePage grcHomePage;
    private String homePageNeedsActionCount;
    private String compliancesPageNeedsActionCount;

    @Given("I am on the GRC home page")
    public void i_am_on_the_grc_home_page() {
        grcHomePage = new GRCHomePage(Hooks.getDriver());
        // Navigate to GRC home page - URL should be configured in properties or passed as parameter
        Hooks.getDriver().get("https://your-grc-app-url.com"); // Replace with actual URL
        Assert.assertTrue(grcHomePage.isHomePageDisplayed(), "GRC home page should be displayed");
    }

    @Given("I am logged in as a valid user")
    public void i_am_logged_in_as_a_valid_user() {
        // This step assumes user is already logged in
        // If login is required, implement login logic here
        Assert.assertTrue(grcHomePage.isHomePageDisplayed(), "User should be logged in and home page displayed");
    }

    @When("I fetch the count of {string} from the home page")
    public void i_fetch_the_count_of_from_the_home_page(String section) {
        Assert.assertEquals(section, "Needs Action", "Section should be 'Needs Action'");
        homePageNeedsActionCount = grcHomePage.getNeedsActionCount();
        Assert.assertNotNull(homePageNeedsActionCount, "Needs Action count should not be null");
        System.out.println("Home page Needs Action count: " + homePageNeedsActionCount);
    }

    @When("I click on the {string} section")
    public void i_click_on_the_section(String section) {
        Assert.assertEquals(section, "Needs Action", "Section should be 'Needs Action'");
        grcHomePage.clickNeedsAction();
    }

    @Then("I should be redirected to the compliances tab")
    public void i_should_be_redirected_to_the_compliances_tab() {
        // Wait for navigation to complete
        try {
            Thread.sleep(2000); // Allow time for page transition
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Assert.assertTrue(grcHomePage.isOnCompliancesPage(), "Should be redirected to compliances page");
    }

    @Then("the {string} count on compliances page should match the home page count")
    public void the_count_on_compliances_page_should_match_the_home_page_count(String section) {
        Assert.assertEquals(section, "Needs Action", "Section should be 'Needs Action'");
        compliancesPageNeedsActionCount = grcHomePage.getCompliancesNeedsActionCount();
        Assert.assertNotNull(compliancesPageNeedsActionCount, "Compliances page Needs Action count should not be null");
        System.out.println("Compliances page Needs Action count: " + compliancesPageNeedsActionCount);
        
        Assert.assertEquals(compliancesPageNeedsActionCount, homePageNeedsActionCount, 
            "Needs Action count should match between home page (" + homePageNeedsActionCount + 
            ") and compliances page (" + compliancesPageNeedsActionCount + ")");
    }

    @Given("the {string} section is visible")
    public void the_section_is_visible(String section) {
        Assert.assertEquals(section, "Needs Action", "Section should be 'Needs Action'");
        Assert.assertTrue(grcHomePage.isNeedsActionVisible(), "Needs Action section should be visible");
    }

    @Then("the compliances page should be displayed correctly")
    public void the_compliances_page_should_be_displayed_correctly() {
        Assert.assertTrue(grcHomePage.isCompliancesPageDisplayedCorrectly(), 
            "Compliances page should be displayed correctly with proper title and navigation");
    }

    @When("I view the {string} section")
    public void i_view_the_section(String section) {
        Assert.assertEquals(section, "Needs Action", "Section should be 'Needs Action'");
        Assert.assertTrue(grcHomePage.isNeedsActionVisible(), "Needs Action section should be visible for viewing");
    }

    @Then("the {string} count should be displayed")
    public void the_count_should_be_displayed(String section) {
        Assert.assertEquals(section, "Needs Action", "Section should be 'Needs Action'");
        String count = grcHomePage.getNeedsActionCount();
        Assert.assertNotNull(count, "Needs Action count should be displayed");
        Assert.assertFalse(count.trim().isEmpty(), "Needs Action count should not be empty");
    }

    @Then("the count should be a valid number")
    public void the_count_should_be_a_valid_number() {
        String count = grcHomePage.getNeedsActionCount();
        Assert.assertTrue(grcHomePage.isValidCount(count), 
            "Count '" + count + "' should be a valid number");
        
        // Additional validation - count should be non-negative
        int numericCount = Integer.parseInt(count);
        Assert.assertTrue(numericCount >= 0, "Count should be non-negative, but got: " + numericCount);
    }
}