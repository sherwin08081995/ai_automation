package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class GrcLoginPage extends BasePage {
    
    // Using generic locators since no HTML was provided
    @FindBy(xpath = "//input[contains(@placeholder,'email') or contains(@placeholder,'Email') or contains(@name,'email')]") 
    private WebElement emailAddressField;
    
    @FindBy(xpath = "//button[contains(text(),'Get OTP') or contains(text(),'GET OTP')]") 
    private WebElement getOtpButton;
    
    @FindBy(xpath = "//input[@type='tel' or contains(@placeholder,'OTP') or contains(@name,'otp')][1]")
    private WebElement otpInput1;
    
    @FindBy(xpath = "//input[@type='tel' or contains(@placeholder,'OTP') or contains(@name,'otp')][2]")
    private WebElement otpInput2;
    
    @FindBy(xpath = "//input[@type='tel' or contains(@placeholder,'OTP') or contains(@name,'otp')][3]")
    private WebElement otpInput3;
    
    @FindBy(xpath = "//input[@type='tel' or contains(@placeholder,'OTP') or contains(@name,'otp')][4]")
    private WebElement otpInput4;
    
    @FindBy(xpath = "//input[@type='tel' or contains(@placeholder,'OTP') or contains(@name,'otp')][5]")
    private WebElement otpInput5;
    
    @FindBy(xpath = "//input[@type='tel' or contains(@placeholder,'OTP') or contains(@name,'otp')][6]")
    private WebElement otpInput6;
    
    @FindBy(xpath = "//button[contains(text(),'Submit') or contains(text(),'Verify') or @type='submit']")
    private WebElement submitOtpButton;
    
    @FindBy(xpath = "//div[contains(@class,'error') or contains(text(),'error') or contains(text(),'Error')]")
    private WebElement errorMessage;
    
    @FindBy(xpath = "//div[contains(text(),'OTP') or contains(text(),'Enter OTP')]")
    private WebElement otpPageIndicator;
    
    public GrcLoginPage(WebDriver driver) {
        super(driver);
    }
    
    public void navigateToLoginPage() {
        // Update with actual URL when available
        driver.get("https://grc-login-url.com");
    }
    
    public void enterEmailAddress(String email) {
        enterText(emailAddressField, email);
    }
    
    public void clickGetOtpButton() {
        clickElement(getOtpButton);
    }
    
    public boolean isOnOtpPage() {
        return isElementDisplayed(otpPageIndicator);
    }
    
    public void enterOtpDigits(String otp) {
        WebElement[] otpInputs = {otpInput1, otpInput2, otpInput3, otpInput4, otpInput5, otpInput6};
        char[] otpChars = otp.toCharArray();
        
        for (int i = 0; i < Math.min(otpChars.length, otpInputs.length); i++) {
            enterText(otpInputs[i], String.valueOf(otpChars[i]));
        }
    }
    
    public void submitOtp() {
        clickElement(submitOtpButton);
    }
    
    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage);
    }
    
    public String getErrorMessage() {
        if (isErrorMessageDisplayed()) {
            return getElementText(errorMessage);
        }
        return "";
    }
    
    public boolean isLoggedInSuccessfully() {
        // This would check for dashboard or success page elements
        // Update with actual success indicators when available
        return driver.getCurrentUrl().contains("dashboard") || 
               driver.getTitle().contains("Dashboard");
    }
}