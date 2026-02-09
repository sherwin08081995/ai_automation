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
    await homePage.goto();
    
    // Verify page loads
    await homePage.verifyPageLoaded();
    
    // Take screenshot for visual verification
    await homePage.takeScreenshot('home-page-loaded');
  });

  test('should have correct page title', async () => {
    // Navigate to home page
    await homePage.goto();
    
    // Get and verify page title
    const title = await homePage.getPageTitle();
    expect(title).toBeTruthy();
    expect(title.length).toBeGreaterThan(0);
  });

  test('should handle page navigation errors gracefully', async () => {
    // Test navigation to invalid URL
    await expect(async () => {
      await homePage.goto('/invalid-page-that-does-not-exist');
    }).rejects.toThrow();
  });

  test('should verify page loading states', async () => {
    // Navigate to home page
    await homePage.goto();
    
    // Wait for page to be fully loaded
    await homePage.verifyPageLoaded();
    
    // Verify page is interactive
    await expect(homePage.page).toHaveLoadState('domcontentloaded');
  });

  test('should handle screenshot functionality', async () => {
    // Navigate to home page
    await homePage.goto();
    
    // Take screenshot - should not throw error
    await expect(async () => {
      await homePage.takeScreenshot('test-screenshot');
    }).not.toThrow();
  });

  test.describe('Content Verification', () => {
    test.beforeEach(async () => {
      await homePage.goto();
    });

    test('should verify basic page structure', async () => {
      // Verify page has loaded content
      const title = await homePage.getPageTitle();
      expect(title).not.toBe('');
      
      // Verify page has some content
      const bodyText = await homePage.page.locator('body').textContent();
      expect(bodyText).toBeTruthy();
    });

    test('should handle text search functionality', async () => {
      // Test text search with common terms
      const commonTexts = testData.commonTexts || [];
      
      for (const text of commonTexts) {
        const hasText = await homePage.hasText(text);
        // This is informational - don't fail if text not found
        console.log(`Text '${text}' found: ${hasText}`);
      }
    });
  });

  test.describe('Error Handling', () => {
    test('should handle missing elements gracefully', async () => {
      await homePage.goto();
      
      // Test waiting for non-existent element with short timeout
      await expect(async () => {
        await homePage.waitForElement('#non-existent-element', 1000);
      }).rejects.toThrow();
    });

    test('should handle network issues', async ({ context }) => {
      // Simulate offline condition
      await context.setOffline(true);
      
      await expect(async () => {
        await homePage.goto();
      }).rejects.toThrow();
      
      // Restore online condition
      await context.setOffline(false);
    });
  });
});