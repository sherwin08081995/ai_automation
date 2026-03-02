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
    
    protected void waitForElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    protected void waitForElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    protected void click(WebElement element) {
        waitForElementToBeClickable(element);
        element.click();
    }
    
    protected void sendKeys(WebElement element, String text) {
        waitForElementToBeVisible(element);
        element.clear();
        element.sendKeys(text);
    }
    
    protected String getText(WebElement element) {
        waitForElementToBeVisible(element);
        return element.getText();
    }
    
    protected boolean isElementDisplayed(WebElement element) {
        try {
            waitForElementToBeVisible(element);
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}