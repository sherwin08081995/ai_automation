import { expect } from '@playwright/test';

/**
 * Home Page Object Model
 * Handles interactions with the home page
 */
export class HomePage {
  constructor(page) {
    this.page = page;
  }

  /**
   * Navigate to the home page
   * @param {string} baseURL - The base URL to navigate to
   */
  async goto(baseURL = '/') {
    try {
      await this.page.goto(baseURL);
      await this.page.waitForLoadState('networkidle');
    } catch (error) {
      throw new Error(`Failed to navigate to home page: ${error.message}`);
    }
  }

  /**
   * Verify page is loaded by checking page title
   * @param {string} expectedTitle - Expected page title
   */
  async verifyPageLoaded(expectedTitle = '') {
    try {
      await this.page.waitForLoadState('domcontentloaded');
      if (expectedTitle) {
        await expect(this.page).toHaveTitle(new RegExp(expectedTitle, 'i'));
      }
    } catch (error) {
      throw new Error(`Page load verification failed: ${error.message}`);
    }
  }

  /**
   * Get the page title
   * @returns {Promise<string>} - The page title
   */
  async getPageTitle() {
    try {
      return await this.page.title();
    } catch (error) {
      throw new Error(`Failed to get page title: ${error.message}`);
    }
  }

  /**
   * Take a screenshot of the current page
   * @param {string} screenshotName - Name for the screenshot file
   */
  async takeScreenshot(screenshotName = 'home-page') {
    try {
      await this.page.screenshot({ 
        path: `screenshots/${screenshotName}-${Date.now()}.png`,
        fullPage: true 
      });
    } catch (error) {
      throw new Error(`Failed to take screenshot: ${error.message}`);
    }
  }

  /**
   * Wait for specific element to be visible
   * @param {string} selector - Element selector
   * @param {number} timeout - Wait timeout in milliseconds
   */
  async waitForElement(selector, timeout = 10000) {
    try {
      await this.page.locator(selector).waitFor({ state: 'visible', timeout });
    } catch (error) {
      throw new Error(`Element ${selector} not found within ${timeout}ms: ${error.message}`);
    }
  }

  /**
   * Check if page contains specific text
   * @param {string} text - Text to search for
   * @returns {Promise<boolean>} - True if text is found
   */
  async hasText(text) {
    try {
      const element = this.page.getByText(text).first();
      return await element.isVisible();
    } catch (error) {
      return false;
    }
  }
}