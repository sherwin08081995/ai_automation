import { expect } from '@playwright/test';

/**
 * Base Page Object class
 * Provides common functionality for all page objects
 */
export class Page {
  /**
   * Initialize page object
   * @param {import('@playwright/test').Page} page - Playwright page instance
   */
  constructor(page) {
    this.page = page;
  }

  /**
   * Navigate to a specific URL
   * @param {string} url - URL to navigate to
   */
  async navigateTo(url) {
    try {
      await this.page.goto(url, { waitUntil: 'networkidle' });
    } catch (error) {
      throw new Error(`Failed to navigate to ${url}: ${error.message}`);
    }
  }

  /**
   * Wait for page to be fully loaded
   */
  async waitForPageLoad() {
    try {
      await this.page.waitForLoadState('networkidle');
    } catch (error) {
      throw new Error(`Page failed to load: ${error.message}`);
    }
  }

  /**
   * Get page title
   * @returns {Promise<string>} Page title
   */
  async getTitle() {
    try {
      return await this.page.title();
    } catch (error) {
      throw new Error(`Failed to get page title: ${error.message}`);
    }
  }

  /**
   * Check if element is visible
   * @param {import('@playwright/test').Locator} locator - Element locator
   * @returns {Promise<boolean>} True if visible
   */
  async isElementVisible(locator) {
    try {
      return await locator.isVisible();
    } catch (error) {
      return false;
    }
  }

  /**
   * Wait for element to be visible
   * @param {import('@playwright/test').Locator} locator - Element locator
   * @param {number} timeout - Timeout in milliseconds
   */
  async waitForElement(locator, timeout = 30000) {
    try {
      await locator.waitFor({ state: 'visible', timeout });
    } catch (error) {
      throw new Error(`Element not visible within ${timeout}ms: ${error.message}`);
    }
  }

  /**
   * Take screenshot
   * @param {string} name - Screenshot name
   */
  async takeScreenshot(name) {
    try {
      await this.page.screenshot({ path: `screenshots/${name}.png`, fullPage: true });
    } catch (error) {
      console.error(`Failed to take screenshot: ${error.message}`);
    }
  }
}