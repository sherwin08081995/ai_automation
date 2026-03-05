package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ServiceHubPage extends BasePage {
    
    // VERIFY: Service Hub navigation elements - no locator map available
    @FindBy(xpath = "//div[contains(@class, 'sidebar')]//a[normalize-space()='Service Hub']")
    private WebElement serviceHubTab; // VERIFY
    
    @FindBy(xpath = "//div[contains(@class, 'service-hub')]")
    private WebElement serviceHubContainer; // VERIFY
    
    @FindBy(xpath = "//a[normalize-space()='Licenses/Registration'] | //tab[normalize-space()='Licenses/Registration']")
    private WebElement licensesRegistrationTab; // VERIFY
    
    @FindBy(xpath = "//div[contains(@class, 'licenses')] | //div[contains(@class, 'registration')]")
    private WebElement licensesRegistrationContainer; // VERIFY
    
    @FindBy(xpath = "//a[normalize-space()='GST Registration'] | //button[normalize-space()='GST Registration'] | //div[normalize-space()='GST Registration']")
    private WebElement gstRegistrationOption; // VERIFY
    
    @FindBy(xpath = "//div[contains(@class, 'gst')] | //panel[contains(@class, 'gst')] | //form[contains(@class, 'gst')]")
    private WebElement gstRegistrationPanel; // VERIFY
    
    @FindBy(xpath = "//form[contains(@class, 'gst-registration')] | //div[contains(@class, 'gst-form')]")
    private WebElement gstRegistrationForm; // VERIFY
    
    public ServiceHubPage(WebDriver driver) {
        super(driver);
    }
    
    public void clickServiceHubTab() {
        clickElement(serviceHubTab);
    }
    
    public boolean isServiceHubPageLoaded() {
        return isElementDisplayed(serviceHubContainer);
    }
    
    public void clickLicensesRegistrationTab() {
        clickElement(licensesRegistrationTab);
    }
    
    public boolean isLicensesRegistrationPageLoaded() {
        return isElementDisplayed(licensesRegistrationContainer);
    }
    
    public void clickGstRegistrationOption() {
        clickElement(gstRegistrationOption);
    }
    
    public boolean isGstRegistrationPanelOpen() {
        return isElementDisplayed(gstRegistrationPanel);
    }
    
    public boolean isGstRegistrationFormDisplayed() {
        return isElementDisplayed(gstRegistrationForm);
    }
    
    public boolean isServiceHubAccessible() {
        return isElementDisplayed(serviceHubTab);
    }
    
    public boolean isLicensesRegistrationTabVisible() {
        return isElementDisplayed(licensesRegistrationTab);
    }
    
    public boolean isGstRegistrationOptionAvailable() {
        return isElementDisplayed(gstRegistrationOption);
    }
}