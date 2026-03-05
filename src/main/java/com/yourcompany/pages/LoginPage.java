package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class LoginPage extends BasePage {

    // VERIFY - These locators need to be updated based on actual page elements
    @FindBy(xpath = "//*[normalize-space()='Licenses/Registrations']")
    private WebElement licensesRegistrationsTab;

    // VERIFY - GST Registration tab locator
    @FindBy(xpath = "//*[normalize-space()='GST Registration']")
    private WebElement gstRegistrationTab;

    // VERIFY - Side panel container
    @FindBy(css = ".side-panel, .panel, [class*='panel'], [class*='sidebar']")
    private WebElement sidePanel;

    // VERIFY - Avail Service button
    @FindBy(xpath = "//*[normalize-space()='Avail Service' or normalize-space()='Avail service']")
    private WebElement availServiceButton;

    // VERIFY - Toast message container
    @FindBy(css = ".toast, .notification, [class*='toast'], [class*='notification'], [class*='success']")
    private WebElement toastMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToGRCServiceHub() {
        // VERIFY - Update URL based on actual GRC Service Hub URL
        driver.get("https://grc-service-hub.example.com");
    }

    public void clickLicensesRegistrationsTab() {
        waitForElementToBeClickable(licensesRegistrationsTab);
        clickElement(licensesRegistrationsTab);
    }

    public void clickGSTRegistrationTab() {
        waitForElementToBeClickable(gstRegistrationTab);
        clickElement(gstRegistrationTab);
    }

    public void waitForSidePanelToOpen() {
        wait.until(ExpectedConditions.visibilityOf(sidePanel));
        waitForSeconds(2); // Additional wait for panel animation
    }

    public void clickAvailServiceButton() {
        waitForElementToBeClickable(availServiceButton);
        scrollToElement(availServiceButton);
        clickElement(availServiceButton);
    }

    public boolean isToastMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(toastMessage));
            return isElementDisplayed(toastMessage);
        } catch (Exception e) {
            return false;
        }
    }

    public String getToastMessageText() {
        if (isToastMessageDisplayed()) {
            return getElementText(toastMessage);
        }
        return "";
    }

    public boolean isSuccessToastDisplayed() {
        if (isToastMessageDisplayed()) {
            String toastText = getToastMessageText().toLowerCase();
            return toastText.contains("success") || 
                   toastText.contains("successful") || 
                   toastText.contains("completed") ||
                   toastText.contains("done");
        }
        return false;
    }
}