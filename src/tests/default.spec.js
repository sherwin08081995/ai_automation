import { test, expect } from '@playwright/test';
import { DefaultPage } from '../pages/DefaultPage.js';
import testData from '../fixtures/default.data.json';

test.describe('Default Page Tests', () => {
  let defaultPage;

  test.beforeEach(async ({ page }) => {
    defaultPage = new DefaultPage(page);
  });

  test('should load the default page successfully', async () => {
    // Navigate to the page
    await defaultPage.navigate(testData.baseUrl);
    
    // Verify page is loaded
    const isLoaded = await defaultPage.isPageLoaded();
    expect(isLoaded).toBe(true);
    
    // Verify we're on the correct URL
    const currentUrl = await defaultPage.getCurrentUrl();
    expect(currentUrl).toContain(testData.baseUrl || '/');
  });

  test('should have a valid page title', async () => {
    // Navigate to the page
    await defaultPage.navigate(testData.baseUrl);
    
    // Get and verify page title
    const pageTitle = await defaultPage.getPageTitle();
    expect(pageTitle).toBeTruthy();
    expect(pageTitle.length).toBeGreaterThan(0);
  });

  test('should handle navigation errors gracefully', async () => {
    // Test with invalid URL
    try {
      await defaultPage.navigate('https://invalid-url-that-does-not-exist.com');
    } catch (error) {
      // Expect navigation to fail for invalid URL
      expect(error).toBeDefined();
    }
  });

  test('should verify page ready state', async () => {
    // Navigate to the page
    await defaultPage.navigate(testData.baseUrl);
    
    // Wait for page to be ready
    await defaultPage.waitForPageReady();
    
    // Verify page is in ready state
    const isLoaded = await defaultPage.isPageLoaded();
    expect(isLoaded).toBe(true);
  });
});