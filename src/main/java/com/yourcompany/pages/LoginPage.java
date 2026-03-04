package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends BasePage {

    @FindBy(css="[data-testid='login-with-password']")
    private WebElement loginWithPasswordButton;

    @FindBy(id="email")
    private WebElement emailField;

    @FindBy(id="password")
    private WebElement passwordField;

    @FindBy(css="[data-testid='login-button']")
    private WebElement loginButton;

    @FindBy(css=".error-message")
    private WebElement errorMessage;

    @FindBy(css=".validation-error")
    private WebElement validationError;

    @FindBy(css="[data-testid='home-page-indicator']")
    private WebElement homePageIndicator;

    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
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

    public boolean isHomePageLoaded() {
        return isElementVisible(homePageIndicator) || 
               getCurrentUrl().contains("/home") || 
               getPageTitle().toLowerCase().contains("home");
    }

    public boolean isErrorMessageVisible() {
        return isElementVisible(errorMessage);
    }

    public boolean isValidationErrorVisible() {
        return isElementVisible(validationError);
    }

    public void navigateToLoginPage() {
        driver.get("https://grc.yourcompany.com/login");
    }
}