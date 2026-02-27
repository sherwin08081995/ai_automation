package com.yourcompany.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class Hooks {
    
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    
    @Before
    public void setUp() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        WebDriver driver;
        
        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-blink-features=AutomationControlled");
                options.addArguments("--disable-extensions");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                if (System.getProperty("headless", "false").equals("true")) {
                    options.addArguments("--headless");
                }
                driver = new ChromeDriver(options);
                break;
        }
        
        driver.manage().window().maximize();
        driverThreadLocal.set(driver);
    }
    
    @After
    public void tearDown() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
    }
    
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }
}