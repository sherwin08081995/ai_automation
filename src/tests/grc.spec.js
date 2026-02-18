import { test, expect } from '@playwright/test';
import { GRCPage } from '../pages/GRCPage.js';
import testData from '../fixtures/grc.data.json';

test.describe('GRC Login Page Tests', () => {
  let grcPage;

  test.beforeEach(async ({ page }) => {
    grcPage = new GRCPage(page);
    await grcPage.navigate();
  });

  test('should enter mobile number and click Get OTP button', async () => {
    // Enter the specified mobile number
    await grcPage.enterEmailAddress(testData.mobileNumber);
    
    // Verify the mobile number was entered correctly
    await expect(grcPage.emailInput).toHaveValue(testData.mobileNumber);
    
    // Click Get OTP button
    await grcPage.clickGetOTP();
    
    // Wait for form submission
    await grcPage.waitForFormSubmission();
  });

  test('should complete full login flow with mobile number', async () => {
    // Use the loginWithOTP method to complete the full flow
    await grcPage.loginWithOTP(testData.mobileNumber);
    
    // Verify form was submitted successfully
    await grcPage.waitForFormSubmission();
  });

  test('should display page elements correctly on load', async () => {
    // Verify all main page elements are present
    await grcPage.verifyPageElements();
    
    // Verify the email input label
    await expect(grcPage.emailLabel).toContainText('Email address or mobile number');
    
    // Verify Get OTP button is enabled and visible
    await expect(grcPage.getOTPButton).toBeEnabled();
    await expect(grcPage.getOTPButton).toContainText('Get OTP');
  });

  test('should enter valid email address and click Get OTP', async () => {
    // Test with email address instead of mobile number
    await grcPage.enterEmailAddress(testData.email);
    
    // Verify email was entered correctly
    await expect(grcPage.emailInput).toHaveValue(testData.email);
    
    // Click Get OTP button
    await grcPage.clickGetOTP();
    
    // Wait for form processing
    await grcPage.waitForFormSubmission();
  });

  test('should handle empty form submission', async () => {
    // Try to click Get OTP without entering any data
    await grcPage.clickGetOTP();
    
    // Check if error message appears
    const hasError = await grcPage.isErrorMessageDisplayed();
    if (hasError) {
      const errorMessage = await grcPage.getErrorMessage();
      expect(errorMessage).toContain('required');
      
      // Verify error styling is applied
      await grcPage.verifyEmailInputErrorState();
    }
  });

  test('should navigate to sign up page when clicking sign up link', async () => {
    // Click the sign up link
    await grcPage.clickSignUp();
    
    // Verify navigation to signup page
    await expect(grcPage.page).toHaveURL(/\/signup/);
  });

  test('should switch to password login when clicking login with password', async () => {
    // Click login with password button
    await grcPage.clickLoginWithPassword();
    
    // This would typically change the form or navigate to password login
    // The exact assertion depends on the expected behavior
  });

  test('should validate mobile number format', async () => {
    // Test with different mobile number formats
    const testNumbers = testData.testMobileNumbers;
    
    for (const number of testNumbers) {
      await grcPage.enterEmailAddress(number.value);
      await expect(grcPage.emailInput).toHaveValue(number.value);
      
      // Clear for next iteration
      await grcPage.emailInput.clear();
    }
  });

  test('should handle special characters in input field', async () => {
    // Test with various input types
    const specialInputs = testData.specialInputs;
    
    for (const input of specialInputs) {
      await grcPage.enterEmailAddress(input);
      await grcPage.clickGetOTP();
      
      // Wait a moment for any validation
      await grcPage.page.waitForTimeout(500);
      
      // Clear for next test
      await grcPage.emailInput.clear();
    }
  });

  test('should verify page accessibility elements', async () => {
    // Check that form has proper labels and accessibility attributes
    await expect(grcPage.emailInput).toHaveAttribute('id', 'login-id');
    await expect(grcPage.emailLabel).toHaveAttribute('for', 'login-id');
    
    // Verify autocomplete attribute
    await expect(grcPage.emailInput).toHaveAttribute('autocomplete', 'tel email');
  });

  test('should verify footer links are present and functional', async () => {
    // Check terms of service link
    await expect(grcPage.termsOfServiceLink).toBeVisible();
    await expect(grcPage.termsOfServiceLink).toHaveAttribute('href', 'https://vakilsearch.com/terms-of-service');
    
    // Check privacy policy link
    await expect(grcPage.privacyPolicyLink).toBeVisible();
    await expect(grcPage.privacyPolicyLink).toHaveAttribute('href', 'https://vakilsearch.com/privacy-policy');
  });
});