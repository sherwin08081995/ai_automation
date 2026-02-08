import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage.js';
import loginData from '../fixtures/login.data.json';

test.describe('Login Page Tests', () => {
  let loginPage;

  test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page);
    await loginPage.navigate();
  });

  test('should display login form correctly', async () => {
    await loginPage.verifyLoginFormDisplayed();
    await expect(loginPage.pageTitle).toContainText('Sign In');
    await expect(loginPage.emailInput).toBeVisible();
    await expect(loginPage.passwordInput).toBeVisible();
    await expect(loginPage.loginButton).toBeVisible();
    await expect(loginPage.forgotPasswordLink).toBeVisible();
    await expect(loginPage.signupLink).toBeVisible();
  });

  test('should login successfully with valid credentials', async ({ page }) => {
    const { validUser } = loginData;
    
    await loginPage.login(validUser.email, validUser.password);
    
    // Verify no error message is shown
    await loginPage.verifyNoErrorMessage();
    
    // Wait for successful login redirect (assuming redirect to dashboard/home)
    await loginPage.waitForSuccessfulLogin();
    
    // Verify we're no longer on login page
    expect(page.url()).not.toContain('/login');
  });

  test('should show error message with invalid password', async () => {
    const { validUser, invalidPasswords } = loginData;
    
    await loginPage.login(validUser.email, invalidPasswords.wrongPassword);
    
    // Wait for error message to appear
    await loginPage.verifyErrorMessage();
    
    // Verify we're still on login page
    await expect(loginPage.loginForm).toBeVisible();
  });

  test('should show validation error with empty email field', async () => {
    const { validUser } = loginData;
    
    // Try to login with empty email
    await loginPage.login('', validUser.password);
    
    // Verify email field has required attribute
    await loginPage.verifyRequiredFieldValidation('email');
    
    // Browser should prevent form submission with empty required field
    await expect(loginPage.emailInput).toBeVisible();
    await expect(loginPage.emailInput).toHaveValue('');
  });

  test('should show validation error with empty password field', async () => {
    const { validUser } = loginData;
    
    // Try to login with empty password
    await loginPage.login(validUser.email, '');
    
    // Verify password field has required attribute
    await loginPage.verifyRequiredFieldValidation('password');
    
    // Browser should prevent form submission with empty required field
    await expect(loginPage.passwordInput).toBeVisible();
    await expect(loginPage.passwordInput).toHaveValue('');
  });

  test('should verify forgot password link is visible and functional', async ({ page }) => {
    // Verify forgot password link is visible
    await loginPage.verifyForgotPasswordLink();
    
    // Click forgot password link
    await loginPage.clickForgotPassword();
    
    // Verify navigation to forgot password page
    await page.waitForURL('**/forgot-password');
    expect(page.url()).toContain('/forgot-password');
  });

  test('should handle multiple invalid login attempts', async () => {
    const { invalidCredentials } = loginData;
    
    for (const credential of invalidCredentials) {
      await loginPage.clearForm();
      await loginPage.login(credential.email, credential.password);
      await loginPage.verifyErrorMessage();
      
      // Verify we're still on login page
      await expect(loginPage.loginForm).toBeVisible();
    }
  });

  test('should remember me checkbox function correctly', async () => {
    const { validUser } = loginData;
    
    // Verify checkbox is initially unchecked
    await expect(loginPage.rememberMeCheckbox).not.toBeChecked();
    
    // Toggle remember me checkbox
    await loginPage.toggleRememberMe();
    await expect(loginPage.rememberMeCheckbox).toBeChecked();
    
    // Login with remember me checked
    await loginPage.login(validUser.email, validUser.password, true);
  });

  test('should validate email format', async () => {
    const { invalidEmails } = loginData;
    const { validUser } = loginData;
    
    for (const invalidEmail of invalidEmails) {
      await loginPage.clearForm();
      await loginPage.fillEmail(invalidEmail);
      await loginPage.fillPassword(validUser.password);
      await loginPage.clickLogin();
      
      // HTML5 email validation should prevent submission
      // or show appropriate error message
      await expect(loginPage.emailInput).toHaveAttribute('type', 'email');
    }
  });

  test('should verify signup link is present and functional', async ({ page }) => {
    // Verify signup link is visible
    await expect(loginPage.signupLink).toBeVisible();
    await expect(loginPage.signupLink).toHaveAttribute('href', '/signup');
    
    // Click signup link
    await loginPage.signupLink.click();
    
    // Verify navigation to signup page
    await page.waitForURL('**/signup');
    expect(page.url()).toContain('/signup');
  });
});