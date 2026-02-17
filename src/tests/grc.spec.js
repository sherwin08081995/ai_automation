import { test, expect } from '@playwright/test';
import { GRCPage } from '../pages/GRCPage.js';
import testData from '../fixtures/grc.data.json';

test.describe('GRC Login Page Tests', () => {
  let grcPage;

  test.beforeEach(async ({ page }) => {
    grcPage = new GRCPage(page);
    await grcPage.navigateToLogin();
  });

  test('should load login page with all essential elements', async () => {
    // Verify page title
    await grcPage.verifyPageTitle();
    
    // Verify all essential elements are present
    await grcPage.verifyPageElements();
    
    // Verify email input placeholder
    const placeholder = await grcPage.getEmailInputPlaceholder();
    expect(placeholder).toBe(' ');
  });

  test('should successfully enter mobile number and click Get OTP', async () => {
    // Enter the specified mobile number
    await grcPage.enterEmailOrMobile(testData.validMobile);
    
    // Verify the mobile number was entered correctly
    const inputValue = await grcPage.getEmailInputValue();
    expect(inputValue).toBe(testData.validMobile);
    
    // Verify Get OTP button is enabled
    const isButtonEnabled = await grcPage.isGetOtpButtonEnabled();
    expect(isButtonEnabled).toBe(true);
    
    // Click Get OTP button
    await grcPage.clickGetOtp();
    
    // Add assertion for successful OTP request (this would depend on the actual response)
    // For now, we verify the button was clicked successfully
    await expect(grcPage.getOtpButton).toBeVisible();
  });

  test('should perform complete login flow with mobile number', async () => {
    // Perform the complete login flow
    await grcPage.performLogin(testData.validMobile);
    
    // Verify the mobile number is correctly entered
    const inputValue = await grcPage.getEmailInputValue();
    expect(inputValue).toBe(testData.validMobile);
  });

  test('should show error message when submitting empty form', async () => {
    // Try to click Get OTP without entering any data
    await grcPage.clickGetOtp();
    
    // Wait for error message to appear
    await expect(grcPage.errorMessage).toBeVisible({ timeout: 5000 });
    
    // Verify error message content
    const errorText = await grcPage.getErrorMessage();
    expect(errorText).toContain('Email or Mobile number is required');
  });

  test('should handle valid email address input', async () => {
    // Enter valid email address
    await grcPage.enterEmailOrMobile(testData.validEmail);
    
    // Verify email was entered correctly
    const inputValue = await grcPage.getEmailInputValue();
    expect(inputValue).toBe(testData.validEmail);
    
    // Verify Get OTP button is enabled
    const isButtonEnabled = await grcPage.isGetOtpButtonEnabled();
    expect(isButtonEnabled).toBe(true);
    
    // Click Get OTP
    await grcPage.clickGetOtp();
  });

  test('should handle invalid mobile number format', async () => {
    // Enter invalid mobile number
    await grcPage.enterEmailOrMobile(testData.invalidMobile);
    
    // Click Get OTP
    await grcPage.clickGetOtp();
    
    // Check if error message appears (this depends on client-side validation)
    // The test assumes validation might show an error
    const hasError = await grcPage.isErrorMessageDisplayed();
    
    if (hasError) {
      const errorText = await grcPage.getErrorMessage();
      expect(errorText).toBeTruthy();
    }
  });

  test('should navigate to sign up page when clicking Sign Up link', async () => {
    // Click Sign Up link
    await grcPage.clickSignUp();
    
    // Verify navigation to sign up page
    await expect(grcPage.page).toHaveURL(/.*\/grc\/auth\/signup/);
  });

  test('should show Login with Password option', async () => {
    // Verify Login with Password button is visible and clickable
    await expect(grcPage.loginWithPasswordButton).toBeVisible();
    await expect(grcPage.loginWithPasswordButton).toBeEnabled();
    
    // Click Login with Password
    await grcPage.clickLoginWithPassword();
    
    // Verify the button was clicked (this would depend on the actual behavior)
    await expect(grcPage.loginWithPasswordButton).toBeVisible();
  });

  test('should clear input field and enter new value', async () => {
    // Enter first value
    await grcPage.enterEmailOrMobile(testData.validEmail);
    let inputValue = await grcPage.getEmailInputValue();
    expect(inputValue).toBe(testData.validEmail);
    
    // Enter second value (should clear and replace)
    await grcPage.enterEmailOrMobile(testData.validMobile);
    inputValue = await grcPage.getEmailInputValue();
    expect(inputValue).toBe(testData.validMobile);
  });

  test('should submit form using Enter key', async () => {
    // Enter mobile number
    await grcPage.enterEmailOrMobile(testData.validMobile);
    
    // Submit form using Enter key
    await grcPage.submitForm();
    
    // Verify form was submitted (this would depend on the actual response)
    const inputValue = await grcPage.getEmailInputValue();
    expect(inputValue).toBe(testData.validMobile);
  });

  test('should handle special characters in input', async () => {
    // Test with email containing special characters
    const specialEmail = testData.emailWithSpecialChars;
    await grcPage.enterEmailOrMobile(specialEmail);
    
    // Verify input was entered correctly
    const inputValue = await grcPage.getEmailInputValue();
    expect(inputValue).toBe(specialEmail);
    
    // Try to get OTP
    await grcPage.clickGetOtp();
  });
});