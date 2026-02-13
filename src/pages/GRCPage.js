import { expect } from '@playwright/test';

/**
 * Page Object Model for GRC Login Page
 * Handles user authentication and OTP generation functionality
 */
export class GRCPage {
  constructor(page) {
    this.page = page;
    
    // Primary login form locators using best practices
    this.loginIdInput = page.locator('#login-id');
    this.getOtpButton = page.getByRole('button', { name: 'Get OTP' });
    this.loginWithPasswordButton = page.getByRole('button', { name: 'Login with Password' });
    this.signUpLink = page.getByRole('link', { name: 'Sign Up' });
    
    // Error message locators
    this.errorMessage = page.locator('span.text-red-500');
    
    // Form validation locators
    this.loginForm = page.locator('form');
    this.floatingLabel = page.locator('label[for="login-id"]');
    
    // Modal and overlay locators
    this.modalOverlay = page.locator('.fixed.bg-black\/60');
    this.emailSelectionModal = page.locator('text=Choose an Email to Log In');
    
    // Header elements
    this.zolvitLogo = page.locator('img[alt="zolvitLogo"]');
    this.pageTitle = page.getByRole('heading', { name: 'Log into your account' });
  }

  /**
   * Navigate to the GRC login page
   * @param {string} url - The URL to navigate to
   */
  async navigate(url = '/grc/auth/signin') {
    await this.page.goto(url);
    await this.waitForPageLoad();
  }

  /**
   * Wait for the page to fully load
   */
  async waitForPageLoad() {
    await expect(this.pageTitle).toBeVisible();
    await expect(this.loginIdInput).toBeVisible();
    await expect(this.getOtpButton).toBeVisible();
  }

  /**
   * Enter email or mobile number in the login field
   * @param {string} loginId - Email address or mobile number
   */
  async enterLoginId(loginId) {
    await this.loginIdInput.click();
    await this.loginIdInput.clear();
    await this.loginIdInput.fill(loginId);
    
    // Verify the input was filled correctly
    await expect(this.loginIdInput).toHaveValue(loginId);
  }

  /**
   * Click the Get OTP button
   */
  async clickGetOtpButton() {
    await expect(this.getOtpButton).toBeEnabled();
    await this.getOtpButton.click();
  }

  /**
   * Complete the login flow with mobile number and OTP request
   * @param {string} mobileNumber - Mobile number to login with
   */
  async loginWithMobileNumber(mobileNumber) {
    await this.enterLoginId(mobileNumber);
    await this.clickGetOtpButton();
  }

  /**
   * Click the Login with Password button
   */
  async clickLoginWithPassword() {
    await expect(this.loginWithPasswordButton).toBeVisible();
    await this.loginWithPasswordButton.click();
  }

  /**
   * Click the Sign Up link
   */
  async clickSignUp() {
    await this.signUpLink.click();
  }

  /**
   * Get the current error message text
   * @returns {Promise<string>} Error message text
   */
  async getErrorMessage() {
    await expect(this.errorMessage).toBeVisible();
    return await this.errorMessage.textContent();
  }

  /**
   * Check if error message is displayed
   * @returns {Promise<boolean>} True if error message is visible
   */
  async isErrorMessageVisible() {
    try {
      await expect(this.errorMessage).toBeVisible({ timeout: 3000 });
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Check if Get OTP button is enabled
   * @returns {Promise<boolean>} True if button is enabled
   */
  async isGetOtpButtonEnabled() {
    return await this.getOtpButton.isEnabled();
  }

  /**
   * Verify the floating label behavior
   * @returns {Promise<boolean>} True if label is properly positioned
   */
  async isFloatingLabelActive() {
    const labelClasses = await this.floatingLabel.getAttribute('class');
    return labelClasses.includes('scale-[0.8]') && labelClasses.includes('-translate-y-[1.8rem]');
  }

  /**
   * Check if the page title is visible
   * @returns {Promise<boolean>} True if page title is visible
   */
  async isPageTitleVisible() {
    return await this.pageTitle.isVisible();
  }

  /**
   * Wait for potential navigation or modal after OTP request
   * @param {number} timeout - Timeout in milliseconds
   */
  async waitForOtpResponse(timeout = 10000) {
    try {
      // Wait for either navigation, modal, or error message
      await this.page.waitForLoadState('networkidle', { timeout });
    } catch (error) {
      // Handle timeout or other errors gracefully
      console.warn('OTP response timeout or error:', error.message);
    }
  }

  /**
   * Validate input field styling for error state
   * @returns {Promise<boolean>} True if input has error styling
   */
  async hasInputErrorStyling() {
    const inputClasses = await this.loginIdInput.getAttribute('class');
    return inputClasses.includes('text-red-500') && inputClasses.includes('border-red-500');
  }

  /**
   * Clear the login input field
   */
  async clearLoginInput() {
    await this.loginIdInput.click();
    await this.loginIdInput.clear();
  }

  /**
   * Submit the form by pressing Enter
   */
  async submitFormByEnter() {
    await this.loginIdInput.press('Enter');
  }
}