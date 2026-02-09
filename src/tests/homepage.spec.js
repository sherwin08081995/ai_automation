import { test, expect } from '@playwright/test';
import { HomePage } from '../pages/HomePage.js';

test.describe('Home Page Tests', () => {
  let homePage;

  test.beforeEach(async ({ page }) => {
    homePage = new HomePage(page);
  });

  test('should load home page successfully', async () => {
    await homePage.navigate();
    await homePage.verifyPageLoaded();
  });

  test('should have a valid page title', async () => {
    await homePage.navigate();
    const title = await homePage.getPageTitle();
    expect(title).toBeTruthy();
    expect(title.length).toBeGreaterThan(0);
  });

  test('should be responsive across different screen sizes', async () => {
    await homePage.navigate();
    await homePage.verifyResponsiveLayout();
  });

  test('should load within acceptable time', async ({ page }) => {
    const startTime = Date.now();
    await homePage.navigate();
    const loadTime = Date.now() - startTime;
    
    // Page should load within 5 seconds
    expect(loadTime).toBeLessThan(5000);
  });

  test('should not have console errors', async ({ page }) => {
    const consoleErrors = [];
    page.on('console', msg => {
      if (msg.type() === 'error') {
        consoleErrors.push(msg.text());
      }
    });

    await homePage.navigate();
    expect(consoleErrors).toHaveLength(0);
  });

  test('should take screenshot for visual testing', async () => {
    await homePage.navigate();
    await homePage.takeScreenshot('homepage-full');
  });
});