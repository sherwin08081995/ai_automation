package com.yourcompany.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor jsExecutor;
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.jsExecutor = (JavascriptExecutor) driver;
        PageFactory.initElements(driver, this);
    }
    
    protected void waitForElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    protected void waitForElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    protected void clickElement(WebElement element) {
        waitForElementToBeClickable(element);
        element.click();
    }
    
    protected String getElementText(WebElement element) {
        waitForElementToBeVisible(element);
        return element.getText().trim();
    }
    
    protected void scrollToElement(WebElement element) {
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }
    
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    protected void waitForPageToLoad() {
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
            .executeScript("return document.readyState").equals("complete"));
    }
}