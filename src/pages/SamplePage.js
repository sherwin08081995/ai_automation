import { expect } from '@playwright/test';

/**
 * Sample Page Object Class
 * This is a template page object when no specific requirements are provided
 */
export class SamplePage {
  constructor(page) {
    this.page = page;
    
    // Common locators that might exist on most pages
    this.pageTitle = page.locator('h1').first();
    this.navigationMenu = page.locator('nav');
    this.mainContent = page.locator('main, [role="main"]');
    this.footerSection = page.locator('footer');
  }

  /**
   * Navigate to the page
   * @param {string} url - The URL to navigate to
   */
  async goto(url = '/') {
    try {
      await this.page.goto(url);
      await this.page.waitForLoadState('networkidle');
    } catch (error) {
      throw new Error(`Failed to navigate to ${url}: ${error.message}`);
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
   * Verify page is loaded by checking for main content
   */
  async verifyPageLoaded() {
    try {
      await expect(this.mainContent).toBeVisible();
    } catch (error) {
      throw new Error(`Page did not load properly: ${error.message}`);
    }
  }

  /**
   * Check if navigation menu is visible
   * @returns {Promise<boolean>} True if navigation is visible
   */
  async isNavigationVisible() {
    try {
      return await this.navigationMenu.isVisible();
    } catch (error) {
      return false;
    }
  }
}