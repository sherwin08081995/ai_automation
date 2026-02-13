package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page Object Model for GRC Login page
 * Handles login functionality including OTP request and validation
 */
public class GRCPage {
    private final Page page;
    
    // Locators
    private final Locator emailOrMobileInput;
    private final Locator getOtpButton;
    private final Locator loginWithPasswordButton;
    private final Locator errorMessage;
    private final Locator loginFormContainer;
    private final Locator signupLink;
    
    public GRCPage(Page page) {
        this.page = page;
        this.emailOrMobileInput = page.locator("#login-id");
        this.getOtpButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Get OTP"));
        this.loginWithPasswordButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Login with Password"));
        this.errorMessage = page.locator("span.text-red-500");
        this.loginFormContainer = page.locator("form");
        this.signupLink = page.getByRole("link", new Page.GetByRoleOptions().setName("Sign Up"));
    }
    
    /**
     * Navigate to the GRC login page
     */
    public void navigateToLoginPage() {
        page.navigate("https://grc.vakilsearch.com/grc/auth/signin");
        waitForPageLoad();
    }
    
    /**
     * Wait for the login page to fully load
     */
    public void waitForPageLoad() {
        loginFormContainer.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        emailOrMobileInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }
    
    /**
     * Enter email or mobile number in the input field
     * @param emailOrMobile The email or mobile number to enter
     */
    public void enterEmailOrMobile(String emailOrMobile) {
        emailOrMobileInput.clear();
        emailOrMobileInput.fill(emailOrMobile);
    }
    
    /**
     * Click the Get OTP button
     */
    public void clickGetOtpButton() {
        getOtpButton.click();
    }
    
    /**
     * Click the Login with Password button
     */
    public void clickLoginWithPasswordButton() {
        loginWithPasswordButton.click();
    }
    
    /**
     * Submit the login form (equivalent to clicking Get OTP)
     */
    public void submitLoginForm() {
        getOtpButton.click();
    }
    
    /**
     * Get the current value of the email/mobile input field
     * @return Current input value
     */
    public String getEmailOrMobileValue() {
        return emailOrMobileInput.inputValue();
    }
    
    /**
     * Check if the Get OTP button is enabled
     * @return true if button is enabled, false otherwise
     */
    public boolean isGetOtpButtonEnabled() {
        return getOtpButton.isEnabled();
    }
    
    /**
     * Check if the Login with Password button is visible
     * @return true if button is visible, false otherwise
     */
    public boolean isLoginWithPasswordButtonVisible() {
        return loginWithPasswordButton.isVisible();
    }
    
    /**
     * Get the error message text if validation fails
     * @return Error message text or empty string if no error
     */
    public String getErrorMessage() {
        try {
            if (errorMessage.isVisible()) {
                return errorMessage.textContent().trim();
            }
        } catch (Exception e) {
            // Error message not found or not visible
        }
        return "";
    }
    
    /**
     * Check if validation error is displayed
     * @return true if error message is visible, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        try {
            return errorMessage.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if the signup link is visible
     * @return true if signup link is visible, false otherwise
     */
    public boolean isSignupLinkVisible() {
        return signupLink.isVisible();
    }
    
    /**
     * Get the placeholder text of the email/mobile input field
     * @return Placeholder text
     */
    public String getInputPlaceholder() {
        return emailOrMobileInput.getAttribute("placeholder");
    }
    
    /**
     * Check if the input field has focus
     * @return true if input field is focused, false otherwise
     */
    public boolean isInputFieldFocused() {
        return emailOrMobileInput.evaluate("element => element === document.activeElement").toString().equals("true");
    }
    
    /**
     * Clear the email/mobile input field
     */
    public void clearEmailOrMobileField() {
        emailOrMobileInput.clear();
    }
    
    /**
     * Wait for error message to appear
     */
    public void waitForErrorMessage() {
        try {
            errorMessage.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        } catch (Exception e) {
            // Error message might not appear, continue test
        }
    }
}