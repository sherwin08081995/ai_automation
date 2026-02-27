package com.yourcompany.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public abstract class BasePage {
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }
    
    protected void waitForElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    protected void waitForElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    protected void clickElement(WebElement element) {
        waitForElementToBeClickable(element);
        element.click();
    }
    
    protected void enterText(WebElement element, String text) {
        waitForElementToBeVisible(element);
        element.clear();
        element.sendKeys(text);
    }
    
    protected String getElementText(WebElement element) {
        waitForElementToBeVisible(element);
        return element.getText();
    }
    
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    protected boolean isElementEnabled(WebElement element) {
        try {
            waitForElementToBeVisible(element);
            return element.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }
}