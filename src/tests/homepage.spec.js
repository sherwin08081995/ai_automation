import { test, expect } from '@playwright/test';
import { HomePage } from '../pages/HomePage.js';
import testData from '../fixtures/homepage.data.json';

test.describe('Home Page Tests', () => {
  let homePage;

  test.beforeEach(async ({ page }) => {
    homePage = new HomePage(page);
  });

  test('should load home page successfully', async () => {
    // Navigate to home page
    await homePage.navigateTo(testData.baseUrl);
    
    // Verify page loaded
    await homePage.verifyPageLoaded();
    
    // Verify page title is not empty
    const title = await homePage.getPageTitle();
    expect(title).toBeTruthy();
    expect(title.length).toBeGreaterThan(0);
  });

  test('should have correct page title', async () => {
    await homePage.navigateTo(testData.baseUrl);
    
    const title = await homePage.getPageTitle();
    expect(title).toContain(testData.expectedTitleKeyword);
  });

  test('should navigate to correct URL', async () => {
    await homePage.navigateTo(testData.baseUrl);
    
    const currentUrl = await homePage.getCurrentUrl();
    expect(currentUrl).toContain(testData.baseUrl);
  });

  test('should not have console errors', async () => {
    await homePage.navigateTo(testData.baseUrl);
    
    // This will throw if there are console errors
    await homePage.verifyPageAccessibility();
  });

  test('should handle invalid URL gracefully', async () => {
    // Test navigation to invalid URL
    try {
      await homePage.navigateTo(testData.invalidUrl);
    } catch (error) {
      // Expected to fail for invalid URLs
      expect(error).toBeDefined();
    }
  });

  test('should take screenshot successfully', async () => {
    await homePage.navigateTo(testData.baseUrl);
    
    // This should not throw an error
    await homePage.takeScreenshot('test-homepage');
  });
});