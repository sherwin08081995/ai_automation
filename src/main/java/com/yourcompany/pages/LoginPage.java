package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {
    
    // Overall Compliances section elements
    @FindBy(xpath = "//div[contains(text(), 'Overall Compliances')]/following-sibling::*[contains(@class, 'count') or contains(text(), 'number') or contains(@data-testid, 'count')]")
    private WebElement overallCompliancesCount; // VERIFY ONLY - adjust XPath based on actual HTML structure
    
    @FindBy(xpath = "//span[normalize-space()='Overall Compliances']")
    private WebElement overallCompliancesLabel; // VERIFY ONLY - adjust based on actual tag and text
    
    // Risk Based Compliances section elements
    @FindBy(xpath = "//div[contains(text(), 'Risk Based Compliances')]/following-sibling::*[contains(@class, 'count') or contains(text(), 'number') or contains(@data-testid, 'count')]")
    private WebElement riskBasedCompliancesCount; // VERIFY ONLY - adjust XPath based on actual HTML structure
    
    @FindBy(xpath = "//span[normalize-space()='Risk Based Compliances']")
    private WebElement riskBasedCompliancesLabel; // VERIFY ONLY - adjust based on actual tag and text
    
    // Alternative selectors - use if above don't work
    @FindBy(css = "[data-testid='overall-compliance-count']")
    private WebElement overallCountByTestId; // VERIFY ONLY - if data-testid exists
    
    @FindBy(css = "[data-testid='risk-based-compliance-count']")
    private WebElement riskBasedCountByTestId; // VERIFY ONLY - if data-testid exists
    
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    public void navigateToGRCHomePage() {
        String baseUrl = System.getProperty("base.url", "https://your-grc-app.com");
        driver.get(baseUrl);
        waitForPageToLoad();
    }
    
    public boolean isPageLoaded() {
        try {
            return isElementDisplayed(overallCompliancesLabel) && isElementDisplayed(riskBasedCompliancesLabel);
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getOverallCompliancesCount() {
        try {
            // Try primary selector first
            if (isElementDisplayed(overallCompliancesCount)) {
                return getElementText(overallCompliancesCount);
            }
            // Try alternative selector
            else if (isElementDisplayed(overallCountByTestId)) {
                return getElementText(overallCountByTestId);
            }
            // Fallback: search for any numeric element near "Overall Compliances" text
            else {
                WebElement countElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//text()[contains(., 'Overall Compliances')]/ancestor::div//span[matches(text(), '^\\d+$')] | //text()[contains(., 'Overall Compliances')]/following::*[matches(text(), '^\\d+$')][1]")
                ));
                return countElement.getText().trim();
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to locate Overall Compliances count element: " + e.getMessage());
        }
    }
    
    public String getRiskBasedCompliancesCount() {
        try {
            // Try primary selector first
            if (isElementDisplayed(riskBasedCompliancesCount)) {
                return getElementText(riskBasedCompliancesCount);
            }
            // Try alternative selector
            else if (isElementDisplayed(riskBasedCountByTestId)) {
                return getElementText(riskBasedCountByTestId);
            }
            // Fallback: search for any numeric element near "Risk Based Compliances" text
            else {
                WebElement countElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//text()[contains(., 'Risk Based Compliances')]/ancestor::div//span[matches(text(), '^\\d+$')] | //text()[contains(., 'Risk Based Compliances')]/following::*[matches(text(), '^\\d+$')][1]")
                ));
                return countElement.getText().trim();
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to locate Risk Based Compliances count element: " + e.getMessage());
        }
    }
    
    public boolean isOverallCompliancesSectionVisible() {
        return isElementDisplayed(overallCompliancesLabel);
    }
    
    public boolean isRiskBasedCompliancesSectionVisible() {
        return isElementDisplayed(riskBasedCompliancesLabel);
    }
    
    public boolean isNumericValue(String value) {
        try {
            // Remove any non-digit characters (commas, spaces, etc.) and check if it's a number
            String cleanValue = value.replaceAll("[^\\d]", "");
            Integer.parseInt(cleanValue);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public int extractNumericValue(String value) {
        try {
            // Remove any non-digit characters and convert to integer
            String cleanValue = value.replaceAll("[^\\d]", "");
            return Integer.parseInt(cleanValue);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Unable to extract numeric value from: " + value);
        }
    }
}