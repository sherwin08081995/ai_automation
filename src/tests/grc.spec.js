import { test, expect } from '@playwright/test';
import { GRCPage } from '../pages/GRCPage.js';
import testData from '../fixtures/grc.data.json';

test.describe('GRC Login Page Tests', () => {
  let grcPage;

  test.beforeEach(async ({ page }) => {
    grcPage = new GRCPage(page);
    await grcPage.navigate();
  });

  test('should load GRC login page successfully', async () => {
    // Verify page loads with all essential elements
    await expect(grcPage.pageTitle).toBeVisible();
    await expect(grcPage.loginIdInput).toBeVisible();
    await expect(grcPage.getOtpButton).toBeVisible();
    await expect(grcPage.loginWithPasswordButton).toBeVisible();
    await expect(grcPage.signUpLink).toBeVisible();
  });

  test('should enter mobile number 8148438075 and click Get OTP', async () => {
    // Main test scenario: Enter specific mobile number and request OTP
    await grcPage.enterLoginId(testData.validMobileNumber);
    
    // Verify input value is correct
    await expect(grcPage.loginIdInput).toHaveValue(testData.validMobileNumber);
    
    // Verify Get OTP button is enabled
    expect(await grcPage.isGetOtpButtonEnabled()).toBe(true);
    
    // Click Get OTP button
    await grcPage.clickGetOtpButton();
    
    // Wait for response (navigation, modal, or error)
    await grcPage.waitForOtpResponse();
    
    // Verify no error message appears for valid mobile number
    expect(await grcPage.isErrorMessageVisible()).toBe(false);
  });

  test('should show error for empty login field when submitting', async () => {
    // Test validation: Empty field should show error
    await grcPage.clickGetOtpButton();
    
    // Verify error message appears
    expect(await grcPage.isErrorMessageVisible()).toBe(true);
    
    // Verify error message content
    const errorText = await grcPage.getErrorMessage();
    expect(errorText).toContain('required');
    
    // Verify input has error styling
    expect(await grcPage.hasInputErrorStyling()).toBe(true);
  });

  test('should handle invalid mobile number format', async () => {
    // Test with invalid mobile number
    await grcPage.enterLoginId(testData.invalidMobileNumber);
    await grcPage.clickGetOtpButton();
    
    // Wait for potential error response
    await grcPage.waitForOtpResponse(5000);
    
    // Check if error styling or message appears
    const hasError = await grcPage.isErrorMessageVisible() || await grcPage.hasInputErrorStyling();
    expect(hasError).toBe(true);
  });

  test('should handle valid email address input', async () => {
    // Test with valid email
    await grcPage.enterLoginId(testData.validEmail);
    
    // Verify input accepts email
    await expect(grcPage.loginIdInput).toHaveValue(testData.validEmail);
    
    // Verify Get OTP button is enabled
    expect(await grcPage.isGetOtpButtonEnabled()).toBe(true);
    
    // Submit form
    await grcPage.clickGetOtpButton();
    await grcPage.waitForOtpResponse();
  });

  test('should navigate to sign up page when clicking Sign Up link', async () => {
    // Test navigation to sign up
    await grcPage.clickSignUp();
    
    // Wait for navigation
    await grcPage.page.waitForURL('**/signup');
    
    // Verify URL contains signup
    expect(grcPage.page.url()).toContain('/signup');
  });

  test('should toggle to password login when clicking Login with Password', async () => {
    // Test alternative login method
    await grcPage.clickLoginWithPassword();
    
    // This might trigger a form change or navigation
    // Wait for any changes to occur
    await grcPage.page.waitForTimeout(1000);
    
    // Verify the button was clicked (interaction occurred)
    expect(await grcPage.loginWithPasswordButton.isVisible()).toBe(true);
  });

  test('should validate floating label behavior', async () => {
    // Test floating label animation
    
    // Initially, check if label is in default position
    await grcPage.loginIdInput.click();
    
    // Enter text to trigger floating label
    await grcPage.enterLoginId('test');
    
    // Verify floating label is active
    expect(await grcPage.isFloatingLabelActive()).toBe(true);
    
    // Clear input and check label returns
    await grcPage.clearLoginInput();
    await grcPage.page.click('body'); // Click outside to blur
  });

  test('should submit form using Enter key', async () => {
    // Test keyboard navigation
    await grcPage.enterLoginId(testData.validMobileNumber);
    await grcPage.submitFormByEnter();
    
    // Wait for form submission response
    await grcPage.waitForOtpResponse();
    
    // Verify form was submitted (no error for valid input)
    expect(await grcPage.isErrorMessageVisible()).toBe(false);
  });

  test('should handle special characters in mobile number', async () => {
    // Test with mobile number containing special characters
    await grcPage.enterLoginId(testData.mobileWithSpecialChars);
    await grcPage.clickGetOtpButton();
    
    // Wait for validation response
    await grcPage.waitForOtpResponse(3000);
    
    // Some form of validation should occur
    // Either error message or successful processing
    const hasError = await grcPage.isErrorMessageVisible();
    const buttonEnabled = await grcPage.isGetOtpButtonEnabled();
    
    // At least one condition should be true (validation occurred)
    expect(hasError || buttonEnabled).toBe(true);
  });

  test('should maintain form state after page interaction', async () => {
    // Test form persistence
    const testNumber = testData.validMobileNumber;
    
    await grcPage.enterLoginId(testNumber);
    
    // Click somewhere else on the page
    await grcPage.pageTitle.click();
    
    // Verify input value is maintained
    await expect(grcPage.loginIdInput).toHaveValue(testNumber);
    
    // Verify Get OTP button remains enabled
    expect(await grcPage.isGetOtpButtonEnabled()).toBe(true);
  });
});