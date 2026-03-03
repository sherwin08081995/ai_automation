package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class GrcLoginPage extends BasePage {

    // Login page elements
    @FindBy(xpath="//input[@type='text' or @type='email']")
    private WebElement emailAddressField;
    
    @FindBy(xpath="//button[contains(text(), 'Get OTP') or contains(text(), 'GET OTP')]")
    private WebElement getOtpButton;
    
    // OTP page elements
    @FindBy(xpath="//input[@type='text' or @type='number'][1]")
    private WebElement otpField1;
    
    @FindBy(xpath="//input[@type='text' or @type='number'][2]")
    private WebElement otpField2;
    
    @FindBy(xpath="//input[@type='text' or @type='number'][3]")
    private WebElement otpField3;
    
    @FindBy(xpath="//input[@type='text' or @type='number'][4]")
    private WebElement otpField4;
    
    @FindBy(xpath="//input[@type='text' or @type='number'][5]")
    private WebElement otpField5;
    
    @FindBy(xpath="//input[@type='text' or @type='number'][6]")
    private WebElement otpField6;
    
    // Popup and email selection elements
    @FindBy(xpath="//div[contains(@class, 'popup') or contains(@class, 'modal')]")
    private WebElement emailSelectionPopup;
    
    @FindBy(xpath="//div[contains(text(), 'sherwinzolvit360@yopmail.com') or text()='sherwinzolvit360@yopmail.com']")
    private WebElement targetEmailOption;
    
    // Home page elements
    @FindBy(xpath="//h1[contains(text(), 'Home') or contains(text(), 'Dashboard') or contains(text(), 'Welcome')]")
    private WebElement homePageHeader;
    
    // Error messages
    @FindBy(xpath="//div[contains(@class, 'error') or contains(@class, 'alert')]//span")
    private WebElement errorMessage;
    
    public GrcLoginPage(WebDriver driver) {
        super(driver);
    }
    
    public void navigateToLoginPage(String url) {
        driver.get(url);
        waitForPageToLoad();
    }
    
    public void enterEmailAddress(String email) {
        enterText(emailAddressField, email);
    }
    
    public void clickGetOtpButton() {
        clickElement(getOtpButton);
        waitForPageToLoad();
    }
    
    public boolean isOtpPageDisplayed() {
        return isElementDisplayed(otpField1);
    }
    
    public void enterOtp(String otp) {
        if (otp.length() == 6) {
            enterText(otpField1, String.valueOf(otp.charAt(0)));
            enterText(otpField2, String.valueOf(otp.charAt(1)));
            enterText(otpField3, String.valueOf(otp.charAt(2)));
            enterText(otpField4, String.valueOf(otp.charAt(3)));
            enterText(otpField5, String.valueOf(otp.charAt(4)));
            enterText(otpField6, String.valueOf(otp.charAt(5)));
        }
        waitForPageToLoad();
    }
    
    public boolean isEmailSelectionPopupDisplayed() {
        return isElementDisplayed(emailSelectionPopup);
    }
    
    public void selectEmailFromPopup(String email) {
        waitForElementToBeVisible(emailSelectionPopup);
        clickElement(targetEmailOption);
        waitForPageToLoad();
    }
    
    public boolean isHomePageDisplayed() {
        return isElementDisplayed(homePageHeader);
    }
    
    public String getHomePageHeaderText() {
        return getElementText(homePageHeader);
    }
    
    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage);
    }
    
    public String getErrorMessage() {
        return getElementText(errorMessage);
    }
    
    public boolean confirmSuccessfulLogin() {
        try {
            String currentUrl = driver.getCurrentUrl();
            boolean isHomePageVisible = isHomePageDisplayed();
            boolean isUrlCorrect = currentUrl.contains("home") || currentUrl.contains("dashboard");
            
            return isHomePageVisible || isUrlCorrect;
        } catch (Exception e) {
            return false;
        }
    }
}