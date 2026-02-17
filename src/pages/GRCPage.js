import { expect } from '@playwright/test';

/**
 * Page Object Model for GRC Login Page
 * Handles login functionality with email/mobile and OTP generation
 */
export class GRCPage {
  constructor(page) {
    this.page = page;
    
    // Locators using Playwright best practices
    this.emailInput = page.locator('#login-id');
    this.emailLabel = page.locator('label[for="login-id"]');
    this.getOtpButton = page.getByRole('button', { name: 'Get OTP' });
    this.loginWithPasswordButton = page.getByRole('button', { name: 'Login with Password' });
    this.signUpLink = page.getByRole('link', { name: 'Sign Up' });
    this.errorMessage = page.locator('.text-red-500.text-\[1\.2rem\]');
    this.formContainer = page.locator('form');
    this.pageTitle = page.locator('h1', { hasText: 'Log into your account' });
  }

  /**
   * Navigate to the GRC login page
   */
  async navigateToLogin() {
    await this.page.goto('/grc/auth/signin');
    await this.waitForPageLoad();
  }

  /**
   * Wait for the login page to fully load
   */
  async waitForPageLoad() {
    await expect(this.pageTitle).toBeVisible();
    await expect(this.emailInput).toBeVisible();
    await expect(this.getOtpButton).toBeVisible();
  }

  /**
   * Enter email or mobile number in the login field
   * @param {string} emailOrMobile - Email address or mobile number
   */
  async enterEmailOrMobile(emailOrMobile) {
    await this.emailInput.waitFor({ state: 'visible' });
    await this.emailInput.clear();
    await this.emailInput.fill(emailOrMobile);
    
    // Verify the input was entered correctly
    await expect(this.emailInput).toHaveValue(emailOrMobile);
  }

  /**
   * Click the Get OTP button
   */
  async clickGetOtp() {
    await this.getOtpButton.waitFor({ state: 'visible' });
    await expect(this.getOtpButton).toBeEnabled();
    await this.getOtpButton.click();
  }

  /**
   * Complete the login flow by entering email/mobile and clicking Get OTP
   * @param {string} emailOrMobile - Email address or mobile number
   */
  async performLogin(emailOrMobile) {
    await this.enterEmailOrMobile(emailOrMobile);
    await this.clickGetOtp();
  }

  /**
   * Click the Login with Password button
   */
  async clickLoginWithPassword() {
    await this.loginWithPasswordButton.waitFor({ state: 'visible' });
    await this.loginWithPasswordButton.click();
  }

  /**
   * Click the Sign Up link
   */
  async clickSignUp() {
    await this.signUpLink.waitFor({ state: 'visible' });
    await this.signUpLink.click();
  }

  /**
   * Get the current value of the email input field
   * @returns {Promise<string>} Current input value
   */
  async getEmailInputValue() {
    return await this.emailInput.inputValue();
  }

  /**
   * Check if the Get OTP button is enabled
   * @returns {Promise<boolean>} True if button is enabled
   */
  async isGetOtpButtonEnabled() {
    return await this.getOtpButton.isEnabled();
  }

  /**
   * Get error message text if displayed
   * @returns {Promise<string>} Error message text
   */
  async getErrorMessage() {
    if (await this.errorMessage.isVisible()) {
      return await this.errorMessage.textContent();
    }
    return null;
  }

  /**
   * Check if error message is displayed
   * @returns {Promise<boolean>} True if error message is visible
   */
  async isErrorMessageDisplayed() {
    return await this.errorMessage.isVisible();
  }

  /**
   * Verify the page title is correct
   */
  async verifyPageTitle() {
    await expect(this.pageTitle).toHaveText('Log into your account');
  }

  /**
   * Verify all essential elements are present
   */
  async verifyPageElements() {
    await expect(this.emailInput).toBeVisible();
    await expect(this.emailLabel).toBeVisible();
    await expect(this.getOtpButton).toBeVisible();
    await expect(this.loginWithPasswordButton).toBeVisible();
    await expect(this.signUpLink).toBeVisible();
  }

  /**
   * Get the placeholder text of the email input
   * @returns {Promise<string>} Placeholder text
   */
  async getEmailInputPlaceholder() {
    return await this.emailInput.getAttribute('placeholder');
  }

  /**
   * Submit the form (alternative to clicking Get OTP button)
   */
  async submitForm() {
    await this.formContainer.press('Enter');
  }
}