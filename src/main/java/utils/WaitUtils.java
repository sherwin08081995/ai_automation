package utils;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;


/**
 * Utility class to handle explicit wait conditions in Selenium WebDriver.
 * Provides reusable wait methods for various element states and conditions.
 * Configurable via `explicitWait` value in the configuration file.
 *
 * @author Sherwin
 * @since 17-06-2025
 */

public class WaitUtils {

    private WebDriver driver;
    private WebDriverWait wait;
    Logger logger;

    /**
     * Initializes the WaitUtils with a configured timeout.
     */
    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        int timeout = Integer.parseInt(ConfigReader.get("explicitWait")); // configurable via properties
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    /**
     * Waits for the visibility of an element located by the given locator.
     */
    public WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits for the visibility of a specific WebElement.
     */
    public WebElement waitForVisibility(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Waits until an element located by the given locator becomes invisible.
     */
    public boolean waitForInvisibility(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Waits until the presence of an element located by the given locator.
     */
    public WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits until all elements matching the locator are present in the DOM.
     */
    public List<WebElement> waitForPresenceOfAllElements(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    /**
     * Waits until all elements matching the locator are visible.
     */
    public List<WebElement> waitForVisibilityOfAllElements(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    /**
     * Waits for an element to be clickable using a locator.
     */
    public WebElement waitForElementToBeClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits for a specific WebElement to become clickable.
     */
    public WebElement waitForElementToBeClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Waits until exactly the specified number of elements are present.
     */
    public List<WebElement> waitForNumberOfElements(By locator, int number) {
        return wait.until(ExpectedConditions.numberOfElementsToBe(locator, number));
    }

    /**
     * Waits until more than the specified number of elements are present.
     */
    public List<WebElement> waitForNumberOfElementsToBeMoreThan(By locator, int number) {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(locator, number));
        return driver.findElements(locator);
    }

    /**
     * Waits until fewer than the specified number of elements are present.
     */
    public List<WebElement> waitForNumberOfElementsToBeLessThan(By locator, int number) {
        wait.until(ExpectedConditions.numberOfElementsToBeLessThan(locator, number));
        return driver.findElements(locator);
    }

    /**
     * Waits for a JavaScript alert to be present.
     */
    public Alert waitForAlert() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    /**
     * Waits until the element contains the given text.
     */
    public boolean waitForTextToBePresent(By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     * Waits until the current URL contains the specified partial string.
     */
    public boolean waitForUrlContains(String partialUrl) {
        return wait.until(ExpectedConditions.urlContains(partialUrl));
    }

    /**
     * Waits until the current URL exactly matches the given expected URL.
     */
    public boolean waitForUrlToBe(String expectedUrl) {
        return wait.until(ExpectedConditions.urlToBe(expectedUrl));
    }

    /**
     * Waits until the page title contains the specified text.
     */
    public boolean waitForTitleContains(String partialTitle) {
        return wait.until(ExpectedConditions.titleContains(partialTitle));
    }

    /**
     * Waits until the page title exactly matches the given text.
     */
    public boolean waitForTitleToBe(String expectedTitle) {
        return wait.until(ExpectedConditions.titleIs(expectedTitle));
    }

    /**
     * Waits until the specified attribute contains a certain value.
     */
    public boolean waitForAttributeToContain(WebElement element, String attribute, String value) {
        return wait.until(ExpectedConditions.attributeContains(element, attribute, value));
    }

    /**
     * Waits until the specified attribute exactly matches a given value.
     */
    public boolean waitForAttributeToBe(By locator, String attribute, String value) {
        return wait.until(ExpectedConditions.attributeToBe(locator, attribute, value));
    }

    /**
     * Waits until an element is selected.
     */
    public boolean waitForElementToBeSelected(By locator) {
        return wait.until(ExpectedConditions.elementToBeSelected(locator));
    }

    /**
     * Waits for a frame to be available and switches the context to it.
     */
    public boolean waitForFrameAndSwitch(By locator) {
        return wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator)) != null;
    }

    /**
     * Retrieves the configured page load timeout in seconds.
     */
    public long getPageLoadTimeoutInSeconds() {
        return driver.manage().timeouts().getPageLoadTimeout().getSeconds();
    }


    /**
     * Waits until the specified condition is met or the timeout is reached.
     *
     * @param condition A function representing the condition to be evaluated.
     *                  Typically used for custom waits like checking element text, attribute, or state.
     */
    public void waitUntil(Function<WebDriver, Boolean> condition) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(condition);
    }

    public void waitForPageToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(
                webDriver -> ((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState").equals("complete")
        );
        // Add a small buffer wait if UI is animation-heavy
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }





}