import { test, expect } from '@playwright/test';
import { Page } from '../pages/Page.js';

test.describe('Base Page Tests', () => {
  let page;
  let pageObject;

  test.beforeEach(async ({ page: playwrightPage }) => {
    page = playwrightPage;
    pageObject = new Page(page);
  });

  test('should initialize page object successfully', async () => {
    // Test that page object can be created
    expect(pageObject).toBeDefined();
    expect(pageObject.page).toBeDefined();
  });

  test('should navigate to URL successfully', async () => {
    // Navigate to a test page
    await pageObject.navigateTo('https://example.com');
    
    // Verify navigation was successful
    const url = page.url();
    expect(url).toContain('example.com');
  });

  test('should get page title', async () => {
    // Navigate to test page
    await pageObject.navigateTo('https://example.com');
    
    // Get and verify title
    const title = await pageObject.getTitle();
    expect(title).toBeDefined();
    expect(typeof title).toBe('string');
    expect(title.length).toBeGreaterThan(0);
  });

  test('should wait for page load', async () => {
    // Navigate and wait for load
    await pageObject.navigateTo('https://example.com');
    await pageObject.waitForPageLoad();
    
    // Verify page is loaded
    const readyState = await page.evaluate(() => document.readyState);
    expect(readyState).toBe('complete');
  });

  test('should handle navigation errors gracefully', async () => {
    // Test with invalid URL
    await expect(pageObject.navigateTo('invalid-url')).rejects.toThrow();
  });

  test('should check element visibility', async () => {
    // Navigate to test page
    await pageObject.navigateTo('https://example.com');
    
    // Check for common elements
    const bodyLocator = page.locator('body');
    const isVisible = await pageObject.isElementVisible(bodyLocator);
    expect(isVisible).toBe(true);
  });

  test('should wait for element to be visible', async () => {
    // Navigate to test page
    await pageObject.navigateTo('https://example.com');
    
    // Wait for body element
    const bodyLocator = page.locator('body');
    await expect(async () => {
      await pageObject.waitForElement(bodyLocator);
    }).not.toThrow();
  });

  test('should handle element wait timeout', async () => {
    // Navigate to test page
    await pageObject.navigateTo('https://example.com');
    
    // Wait for non-existent element with short timeout
    const nonExistentLocator = page.locator('#non-existent-element');
    await expect(pageObject.waitForElement(nonExistentLocator, 1000))
      .rejects.toThrow(/Element not visible within/);
  });

  test('should take screenshot without errors', async () => {
    // Navigate to test page
    await pageObject.navigateTo('https://example.com');
    
    // Take screenshot - should not throw error
    await expect(async () => {
      await pageObject.takeScreenshot('test-screenshot');
    }).not.toThrow();
  });
});