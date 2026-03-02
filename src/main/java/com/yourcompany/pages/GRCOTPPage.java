package com.yourcompany.pages;

import com.yourcompany.base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class GRCOTPPage extends BasePage {
    
    @FindBy(css = "input[type='text'][inputmode='numeric'][maxlength='1'][autocomplete='one-time-code']")
    private List<WebElement> otpInputFields;
    
    @FindBy(css = "p.text-\\[1\\.4rem\\].lg\\:text-\\[2rem\\].font-bold.text-\\[\\#231F20\\].no-underline.truncate")
    private WebElement mobileNumberDisplay;
    
    @FindBy(css = "button.text-\\[\\#077AFF\\].text-\\[1\\.4rem\\].lg\\:text-\\[2rem\\].font-bold.no-underline")
    private WebElement editButton;
    
    @FindBy(css = "span.text-\\[\\#007AFF\\].text-\\[1\\.2rem\\].lg\\:text-\\[1\\.6rem\\].font-bold.cursor-pointer.hover\\:underline")
    private WebElement resendLink;
    
    @FindBy(css = "p.text-\\[1\\.4rem\\].lg\\:text-\\[2rem\\].font-bold.text-\\[\\#231F20\\]")
    private WebElement enterOTPLabel;
    
    @FindBy(css = "p.text-\\[1\\.4rem\\].lg\\:text-\\[1\\.6rem\\].font-medium.text-\\[\\#606162\\]")
    private WebElement verificationMessage;
    
    public GRCOTPPage(WebDriver driver) {
        super(driver);
    }
    
    public void navigateToOTPPage() {
        driver.get("https://grc.vakilsearch.com/grc/auth/signin");
    }
    
    public void enterOTPCode(String otpCode) {
        waitForElementToBeVisible(otpInputFields.get(0));
        
        // Clear all fields first
        for (WebElement field : otpInputFields) {
            field.clear();
        }
        
        // Enter each digit in respective field
        for (int i = 0; i < Math.min(otpCode.length(), otpInputFields.size()); i++) {
            char digit = otpCode.charAt(i);
            if (Character.isDigit(digit)) {
                sendKeys(otpInputFields.get(i), String.valueOf(digit));
            }
        }
    }
    
    public void clearOTPFields() {
        for (WebElement field : otpInputFields) {
            waitForElementToBeVisible(field);
            field.clear();
        }
    }
    
    public void clickEditButton() {
        click(editButton);
    }
    
    public void clickResendLink() {
        click(resendLink);
    }
    
    public String getOTPFieldValue(int fieldIndex) {
        if (fieldIndex >= 0 && fieldIndex < otpInputFields.size()) {
            waitForElementToBeVisible(otpInputFields.get(fieldIndex));
            return otpInputFields.get(fieldIndex).getAttribute("value");
        }
        return "";
    }
    
    public String getAllOTPFieldValues() {
        StringBuilder otpValue = new StringBuilder();
        for (WebElement field : otpInputFields) {
            waitForElementToBeVisible(field);
            otpValue.append(field.getAttribute("value"));
        }
        return otpValue.toString();
    }
    
    public String getMobileNumberDisplay() {
        waitForElementToBeVisible(mobileNumberDisplay);
        return getText(mobileNumberDisplay);
    }
    
    public boolean areOTPFieldsVisible() {
        return otpInputFields.size() == 6 && otpInputFields.stream().allMatch(this::isElementDisplayed);
    }
    
    public boolean isEditButtonVisible() {
        return isElementDisplayed(editButton);
    }
    
    public boolean isResendLinkVisible() {
        return isElementDisplayed(resendLink);
    }
    
    public boolean areAllOTPFieldsEmpty() {
        return otpInputFields.stream().allMatch(field -> {
            waitForElementToBeVisible(field);
            String value = field.getAttribute("value");
            return value == null || value.isEmpty();
        });
    }
    
    public boolean areAllOTPFieldsFilled() {
        return otpInputFields.stream().allMatch(field -> {
            waitForElementToBeVisible(field);
            String value = field.getAttribute("value");
            return value != null && !value.isEmpty() && value.length() == 1;
        });
    }
    
    public int getNumberOfOTPFields() {
        return otpInputFields.size();
    }
    
    public boolean isOTPFieldProperlyFormatted(int fieldIndex) {
        if (fieldIndex >= 0 && fieldIndex < otpInputFields.size()) {
            WebElement field = otpInputFields.get(fieldIndex);
            waitForElementToBeVisible(field);
            String maxLength = field.getAttribute("maxlength");
            String inputMode = field.getAttribute("inputmode");
            return "1".equals(maxLength) && "numeric".equals(inputMode);
        }
        return false;
    }
    
    public boolean areAllOTPFieldsProperlyFormatted() {
        for (int i = 0; i < otpInputFields.size(); i++) {
            if (!isOTPFieldProperlyFormatted(i)) {
                return false;
            }
        }
        return true;
    }
}