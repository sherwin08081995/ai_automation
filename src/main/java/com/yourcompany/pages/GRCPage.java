package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class GRCPage extends BasePage {

    @FindBy(id = "login-id")
    private WebElement emailAddressField;

    @FindBy(xpath = "//button[.//p[text()='Get OTP']]")
    private WebElement getOtpButton;

    @FindBy(css = "span.text-red-500")
    private WebElement validationErrorMessage;

    @FindBy(css = "label[for='login-id']")
    private WebElement emailFieldLabel;

    @FindBy(xpath = "//p[text()='Log into your account']")
    private WebElement loginPageHeader;

    @FindBy(css = "input.border-red-500")
    private WebElement fieldWithError;

    public GRCPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public boolean isLoginPageDisplayed() {
        return isElementDisplayed(loginPageHeader);
    }

    public void enterEmailAddress(String emailOrMobile) {
        waitForElementToBeVisible(emailAddressField);
        emailAddressField.clear();
        emailAddressField.sendKeys(emailOrMobile);
    }

    public void clickGetOtpButton() {
        waitForElementToBeClickable(getOtpButton);
        getOtpButton.click();
    }

    public boolean isValidationErrorDisplayed() {
        return isElementDisplayed(validationErrorMessage);
    }

    public String getValidationErrorText() {
        waitForElementToBeVisible(validationErrorMessage);
        return validationErrorMessage.getText();
    }

    public boolean isFieldHighlightedWithError() {
        try {
            return fieldWithError.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void leaveEmailFieldEmpty() {
        waitForElementToBeVisible(emailAddressField);
        emailAddressField.clear();
    }

    public String getEmailFieldValue() {
        return emailAddressField.getAttribute("value");
    }

    public boolean isGetOtpButtonEnabled() {
        return getOtpButton.isEnabled();
    }

    public String getCurrentPageUrl() {
        return getCurrentUrl();
    }
}