package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    // VERIFY: No locator map provided - these selectors need validation against actual page
    @FindBy(xpath = "//button[normalize-space()='Login with Password']")
    private WebElement loginWithPasswordButton; // VERIFY

    @FindBy(xpath = "//input[@type='email']")
    private WebElement emailField; // VERIFY

    @FindBy(xpath = "//input[@type='password']")
    private WebElement passwordField; // VERIFY

    @FindBy(xpath = "//button[normalize-space()='Log In']")
    private WebElement logInButton; // VERIFY

    @FindBy(xpath = "//div[@class='error-message']")
    private WebElement errorMessage; // VERIFY

    @FindBy(xpath = "//div[contains(@class,'validation-error')]")
    private WebElement validationError; // VERIFY

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void clickLoginWithPassword() {
        clickElement(loginWithPasswordButton);
    }

    public void enterEmailAddress(String email) {
        sendKeysToElement(emailField, email);
    }

    public void enterPassword(String password) {
        sendKeysToElement(passwordField, password);
    }

    public void clickLogInButton() {
        clickElement(logInButton);
    }

    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage);
    }

    public String getErrorMessage() {
        return getElementText(errorMessage);
    }

    public boolean isValidationErrorDisplayed() {
        return isElementDisplayed(validationError);
    }

    public boolean isLoginPageDisplayed() {
        return getCurrentUrl().contains("login") || getPageTitle().toLowerCase().contains("login");
    }

    public void performLogin(String email, String password) {
        clickLoginWithPassword();
        enterEmailAddress(email);
        enterPassword(password);
        clickLogInButton();
    }
}