import { expect } from '@playwright/test';

/**
 * LoginPage class following Page Object Model pattern
 * Handles all interactions with the login page
 */
export class LoginPage {
  /**
   * @param {import('@playwright/test').Page} page
   */
  constructor(page) {
    this.page = page;
    
    // Page elements using priority-based locator strategy
    this.pageTitle = page.getByRole('heading', { name: 'Sign In' });
    this.emailInput = page.getByRole('textbox', { name: 'Email Address' });
    this.passwordInput = page.getByLabel('Password');
    this.rememberMeCheckbox = page.getByRole('checkbox', { name: 'Remember me' });
    this.loginButton = page.getByRole('button', { name: 'Sign In' });
    this.forgotPasswordLink = page.getByRole('link', { name: 'Forgot password?' });
    this.signupLink = page.getByRole('link', { name: 'Sign up' });
    this.errorMessage = page.locator('#errorMessage');
    this.loginForm = page.locator('#loginForm');
  }

  /**
   * Navigate to the login page
   * @param {string} url - The login page URL
   */
  async navigate(url = '/login') {
    await this.page.goto(url);
    await expect(this.pageTitle).toBeVisible();
  }

  /**
   * Fill email field
   * @param {string} email - Email address to enter
   */
  async fillEmail(email) {
    await this.emailInput.fill(email);
    await expect(this.emailInput).toHaveValue(email);
  }

  /**
   * Fill password field
   * @param {string} password - Password to enter
   */
  async fillPassword(password) {
    await this.passwordInput.fill(password);
    await expect(this.passwordInput).toHaveValue(password);
  }

  /**
   * Toggle remember me checkbox
   */
  async toggleRememberMe() {
    await this.rememberMeCheckbox.click();
  }

  /**
   * Click the login button
   */
  async clickLogin() {
    await this.loginButton.click();
  }

  /**
   * Perform complete login action
   * @param {string} email - Email address
   * @param {string} password - Password
   * @param {boolean} rememberMe - Whether to check remember me checkbox
   */
  async login(email, password, rememberMe = false) {
    if (email) {
      await this.fillEmail(email);
    }
    if (password) {
      await this.fillPassword(password);
    }
    if (rememberMe) {
      await this.toggleRememberMe();
    }
    await this.clickLogin();
  }

  /**
   * Wait for and verify error message is displayed
   * @param {string} expectedMessage - Expected error message text
   */
  async verifyErrorMessage(expectedMessage = 'Invalid email or password') {
    await expect(this.errorMessage).toBeVisible();
    await expect(this.errorMessage).toContainText(expectedMessage);
  }

  /**
   * Verify error message is not displayed
   */
  async verifyNoErrorMessage() {
    await expect(this.errorMessage).not.toBeVisible();
  }

  /**
   * Verify forgot password link is visible and clickable
   */
  async verifyForgotPasswordLink() {
    await expect(this.forgotPasswordLink).toBeVisible();
    await expect(this.forgotPasswordLink).toHaveAttribute('href', '/forgot-password');
  }

  /**
   * Click forgot password link
   */
  async clickForgotPassword() {
    await this.forgotPasswordLink.click();
  }

  /**
   * Verify required field validation
   * @param {string} fieldType - 'email' or 'password'
   */
  async verifyRequiredFieldValidation(fieldType) {
    const field = fieldType === 'email' ? this.emailInput : this.passwordInput;
    await expect(field).toHaveAttribute('required');
  }

  /**
   * Clear all form fields
   */
  async clearForm() {
    await this.emailInput.clear();
    await this.passwordInput.clear();
  }

  /**
   * Wait for successful login redirect
   */
  async waitForSuccessfulLogin() {
    // Wait for navigation away from login page
    await this.page.waitForURL(url => !url.includes('/login'), { timeout: 5000 });
  }

  /**
   * Verify login form is displayed
   */
  async verifyLoginFormDisplayed() {
    await expect(this.loginForm).toBeVisible();
    await expect(this.emailInput).toBeVisible();
    await expect(this.passwordInput).toBeVisible();
    await expect(this.loginButton).toBeVisible();
  }
}