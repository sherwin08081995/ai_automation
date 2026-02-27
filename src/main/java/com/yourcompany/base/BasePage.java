package com.yourcompany.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import java.time.Duration;

/**
 * Base Page class with common WebDriver utilities and wait methods.
 * All page objects should extend this class.
 */
public abstract class BasePage {
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected JavascriptExecutor jsExecutor;
    
    private static final int DEFAULT_WAIT_TIMEOUT = 15;
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }
    
    /**
     * Wait for element to be visible and clickable
     */
    protected WebElement waitForElementToBeClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    /**
     * Wait for element to be visible
     */
    protected WebElement waitForElementToBeVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    /**
     * Safe click method with wait
     */
    protected void safeClick(WebElement element) {
        waitForElementToBeClickable(element).click();
    }
    
    /**
     * Safe text input method with wait
     */
    protected void safeType(WebElement element, String text) {
        WebElement visibleElement = waitForElementToBeVisible(element);
        visibleElement.clear();
        visibleElement.sendKeys(text);
    }
    
    /**
     * Get text from element with wait
     */
    protected String getElementText(WebElement element) {
        return waitForElementToBeVisible(element).getText();
    }
    
    /**
     * Check if element is displayed
     */
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Scroll to element
     */
    protected void scrollToElement(WebElement element) {
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }
    
    /**
     * Wait for page title to contain text
     */
    protected boolean waitForTitleContains(String titleText) {
        return wait.until(ExpectedConditions.titleContains(titleText));
    }
    
    /**
     * Get current page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }
    
    /**
     * Get current page URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}