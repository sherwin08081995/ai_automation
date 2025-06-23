package pages;

import base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page Object Model class for the Login page.
 * Handles login interactions like entering username, OTP, and validation.
 *
 * @author Sherwin
 * @since 09-06-2025
 */

public class LoginPage extends BasePage {

    @FindBy(xpath = "(//input[@id='mobile'])[2]")
    private WebElement userName;

    @FindBy(xpath = "//button[@type='button']//following::p[normalize-space()='Get OTP']")
    private WebElement otpBtn;

    @FindBy(xpath = "//p[text()='Enter OTP']/following-sibling::div//input[@inputmode='numeric' and @maxlength='1']")
    private List<WebElement> otpInputs;

    @FindBy(xpath = "(//p[text()='Enter OTP']/following-sibling::div//input[@inputmode='numeric' and @maxlength='1'])[7]")
    private WebElement otpInputBox;

    @FindBy(xpath = "(//p[normalize-space()='Log into your account'])[2]")
    private WebElement loginSubtitleText;

    @FindBy(xpath = "//img[@alt='Vakilsearch']")
    private WebElement loginSuccessfulConfirmation;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Enters the given email into the login input field.
     *
     * @param emailInput The email or mobile number to be entered.
     */

    public void enterEmail(String emailInput) {
        wait.waitForElementToBeClickable(userName).sendKeys(emailInput);
    }

    /**
     * Clicks the "Get OTP" button.
     */

    public void clickOtpButton() {
        wait.waitForElementToBeClickable(otpBtn).click();
    }

    /**
     * Enters the given OTP into the OTP input fields (starting from index 6).
     *
     * @param otp A numeric string representing the OTP to be entered.
     */

    public void enterOtp(String otp) {
        int startIndex = 6;

        // Safety check to avoid IndexOutOfBoundsException
        if (otpInputs.size() < startIndex + otp.length()) {
            throw new RuntimeException("Not enough OTP input boxes. Found: " + otpInputs.size() + ", Required: " + (startIndex + otp.length()));
        }

        for (int i = 0; i < otp.length(); i++) {
            WebElement inputBox = wait.waitForElementToBeClickable(otpInputs.get(startIndex + i));
            inputBox.clear();
            inputBox.sendKeys(String.valueOf(otp.charAt(i)));
        }
    }

    /**
     * Checks whether the login subtitle is correct and matches expectations.
     *
     * @return true if subtitle text matches, false otherwise.
     */

    public boolean isLoginSubtitleCorrect() {
        try {
            String expected = "Log into your account";
            WebElement subtitleElement = wait.waitForVisibility(loginSubtitleText);
            String actual = subtitleElement.getText().trim();
            if (!expected.equals(actual)) {
                System.err.println("❌ Subtitle mismatch: Expected='" + expected + "' | Actual='" + actual + "'");
            }
            return expected.equals(actual);
        } catch (Exception e) {
            System.err.println("❌ Error verifying subtitle: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifies whether the login was successful by checking for the logo alt text.
     *
     * @param expectedAltText The expected logo alt text (not used, kept for future use).
     * @return true if login success logo is found, false otherwise.
     */


    public boolean isLoginSuccessful(String expectedAltText) {
        try {
            wait.waitForVisibility(loginSuccessfulConfirmation);
            String altText = loginSuccessfulConfirmation.getAttribute("alt");
            return altText != null && altText.equalsIgnoreCase(expectedAltText);
        } catch (Exception e) {
            System.err.println("❌ Login success check failed: " + e.getMessage());
            return false;
        }
    }



    /**
     * Checks whether the "Get OTP" button is visible and enabled.
     *
     * @return true if visible and enabled, false otherwise.
     */

    public boolean isSendOtpButtonVisibleAndEnabled() {
        try {
            WebElement sendOtpBtn = wait.waitForVisibility(otpBtn);
            return sendOtpBtn.isDisplayed() && sendOtpBtn.isEnabled();
        } catch (Exception e) {
            System.err.println("❌ OTP Button not visible/enabled: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks whether the email/mobile input field is visible and enabled.
     *
     * @return true if visible and enabled, false otherwise.
     */

    public boolean isEmailFieldVisibleAndEnabled() {
        try {
            WebElement emailInput = wait.waitForVisibility(userName);
            return emailInput.isDisplayed() && emailInput.isEnabled();
        } catch (Exception e) {
            System.err.println("❌ Email input field not visible/enabled: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks whether the OTP field (7th box) is visible and enabled.
     *
     * @return true if visible and enabled, false otherwise.
     */

    public boolean isOtpFieldVisibleAndEnabled() {
        try {
            WebElement otpBox = wait.waitForVisibility(otpInputBox);
            return otpBox.isDisplayed() && otpBox.isEnabled();
        } catch (Exception e) {
            System.err.println("❌ OTP field not visible/enabled: " + e.getMessage());
            return false;
        }
    }
}