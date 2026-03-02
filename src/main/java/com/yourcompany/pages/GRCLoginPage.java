package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GRCLoginPage extends BasePage {
    
    @FindBy(id = "login-id")
    private WebElement emailAddressField;
    
    @FindBy(css = "button[type='submit']")
    private WebElement getOTPButton;
    
    @FindBy(css = "button[type='button'] span p")
    private WebElement loginWithPasswordLink;
    
    @FindBy(css = "h1")
    private WebElement pageTitle;
    
    @FindBy(css = "span.text-red-500")
    private WebElement errorMessage;
    
    @FindBy(css = "label[for='login-id']")
    private WebElement emailFieldLabel;
    
    public GRCLoginPage(WebDriver driver) {
        super(driver);
    }
    
    public void navigateToLoginPage() {
        driver.get("https://grc.vakilsearch.com/grc/auth/signin");
    }
    
    public void enterEmailAddress(String emailOrMobile) {
        sendKeys(emailAddressField, emailOrMobile);
    }
    
    public void clickGetOTPButton() {
        click(getOTPButton);
    }
    
    public void clickLoginWithPasswordLink() {
        click(loginWithPasswordLink);
    }
    
    public String getEmailFieldValue() {
        waitForElementToBeVisible(emailAddressField);
        return emailAddressField.getAttribute("value");
    }
    
    public String getErrorMessage() {
        waitForElementToBeVisible(errorMessage);
        return getText(errorMessage);
    }
    
    public String getPageTitle() {
        waitForElementToBeVisible(pageTitle);
        return getText(pageTitle);
    }
    
    public boolean isEmailFieldVisible() {
        return isElementDisplayed(emailAddressField);
    }
    
    public boolean isGetOTPButtonVisible() {
        return isElementDisplayed(getOTPButton);
    }
    
    public boolean isLoginWithPasswordLinkVisible() {
        return isElementDisplayed(loginWithPasswordLink);
    }
    
    public boolean isGetOTPButtonClickable() {
        try {
            waitForElementToBeClickable(getOTPButton);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean hasEmailFieldValidationStyling() {
        waitForElementToBeVisible(emailAddressField);
        String classAttribute = emailAddressField.getAttribute("class");
        return classAttribute.contains("text-red-500") || classAttribute.contains("border-red-500");
    }
    
    public void clearEmailField() {
        waitForElementToBeVisible(emailAddressField);
        emailAddressField.clear();
    }
}