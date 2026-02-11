package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page Object Model for GRC Login Page
 * Handles login functionality including OTP and password login options
 */
public class GRCPage {
    private final Page page;
    
    // Locators
    private final Locator emailInput;
    private final Locator getOtpButton;
    private final Locator loginWithPasswordButton;
    private final Locator errorMessage;
    private final Locator signUpLink;
    private final Locator pageTitle;
    
    public GRCPage(Page page) {
        this.page = page;
        this.emailInput = page.locator("#login-id");
        this.getOtpButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Get OTP"));
        this.loginWithPasswordButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Login with Password"));
        this.errorMessage = page.locator(".text-red-500").filter(new Locator.FilterOptions().setHasText("Email or Mobile number is required"));
        this.signUpLink = page.getByRole("link", new Page.GetByRoleOptions().setName("Sign Up"));
        this.pageTitle = page.getByRole("heading", new Page.GetByRoleOptions().setName("Log into your account"));
    }
    
    /**
     * Navigate to the GRC login page
     */
    public void navigateToLoginPage() {
        page.navigate("https://grc.vakilsearch.com/grc/auth/signin");
        page.waitForLoadState();
    }
    
    /**
     * Enter email or mobile number in the login field
     * @param emailOrMobile The email address or mobile number to enter
     */
    public void enterEmailOrMobile(String emailOrMobile) {
        emailInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        emailInput.clear();
        emailInput.fill(emailOrMobile);
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
     * Clear the email/mobile input field
     */
    public void clearEmailField() {
        emailInput.clear();
    }
    
    /**
     * Check if the page title is visible
     * @return true if page title is visible
     */
    public boolean isPageTitleVisible() {
        return pageTitle.isVisible();
    }
    
    /**
     * Check if error message is displayed
     * @return true if error message is visible
     */
    public boolean isErrorMessageVisible() {
        return errorMessage.isVisible();
    }
    
    /**
     * Get the error message text
     * @return error message text
     */
    public String getErrorMessageText() {
        return errorMessage.textContent();
    }
    
    /**
     * Check if Get OTP button is enabled
     * @return true if button is enabled
     */
    public boolean isGetOtpButtonEnabled() {
        return getOtpButton.isEnabled();
    }
    
    /**
     * Check if Login with Password button is visible
     * @return true if button is visible
     */
    public boolean isLoginWithPasswordButtonVisible() {
        return loginWithPasswordButton.isVisible();
    }
    
    /**
     * Check if Sign Up link is visible
     * @return true if link is visible
     */
    public boolean isSignUpLinkVisible() {
        return signUpLink.isVisible();
    }
    
    /**
     * Get the placeholder text of email input field
     * @return placeholder text
     */
    public String getEmailInputPlaceholder() {
        return emailInput.getAttribute("placeholder");
    }
    
    /**
     * Get the current value in email input field
     * @return current input value
     */
    public String getEmailInputValue() {
        return emailInput.inputValue();
    }
    
    /**
     * Wait for page to be fully loaded
     */
    public void waitForPageLoad() {
        pageTitle.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }
    
    /**
     * Check if email field has error styling (red border)
     * @return true if field has error styling
     */
    public boolean hasEmailFieldErrorStyling() {
        String borderColor = emailInput.evaluate("el => getComputedStyle(el).borderColor").toString();
        return borderColor.contains("rgb(239, 68, 68)") || emailInput.getAttribute("class").contains("border-red-500");
    }
}