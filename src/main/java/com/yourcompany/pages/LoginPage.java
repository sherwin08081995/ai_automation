package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    // VERIFY - No locator map provided, using assumed selectors
    @FindBy(xpath = "//div[normalize-space()='Service Hub']")
    private WebElement serviceHubTab;

    // VERIFY - No locator map provided, using assumed selectors
    @FindBy(xpath = "//div[normalize-space()='Licenses/Registrations']")
    private WebElement licensesRegistrationsOption;

    // VERIFY - No locator map provided, using assumed selectors
    @FindBy(xpath = "//div[normalize-space()='GST Registration']")
    private WebElement gstRegistrationOption;

    // VERIFY - No locator map provided, using assumed selectors
    @FindBy(xpath = "//div[@class='side-panel']")
    private WebElement gstRegistrationSidePanel;

    // VERIFY - No locator map provided, using assumed selectors
    @FindBy(xpath = "//button[normalize-space()='Avail service']")
    private WebElement availServiceButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void clickServiceHubTab() {
        clickElement(serviceHubTab);
    }

    public void clickLicensesRegistrationsOption() {
        clickElement(licensesRegistrationsOption);
    }

    public void clickGstRegistration() {
        clickElement(gstRegistrationOption);
    }

    public boolean isGstRegistrationSidePanelDisplayed() {
        return isElementDisplayed(gstRegistrationSidePanel);
    }

    public boolean isAvailServiceButtonDisplayed() {
        return isElementDisplayed(availServiceButton);
    }

    public String getAvailServiceButtonText() {
        return getElementText(availServiceButton);
    }

    public void waitForPageLoad() {
        try {
            Thread.sleep(2000); // Wait for page to load - replace with proper wait condition
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}