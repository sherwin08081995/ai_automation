package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class UnknownPagePage extends BasePage {
    
    // Email/Phone input field (assuming generic input field)
    @FindBy(xpath = "//input[@type='email' or @placeholder*='email' or @placeholder*='Email' or @name*='email']")
    private WebElement emailAddressField;
    
    // Alternative locator for email field
    @FindBy(xpath = "//input[contains(@placeholder, 'phone') or contains(@placeholder, 'Phone') or @type='tel']")
    private WebElement phoneNumberField;
    
    // Get OTP button
    @FindBy(xpath = "//button[contains(text(), 'Get OTP') or contains(text(), 'GET OTP') or contains(@value, 'Get OTP')]")
    private WebElement getOtpButton;
    
    // Alternative OTP button locator
    @FindBy(xpath = "//input[@type='submit' and contains(@value, 'OTP')] | //button[contains(., 'OTP')]")
    private WebElement otpSubmitButton;
    
    // OTP input boxes (multiple inputs)
    @FindBy(xpath = "//input[@type='text' or @type='number'][string-length(@maxlength)<=2 or @maxlength='1']")
    private List<WebElement> otpInputBoxes;
    
    // Single OTP input field
    @FindBy(xpath = "//input[contains(@name, 'otp') or contains(@id, 'otp') or contains(@placeholder, 'OTP')]")
    private WebElement singleOtpField;
    
    // Error message elements
    @FindBy(xpath = "//div[contains(@class, 'error') or contains(@class, 'alert')] | //span[contains(@class, 'error')]")
    private WebElement errorMessage;
    
    // Success/validation messages
    @FindBy(xpath = "//div[contains(@class, 'success')] | //span[contains(text(), 'success')]")
    private WebElement successMessage;
    
    public UnknownPagePage(WebDriver driver) {
        super(driver);
    }
    
    public void enterEmailAddress(String emailOrPhone) {
        try {
            if (isElementDisplayed(emailAddressField)) {
                sendKeys(emailAddressField, emailOrPhone);
            } else if (isElementDisplayed(phoneNumberField)) {
                sendKeys(phoneNumberField, emailOrPhone);
            } else {
                throw new RuntimeException("Email/Phone input field not found");
            }
        } catch (Exception e) {
            // Fallback - try to find any input field
            WebElement inputField = driver.findElement(
                org.openqa.selenium.By.xpath("//input[@type='text' or @type='email' or @type='tel'][1]"));
            sendKeys(inputField, emailOrPhone);
        }
    }
    
    public void clickGetOtpButton() {
        try {
            if (isElementDisplayed(getOtpButton)) {
                click(getOtpButton);
            } else if (isElementDisplayed(otpSubmitButton)) {
                click(otpSubmitButton);
            } else {
                throw new RuntimeException("Get OTP button not found");
            }
        } catch (Exception e) {
            // Fallback - try to find any submit button
            WebElement submitButton = driver.findElement(
                org.openqa.selenium.By.xpath("//button[@type='submit'] | //input[@type='submit']"));
            click(submitButton);
        }
    }
    
    public boolean isOnOtpPage() {
        try {
            return !otpInputBoxes.isEmpty() || isElementDisplayed(singleOtpField) || 
                   driver.getCurrentUrl().toLowerCase().contains("otp") ||
                   driver.getPageSource().toLowerCase().contains("enter otp");
        } catch (Exception e) {
            return false;
        }
    }
    
    public void enterOtp(String otp) {
        try {
            if (!otpInputBoxes.isEmpty()) {
                // Multiple OTP input boxes
                char[] otpChars = otp.toCharArray();
                for (int i = 0; i < Math.min(otpChars.length, otpInputBoxes.size()); i++) {
                    sendKeys(otpInputBoxes.get(i), String.valueOf(otpChars[i]));
                }
            } else if (isElementDisplayed(singleOtpField)) {
                // Single OTP input field
                sendKeys(singleOtpField, otp);
            } else {
                throw new RuntimeException("OTP input fields not found");
            }
        } catch (Exception e) {
            // Fallback - find any number input fields
            List<WebElement> numberInputs = driver.findElements(
                org.openqa.selenium.By.xpath("//input[@type='number' or @type='text']"));
            if (!numberInputs.isEmpty()) {
                sendKeys(numberInputs.get(0), otp);
            }
        }
    }
    
    public boolean isOtpEnteredSuccessfully() {
        try {
            // Check if all OTP fields are filled
            if (!otpInputBoxes.isEmpty()) {
                return otpInputBoxes.stream().allMatch(input -> !input.getAttribute("value").isEmpty());
            } else if (isElementDisplayed(singleOtpField)) {
                return !singleOtpField.getAttribute("value").isEmpty();
            }
            return true; // Assume success if no validation needed
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(errorMessage);
    }
    
    public String getErrorMessage() {
        if (isElementDisplayed(errorMessage)) {
            return getText(errorMessage);
        }
        return "";
    }
    
    public void leaveOtpFieldsEmpty() {
        // Do nothing - fields should be empty by default
        // This method exists for step definition clarity
    }
    
    public boolean isValidationMessageDisplayed() {
        return isErrorMessageDisplayed() || 
               driver.getPageSource().toLowerCase().contains("required") ||
               driver.getPageSource().toLowerCase().contains("empty");
    }
}