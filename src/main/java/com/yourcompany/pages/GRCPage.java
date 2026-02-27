package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GRCPage extends BasePage {
    
    @FindBy(id = "login-id")
    private WebElement emailAddressField;
    
    @FindBy(xpath = "//button[.//p[text()='Get OTP']]")
    private WebElement getOTPButton;
    
    @FindBy(xpath = "//span[text()='Email or Mobile number is required']")
    private WebElement errorMessage;
    
    @FindBy(xpath = "//label[@for='login-id']")
    private WebElement emailFieldLabel;
    
    @FindBy(xpath = "//a[text()='Sign Up']")
    private WebElement signUpLink;
    
    @FindBy(xpath = "//button[.//p[text()='Login with Password']]")
    private WebElement loginWithPasswordButton;
    
    public GRCPage(WebDriver driver) {
        super(driver);
    }
    
    public void navigateToGRCLoginPage() {
        driver.get("https://grc.vakilsearch.com/grc/auth/signin");
    }
    
    public void enterEmailAddress(String emailOrMobile) {
        enterText(emailAddressField, emailOrMobile);
    }
    
    public void clickGetOTPButton() {
        clickElement(getOTPButton);
    }
    
    public boolean isGetOTPButtonClickable() {
        return isElementEnabled(getOTPButton);
    }
    
    public String getEmailAddressFieldValue() {
        waitForElementToBeVisible(emailAddressField);
        return emailAddressField.getAttribute("value");
    }
    
    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage);
    }
    
    public String getErrorMessageText() {
        return getElementText(errorMessage);
    }
    
    public boolean isEmailFieldHighlightedWithRedBorder() {
        waitForElementToBeVisible(emailAddressField);
        String borderColor = emailAddressField.getCssValue("border-color");
        String textColor = emailAddressField.getCssValue("color");
        return borderColor.contains("rgb(239, 68, 68)") || textColor.contains("rgb(239, 68, 68)");
    }
    
    public void clickSignUpLink() {
        clickElement(signUpLink);
    }
    
    public void clickLoginWithPasswordButton() {
        clickElement(loginWithPasswordButton);
    }
    
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    public boolean isValidationErrorDisplayed() {
        return isErrorMessageDisplayed();
    }
}