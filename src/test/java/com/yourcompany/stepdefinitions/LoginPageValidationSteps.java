package com.yourcompany.stepdefinitions;

import com.yourcompany.hooks.Hooks;
import com.yourcompany.pages.LoginPage;
import io.cucumber.java.en.*;
import org.testng.Assert;

public class LoginPageValidationSteps {
    
    private LoginPage loginPage;
    private String overallCompliancesCount;
    private String riskBasedCompliancesCount;
    private int overallNumericCount;
    private int riskBasedNumericCount;
    
    @Given("I navigate to the GRC home page")
    public void i_navigate_to_the_grc_home_page() {
        loginPage = new LoginPage(Hooks.getDriver());
        loginPage.navigateToGRCHomePage();
    }
    
    @Given("I wait for the page to load completely")
    public void i_wait_for_the_page_to_load_completely() {
        Assert.assertTrue(loginPage.isPageLoaded(), "GRC home page did not load completely");
    }
    
    @When("I fetch the Overall Compliances count")
    public void i_fetch_the_overall_compliances_count() {
        overallCompliancesCount = loginPage.getOverallCompliancesCount();
        System.out.println("Overall Compliances Count: " + overallCompliancesCount);
        
        Assert.assertNotNull(overallCompliancesCount, "Overall Compliances count should not be null");
        Assert.assertFalse(overallCompliancesCount.isEmpty(), "Overall Compliances count should not be empty");
        
        overallNumericCount = loginPage.extractNumericValue(overallCompliancesCount);
    }
    
    @When("I fetch the Risk Based Compliances count")
    public void i_fetch_the_risk_based_compliances_count() {
        riskBasedCompliancesCount = loginPage.getRiskBasedCompliancesCount();
        System.out.println("Risk Based Compliances Count: " + riskBasedCompliancesCount);
        
        Assert.assertNotNull(riskBasedCompliancesCount, "Risk Based Compliances count should not be null");
        Assert.assertFalse(riskBasedCompliancesCount.isEmpty(), "Risk Based Compliances count should not be empty");
        
        riskBasedNumericCount = loginPage.extractNumericValue(riskBasedCompliancesCount);
    }
    
    @Then("the Overall Compliances count should match the Risk Based Compliances count")
    public void the_overall_compliances_count_should_match_the_risk_based_compliances_count() {
        System.out.println("Comparing counts: Overall=" + overallNumericCount + ", Risk Based=" + riskBasedNumericCount);
        
        Assert.assertEquals(overallNumericCount, riskBasedNumericCount, 
            String.format("COMPLIANCE COUNT MISMATCH: Overall Compliances (%d) does not match Risk Based Compliances (%d)", 
                overallNumericCount, riskBasedNumericCount));
        
        System.out.println("✓ SUCCESS: Both compliance counts match (" + overallNumericCount + ")");
    }
    
    @When("the counts do not match")
    public void the_counts_do_not_match() {
        // This step assumes counts don't match - used for negative test scenarios
        if (overallNumericCount == riskBasedNumericCount) {
            System.out.println("WARNING: Counts actually match. This step expects mismatch for testing purposes.");
        }
    }
    
    @Then("the test should fail with appropriate error message")
    public void the_test_should_fail_with_appropriate_error_message() {
        if (overallNumericCount != riskBasedNumericCount) {
            String errorMessage = String.format(
                "EXPECTED FAILURE: Compliance counts do not match. Overall=%d, Risk Based=%d", 
                overallNumericCount, riskBasedNumericCount);
            System.out.println(errorMessage);
            Assert.fail(errorMessage);
        } else {
            Assert.fail("Expected counts to be different for this test scenario, but they match: " + overallNumericCount);
        }
    }
    
    @Then("I should capture the actual count values for debugging")
    public void i_should_capture_the_actual_count_values_for_debugging() {
        System.out.println("=== COMPLIANCE COUNT DEBUG INFO ===");
        System.out.println("Overall Compliances Raw Text: '" + overallCompliancesCount + "'");
        System.out.println("Overall Compliances Numeric Value: " + overallNumericCount);
        System.out.println("Risk Based Compliances Raw Text: '" + riskBasedCompliancesCount + "'");
        System.out.println("Risk Based Compliances Numeric Value: " + riskBasedNumericCount);
        System.out.println("Match Status: " + (overallNumericCount == riskBasedNumericCount ? "MATCH" : "MISMATCH"));
        System.out.println("====================================");
    }
    
    @Then("the Overall Compliances section should be visible")
    public void the_overall_compliances_section_should_be_visible() {
        Assert.assertTrue(loginPage.isOverallCompliancesSectionVisible(), 
            "Overall Compliances section should be visible on the page");
    }
    
    @Then("the Risk Based Compliances section should be visible")
    public void the_risk_based_compliances_section_should_be_visible() {
        Assert.assertTrue(loginPage.isRiskBasedCompliancesSectionVisible(), 
            "Risk Based Compliances section should be visible on the page");
    }
    
    @Then("both compliance counts should be numeric values")
    public void both_compliance_counts_should_be_numeric_values() {
        Assert.assertTrue(loginPage.isNumericValue(overallCompliancesCount), 
            "Overall Compliances count should be numeric: '" + overallCompliancesCount + "'");
        
        Assert.assertTrue(loginPage.isNumericValue(riskBasedCompliancesCount), 
            "Risk Based Compliances count should be numeric: '" + riskBasedCompliancesCount + "'");
    }
    
    @Then("both compliance counts should be greater than zero")
    public void both_compliance_counts_should_be_greater_than_zero() {
        Assert.assertTrue(overallNumericCount > 0, 
            "Overall Compliances count should be greater than zero: " + overallNumericCount);
        
        Assert.assertTrue(riskBasedNumericCount > 0, 
            "Risk Based Compliances count should be greater than zero: " + riskBasedNumericCount);
    }
}