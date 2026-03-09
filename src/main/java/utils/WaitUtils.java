package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.DocumentPage;

import java.util.function.Supplier;
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

    private final WebDriver driver;
    private final WebDriverWait wait;
    private static final Logger logger = LogManager.getLogger(WaitUtils.class);



    /**
     * Initializes the WaitUtils with a configured timeout.
     */
    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        int timeout = 10;
        try { timeout = Integer.parseInt(ConfigReader.get("explicitWait")); } catch (Exception ignore) {}
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
     * Waits for the visibility of a specific WebElement, with a custom timeout.
     */
    public WebElement waitForVisibilitywithacustomtimeout(WebElement element, Duration timeout) {
        WebDriverWait customWait = new WebDriverWait(driver, timeout);
        return customWait.until(ExpectedConditions.visibilityOf(element));
    }


    /**
     * Waits for the visibility of an element located by the given locator,
     * using a custom timeout instead of the default configured value.
     */

    public WebElement waitForVisibilityCustomTimeOut(By locator, Duration timeout) {
        WebDriverWait customWait = new WebDriverWait(driver, timeout);
        return customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }


    /**
     * Waits until an element located by the given locator becomes invisible.
     */
    public boolean waitForInvisibility(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Waits until an element located by the given WebELement becomes invisible.
     */
    public boolean waitForInvisibility(WebElement element) {
        return wait.until(ExpectedConditions.invisibilityOf(element));
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

    public void waitForVisibilityOfAllElements(List<WebElement> elements) {
        wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    /**
     * Waits for an element to be clickable using a locator.
     */
    public WebElement waitForElementToBeClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void waitForInvisibilityOfElement(By locator, int timeoutSeconds) {
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        w.until(ExpectedConditions.invisibilityOfElementLocated(locator));
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

    public boolean waitForTextToBePresent(WebElement element, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
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

    /**
     * Waits until the given WebElement is no longer attached to the DOM.
     * Returns true if it became stale within the timeout, false otherwise.
     */
    public boolean waitForStaleness(WebElement element) {
        try {
            return wait.until(ExpectedConditions.stalenessOf(element));
        } catch (TimeoutException e) {
            logger.warn("⚠ Timeout waiting for element to become stale: {}", element);
            return false;
        } catch (Exception e) {
            logger.error("❌ Error while waiting for element staleness: {}", e.getMessage());
            throw e;
        }
    }


    public void waitForPageToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public void waitForDocumentReady() {
        WebDriverWait jsWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        jsWait.until(webDriver -> {
            String state = ((JavascriptExecutor) webDriver)
                    .executeScript("return document.readyState")
                    .toString();
            return state.equals("complete");
        });
    }


    public void waitForUrlNotContains(String partialUrl, int timeoutInSeconds) {
        WebDriverWait urlWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        urlWait.until(ExpectedConditions.not(ExpectedConditions.urlContains(partialUrl)));
    }


    /** Use the default configured timeout */
    public <T> T until(ExpectedCondition<T> condition) {
        return wait.until(condition);
    }

    /** Use a custom timeout ad-hoc */
    public <T> T until(ExpectedCondition<T> condition, Duration timeout) {
        return new WebDriverWait(driver, timeout).until(condition);
    }

    /**
     * Waits until the given condition is true or times out.
     *
     * @param condition       A lambda that returns true when the condition is met
     * @param timeoutMillis   How long to wait in milliseconds
     * @param pollMillis      How often to poll in milliseconds
     * @param failureMessage  Message included in the AssertionError if timeout occurs
     */
    public static void waitUntilTrue(Supplier<Boolean> condition,
                                     long timeoutMillis,
                                     long pollMillis,
                                     String failureMessage) {
        long end = System.currentTimeMillis() + timeoutMillis;
        Throwable lastError = null;
        while (System.currentTimeMillis() < end) {
            try {
                if (Boolean.TRUE.equals(condition.get())) {
                    return;
                }
            } catch (Throwable t) {
                lastError = t;
            }
            try {
                Thread.sleep(pollMillis);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        throw new AssertionError(failureMessage, lastError);
    }

    /** Visibility with refreshed() and custom timeout */
    public WebElement waitForVisibilityRefreshed(WebElement element, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOf(element)));
    }

    /** Visibility with refreshed() and default timeout */
    public WebElement waitForVisibilityRefreshed(WebElement element) {
        return wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOf(element)));
    }

    /** Clickable with refreshed() and custom timeout */
    public WebElement waitForClickableRefreshed(WebElement element, Duration timeout) {
        return new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(element)));
    }

    /** Clickable with refreshed() and default timeout */
    public WebElement waitForClickableRefreshed(WebElement element) {
        return wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(element)));
    }

    /** Returns true when element becomes displayed (refreshed), false on timeout */
    public boolean waitUntilDisplayedRefreshed(WebElement element, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOf(element)));
            return element.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void waitForNumberOfWindowsToBe(final int expectedCount) {
        wait.until(ExpectedConditions.numberOfWindowsToBe(expectedCount));
    }


    public void waitForUrlToChange(String oldUrl) {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> !d.getCurrentUrl().equals(oldUrl));
    }

    /**
     * Safely checks if an element is displayed within the given timeout.
     *
     * @param locator  By locator
     * @param timeoutSeconds max wait time in seconds
     * @return true if displayed, false if not found or not visible
     */
    public boolean isElementDisplayed(By locator, int timeoutSeconds) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            WebElement el = shortWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return el.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Waits for either:
     *  - The URL to change from the given oldUrl, OR
     *  - A specific element to become visible.
     *
     * @param oldUrl the URL before navigation
     * @param locator locator of an element that identifies the target page
     * @param timeoutSeconds max wait time
     * @return true if URL changed OR element became visible; false otherwise
     */
    public boolean waitForEitherUrlChangeOrElement(String oldUrl, By locator, int timeoutSeconds) {
        logger.info("⏳ Waiting for either URL change from '{}' OR visibility of element: {}",
                oldUrl, locator);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));

        try {
            Boolean result = wait.until(d -> {
                // 1) URL changed?
                try {
                    String current = d.getCurrentUrl();
                    if (!current.equals(oldUrl)) {
                        logger.info("🔄 URL changed: {} → {}", oldUrl, current);
                        return true;
                    }
                } catch (Exception ignored) {}

                // 2) Target element visible?
                try {
                    WebElement el = d.findElement(locator);
                    if (el != null && el.isDisplayed()) {
                        logger.info("👀 Target element now visible for locator: {}", locator);
                        return true;
                    }
                } catch (Exception ignored) {}

                return false; // keep waiting
            });

            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            logger.warn("⚠️ Neither URL changed nor element became visible within {} seconds", timeoutSeconds);
            return false;
        }
    }


    /**
     * Waits for an element to be present in the DOM (located by By locator)
     * and returns the WebElement when found.
     *
     * @param locator            By locator of the element
     * @param timeoutInSeconds   max wait time
     * @return WebElement once located
     */
    public WebElement waitForElementLocated(By locator, int timeoutInSeconds) {
        try {
            WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return webDriverWait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception e) {
            logger.error("❌ Element not located within timeout. Locator: {}", locator, e);
            throw e;
        }
    }


    /**
     * Waits for all elements matching the locator and returns list.
     */
    public List<WebElement> waitForElementsLocated(By locator, int timeoutSec) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSec))
                .until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }


}


