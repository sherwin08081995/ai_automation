package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class LoginPage extends BasePage {
    
    // VERIFY - No locator map provided, using generic selectors that need verification
    @FindBy(xpath = "//*[contains(text(), 'Licenses') and contains(text(), 'Registrations')]")
    private WebElement licensesRegistrationsTab; // VERIFY
    
    @FindBy(xpath = "//*[contains(text(), 'GST Registration') or contains(text(), 'GST')]") 
    private WebElement gstRegistrationTab; // VERIFY
    
    @FindBy(xpath = "//*[contains(text(), 'Avail Service') or contains(text(), 'Avail') or contains(@class, 'cta')]") 
    private WebElement availServiceButton; // VERIFY
    
    @FindBy(xpath = "//div[contains(@class, 'panel') or contains(@class, 'sidebar') or contains(@class, 'drawer')]")
    private WebElement sidePanel; // VERIFY
    
    @FindBy(xpath = "//div[contains(@class, 'gst') or contains(text(), 'GST')]//parent::div[contains(@class, 'panel')]")
    private WebElement gstSidePanel; // VERIFY
    
    @FindBy(xpath = "//*[contains(@class, 'active') and contains(text(), 'GST')]")
    private WebElement activeGstTab; // VERIFY
    
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    public void clickLicensesRegistrationsTab() {
        waitForElementToBeClickable(licensesRegistrationsTab);
        clickElement(licensesRegistrationsTab);
    }
    
    public void clickGstRegistrationTab() {
        waitForElementToBeClickable(gstRegistrationTab);
        clickElement(gstRegistrationTab);
    }
    
    public void waitForSidePanelToOpen() {
        wait.until(ExpectedConditions.visibilityOf(sidePanel));
        // Additional wait for animation/content loading
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void clickAvailServiceButton() {
        waitForElementToBeClickable(availServiceButton);
        clickElement(availServiceButton);
    }
    
    public boolean isLicensesRegistrationsSectionDisplayed() {
        return isElementDisplayed(licensesRegistrationsTab);
    }
    
    public boolean isGstRegistrationSidePanelDisplayed() {
        return isElementDisplayed(gstSidePanel) || isElementDisplayed(sidePanel);
    }
    
    public boolean isAvailServiceButtonVisible() {
        return isElementDisplayed(availServiceButton);
    }
    
    public boolean isGstRegistrationTabActive() {
        return isElementDisplayed(activeGstTab) || gstRegistrationTab.getAttribute("class").contains("active");
    }
    
    public boolean sidePanelContainsGstInformation() {
        try {
            return sidePanel.getText().toLowerCase().contains("gst") || 
                   sidePanel.getText().toLowerCase().contains("goods and services tax") ||
                   sidePanel.getText().toLowerCase().contains("registration");
        } catch (Exception e) {
            return false;
        }
    }
    
    public void verifyExternalRedirect() {
        // Wait for potential new window/tab or URL change
        wait.withTimeout(Duration.ofSeconds(10));
        String currentUrl = driver.getCurrentUrl();
        
        // Check if we're on a different domain or new window
        if (driver.getWindowHandles().size() > 1) {
            // Switch to new window if opened
            for (String windowHandle : driver.getWindowHandles()) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
    }
    
    public boolean isPageLoadedSuccessfully() {
        try {
            // Basic check for page load
            return !driver.getTitle().isEmpty() && 
                   driver.getCurrentUrl().startsWith("http");
        } catch (Exception e) {
            return false;
        }
    }
}