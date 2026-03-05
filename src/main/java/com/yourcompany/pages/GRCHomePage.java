package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * GRC Home Page object containing elements and actions
 */
public class GRCHomePage extends BasePage {

    // VERIFY ONLY - Needs Action section elements (no locator map provided)
    @FindBy(xpath = "//div[contains(text(), 'Needs Action') or contains(text(), 'Need Action')]")
    private WebElement needsActionSection; // VERIFY ONLY

    @FindBy(xpath = "//div[contains(text(), 'Needs Action') or contains(text(), 'Need Action')]/following-sibling::*[contains(@class, 'count') or contains(text(), '0123456789')]")
    private WebElement needsActionCount; // VERIFY ONLY

    @FindBy(xpath = "//span[contains(text(), 'Needs Action') or contains(text(), 'Need Action')]")
    private WebElement needsActionLabel; // VERIFY ONLY

    @FindBy(xpath = "//p[contains(text(), 'Needs Action') or contains(text(), 'Need Action')]")
    private WebElement needsActionTitle; // VERIFY ONLY

    // Alternative selectors for Needs Action clickable element
    @FindBy(xpath = "//div[contains(text(), 'Needs Action') or contains(text(), 'Need Action')]/parent::*")
    private WebElement needsActionClickableArea; // VERIFY ONLY

    // Compliances page elements
    @FindBy(xpath = "//h1[contains(text(), 'Compliances') or contains(text(), 'Compliance')]")
    private WebElement compliancesPageTitle; // VERIFY ONLY

    @FindBy(xpath = "//div[contains(text(), 'Compliances') or contains(text(), 'Compliance')]")
    private WebElement compliancesTab; // VERIFY ONLY

    @FindBy(xpath = "//div[contains(@class, 'tab') and (contains(text(), 'Compliances') or contains(text(), 'Compliance'))]")
    private WebElement compliancesTabActive; // VERIFY ONLY

    // Compliances page Needs Action count
    @FindBy(xpath = "//div[contains(text(), 'Needs Action') or contains(text(), 'Need Action')]//*[contains(text(), '0123456789')]")
    private WebElement compliancesNeedsActionCount; // VERIFY ONLY

    public GRCHomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Check if home page is displayed
     */
    public boolean isHomePageDisplayed() {
        try {
            return isElementDisplayed(needsActionSection) || isElementDisplayed(needsActionLabel) || isElementDisplayed(needsActionTitle);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get the Needs Action count from home page
     */
    public String getNeedsActionCount() {
        try {
            if (isElementDisplayed(needsActionCount)) {
                return safeGetText(needsActionCount).trim();
            }
            // Try to extract count from section text
            String sectionText = safeGetText(needsActionSection);
            return extractCountFromText(sectionText);
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * Click on Needs Action section
     */
    public void clickNeedsAction() {
        try {
            if (isElementDisplayed(needsActionClickableArea)) {
                safeClick(needsActionClickableArea);
            } else if (isElementDisplayed(needsActionSection)) {
                safeClick(needsActionSection);
            } else if (isElementDisplayed(needsActionLabel)) {
                safeClick(needsActionLabel);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to click on Needs Action section: " + e.getMessage());
        }
    }

    /**
     * Check if Needs Action section is visible
     */
    public boolean isNeedsActionVisible() {
        return isElementDisplayed(needsActionSection) || 
               isElementDisplayed(needsActionLabel) || 
               isElementDisplayed(needsActionTitle);
    }

    /**
     * Check if redirected to compliances page
     */
    public boolean isOnCompliancesPage() {
        try {
            return getCurrentUrl().toLowerCase().contains("compliances") || 
                   getCurrentUrl().toLowerCase().contains("compliance") ||
                   isElementDisplayed(compliancesPageTitle) ||
                   isElementDisplayed(compliancesTab) ||
                   isElementDisplayed(compliancesTabActive);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get Needs Action count from compliances page
     */
    public String getCompliancesNeedsActionCount() {
        try {
            if (isElementDisplayed(compliancesNeedsActionCount)) {
                return safeGetText(compliancesNeedsActionCount).trim();
            }
            return "0";
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * Check if compliances page is displayed correctly
     */
    public boolean isCompliancesPageDisplayedCorrectly() {
        return isOnCompliancesPage() && 
               (isElementDisplayed(compliancesPageTitle) || 
                isElementDisplayed(compliancesTab) || 
                isElementDisplayed(compliancesTabActive));
    }

    /**
     * Extract numeric count from text
     */
    private String extractCountFromText(String text) {
        if (text == null || text.isEmpty()) {
            return "0";
        }
        // Extract first number found in text
        String[] parts = text.split("\\s+");
        for (String part : parts) {
            if (part.matches("\\d+")) {
                return part;
            }
        }
        return "0";
    }

    /**
     * Validate if count is a valid number
     */
    public boolean isValidCount(String count) {
        try {
            Integer.parseInt(count);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}