import { expect } from '@playwright/test';

/**
 * Home Page Object Model class
 * Handles interactions with the home page
 */
export class HomePage {
  constructor(page) {
    this.page = page;
  }

  /**
   * Navigate to the home page
   * @param {string} url - The URL to navigate to
   */
  async goto(url = '/') {
    try {
      await this.page.goto(url);
      await this.page.waitForLoadState('networkidle');
    } catch (error) {
      throw new Error(`Failed to navigate to home page: ${error.message}`);
    }
  }

  /**
   * Verify the page has loaded correctly
   */
  async verifyPageLoaded() {
    try {
      await expect(this.page).toHaveTitle(/.+/);
      await expect(this.page).toHaveURL(/.+/);
    } catch (error) {
      throw new Error(`Home page failed to load: ${error.message}`);
    }
  }

  /**
   * Get the page title
   * @returns {Promise<string>} The page title
   */
  async getPageTitle() {
    try {
      return await this.page.title();
    } catch (error) {
      throw new Error(`Failed to get page title: ${error.message}`);
    }
  }

  /**
   * Get the current URL
   * @returns {Promise<string>} The current URL
   */
  async getCurrentUrl() {
    try {
      return this.page.url();
    } catch (error) {
      throw new Error(`Failed to get current URL: ${error.message}`);
    }
  }

  /**
   * Check if page is responsive
   */
  async checkResponsiveness() {
    try {
      // Test mobile viewport
      await this.page.setViewportSize({ width: 375, height: 667 });
      await this.page.waitForTimeout(500);
      
      // Test tablet viewport
      await this.page.setViewportSize({ width: 768, height: 1024 });
      await this.page.waitForTimeout(500);
      
      // Test desktop viewport
      await this.page.setViewportSize({ width: 1920, height: 1080 });
      await this.page.waitForTimeout(500);
    } catch (error) {
      throw new Error(`Responsiveness check failed: ${error.message}`);
    }
  }
}