package com.yourcompany.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.By;
import java.time.Duration;

/**
 * Base page class providing common functionality for all page objects
 */
public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    private static final int TIMEOUT_SECONDS = 30;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        PageFactory.initElements(driver, this);
    }

    /**
     * Wait for element to be visible
     */
    protected WebElement waitForElementVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait for element to be clickable
     */
    protected WebElement waitForElementClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Wait for element to be visible by locator
     */
    protected WebElement waitForElementVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Safe click with wait
     */
    protected void safeClick(WebElement element) {
        waitForElementClickable(element).click();
    }

    /**
     * Safe send keys with wait
     */
    protected void safeSendKeys(WebElement element, String text) {
        WebElement visibleElement = waitForElementVisible(element);
        visibleElement.clear();
        visibleElement.sendKeys(text);
    }

    /**
     * Get text with wait
     */
    protected String safeGetText(WebElement element) {
        return waitForElementVisible(element).getText();
    }

    /**
     * Check if element is displayed with wait
     */
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return waitForElementVisible(element).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get current page URL
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Get page title
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }
}