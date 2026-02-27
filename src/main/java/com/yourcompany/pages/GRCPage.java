package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object Model for GRC page containing upload document functionality.
 * Extends BasePage for common WebDriver utilities.
 */
public class GRCPage extends BasePage {
    
    // Header elements
    @FindBy(id = "grc-header")
    private WebElement headerSection;
    
    // Notification button
    @FindBy(id = "notification-button")
    private WebElement notificationButton;
    
    // Cart button  
    @FindBy(id = "cart-button")
    private WebElement cartButton;
    
    // Office selector dropdown
    @FindBy(id = "react-select-2-input")
    private WebElement officeSelector;
    
    // Scenario search input
    @FindBy(id = "scenario-search")
    private WebElement scenarioSearchInput;
    
    // Upload related elements (based on common patterns for upload CTAs)
    @FindBy(css = "button[data-track*='upload'], button:contains('Upload'), .upload-btn")
    private WebElement uploadButton;
    
    // Document upload interface elements
    @FindBy(css = ".upload-interface, .document-upload, [data-testid='upload-area']")
    private WebElement documentUploadInterface;
    
    // Services section
    @FindBy(css = ".ongoing-services, .services-section")
    private WebElement ongoingServicesSection;
    
    // No services message
    @FindBy(css = ".no-services, .empty-services-message")
    private WebElement noServicesMessage;
    
    // Report issue button
    @FindBy(xpath = "//button[contains(., 'Report an issue')]")
    private WebElement reportIssueButton;
    
    public GRCPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }
    
    /**
     * Verify if user is on GRC dashboard page
     */
    public boolean isOnGRCDashboard() {
        return isElementDisplayed(headerSection) && 
               getCurrentUrl().contains("grc") &&
               getPageTitle().contains("GRC");
    }
    
    /**
     * Click on Upload button from ongoing services
     */
    public void clickUploadButton() {
        scrollToElement(uploadButton);
        safeClick(uploadButton);
    }
    
    /**
     * Check if Upload button is visible
     */
    public boolean isUploadButtonVisible() {
        return isElementDisplayed(uploadButton);
    }
    
    /**
     * Verify if redirected to upload document page
     */
    public boolean isOnUploadDocumentPage() {
        return getCurrentUrl().contains("upload") || 
               getPageTitle().toLowerCase().contains("upload");
    }
    
    /**
     * Check if document upload interface is visible
     */
    public boolean isDocumentUploadInterfaceVisible() {
        return isElementDisplayed(documentUploadInterface);
    }
    
    /**
     * Check if no ongoing services message is displayed
     */
    public boolean isNoServicesMessageDisplayed() {
        return isElementDisplayed(noServicesMessage);
    }
    
    /**
     * Navigate back to previous page
     */
    public void navigateBack() {
        driver.navigate().back();
    }
    
    /**
     * Wait for page to load completely
     */
    public void waitForPageLoad() {
        waitForElementToBeVisible(headerSection);
    }
    
    /**
     * Get current page title
     */
    public String getCurrentPageTitle() {
        return getPageTitle();
    }
    
    /**
     * Search for scenario
     */
    public void searchScenario(String scenario) {
        safeType(scenarioSearchInput, scenario);
    }
    
    /**
     * Click notification button
     */
    public void clickNotificationButton() {
        safeClick(notificationButton);
    }
    
    /**
     * Click report issue button
     */
    public void clickReportIssueButton() {
        safeClick(reportIssueButton);
    }
}