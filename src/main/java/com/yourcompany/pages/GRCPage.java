package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GRCPage extends BasePage {
    
    // Login form elements
    @FindBy(id = "login-id")
    private WebElement emailAddressField;
    
    @FindBy(css = "button[type='submit']")
    private WebElement getOtpButton;
    
    @FindBy(css = "button:contains('Login with Password')")
    private WebElement loginWithPasswordButton;
    
    @FindBy(css = "a[href='/grc/auth/signup']")
    private WebElement signUpLink;
    
    // Validation elements
    @FindBy(css = "span.text-red-500")
    private WebElement validationMessage;
    
    @FindBy(css = "label[for='login-id']")
    private WebElement emailFieldLabel;
    
    // Page title and branding
    @FindBy(css = "h1")
    private WebElement pageTitle;
    
    @FindBy(css = "img[alt='zolvitLogo']")
    private WebElement zolvitLogo;
    
    public GRCPage(WebDriver driver) {
        super(driver);
    }
    
    public void navigateToGRCLoginPage() {
        driver.get("https://grc.vakilsearch.com/grc/auth/signin");
    }
    
    public void enterEmailAddress(String emailOrMobile) {
        sendKeys(emailAddressField, emailOrMobile);
    }
    
    public void clickGetOtpButton() {
        clickElement(getOtpButton);
    }
    
    public void clickLoginWithPasswordButton() {
        scrollToElement(loginWithPasswordButton);
        clickElement(loginWithPasswordButton);
    }
    
    public void clickSignUpLink() {
        clickElement(signUpLink);
    }
    
    public String getValidationMessage() {
        waitForElementToBeVisible(validationMessage);
        return getText(validationMessage);
    }
    
    public boolean isValidationMessageDisplayed() {
        return isElementDisplayed(validationMessage);
    }
    
    public String getPageTitle() {
        return super.getPageTitle();
    }
    
    public boolean isEmailFieldDisplayed() {
        return isElementDisplayed(emailAddressField);
    }
    
    public boolean isGetOtpButtonDisplayed() {
        return isElementDisplayed(getOtpButton);
    }
    
    public boolean isLoginWithPasswordButtonDisplayed() {
        return isElementDisplayed(loginWithPasswordButton);
    }
    
    public boolean isSignUpLinkDisplayed() {
        return isElementDisplayed(signUpLink);
    }
    
    public String getEmailFieldPlaceholder() {
        return emailAddressField.getAttribute("placeholder");
    }
    
    public String getEmailFieldValue() {
        return emailAddressField.getAttribute("value");
    }
    
    public boolean isZolvitLogoDisplayed() {
        return isElementDisplayed(zolvitLogo);
    }
    
    public String getCurrentPageUrl() {
        return getCurrentUrl();
    }
    
    public void waitForPageToLoad() {
        waitForElementToBeVisible(pageTitle);
        waitForElementToBeVisible(emailAddressField);
    }
}