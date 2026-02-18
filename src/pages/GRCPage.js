import { expect } from '@playwright/test';

/**
 * Page Object Model for GRC Login Page
 * Handles user authentication and login functionality
 */
export class GRCPage {
  constructor(page) {
    this.page = page;
    
    // Locators
    this.emailInput = page.locator('#login-id');
    this.emailLabel = page.locator('label[for="login-id"]');
    this.getOTPButton = page.getByRole('button', { name: 'Get OTP' });
    this.loginForm = page.locator('form');
    this.signUpLink = page.getByRole('link', { name: 'Sign Up' });
    this.loginWithPasswordButton = page.getByRole('button', { name: 'Login with Password' });
    this.errorMessage = page.locator('.text-red-500.text-\[1\.2rem\]');
    this.pageTitle = page.locator('h1');
    
    // Navigation elements
    this.zolvitLogo = page.locator('img[alt="zolvitLogo"]');
    this.termsOfServiceLink = page.getByRole('link', { name: 'Terms of service' });
    this.privacyPolicyLink = page.getByRole('link', { name: 'Privacy policy' });
  }

  /**
   * Navigate to the GRC login page
   */
  async navigate() {
    await this.page.goto('/grc/auth/signin');
    await this.waitForPageLoad();
  }

  /**
   * Wait for the page to be fully loaded
   */
  async waitForPageLoad() {
    await expect(this.pageTitle).toContainText('Log into your account');
    await expect(this.emailInput).toBeVisible();
    await expect(this.getOTPButton).toBeVisible();
  }

  /**
   * Enter email address or mobile number in the login field
   * @param {string} emailOrMobile - Email address or mobile number to enter
   */
  async enterEmailAddress(emailOrMobile) {
    await this.emailInput.waitFor({ state: 'visible' });
    await this.emailInput.clear();
    await this.emailInput.fill(emailOrMobile);
    
    // Verify the input was entered correctly
    await expect(this.emailInput).toHaveValue(emailOrMobile);
  }

  /**
   * Click the Get OTP button
   */
  async clickGetOTP() {
    await this.getOTPButton.waitFor({ state: 'visible' });
    await expect(this.getOTPButton).toBeEnabled();
    await this.getOTPButton.click();
  }

  /**
   * Complete the login flow by entering email and clicking Get OTP
   * @param {string} emailOrMobile - Email address or mobile number
   */
  async loginWithOTP(emailOrMobile) {
    await this.enterEmailAddress(emailOrMobile);
    await this.clickGetOTP();
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
   * Get the current error message text
   * @returns {Promise<string>} Error message text
   */
  async getErrorMessage() {
    await this.errorMessage.waitFor({ state: 'visible' });
    return await this.errorMessage.textContent();
  }

  /**
   * Check if error message is displayed
   * @returns {Promise<boolean>} True if error message is visible
   */
  async isErrorMessageDisplayed() {
    try {
      await this.errorMessage.waitFor({ state: 'visible', timeout: 3000 });
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Verify the email input has the correct styling (error state)
   */
  async verifyEmailInputErrorState() {
    await expect(this.emailInput).toHaveClass(/border-red-500/);
    await expect(this.emailInput).toHaveClass(/text-red-500/);
  }

  /**
   * Verify the email input is in normal state
   */
  async verifyEmailInputNormalState() {
    await expect(this.emailInput).not.toHaveClass(/border-red-500/);
    await expect(this.emailInput).not.toHaveClass(/text-red-500/);
  }

  /**
   * Get the placeholder text of the email input
   * @returns {Promise<string>} Placeholder text
   */
  async getEmailInputPlaceholder() {
    return await this.emailInput.getAttribute('placeholder');
  }

  /**
   * Verify page title and main elements are present
   */
  async verifyPageElements() {
    await expect(this.pageTitle).toContainText('Log into your account');
    await expect(this.emailInput).toBeVisible();
    await expect(this.getOTPButton).toBeVisible();
    await expect(this.loginWithPasswordButton).toBeVisible();
    await expect(this.signUpLink).toBeVisible();
  }

  /**
   * Wait for form submission to complete
   */
  async waitForFormSubmission() {
    // Wait for any loading states or navigation after form submission
    await this.page.waitForLoadState('networkidle');
  }
}