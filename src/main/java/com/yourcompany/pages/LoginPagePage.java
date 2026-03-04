package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPagePage extends BasePage {
    
    @FindBy(id = "login-with-password")
    private WebElement loginWithPasswordOption;
    
    @FindBy(id = "email")
    private WebElement emailAddressField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "login-button")
    private WebElement loginButton;
    
    @FindBy(css = ".home-page-title")
    private WebElement homePageTitle;
    
    @FindBy(css = ".error-message")
    private WebElement errorMessage;
    
    @FindBy(css = ".email-error")
    private WebElement emailError;
    
    @FindBy(css = ".password-error")
    private WebElement passwordError;
    
    @FindBy(css = ".welcome-message")
    private WebElement welcomeMessage;
    
    public LoginPagePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }
    
    public void navigateToLoginPage() {
        driver.get("https://grc-app.com/login");
    }
    
    public void clickLoginWithPasswordOption() {
        clickElement(loginWithPasswordOption);
    }
    
    public void enterEmailAddress(String email) {
        enterText(emailAddressField, email);
    }
    
    public void enterPassword(String password) {
        enterText(passwordField, password);
    }
    
    public void clickLoginButton() {
        clickElement(loginButton);
    }
    
    public boolean isHomePageLoaded() {
        return isElementDisplayed(homePageTitle) && 
               (getElementText(homePageTitle).contains("Home") || 
                getElementText(homePageTitle).contains("Dashboard"));
    }
    
    public boolean isWelcomeMessageDisplayed() {
        return isElementDisplayed(welcomeMessage);
    }
    
    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage);
    }
    
    public String getErrorMessage() {
        return getElementText(errorMessage);
    }
    
    public boolean isEmailErrorDisplayed() {
        return isElementDisplayed(emailError);
    }
    
    public boolean isPasswordErrorDisplayed() {
        return isElementDisplayed(passwordError);
    }
    
    public void leaveEmailEmpty() {
        waitForElementToBeVisible(emailAddressField);
        emailAddressField.clear();
    }
    
    public void leavePasswordEmpty() {
        waitForElementToBeVisible(passwordField);
        passwordField.clear();
    }
}