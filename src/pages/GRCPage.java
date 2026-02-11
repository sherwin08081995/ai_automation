package src.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page Object Model class for GRC login page
 * Handles all interactions with the GRC authentication page
 */
public class GRCPage {
    private final Page page;
    
    // Locators
    private final Locator emailField;
    private final Locator getOTPButton;
    private final Locator loginWithPasswordButton;
    private final Locator validationMessage;
    private final Locator signUpLink;
    private final Locator pageTitle;
    
    public GRCPage(Page page) {
        this.page = page;
        this.emailField = page.locator("#login-id");
        this.getOTPButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Get OTP"));
        this.loginWithPasswordButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Login with Password"));
        this.validationMessage = page.locator(".text-red-500.text-\\[1\\.2rem\\]");
        this.signUpLink = page.getByRole("link", new Page.GetByRoleOptions().setName("Sign Up"));
        this.pageTitle = page.locator("h1:has-text('Log into your account')");
    }
    
    /**
     * Navigate to the GRC login page
     */
    public void navigateToLoginPage() {
        page.navigate("/grc/auth/signin");
        pageTitle.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }
    
    /**
     * Enter email or phone number in the login field
     * @param emailOrPhone The email address or phone number to enter
     */
    public void enterEmailOrPhone(String emailOrPhone) {
        emailField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        emailField.clear();
        emailField.fill(emailOrPhone);
    }
    
    /**
     * Click the Get OTP button
     */
    public void clickGetOTP() {
        getOTPButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        getOTPButton.click();
    }
    
    /**
     * Click the Login with Password button
     */
    public void clickLoginWithPassword() {
        loginWithPasswordButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        loginWithPasswordButton.click();
    }
    
    /**
     * Click the Sign Up link
     */
    public void clickSignUp() {
        signUpLink.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        signUpLink.click();
    }
    
    /**
     * Get the validation error message text
     * @return The validation error message
     */
    public String getValidationMessage() {
        validationMessage.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        return validationMessage.textContent().trim();
    }
    
    /**
     * Check if validation message is visible
     * @return true if validation message is visible, false otherwise
     */
    public boolean isValidationMessageVisible() {
        try {
            return validationMessage.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if Get OTP button is enabled
     * @return true if button is enabled, false otherwise
     */
    public boolean isGetOTPButtonEnabled() {
        return getOTPButton.isEnabled();
    }
    
    /**
     * Check if Login with Password button is visible
     * @return true if button is visible, false otherwise
     */
    public boolean isLoginWithPasswordButtonVisible() {
        return loginWithPasswordButton.isVisible();
    }
    
    /**
     * Get the current page title
     * @return The page title text
     */
    public String getPageTitle() {
        return pageTitle.textContent().trim();
    }
    
    /**
     * Get the email field value
     * @return The current value in the email field
     */
    public String getEmailFieldValue() {
        return emailField.inputValue();
    }
    
    /**
     * Check if email field has focus
     * @return true if email field is focused, false otherwise
     */
    public boolean isEmailFieldFocused() {
        return emailField.evaluate("element => element === document.activeElement").toString().equals("true");
    }
    
    /**
     * Wait for page to be fully loaded
     */
    public void waitForPageLoad() {
        pageTitle.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        emailField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        getOTPButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }
}