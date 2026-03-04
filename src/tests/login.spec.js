const { test, expect } = require('@playwright/test');
const LoginPage = require('../pages/LoginPage');

test.describe('GRC Login Page Tests', () => {
  let loginPage;

  test.beforeEach(async ({ page }) => {
    loginPage = new LoginPage(page);
    await loginPage.navigateToLogin();
  });

  test('should login successfully with valid credentials', async ({ page }) => {
    await loginPage.login('sherwinzolvit360@yopmail.com', 'Vakil@1234');
    
    await expect(page).toHaveURL(/.*home.*/);
    const isHomePageLoaded = await loginPage.isHomePageLoaded();
    expect(isHomePageLoaded).toBe(true);
  });

  test('should show error message with invalid credentials', async ({ page }) => {
    await loginPage.login('invalid@email.com', 'wrongpassword');
    
    const isErrorVisible = await loginPage.isErrorMessageVisible();
    expect(isErrorVisible).toBe(true);
    
    await expect(page).not.toHaveURL(/.*home.*/);
  });

  test('should handle empty email field validation', async ({ page }) => {
    await loginPage.clickLoginWithPassword();
    await loginPage.fillPassword('Vakil@1234');
    await loginPage.clickLoginButton();
    
    const emailField = loginPage.emailInput;
    await expect(emailField).toHaveAttribute('required', '');
    
    await expect(page).not.toHaveURL(/.*home.*/);
  });

  test('should handle empty password field validation', async ({ page }) => {
    await loginPage.clickLoginWithPassword();
    await loginPage.fillEmail('sherwinzolvit360@yopmail.com');
    await loginPage.clickLoginButton();
    
    const passwordField = loginPage.passwordInput;
    await expect(passwordField).toHaveAttribute('required', '');
    
    await expect(page).not.toHaveURL(/.*home.*/);
  });

  test('should display login with password option initially', async () => {
    await expect(loginPage.loginWithPasswordOption).toBeVisible();
    await expect(loginPage.emailInput).not.toBeVisible();
    await expect(loginPage.passwordInput).not.toBeVisible();
  });

  test('should show login form after clicking login with password', async () => {
    await loginPage.clickLoginWithPassword();
    
    await expect(loginPage.emailInput).toBeVisible();
    await expect(loginPage.passwordInput).toBeVisible();
    await expect(loginPage.loginButton).toBeVisible();
  });
});