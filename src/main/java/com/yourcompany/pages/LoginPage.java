package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {
    
    // VERIFY - Service Hub tab locator (no HTML/locator map provided)
    @FindBy(xpath = "//div[normalize-space()='Service Hub']")
    private WebElement serviceHubTab;
    
    // VERIFY - Alternative service hub locator
    @FindBy(css = "[data-testid='service-hub-tab']")
    private WebElement serviceHubTabAlt;
    
    // VERIFY - Licenses/registrations tab locator
    @FindBy(xpath = "//div[normalize-space()='Licenses/registrations']")
    private WebElement licensesRegistrationsTab;
    
    // VERIFY - Alternative licenses tab locator
    @FindBy(css = "[data-testid='licenses-registrations-tab']")
    private WebElement licensesRegistrationsTabAlt;
    
    // VERIFY - GST Registration option locator
    @FindBy(xpath = "//div[normalize-space()='GST Registration']")
    private WebElement gstRegistrationOption;
    
    // VERIFY - Alternative GST registration locator
    @FindBy(css = "[data-searchable='GST Registration']")
    private WebElement gstRegistrationOptionAlt;
    
    // VERIFY - GST side panel container
    @FindBy(css = ".side-panel")
    private WebElement gstSidePanel;
    
    // VERIFY - GST panel title for visibility check
    @FindBy(xpath = "//div[@class='panel-title'][normalize-space()='GST Registration']")
    private WebElement gstPanelTitle;
    
    // VERIFY - Service hub page indicator
    @FindBy(css = ".service-hub-content")
    private WebElement serviceHubContent;
    
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    public void clickServiceHubTab() {
        try {
            clickElement(serviceHubTab);
        } catch (Exception e) {
            // Fallback to alternative locator
            clickElement(serviceHubTabAlt);
        }
    }
    
    public void waitForServiceHubPageToLoad() {
        waitForPageLoad();
        try {
            waitForElementToBeVisible(serviceHubContent);
        } catch (Exception e) {
            // Basic wait if specific content element not found
            waitForPageLoad();
        }
    }
    
    public void clickLicensesRegistrationsTab() {
        try {
            clickElement(licensesRegistrationsTab);
        } catch (Exception e) {
            // Fallback to alternative locator
            clickElement(licensesRegistrationsTabAlt);
        }
    }
    
    public void clickGstRegistration() {
        try {
            clickElement(gstRegistrationOption);
        } catch (Exception e) {
            // Fallback to alternative locator
            clickElement(gstRegistrationOptionAlt);
        }
    }
    
    public boolean isGstSidePanelDisplayed() {
        try {
            return isElementDisplayed(gstPanelTitle);
        } catch (Exception e) {
            // Fallback to general panel check
            return isElementDisplayed(gstSidePanel);
        }
    }
    
    public boolean isServiceHubPageLoaded() {
        try {
            return isElementDisplayed(serviceHubContent);
        } catch (Exception e) {
            return isElementDisplayed(licensesRegistrationsTab);
        }
    }
    
    public boolean isLicensesRegistrationsTabVisible() {
        try {
            return isElementDisplayed(licensesRegistrationsTab);
        } catch (Exception e) {
            return isElementDisplayed(licensesRegistrationsTabAlt);
        }
    }
    
    public boolean hasGstRegistrationOptions() {
        // Check if GST registration content is available
        return isGstSidePanelDisplayed();
    }
}