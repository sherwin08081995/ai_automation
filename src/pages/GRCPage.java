package src.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page Object Model for GRC Login page
 * Handles login functionality with phone number, OTP, and validation
 */
public class GRCPage {
    private final Page page;
    
    // Locators
    private final Locator emailOrPhoneInput;
    private final Locator getOtpButton;
    private final Locator loginWithPasswordButton;
    private final Locator errorMessage;
    private final Locator signUpLink;
    private final Locator loginForm;
    private final Locator pageTitle;
    
    /**
     * Constructor to initialize GRC page
     * @param page Playwright page instance
     */
    public GRCPage(Page page) {
        this.page = page;
        this.emailOrPhoneInput = page.locator("#login-idd");
        this.getOtpButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Get OTP"));
        this.loginWithPasswordButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Login with Password"));
        this.errorMessage = page.locator(".text-red-500").filter(new Locator.FilterOptions().setHasText("Email or Mobile number is required"));
        this.signUpLink = page.getByRole("link", new Page.GetByRoleOptions().setName("Sign Up"));
        this.loginForm = page.locator("form");
        this.pageTitle = page.locator("h1").filter(new Locator.FilterOptions().setHasText("Log into your account"));
    }
    
    /**
     * Navigate to GRC login page
     * @param url The URL to navigate to
     */
    public void navigateToLoginPage(String url) {
        page.navigate(url);
        pageTitle.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }
    
    /**
     * Enter phone number or email in the input field
     * @param phoneOrEmail Phone number or email to enter
     */
    public void enterPhoneOrEmail(String phoneOrEmail) {
        emailOrPhoneInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        emailOrPhoneInput.clear();
        emailOrPhoneInput.fill(phoneOrEmail);
    }
    
    /**
     * Click the Get OTP button
     */
    public void clickGetOtpButton() {
        getOtpButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        getOtpButton.click();
    }
    
    /**
     * Click the Login with Password button
     */
    public void clickLoginWithPasswordButton() {
        loginWithPasswordButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        loginWithPasswordButton.click();
    }
    
    /**
     * Submit the login form without entering any data to trigger validation
     */
    public void submitEmptyForm() {
        getOtpButton.click();
    }
    
    /**
     * Check if error message is visible
     * @return true if error message is displayed
     */
    public boolean isErrorMessageVisible() {
        try {
            errorMessage.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(3000));
            return errorMessage.isVisible();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get the error message text
     * @return Error message text
     */
    public String getErrorMessage() {
        if (isErrorMessageVisible()) {
            return errorMessage.textContent();
        }
        return "";
    }
    
    /**
     * Check if Get OTP button is visible and enabled
     * @return true if button is visible and enabled
     */
    public boolean isGetOtpButtonEnabled() {
        return getOtpButton.isVisible() && getOtpButton.isEnabled();
    }
    
    /**
     * Check if Login with Password button is visible
     * @return true if button is visible
     */
    public boolean isLoginWithPasswordButtonVisible() {
        return loginWithPasswordButton.isVisible();
    }
    
    /**
     * Get the placeholder text of the input field
     * @return Placeholder text
     */
    public String getInputPlaceholder() {
        return emailOrPhoneInput.getAttribute("placeholder");
    }
    
    /**
     * Get the current value of the input field
     * @return Input field value
     */
    public String getInputValue() {
        return emailOrPhoneInput.inputValue();
    }
    
    /**
     * Check if the input field has error styling (red border)
     * @return true if input has error styling
     */
    public boolean hasInputErrorStyling() {
        String classList = emailOrPhoneInput.getAttribute("class");
        return classList != null && classList.contains("border-red-500");
    }
    
    /**
     * Check if Sign Up link is visible
     * @return true if sign up link is visible
     */
    public boolean isSignUpLinkVisible() {
        return signUpLink.isVisible();
    }
    
    /**
     * Wait for page to be fully loaded
     */
    public void waitForPageLoad() {
        pageTitle.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        loginForm.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }
}
