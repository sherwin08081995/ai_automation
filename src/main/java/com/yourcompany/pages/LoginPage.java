package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {
    
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
    
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    public void clickServiceHubTab() {
        clickElement(serviceHubTab);
    }
    
    public void waitForServiceHubPageToLoad() {
        waitForPageToLoad();
        waitForElementToBeVisible(licensesRegistrationsTab);
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
}