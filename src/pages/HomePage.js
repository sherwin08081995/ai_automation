import { expect } from '@playwright/test';

/**
 * Page Object Model class for Home Page
 */
export class HomePage {
  /**
   * @param {import('@playwright/test').Page} page
   */
  constructor(page) {
    this.page = page;
  }

  /**
   * Navigate to the home page
   * @param {string} url - The URL to navigate to
   */
  async navigateTo(url = '/') {
    await this.page.goto(url);
    await this.page.waitForLoadState('networkidle');
  }

  /**
   * Verify the page has loaded correctly
   */
  async verifyPageLoaded() {
    await expect(this.page).toHaveURL(/.*/);
    await expect(this.page).toHaveTitle(/.+/);
  }

  /**
   * Get page title
   * @returns {Promise<string>} Page title
   */
  async getPageTitle() {
    return await this.page.title();
  }

  /**
   * Get page URL
   * @returns {Promise<string>} Current page URL
   */
  async getCurrentUrl() {
    return this.page.url();
  }

  /**
   * Check if page is accessible (no major errors)
   */
  async verifyPageAccessibility() {
    // Check that the page doesn't have any major console errors
    const errors = [];
    this.page.on('pageerror', error => errors.push(error));
    
    // Wait for any potential errors to surface
    await this.page.waitForTimeout(1000);
    
    if (errors.length > 0) {
      throw new Error(`Page has console errors: ${errors.map(e => e.message).join(', ')}`);
    }
  }

  /**
   * Take a screenshot of the current page
   * @param {string} name - Screenshot filename
   */
  async takeScreenshot(name = 'homepage') {
    await this.page.screenshot({ path: `screenshots/${name}.png`, fullPage: true });
  }
}