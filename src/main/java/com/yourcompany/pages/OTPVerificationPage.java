package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class OTPVerificationPage extends BasePage {
    
    @FindBy(css = "input[type='text'][maxlength='1']:nth-child(1)")
    private WebElement firstOTPBox;
    
    @FindBy(css = "input[type='text'][maxlength='1']:nth-child(2)")
    private WebElement secondOTPBox;
    
    @FindBy(css = "input[type='text'][maxlength='1']:nth-child(3)")
    private WebElement thirdOTPBox;
    
    @FindBy(css = "input[type='text'][maxlength='1']:nth-child(4)")
    private WebElement fourthOTPBox;
    
    @FindBy(css = "input[type='text'][maxlength='1']:nth-child(5)")
    private WebElement fifthOTPBox;
    
    @FindBy(css = "input[type='text'][maxlength='1']:nth-child(6)")
    private WebElement sixthOTPBox;
    
    @FindBy(css = "input[type='text'][maxlength='1']")
    private List<WebElement> allOTPBoxes;
    
    @FindBy(css = "button[type='submit']")
    private WebElement verifyOTPButton;
    
    @FindBy(css = "a[href*='resend'], button:contains('Resend')")
    private WebElement resendOTPLink;
    
    @FindBy(css = ".error-message, .validation-error")
    private WebElement validationMessage;
    
    @FindBy(css = ".otp-instruction, .instruction-text")
    private WebElement instructionMessage;
    
    public OTPVerificationPage(WebDriver driver) {
        super(driver);
    }
    
    public void navigateToOTPPage() {
        driver.get("https://grc.vakilsearch.com/grc/auth/verify-otp");
    }
    
    public void enterFirstOTPDigit(String digit) {
        sendKeys(firstOTPBox, digit);
    }
    
    public void enterSecondOTPDigit(String digit) {
        sendKeys(secondOTPBox, digit);
    }
    
    public void enterThirdOTPDigit(String digit) {
        sendKeys(thirdOTPBox, digit);
    }
    
    public void enterFourthOTPDigit(String digit) {
        sendKeys(fourthOTPBox, digit);
    }
    
    public void enterFifthOTPDigit(String digit) {
        sendKeys(fifthOTPBox, digit);
    }
    
    public void enterSixthOTPDigit(String digit) {
        sendKeys(sixthOTPBox, digit);
    }
    
    public void enterCompleteOTP(String otp) {
        String[] digits = otp.split("");
        for (int i = 0; i < Math.min(digits.length, allOTPBoxes.size()); i++) {
            sendKeys(allOTPBoxes.get(i), digits[i]);
        }
    }
    
    public void clickVerifyOTPButton() {
        click(verifyOTPButton);
    }
    
    public void clickResendOTPLink() {
        click(resendOTPLink);
    }
    
    public String getFirstOTPBoxValue() {
        waitForElementToBeVisible(firstOTPBox);
        return firstOTPBox.getAttribute("value");
    }
    
    public String getSecondOTPBoxValue() {
        waitForElementToBeVisible(secondOTPBox);
        return secondOTPBox.getAttribute("value");
    }
    
    public String getThirdOTPBoxValue() {
        waitForElementToBeVisible(thirdOTPBox);
        return thirdOTPBox.getAttribute("value");
    }
    
    public String getFourthOTPBoxValue() {
        waitForElementToBeVisible(fourthOTPBox);
        return fourthOTPBox.getAttribute("value");
    }
    
    public String getFifthOTPBoxValue() {
        waitForElementToBeVisible(fifthOTPBox);
        return fifthOTPBox.getAttribute("value");
    }
    
    public String getSixthOTPBoxValue() {
        waitForElementToBeVisible(sixthOTPBox);
        return sixthOTPBox.getAttribute("value");
    }
    
    public boolean isVerifyButtonEnabled() {
        waitForElementToBeVisible(verifyOTPButton);
        return verifyOTPButton.isEnabled();
    }
    
    public boolean areAllOTPBoxesVisible() {
        return allOTPBoxes.stream().allMatch(this::isElementDisplayed);
    }
    
    public boolean isVerifyOTPButtonVisible() {
        return isElementDisplayed(verifyOTPButton);
    }
    
    public boolean isResendOTPLinkVisible() {
        return isElementDisplayed(resendOTPLink);
    }
    
    public boolean isInstructionMessageVisible() {
        return isElementDisplayed(instructionMessage);
    }
    
    public String getValidationMessage() {
        waitForElementToBeVisible(validationMessage);
        return getText(validationMessage);
    }
    
    public String getInstructionMessage() {
        waitForElementToBeVisible(instructionMessage);
        return getText(instructionMessage);
    }
    
    public void clearAllOTPBoxes() {
        allOTPBoxes.forEach(box -> {
            waitForElementToBeVisible(box);
            box.clear();
        });
    }
}