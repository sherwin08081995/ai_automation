package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ServiceHubPage extends BasePage {
    
    // Service Hub Tab - VERIFY ONLY (no locator map or HTML provided)
    @FindBy(xpath = "//span[normalize-space()='Service Hub']")
    private WebElement serviceHubTab; // VERIFY ONLY
    
    // Licenses/Registrations Tab - VERIFY ONLY
    @FindBy(xpath = "//li[normalize-space()='Licenses/Registrations']")
    private WebElement licensesRegistrationsTab; // VERIFY ONLY
    
    // GST Registration Option - VERIFY ONLY
    @FindBy(xpath = "//p[normalize-space()='GST Registration']")
    private WebElement gstRegistrationOption; // VERIFY ONLY
    
    // GST Registration Panel Title (to verify panel opened) - VERIFY ONLY
    @FindBy(xpath = "//h2[normalize-space()='GST Registration']")
    private WebElement gstPanelTitle; // VERIFY ONLY
    
    // Alternative GST Panel indicator - VERIFY ONLY
    @FindBy(xpath = "//div[contains(text(), 'GST Registration') and contains(@class, 'panel')]")
    private WebElement gstPanelContainer; // VERIFY ONLY
    
    // Error message element - VERIFY ONLY
    @FindBy(xpath = "//div[contains(@class, 'error-message')]")
    private WebElement errorMessage; // VERIFY ONLY
    
    public ServiceHubPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }
    
    public void clickServiceHubTab() {
        clickElement(serviceHubTab);
    }
    
    public void waitForServiceHubPageToLoad() {
        waitForPageToLoad();
        waitForElementToBeVisible(licensesRegistrationsTab);
    }
    
    public void waitForServiceHubPageToLoadWithTime(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
            waitForElementToBeVisible(licensesRegistrationsTab);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void clickLicensesRegistrationsTab() {
        clickElement(licensesRegistrationsTab);
    }
    
    public void clickGstRegistration() {
        clickElement(gstRegistrationOption);
    }
    
    public boolean isGstSidePanelOpened() {
        try {
            return isElementDisplayed(gstPanelTitle) || isElementDisplayed(gstPanelContainer);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isGstSidePanelOpenedWithinTime(int seconds) {
        for (int i = 0; i < seconds; i++) {
            if (isGstSidePanelOpened()) {
                return true;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
    
    public String getGstPanelTitle() {
        if (isElementDisplayed(gstPanelTitle)) {
            return getElementText(gstPanelTitle);
        }
        return "";
    }
    
    public void performCompleteNavigationFlow() {
        clickServiceHubTab();
        waitForServiceHubPageToLoad();
        clickLicensesRegistrationsTab();
        clickGstRegistration();
    }
    
    public boolean isNavigationSuccessful() {
        return isGstSidePanelOpened();
    }
    
    public boolean isServiceHubTabVisible() {
        return isElementDisplayed(serviceHubTab);
    }
    
    public boolean isLicensesRegistrationsTabVisible() {
        return isElementDisplayed(licensesRegistrationsTab);
    }
    
    public boolean isGstRegistrationOptionVisible() {
        return isElementDisplayed(gstRegistrationOption);
    }
    
    public boolean isServiceHubPageLoadFailed() {
        try {
            Thread.sleep(5000);
            return !isElementDisplayed(licensesRegistrationsTab);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return true;
        }
    }
    
    public String getErrorMessage() {
        if (isElementDisplayed(errorMessage)) {
            return getElementText(errorMessage);
        }
        return "";
    }
    
    public boolean hasErrorMessage() {
        return isElementDisplayed(errorMessage);
    }
}