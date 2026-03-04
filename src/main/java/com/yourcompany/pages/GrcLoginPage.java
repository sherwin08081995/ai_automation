package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GrcLoginPage extends BasePage {
    
    // Page elements using generic locators since no HTML provided
    @FindBy(xpath = "//button[contains(text(),'Login with Password') or contains(text(),'login with password')]")
    private WebElement loginWithPasswordButton;
    
    @FindBy(xpath = "//input[@type='email' or contains(@placeholder,'email') or contains(@name,'email')]")
    private WebElement emailField;
    
    @FindBy(xpath = "//input[@type='password' or contains(@placeholder,'password') or contains(@name,'password')]")
    private WebElement passwordField;
    
    @FindBy(xpath = "//button[contains(text(),'Log In') or contains(text(),'LOGIN') or @type='submit']")
    private WebElement loginButton;
    
    @FindBy(xpath = "//div[contains(@class,'error') or contains(@class,'alert')]")
    private WebElement errorMessage;
    
    @FindBy(xpath = "//span[contains(text(),'required') or contains(text(),'error')]")
    private WebElement validationErrorMessage;
    
    // Constructor
    public GrcLoginPage(WebDriver driver) {
        super(driver);
    }
    
    // Page actions
    public void navigateToLoginPage() {
        driver.get("https://grc-login-url.com"); // Replace with actual URL
    }
    
    public void clickLoginWithPassword() {
        clickElement(loginWithPasswordButton);
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
    
    public void leaveEmailEmpty() {
        waitForElementToBeVisible(emailField);
        emailField.clear();
    }
    
    public void leavePasswordEmpty() {
        waitForElementToBeVisible(passwordField);
        passwordField.clear();
    }
    
    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage);
    }
    
    public boolean isValidationErrorDisplayed() {
        return isElementDisplayed(validationErrorMessage);
    }
    
    public boolean isOnLoginPage() {
        return getCurrentUrl().contains("login") || getPageTitle().toLowerCase().contains("login");
    }
    
    public boolean isHomePageLoaded() {
        return getCurrentUrl().contains("home") || getCurrentUrl().contains("dashboard") || 
               getPageTitle().toLowerCase().contains("home") || getPageTitle().toLowerCase().contains("dashboard");
    }
    
    public boolean isSuccessfullyLoggedIn() {
        // Check if redirected away from login page and on a secure page
        return !getCurrentUrl().contains("login") && 
               (getCurrentUrl().contains("home") || getCurrentUrl().contains("dashboard") || getCurrentUrl().contains("app"));
    }
}