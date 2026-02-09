import { test, expect } from '@playwright/test';
import { HomePage } from '../pages/HomePage.js';
import testData from '../fixtures/homepage.data.json';

test.describe('Home Page Tests', () => {
  let homePage;

  test.beforeEach(async ({ page }) => {
    homePage = new HomePage(page);
  });

  test('should load home page successfully', async () => {
    await homePage.goto(testData.baseUrl);
    await homePage.verifyPageLoaded();
    
    const title = await homePage.getPageTitle();
    expect(title).toBeTruthy();
    expect(title.length).toBeGreaterThan(0);
  });

  test('should have valid URL after navigation', async () => {
    await homePage.goto(testData.baseUrl);
    
    const currentUrl = await homePage.getCurrentUrl();
    expect(currentUrl).toContain(testData.expectedUrlFragment);
  });

  test('should be responsive across different viewports', async () => {
    await homePage.goto(testData.baseUrl);
    await homePage.checkResponsiveness();
    
    // Verify page is still functional after viewport changes
    await homePage.verifyPageLoaded();
  });

  test('should handle page reload correctly', async () => {
    await homePage.goto(testData.baseUrl);
    await homePage.verifyPageLoaded();
    
    // Reload the page
    await homePage.page.reload({ waitUntil: 'networkidle' });
    await homePage.verifyPageLoaded();
    
    const title = await homePage.getPageTitle();
    expect(title).toBeTruthy();
  });

  test('should handle navigation errors gracefully', async () => {
    // Test navigation to invalid URL
    await expect(async () => {
      await homePage.goto(testData.invalidUrl);
    }).rejects.toThrow();
  });

  test('should have proper page metadata', async () => {
    await homePage.goto(testData.baseUrl);
    
    const title = await homePage.getPageTitle();
    expect(title).not.toBe('');
    expect(title).not.toContain('404');
    expect(title).not.toContain('Error');
  });
});