package hooks;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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

        // Clear screenshot folder once per test run
        if (System.getProperty("screenshots.cleared") == null) {
            ScreenshotUtils.clearScreenshotFolder();
            System.setProperty("screenshots.cleared", "true");
        }

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        // Set timeouts from config
        int pageLoad = Integer.parseInt(ConfigReader.get("pageLoadTimeout"));
        int implicit = Integer.parseInt(ConfigReader.get("implicitWait"));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoad));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicit));

        // Create ExtentReports test using scenario name
        ExtentReports extent = ExtentReportManager.getInstance();
        ExtentTest extentTest = extent.createTest(scenario.getName());

        // Set test in ThreadLocal for reuse in step definitions
        ExtentTestManager.setTest(extentTest);

        extentTest.log(Status.INFO, "Browser launched and maximized");
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            ExtentTestManager.getTest().log(Status.INFO, "Browser closed");
        }

        // Optional cleanup
        ExtentTestManager.removeTest();
    }
//    @AfterAll
//    public static void afterAll() {
//        ExtentReportManager.getInstance().flush();
//    }
}
