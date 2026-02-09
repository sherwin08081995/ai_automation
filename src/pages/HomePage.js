import { expect } from '@playwright/test';

/**
 * Home Page Object
 * Handles interactions with the home page
 */
export class HomePage {
  constructor(page) {
    this.page = page;
    this.url = '/';
  }

  /**
   * Navigate to the home page
   */
  async navigate() {
    await this.page.goto(this.url);
    await this.page.waitForLoadState('networkidle');
  }

  /**
   * Verify page is loaded by checking URL and title
   */
  async verifyPageLoaded() {
    await expect(this.page).toHaveURL(this.url);
    await expect(this.page).toHaveTitle(/.*/);
  }

  /**
   * Get page title
   * @returns {Promise<string>} Page title
   */
  async getPageTitle() {
    return await this.page.title();
  }

  /**
   * Check if page is responsive
   */
  async verifyResponsiveLayout() {
    // Test different viewport sizes
    const viewports = [
      { width: 1920, height: 1080 }, // Desktop
      { width: 768, height: 1024 },  // Tablet
      { width: 375, height: 667 }    // Mobile
    ];

    for (const viewport of viewports) {
      await this.page.setViewportSize(viewport);
      await this.page.waitForTimeout(500); // Allow layout to settle
      await expect(this.page.locator('body')).toBeVisible();
    }
  }

  /**
   * Take a screenshot of the page
   * @param {string} name - Screenshot name
   */
  async takeScreenshot(name = 'homepage') {
    await this.page.screenshot({ path: `screenshots/${name}.png`, fullPage: true });
  }
}