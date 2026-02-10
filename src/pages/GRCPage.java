package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page Object Model for GRC Login Page
 * Handles login functionality including OTP and password login
 */
public class GRCPage {
    private final Page page;
    
    // Locators
    private final Locator emailField;
    private final Locator getOTPButton;
    private final Locator loginWithPasswordButton;
    private final Locator errorMessage;
    private final Locator signUpLink;
    private final Locator pageTitle;
    
    /**
     * Constructor to initialize GRC page
     * @param page Playwright page instance
     */
    public GRCPage(Page page) {
        this.page = page;
        this.emailField = page.locator("#login-id");
        this.getOTPButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Get OTP"));
        this.loginWithPasswordButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Login with Password"));
        this.errorMessage = page.locator(".text-red-500").filter(new Locator.FilterOptions().setHasText("Email or Mobile number is required"));
        this.signUpLink = page.getByRole("link", new Page.GetByRoleOptions().setName("Sign Up"));
        this.pageTitle = page.getByRole("heading", new Page.GetByRoleOptions().setName("Log into your account"));
    }
    
    /**
     * Navigate to GRC login page
     */
    public void navigateToLoginPage() {
        page.navigate("https://grc.vakilsearch.com/grc/auth/signin");
        waitForPageLoad();
    }
    
    /**
     * Wait for the login page to fully load
     */
    public void waitForPageLoad() {
        pageTitle.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        emailField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }
    
    /**
     * Enter email or mobile number in the login field
     * @param emailOrMobile Email address or mobile number to enter
     */
    public void enterEmailOrMobile(String emailOrMobile) {
        try {
            emailField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            emailField.clear();
            emailField.fill(emailOrMobile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to enter email/mobile: " + e.getMessage());
        }
    }
    
    /**
     * Click the Get OTP button
     */
    public void clickGetOTP() {
        try {
            getOTPButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            getOTPButton.click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click Get OTP button: " + e.getMessage());
        }
    }
    
    /**
     * Click the Login with Password button
     */
    public void clickLoginWithPassword() {
        try {
            loginWithPasswordButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            loginWithPasswordButton.click();
        } catch (Exception e) {
            throw new RuntimeException("Failed to click Login with Password button: " + e.getMessage());
        }
    }
    
    /**
     * Check if validation error message is displayed
     * @return true if error message is visible, false otherwise
     */
    public boolean isValidationErrorDisplayed() {
        try {
            return errorMessage.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get the validation error message text
     * @return Error message text
     */
    public String getValidationErrorText() {
        try {
            errorMessage.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            return errorMessage.textContent().trim();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get validation error text: " + e.getMessage());
        }
    }
    
    /**
     * Get the current value of the email field
     * @return Current input value
     */
    public String getEmailFieldValue() {
        try {
            return emailField.inputValue();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get email field value: " + e.getMessage());
        }
    }
    
    /**
     * Check if Get OTP button is enabled
     * @return true if button is enabled, false otherwise
     */
    public boolean isGetOTPButtonEnabled() {
        try {
            return getOTPButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if Login with Password link is visible
     * @return true if link is visible, false otherwise
     */
    public boolean isLoginWithPasswordLinkVisible() {
        try {
            return loginWithPasswordButton.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if page title is displayed correctly
     * @return true if page title is visible, false otherwise
     */
    public boolean isPageTitleDisplayed() {
        try {
            return pageTitle.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get the page title text
     * @return Page title text
     */
    public String getPageTitle() {
        try {
            return pageTitle.textContent().trim();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get page title: " + e.getMessage());
        }
    }
    
    /**
     * Clear the email field
     */
    public void clearEmailField() {
        try {
            emailField.clear();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear email field: " + e.getMessage());
        }
    }
    
    /**
     * Check if Sign Up link is visible
     * @return true if Sign Up link is visible, false otherwise
     */
    public boolean isSignUpLinkVisible() {
        try {
            return signUpLink.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
}