package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page Object Model for GRC Login Page
 * Handles interactions with login form elements and validation
 */
public class GRCPage {
    private final Page page;
    
    // Locators
    private final Locator emailField;
    private final Locator getOtpButton;
    private final Locator loginWithPasswordLink;
    private final Locator errorMessage;
    private final Locator signUpLink;
    
    /**
     * Constructor to initialize page object with Playwright Page instance
     * @param page Playwright Page instance
     */
    public GRCPage(Page page) {
        this.page = page;
        this.emailField = page.locator("#login-id");
        this.getOtpButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Get OTP"));
        this.loginWithPasswordLink = page.getByRole("button", new Page.GetByRoleOptions().setName("Login with Password"));
        this.errorMessage = page.locator(".text-red-500").filter(page.locator("span"));
        this.signUpLink = page.getByRole("link", new Page.GetByRoleOptions().setName("Sign Up"));
    }
    
    /**
     * Navigate to the GRC login page
     * @param url The URL to navigate to
     */
    public void navigateToLoginPage(String url) {
        page.navigate(url);
        page.waitForLoadState();
    }
    
    /**
     * Enter email or mobile number in the login field
     * @param emailOrMobile Email address or mobile number to enter
     */
    public void enterEmailOrMobile(String emailOrMobile) {
        emailField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        emailField.clear();
        emailField.fill(emailOrMobile);
    }
    
    /**
     * Click the Get OTP button
     */
    public void clickGetOtpButton() {
        getOtpButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        getOtpButton.click();
    }
    
    /**
     * Click the Login with Password link
     */
    public void clickLoginWithPasswordLink() {
        loginWithPasswordLink.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        loginWithPasswordLink.click();
    }
    
    /**
     * Check if error message is displayed for empty field validation
     * @return true if error message is visible, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        return errorMessage.isVisible();
    }
    
    /**
     * Get the error message text
     * @return Error message text
     */
    public String getErrorMessageText() {
        errorMessage.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        return errorMessage.textContent().trim();
    }
    
    /**
     * Check if email field has error styling (red border)
     * @return true if field has error styling, false otherwise
     */
    public boolean hasEmailFieldErrorStyling() {
        return emailField.evaluate("el => getComputedStyle(el).borderColor").toString().contains("239, 68, 68"); // rgb(239, 68, 68) is red-500
    }
    
    /**
     * Get the current value in the email field
     * @return Current value in email field
     */
    public String getEmailFieldValue() {
        return emailField.inputValue();
    }
    
    /**
     * Check if Get OTP button is enabled
     * @return true if button is enabled, false otherwise
     */
    public boolean isGetOtpButtonEnabled() {
        return getOtpButton.isEnabled();
    }
    
    /**
     * Check if Login with Password link is visible
     * @return true if link is visible, false otherwise
     */
    public boolean isLoginWithPasswordLinkVisible() {
        return loginWithPasswordLink.isVisible();
    }
    
    /**
     * Check if Sign Up link is visible
     * @return true if link is visible, false otherwise
     */
    public boolean isSignUpLinkVisible() {
        return signUpLink.isVisible();
    }
    
    /**
     * Get the page title
     * @return Page title
     */
    public String getPageTitle() {
        return page.title();
    }
    
    /**
     * Wait for page to load completely
     */
    public void waitForPageLoad() {
        page.waitForLoadState();
        emailField.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }
    
    /**
     * Clear the email field
     */
    public void clearEmailField() {
        emailField.clear();
    }
}