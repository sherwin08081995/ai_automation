package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page Object Model for GRC Login page
 * Handles login functionality with email/mobile number and OTP generation
 */
public class GRCPage {
    private final Page page;
    
    // Locators
    private final Locator emailOrMobileInput;
    private final Locator getOtpButton;
    private final Locator emailLabel;
    private final Locator errorMessage;
    private final Locator signUpLink;
    private final Locator loginWithPasswordButton;
    private final Locator termsOfServiceLink;
    private final Locator privacyPolicyLink;
    
    // Constructor
    public GRCPage(Page page) {
        this.page = page;
        this.emailOrMobileInput = page.locator("#login-id");
        this.getOtpButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Get OTP"));
        this.emailLabel = page.locator("label[for='login-id']");
        this.errorMessage = page.locator(".text-red-500.text-\\[1\\.2rem\\]");
        this.signUpLink = page.getByRole("link", new Page.GetByRoleOptions().setName("Sign Up"));
        this.loginWithPasswordButton = page.getByRole("button", new Page.GetByRoleOptions().setName("Login with Password"));
        this.termsOfServiceLink = page.getByRole("link", new Page.GetByRoleOptions().setName("Terms of service"));
        this.privacyPolicyLink = page.getByRole("link", new Page.GetByRoleOptions().setName("Privacy policy"));
    }
    
    /**
     * Navigate to the GRC login page
     * @param url The URL of the GRC login page
     */
    public void navigateToLoginPage(String url) {
        page.navigate(url);
        page.waitForLoadState();
    }
    
    /**
     * Enter email or mobile number in the login field
     * @param emailOrMobile The email address or mobile number to enter
     */
    public void enterEmailOrMobile(String emailOrMobile) {
        emailOrMobileInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        emailOrMobileInput.clear();
        emailOrMobileInput.fill(emailOrMobile);
    }
    
    /**
     * Click the Get OTP button
     */
    public void clickGetOtpButton() {
        getOtpButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        getOtpButton.click();
    }
    
    /**
     * Perform complete login flow: enter credentials and click Get OTP
     * @param emailOrMobile The email address or mobile number
     */
    public void performLogin(String emailOrMobile) {
        enterEmailOrMobile(emailOrMobile);
        clickGetOtpButton();
    }
    
    /**
     * Check if the email/mobile input field is visible
     * @return true if the input field is visible, false otherwise
     */
    public boolean isEmailInputVisible() {
        return emailOrMobileInput.isVisible();
    }
    
    /**
     * Check if the Get OTP button is visible
     * @return true if the button is visible, false otherwise
     */
    public boolean isGetOtpButtonVisible() {
        return getOtpButton.isVisible();
    }
    
    /**
     * Check if the Get OTP button is enabled
     * @return true if the button is enabled, false otherwise
     */
    public boolean isGetOtpButtonEnabled() {
        return getOtpButton.isEnabled();
    }
    
    /**
     * Get the current value of the email/mobile input field
     * @return The current input value
     */
    public String getEmailInputValue() {
        return emailOrMobileInput.inputValue();
    }
    
    /**
     * Get the label text for the email/mobile input field
     * @return The label text
     */
    public String getEmailInputLabel() {
        return emailLabel.textContent();
    }
    
    /**
     * Check if error message is displayed
     * @return true if error message is visible, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        return errorMessage.isVisible();
    }
    
    /**
     * Get the error message text
     * @return The error message text if visible, empty string otherwise
     */
    public String getErrorMessage() {
        if (isErrorMessageDisplayed()) {
            return errorMessage.textContent();
        }
        return "";
    }
    
    /**
     * Click the Sign Up link
     */
    public void clickSignUpLink() {
        signUpLink.click();
    }
    
    /**
     * Click the Login with Password button
     */
    public void clickLoginWithPasswordButton() {
        loginWithPasswordButton.click();
    }
    
    /**
     * Wait for page to load completely
     */
    public void waitForPageLoad() {
        page.waitForLoadState();
        emailOrMobileInput.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }
    
    /**
     * Get the page title
     * @return The page title
     */
    public String getPageTitle() {
        return page.title();
    }
    
    /**
     * Check if terms of service link is present
     * @return true if link is visible, false otherwise
     */
    public boolean isTermsOfServiceLinkVisible() {
        return termsOfServiceLink.isVisible();
    }
    
    /**
     * Check if privacy policy link is present
     * @return true if link is visible, false otherwise
     */
    public boolean isPrivacyPolicyLinkVisible() {
        return privacyPolicyLink.isVisible();
    }
}