package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class ReusableHelperMethods {

    // Click element
    public static void click(WebElement element) {
        element.click();
    }

    // JavaScript click
    public static void jsClick(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    // Type text into input field
    public static void type(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    // Get text from element
    public static String getText(WebElement element) {
        return element.getText().trim();
    }

    // Select dropdown by visible text
    public static void selectByVisibleText(WebElement element, String text) {
        new Select(element).selectByVisibleText(text);
    }

    // Select dropdown by value
    public static void selectByValue(WebElement element, String value) {
        new Select(element).selectByValue(value);
    }

    // Select dropdown by index
    public static void selectByIndex(WebElement element, int index) {
        new Select(element).selectByIndex(index);
    }

    // Scroll to element
    public static void scrollToElement(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // Hover over element
    public static void hover(WebDriver driver, WebElement element) {
        new Actions(driver).moveToElement(element).perform();
    }

    // Check if element is displayed
    public static boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // Check if element is enabled
    public static boolean isElementEnabled(WebElement element) {
        return element.isEnabled();
    }

    // Check if element is selected
    public static boolean isElementSelected(WebElement element) {
        return element.isSelected();
    }

    // Get list of texts from elements
    public static List<String> getTextsFromElements(List<WebElement> elements) {
        return elements.stream().map(WebElement::getText).toList();
    }

    // Accept alert
    public static void acceptAlert(WebDriver driver) {
        driver.switchTo().alert().accept();
    }

    // Dismiss alert
    public static void dismissAlert(WebDriver driver) {
        driver.switchTo().alert().dismiss();
    }

    // Get alert text
    public static String getAlertText(WebDriver driver) {
        return driver.switchTo().alert().getText();
    }

    // Switch to frame by element
    public static void switchToFrame(WebDriver driver, WebElement frameElement) {
        driver.switchTo().frame(frameElement);
    }

    // Switch back to default content
    public static void switchToDefaultContent(WebDriver driver) {
        driver.switchTo().defaultContent();
    }

    // Drag and drop
    public static void dragAndDrop(WebDriver driver, WebElement source, WebElement target) {
        new Actions(driver).dragAndDrop(source, target).perform();
    }
}
