package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Utility class providing reusable helper methods for interacting with web elements.
 *
 * @author Sherwin
 * @since 17-06-2025
 */

public class ReusableHelperMethods {

    /**
     * Clicks the given web element.
     */
    public void click(WebElement element) {
        element.click();
    }

    /**
     * Performs a JavaScript-based click on the specified element.
     */
    public void jsClick(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Clears the existing text and types new text into an input field.
     */
    public void type(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Retrieves and returns trimmed visible text from a web element.
     */
    public String getText(WebElement element) {
        return element.getText().trim();
    }

    /**
     * Selects an option from a dropdown using visible text.
     */
    public void selectByVisibleText(WebElement element, String text) {
        new Select(element).selectByVisibleText(text);
    }

    /**
     * Selects an option from a dropdown using its value attribute.
     */
    public void selectByValue(WebElement element, String value) {
        new Select(element).selectByValue(value);
    }

    /**
     * Selects an option from a dropdown using its index.
     */
    public void selectByIndex(WebElement element, int index) {
        new Select(element).selectByIndex(index);
    }

    /**
     * Scrolls the specified element into the visible area of the browser window.
     */
    public void scrollToElement(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Moves the mouse pointer to hover over the given element.
     */
    public void hover(WebDriver driver, WebElement element) {
        new Actions(driver).moveToElement(element).perform();
    }

    /**
     * Returns true if the element is present and displayed on the page.
     */
    public boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Returns true if the element is enabled.
     */
    public boolean isElementEnabled(WebElement element) {
        return element.isEnabled();
    }

    /**
     * Returns true if the element is selected.
     */
    public boolean isElementSelected(WebElement element) {
        return element.isSelected();
    }

    /**
     * Returns a list of trimmed text values from a list of WebElements.
     */
    public List<String> getTextsFromElements(List<WebElement> elements) {
        return elements.stream().map(WebElement::getText).map(String::trim).toList();
    }

    /**
     * Accepts the currently active JavaScript alert.
     */
    public void acceptAlert(WebDriver driver) {
        driver.switchTo().alert().accept();
    }

    /**
     * Dismisses the currently active JavaScript alert.
     */
    public void dismissAlert(WebDriver driver) {
        driver.switchTo().alert().dismiss();
    }

    /**
     * Retrieves the text from the currently active alert popup.
     */
    public String getAlertText(WebDriver driver) {
        return driver.switchTo().alert().getText();
    }

    /**
     * Switches the WebDriver context to the specified frame element.
     */
    public void switchToFrame(WebDriver driver, WebElement frameElement) {
        driver.switchTo().frame(frameElement);
    }

    /**
     * Switches the WebDriver context back to the default content (main document).
     */
    public void switchToDefaultContent(WebDriver driver) {
        driver.switchTo().defaultContent();
    }

    /**
     * Performs a drag and drop action from the source element to the target element.
     */
    public void dragAndDrop(WebDriver driver, WebElement source, WebElement target) {
        new Actions(driver).dragAndDrop(source, target).perform();
    }
}