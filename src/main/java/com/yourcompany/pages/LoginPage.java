package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {
    
    // VERIFY: These selectors need validation against actual page elements
    @FindBy(xpath = "//*[normalize-space()='Login with Password']")
    private WebElement loginWithPasswordOption; // VERIFY: Check if this is a button, link, or div
    
    @FindBy(css = "input[placeholder*='email' i], input[type='email'], input[name*='email' i]")
    private WebElement emailField; // VERIFY: Confirm actual email input selector
    
    @FindBy(css = "input[type='password'], input[placeholder*='password' i], input[name*='password' i]")
    private WebElement passwordField; // VERIFY: Confirm actual password input selector
    
    @FindBy(xpath = "//*[normalize-space()='Log In' or normalize-space()='Login'][self::button or self::input[@type='submit'] or contains(@class,'button') or contains(@class,'btn')]")
    private WebElement loginButton; // VERIFY: Check actual login button selector
    
    @FindBy(xpath = "//*[normalize-space()='Home' or contains(@class,'home')]")
    private WebElement homeTab; // VERIFY: Confirm home tab selector on target page
    
    @FindBy(css = ".error-message, .alert-danger, [class*='error']")
    private WebElement errorMessage; // VERIFY: Check actual error message container
    
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    public void clickLoginWithPasswordOption() {
        clickElement(loginWithPasswordOption);
    }
    
    public void enterEmail(String email) {
        enterText(emailField, email);
    }
    
    public void enterPassword(String password) {
        enterText(passwordField, password);
    }
    
    public void clickLoginButton() {
        clickElement(loginButton);
    }
    
    public boolean isEmailFieldDisplayed() {
        return isElementDisplayed(emailField);
    }
    
    public boolean isPasswordFieldDisplayed() {
        return isElementDisplayed(passwordField);
    }
    
    public boolean isLoginButtonDisplayed() {
        return isElementDisplayed(loginButton);
    }
    
    public boolean isHomeTabVisible() {
        return isElementDisplayed(homeTab);
    }
    
    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage);
    }
    
    public String getErrorMessage() {
        return getElementText(errorMessage);
    }
    
    public void performLogin(String email, String password) {
        clickLoginWithPasswordOption();
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
    }
}