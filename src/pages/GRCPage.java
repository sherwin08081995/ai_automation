// ============================================
// 🤖 AI SELF-HEALING FIX
// ============================================
// Timestamp: 2026-02-19T07:58:19.524Z
// File: src/pages/GRCPage.java
// Field: emailPhoneInput
//
// SELECTOR FIXED:
// Old: #login-idd
// New: #login-id
// Exception reported: #login-idd
//
// Confidence: 85%
// Replacements: 1
// ============================================

package src.pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page Object Model class for GRC Login page
 * Handles login functionality with phone/email, OTP generation, and password login
 */
public class GRCPage {
    private final Page page;
    
    // Locators
    private final String emailPhoneInput = "#login-id";
    private final String getOtpButton = "button:has-text('Get OTP')";
    private final String loginWithPasswordLink = "button:has-text('Login with Password')";
    private final String signUpLink = "a[href='/grc/auth/signup']";
    private final String errorMessage = ".text-red-500";
    private final String emailLabel = "label[for='login-id']";
    private final String loginForm = "form";
    
    public GRCPage(Page page) {
        this.page = page;
    }
    
    /**
     * Navigate to GRC login page
     * @param url The URL to navigate to
     */
    public void navigateTo(String url) {
        page.navigate(url);
        page.waitForSelector(emailPhoneInput, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
    }
    
    /**
     * Enter email or phone number in the login field
     * @param emailOrPhone Email address or phone number to enter
     */
    public void enterEmailOrPhone(String emailOrPhone) {
        page.fill(emailPhoneInput, emailOrPhone);
    }
    
    /**
     * Click the Get OTP button
     */
    public void clickGetOtpButton() {
        page.click(getOtpButton);
    }
    
    /**
     * Click the Login with Password link
     */
    public void clickLoginWithPasswordLink() {
        page.click(loginWithPasswordLink);
    }
    
    /**
     * Click the Sign Up link
     */
    public void clickSignUpLink() {
        page.click(signUpLink);
    }
    
    /**
     * Get the error message text when validation fails
     * @return Error message text
     */
    public String getErrorMessage() {
        return page.textContent(errorMessage);
    }
    
    /**
     * Check if error message is visible
     * @return true if error message is visible
     */
    public boolean isErrorMessageVisible() {
        return page.isVisible(errorMessage);
    }
    
    /**
     * Get the current value in the email/phone input field
     * @return Current input value
     */
    public String getEmailPhoneValue() {
        return page.inputValue(emailPhoneInput);
    }
    
    /**
     * Check if Get OTP button is enabled
     * @return true if button is enabled
     */
    public boolean isGetOtpButtonEnabled() {
        return page.isEnabled(getOtpButton);
    }
    
    /**
     * Check if Login with Password link is visible
     * @return true if link is visible
     */
    public boolean isLoginWithPasswordLinkVisible() {
        return page.isVisible(loginWithPasswordLink);
    }
    
    /**
     * Wait for page to load completely
     */
    public void waitForPageLoad() {
        page.waitForSelector(emailPhoneInput);
        page.waitForSelector(getOtpButton);
        page.waitForSelector(loginWithPasswordLink);
    }
    
    /**
     * Clear the email/phone input field
     */
    public void clearEmailPhoneField() {
        page.fill(emailPhoneInput, "");
    }
    
    /**
     * Get page title
     * @return Page title
     */
    public String getPageTitle() {
        return page.title();
    }
    
    /**
     * Check if the form is visible
     * @return true if login form is visible
     */
    public boolean isLoginFormVisible() {
        return page.isVisible(loginForm);
    }
}
