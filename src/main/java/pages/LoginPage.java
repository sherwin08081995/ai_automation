package pages;

import base.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * LoginPage.java
 * <p>
 * Purpose:
 * Page Object Model (POM) for the Login Page of the application.
 * This class handles:
 * <p>
 * ✅ Entering username/email/mobile into the login input
 * ✅ OTP-based login workflow: triggering OTP and entering values into OTP fields
 * ✅ Verification of successful login via confirmation logo
 * ✅ UI validations: subtitle check, field visibility, button status
 * ✅ Support for automation-friendly interaction via waits and retries
 * <p>
 * Related Utilities:
 * - BasePage.java (parent class providing wait and helper utilities)
 * - WaitUtils.java (explicit wait handling)
 * <p>
 * Author:
 *
 * @author Sherwin
 * @since 09-06-2025
 */


public class LoginPage extends BasePage {

    @FindBy(xpath = "//input[@id='loginId']")
    private WebElement userName;

    @FindBy(xpath = "//p[normalize-space()='Login with OTP']")
    private WebElement otpBtn;

    @FindBy(xpath = "//p[normalize-space()='Get OTP']")
    private WebElement getOtpBtn;

    @FindBy(xpath = "//p[text()='Enter OTP']/following-sibling::div//input[@inputmode='numeric' and @maxlength='1']")
    private List<WebElement> otpInputs;

    @FindBy(xpath = "//p[text()='Enter OTP']/following-sibling::div//input[@inputmode='numeric' and @maxlength='1']")
    private WebElement otpInputBox;

    @FindBy(xpath = "//h1[normalize-space()='Log into your account']")
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
     * Clicks the "Login with OTP" button.
     */

    public void clickLoginWithOtpButton() {
        wait.waitForElementToBeClickable(otpBtn).click();
    }


    /**
     * Clicks the "Get OTP" button.
     */

    public void clickGetOtpButton() {
        wait.waitForElementToBeClickable(getOtpBtn).click();
    }


    /**
     * Enters the given OTP into the OTP input fields (starting from index 6).
     *
     * @param otp A numeric string representing the OTP to be entered.
     */

    public void enterOtp(String otp) {
        // Safety check: OTP must be 6 digits, and 6 input boxes should exist
        if (otpInputs.size() != otp.length()) {
            throw new RuntimeException("Mismatch between OTP digits and input boxes. Found boxes: " + otpInputs.size() + ", OTP length: " + otp.length());
        }

        for (int i = 0; i < otp.length(); i++) {
            WebElement inputBox = wait.waitForElementToBeClickable(otpInputs.get(i));
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
            Thread.sleep(2000);
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

    public List<WebElement> getOtpInputs() {
        return otpInputs;
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