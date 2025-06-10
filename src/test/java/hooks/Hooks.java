package hooks;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.ConfigReader;
import utils.ExtentReportManager;
import utils.ExtentTestManager;
import utils.ScreenshotUtils;

import java.time.Duration;

/**
 * @author Sherwin
 * @since 09-06-2025
 */
public class Hooks {
    public static WebDriver driver;

    @Before
    public void setup(Scenario scenario) {
        // Clear screenshots once per test run
        if (System.getProperty("screenshots.cleared") == null) {
            ScreenshotUtils.clearScreenshotFolder();
            System.setProperty("screenshots.cleared", "true");
        }

        WebDriverManager.chromedriver().setup();

        // Decide between headless and headed based on config
        String headlessConfig = ConfigReader.get("headless");
        boolean isHeadless = headlessConfig != null && headlessConfig.equalsIgnoreCase("true");

        ChromeOptions options = new ChromeOptions();

        if (isHeadless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080"); // Ensure visibility in headless
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--user-data-dir=/tmp/chrome-profile");
        }

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Set timeouts from config
        int pageLoad = Integer.parseInt(ConfigReader.get("pageLoadTimeout"));
        int implicit = Integer.parseInt(ConfigReader.get("implicitWait"));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoad));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicit));

        // Setup ExtentReports for current scenario
        ExtentReports extent = ExtentReportManager.getInstance();
        ExtentTest extentTest = extent.createTest(scenario.getName());
        ExtentTestManager.setTest(extentTest);

        extentTest.log(Status.INFO, "Browser launched with " + (isHeadless ? "headless" : "headed") + " Chrome");
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            ExtentTestManager.getTest().log(Status.INFO, "Browser closed");
        }

        ExtentTestManager.removeTest();
    }
}
