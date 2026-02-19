package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class providing reusable helper methods for interacting with web elements.
 *
 * @author Sherwin
 * @since 17-06-2025
 */

public class SeleniumHelperMethods {

    /**
     * Clicks the given web element with validation.
     */
    public void click(WebElement element) {
        if (element == null) {
            throw new IllegalArgumentException("❌ Cannot click: Element is null.");
        }
        if (!element.isDisplayed() || !element.isEnabled()) {
            throw new IllegalStateException("❌ Cannot click: Element is not visible or not enabled.");
        }
        element.click();
    }

    /**
     * Performs a JavaScript-based click on the specified element.
     */
    public void jsClick(WebDriver driver, WebElement element) {
        if (element == null) {
            throw new IllegalArgumentException("❌ Cannot JS click: Element is null.");
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Clears the existing text and types new text into an input field.
     */
    public void type(WebElement element, String text) {
        if (element == null) {
            throw new IllegalArgumentException("❌ Cannot type: Element is null.");
        }
        if (!element.isDisplayed() || !element.isEnabled()) {
            throw new IllegalStateException("❌ Cannot type: Element is not visible or not enabled.");
        }
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Retrieves and returns trimmed visible text from a web element.
     */
    public String getText(WebElement element) {
        if (element == null) {
            throw new IllegalArgumentException("❌ Cannot get text: Element is null.");
        }
        return element.getText().trim();
    }

    /**
     * Scrolls the specified element into the visible area of the browser window.
     */
    public void scrollToElement(WebDriver driver, WebElement element) {
        if (element == null) {
            throw new IllegalArgumentException("❌ Cannot scroll: Element is null.");
        }
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    public void scrollIntoView(WebDriver driver, WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center',inline:'center'});", el);
    }


    /**
     * Hovers the mouse over the specified web element.
     */
    public void hover(WebDriver driver, WebElement element) {
        if (element == null) {
            throw new IllegalArgumentException("❌ Cannot hover: Element is null.");
        }
        new Actions(driver).moveToElement(element).perform();
    }

    /**
     * Returns true if the element is present and displayed on the page.
     */
    public boolean isElementDisplayed(WebElement element) {
        try {
            return element != null && element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Returns true if the element is enabled.
     */
    public boolean isElementEnabled(WebElement element) {
        if (element == null) {
            return false;
        }
        return element.isEnabled();
    }

    /**
     * Returns true if the element is selected.
     */
    public boolean isElementSelected(WebElement element) {
        if (element == null) {
            return false;
        }
        return element.isSelected();
    }

    /**
     * Returns a list of trimmed text values from a list of WebElements.
     */
    public List<String> getTextsFromElements(List<WebElement> elements) {
        if (elements == null) {
            throw new IllegalArgumentException("❌ Elements list is null.");
        }
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
        if (frameElement == null) {
            throw new IllegalArgumentException("❌ Frame element is null.");
        }
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
        if (source == null || target == null) {
            throw new IllegalArgumentException("❌ Source or target element is null.");
        }
        new Actions(driver).dragAndDrop(source, target).perform();
    }

    /**
     * Selects a dropdown option by visible text.
     */
    public void selectByVisibleText(WebElement dropdownElement, String visibleText) {
        validateSelectElement(dropdownElement);
        new Select(dropdownElement).selectByVisibleText(visibleText);
    }

    /**
     * Selects a dropdown option by value attribute.
     */
    public void selectByValue(WebElement dropdownElement, String value) {
        validateSelectElement(dropdownElement);
        new Select(dropdownElement).selectByValue(value);
    }

    /**
     * Selects a dropdown option by index.
     */
    public void selectByIndex(WebElement dropdownElement, int index) {
        validateSelectElement(dropdownElement);
        new Select(dropdownElement).selectByIndex(index);
    }

    /**
     * Returns the currently selected option's visible text.
     */
    public String getSelectedOptionText(WebElement dropdownElement) {
        validateSelectElement(dropdownElement);
        return new Select(dropdownElement).getFirstSelectedOption().getText().trim();
    }

    /**
     * Returns all available options' visible texts from the dropdown.
     */
    public List<String> getAllDropdownOptionsText(WebElement dropdownElement) {
        validateSelectElement(dropdownElement);
        List<String> optionsText = new ArrayList<>();
        List<WebElement> options = new Select(dropdownElement).getOptions();
        for (WebElement option : options) {
            optionsText.add(option.getText().trim());
        }
        return optionsText;
    }

    /**
     * Checks if the dropdown allows multiple selection.
     */
    public boolean isMultipleSelection(WebElement dropdownElement) {
        validateSelectElement(dropdownElement);
        return new Select(dropdownElement).isMultiple();
    }

    /**
     * Returns the first selected option element from a <select> dropdown.
     */
    public WebElement getFirstSelectedOption(WebElement dropdownElement) {
        validateSelectElement(dropdownElement);
        return new Select(dropdownElement).getFirstSelectedOption();
    }

    /**
     * Validates that the WebElement is a non-null <select> element.
     */
    private void validateSelectElement(WebElement element) {
        if (element == null) {
            throw new IllegalArgumentException("❌ Dropdown element is null.");
        }
        if (!"select".equalsIgnoreCase(element.getTagName())) {
            throw new IllegalArgumentException("❌ Provided element is not a <select> dropdown.");
        }
    }
}
