package com.yourcompany.hooks;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/**
 * Cucumber hooks for WebDriver setup and teardown.
 * Uses ThreadLocal to support parallel execution.
 */
public class Hooks {
    
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final String BROWSER = System.getProperty("browser", "chrome");
    private static final String BASE_URL = System.getProperty("baseUrl", "https://grc.vakilsearch.com");
    
    @Before
    public void setUp() {
        WebDriver driver = createDriver();
        driverThreadLocal.set(driver);
        
        // Navigate to base URL
        driver.get(BASE_URL);
        driver.manage().window().maximize();
    }
    
    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = getDriver();
        
        if (driver != null) {
            // Take screenshot on failure
            if (scenario.isFailed()) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Screenshot");
            }
            
            driver.quit();
            driverThreadLocal.remove();
        }
    }
    
    /**
     * Get WebDriver instance for current thread
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }
    
    /**
     * Create WebDriver based on browser property
     */
    private WebDriver createDriver() {
        WebDriver driver;
        
        switch (BROWSER.toLowerCase()) {
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
                
                // Add Chrome options for stability
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-gpu");
                options.addArguments("--window-size=1920,1080");
                
                // Run headless in CI environment
                if (System.getProperty("headless", "false").equals("true")) {
                    options.addArguments("--headless");
                }
                
                driver = new ChromeDriver(options);
                break;
        }
        
        return driver;
    }
}