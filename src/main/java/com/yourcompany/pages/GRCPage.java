package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GRCPage extends BasePage {
    
    @FindBy(id = "login-id")
    private WebElement emailAddressField;
    
    @FindBy(xpath = "//button[@type='submit' and .//p[text()='Get OTP']]")
    private WebElement getOTPButton;
    
    @FindBy(xpath = "//span[contains(@class, 'text-red-500') and contains(text(), 'Email or Mobile number is required')]")
    private WebElement emailRequiredError;
    
    @FindBy(xpath = "//button[.//p[text()='Login with Password']]")
    private WebElement loginWithPasswordLink;
    
    @FindBy(xpath = "//a[text()='Sign Up']")
    private WebElement signUpLink;
    
    @FindBy(xpath = "//label[@for='login-id']")
    private WebElement emailFieldLabel;
    
    @FindBy(xpath = "//h1[text()='Log into your account']")
    private WebElement loginPageTitle;
    
    public GRCPage(WebDriver driver) {
        super(driver);
    }
    
    public void navigateToLoginPage() {
        driver.get("https://grc.vakilsearch.com/grc/auth/signin");
    }
    
    public void enterEmailAddress(String email) {
        enterText(emailAddressField, email);
    }
    
    public void clickGetOTPButton() {
        clickElement(getOTPButton);
    }
    
    public void clickLoginWithPasswordLink() {
        clickElement(loginWithPasswordLink);
    }
    
    public boolean isEmailAddressFieldDisplayed() {
        return isElementDisplayed(emailAddressField);
    }
    
    public boolean isGetOTPButtonDisplayed() {
        return isElementDisplayed(getOTPButton);
    }
    
    public boolean isLoginWithPasswordLinkDisplayed() {
        return isElementDisplayed(loginWithPasswordLink);
    }
    
    public boolean isSignUpLinkDisplayed() {
        return isElementDisplayed(signUpLink);
    }
    
    public String getEmailRequiredErrorText() {
        waitForElementToBeVisible(emailRequiredError);
        return getElementText(emailRequiredError);
    }
    
    public boolean isEmailRequiredErrorDisplayed() {
        return isElementDisplayed(emailRequiredError);
    }
    
    public String getEmailFieldValue() {
        return emailAddressField.getAttribute("value");
    }
    
    public String getLoginPageTitle() {
        return getElementText(loginPageTitle);
    }
    
    public boolean isLoginPageLoaded() {
        return isElementDisplayed(loginPageTitle) && 
               getLoginPageTitle().equals("Log into your account");
    }
    
    public void clearEmailField() {
        emailAddressField.clear();
    }
    
    public boolean hasEmailFieldValidationError() {
        String classAttribute = emailAddressField.getAttribute("class");
        return classAttribute.contains("text-red-500") || classAttribute.contains("border-red-500");
    }
}