package src.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page Object Model for GRC Login page
 * Handles login functionality with email/mobile and OTP
 */
public class GRCPage {
    private final Page page;
    
    // Locators
    private final Locator emailMobileInput;
    private final Locator getOtpButton;
    private final Locator emailMobileLabel;
    private final Locator errorMessage;
    private final Locator loginForm;
    private final Locator signupLink;
    private final Locator loginWithPasswordButton;
    
    public GRCPage(Page page) {
        this.page = page;
        this.emailMobileInput = page.locator("#login-id");
        this.getOtpButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Get OTP"));
        this.emailMobileLabel = page.locator("label[for='login-id']");
        this.errorMessage = page.locator(".text-red-500.text-\\[1\\.2rem\\]");
        this.loginForm = page.locator("form");
        this.signupLink = page.getByText("Sign Up");
        this.loginWithPasswordButton = page.getByText("Login with Password");
    }
    
    /**
     * Navigate to GRC login page
     */
    public void navigateToLogin() {
        page.navigate("/grc/auth/signin");
        page.waitForLoadState();
    }
    
    /**
     * Enter email or mobile number in the input field
     * @param emailOrMobile The email address or mobile number to enter
     */
    public void enterEmailOrMobile(String emailOrMobile) {
        emailMobileInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        emailMobileInput.clear();
        emailMobileInput.fill(emailOrMobile);
    }
    
    /**
     * Click the Get OTP button
     */
    public void clickGetOtpButton() {
        getOtpButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        getOtpButton.click();
    }
    
    /**
     * Complete login flow by entering email/mobile and clicking Get OTP
     * @param emailOrMobile The email address or mobile number
     */
    public void performLogin(String emailOrMobile) {
        enterEmailOrMobile(emailOrMobile);
        clickGetOtpButton();
    }
    
    /**
     * Check if email/mobile input field is visible
     * @return true if input field is visible
     */
    public boolean isEmailMobileInputVisible() {
        return emailMobileInput.isVisible();
    }
    
    /**
     * Check if Get OTP button is visible
     * @return true if Get OTP button is visible
     */
    public boolean isGetOtpButtonVisible() {
        return getOtpButton.isVisible();
    }
    
    /**
     * Check if Get OTP button is enabled
     * @return true if Get OTP button is enabled
     */
    public boolean isGetOtpButtonEnabled() {
        return getOtpButton.isEnabled();
    }
    
    /**
     * Get the current value in email/mobile input field
     * @return the input field value
     */
    public String getEmailMobileInputValue() {
        return emailMobileInput.inputValue();
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
     * Check if signup link is visible
     * @return true if signup link is visible
     */
    public boolean isSignupLinkVisible() {
        return signupLink.isVisible();
    }
    
    /**
     * Click on signup link
     */
    public void clickSignupLink() {
        signupLink.click();
    }
    
    /**
     * Click on login with password button
     */
    public void clickLoginWithPasswordButton() {
        loginWithPasswordButton.click();
    }
    
    /**
     * Wait for page to load completely
     */
    public void waitForPageLoad() {
        page.waitForLoadState();
        emailMobileInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }
}