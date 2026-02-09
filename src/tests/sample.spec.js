import { test, expect } from '@playwright/test';
import { SamplePage } from '../pages/SamplePage.js';
import testData from '../fixtures/sample.data.json';

test.describe('Sample Page Tests', () => {
  let samplePage;

  test.beforeEach(async ({ page }) => {
    samplePage = new SamplePage(page);
  });

  test('should load the page successfully', async () => {
    await samplePage.goto(testData.baseUrl);
    await samplePage.verifyPageLoaded();
    
    const title = await samplePage.getPageTitle();
    expect(title).toBeTruthy();
    expect(title.length).toBeGreaterThan(0);
  });

  test('should display navigation menu', async () => {
    await samplePage.goto(testData.baseUrl);
    
    const isNavVisible = await samplePage.isNavigationVisible();
    expect(isNavVisible).toBe(true);
  });

  test('should have proper page structure', async () => {
    await samplePage.goto(testData.baseUrl);
    
    // Verify main content is visible
    await expect(samplePage.mainContent).toBeVisible();
    
    // Verify page has a title element
    const titleCount = await samplePage.pageTitle.count();
    expect(titleCount).toBeGreaterThan(0);
  });

  test('should handle invalid URL gracefully', async () => {
    await expect(async () => {
      await samplePage.goto('/invalid-page-url-that-does-not-exist');
    }).rejects.toThrow();
  });

  test('should load page within reasonable time', async () => {
    const startTime = Date.now();
    await samplePage.goto(testData.baseUrl);
    await samplePage.verifyPageLoaded();
    const endTime = Date.now();
    
    const loadTime = endTime - startTime;
    expect(loadTime).toBeLessThan(testData.maxLoadTime);
  });
});