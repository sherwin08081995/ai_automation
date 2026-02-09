/**
 * DefaultPage - Page Object Model class
 * Handles interactions with the default page elements
 */

class DefaultPage {
  /**
   * @param {import('@playwright/test').Page} page
   */
  constructor(page) {
    this.page = page;
  }

  /**
   * Navigate to the page
   * @param {string} url - The URL to navigate to
   */
  async navigate(url = '/') {
    await this.page.goto(url);
    await this.page.waitForLoadState('networkidle');
  }

  /**
   * Get the page title
   * @returns {Promise<string>} Page title
   */
  async getPageTitle() {
    return await this.page.title();
  }

  /**
   * Check if page is loaded
   * @returns {Promise<boolean>} True if page is loaded
   */
  async isPageLoaded() {
    try {
      await this.page.waitForLoadState('domcontentloaded', { timeout: 5000 });
      return true;
    } catch (error) {
      return false;
    }
  }

  /**
   * Get page URL
   * @returns {Promise<string>} Current page URL
   */
  async getCurrentUrl() {
    return this.page.url();
  }

  /**
   * Wait for page to be ready
   */
  async waitForPageReady() {
    await this.page.waitForLoadState('networkidle');
  }
}

export { DefaultPage };