package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GrcLoginPage extends BasePage {
    
    @FindBy(id = "login-id")
    private WebElement emailAddressField;
    
    @FindBy(xpath = "//button[contains(.//p, 'Get OTP')]")
    private WebElement getOtpButton;
    
    @FindBy(xpath = "//span[contains(text(), 'Email or Mobile number is required')]")
    private WebElement emailRequiredErrorMessage;
    
    @FindBy(xpath = "//button[contains(.//p, 'Login with Password')]")
    private WebElement loginWithPasswordButton;
    
    @FindBy(xpath = "//span[contains(@class, 'text-red-500')]")
    private WebElement validationError;
    
    @FindBy(css = "form")
    private WebElement loginForm;

    public GrcLoginPage(WebDriver driver) {
        super(driver);
    }

    public void navigateToGrcLoginPage() {
        driver.get("https://grc.vakilsearch.com/grc/auth/signin");
    }

    public void enterEmailAddress(String emailOrMobile) {
        waitAndSendKeys(emailAddressField, emailOrMobile);
    }

    public void clickGetOtpButton() {
        waitAndClick(getOtpButton);
    }

    public void clickLoginWithPasswordButton() {
        waitAndClick(loginWithPasswordButton);
    }

    public boolean isEmailRequiredErrorDisplayed() {
        return isElementDisplayed(emailRequiredErrorMessage);
    }

    public String getEmailRequiredErrorText() {
        waitForVisibility(emailRequiredErrorMessage);
        return emailRequiredErrorMessage.getText();
    }

    public boolean isValidationErrorDisplayed() {
        return isElementDisplayed(validationError);
    }

    public boolean isLoginFormDisplayed() {
        return isElementDisplayed(loginForm);
    }

    public boolean isGetOtpButtonEnabled() {
        return getOtpButton.isEnabled();
    }

    public boolean isLoginWithPasswordButtonDisplayed() {
        return isElementDisplayed(loginWithPasswordButton);
    }

    public String getEmailFieldValue() {
        return emailAddressField.getAttribute("value");
    }

    public void clearEmailField() {
        emailAddressField.clear();
    }
}