package com.yourcompany.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.time.Duration;

public class Hooks {
    
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    
    @Before
    public void setUp() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        WebDriver driver = createDriver(browser);
        
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driverThreadLocal.set(driver);
    }
    
    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = driverThreadLocal.get();
        
        if (driver != null) {
            if (scenario.isFailed()) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Screenshot");
            }
            
            driver.quit();
            driverThreadLocal.remove();
        }
    }
    
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }
    
    private WebDriver createDriver(String browser) {
        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--no-sandbox");
                return new ChromeDriver(chromeOptions);
                
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                return new FirefoxDriver();
                
            case "edge":
                WebDriverManager.edgedriver().setup();
                return new EdgeDriver();
                
            default:
                throw new IllegalArgumentException("Browser not supported: " + browser);
        }
    }
}