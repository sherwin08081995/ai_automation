package hooks;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import io.cucumber.java.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.HomePage;
import pages.LoginPage;
import utils.ConfigReader;
import utils.ExtentReportManager;
import utils.ExtentTestManager;
import utils.ScreenshotUtils;

import java.time.Duration;

/**
 * Hooks class manages setup and teardown activities for each Cucumber scenario.
 * It includes WebDriver initialization, login, report logging, and cleanup tasks.
 *
 * @author Sherwin
 * @since 09-06-2025
 */


public class Hooks {
    public static WebDriver driver;
    private static final Logger logger = LogManager.getLogger(Hooks.class);

    /**
     * Sets up the WebDriver and test environment before each scenario.
     * - Clears screenshots once per test run
     * - Launches browser with configured options
     * - Initializes reporting
     * - Performs login unless scenario name includes 'login'
     *
     * @param scenario The Cucumber scenario about to be executed
     */

    @Before
    public void setup(Scenario scenario) {
        if (System.getProperty("screenshots.cleared") == null) {
            ScreenshotUtils.clearScreenshotFolder();
            System.setProperty("screenshots.cleared", "true");
            logger.info("✅ Screenshot folder cleaned.");
        }

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        // 🧩 Load headless flags from environment if provided
        String extraFlags = System.getenv("CHROME_HEADLESS_FLAGS");
        if (extraFlags != null && !extraFlags.isBlank()) {
            for (String arg : extraFlags.trim().split("\\s+")) {
                options.addArguments(arg);
            }
            logger.info("🧩 Applied CHROME_HEADLESS_FLAGS from environment: " + extraFlags);
        } else {
            // 🔁 Fallback to CHROME_HEADLESS boolean flag
            boolean isHeadless = Boolean.parseBoolean(System.getenv().getOrDefault("CHROME_HEADLESS", ConfigReader.get("headless")));
            if (isHeadless) {
                options.addArguments("--headless");         // ✅ Legacy headless mode for AShot
                options.addArguments("--disable-gpu");
                logger.info("🔧 Legacy headless mode enabled (default fallback).");
            }
        }

        // 💻 Recommended for CI
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);

        // 🔧 Enforce size (sometimes ignored in headless)
        driver.manage().window().setSize(new Dimension(1920, 1080));

        // ⏱️ Timeouts
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Long.parseLong(ConfigReader.get("pageLoadTimeout"))));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Long.parseLong(ConfigReader.get("implicitWait"))));

        // 📝 Reporting
        ExtentReports extent = ExtentReportManager.getInstance();
        ExtentTest test = extent.createTest(scenario.getName());
        ExtentTestManager.setTest(test);

        test.log(Status.INFO, "🚀 Browser launched for scenario: " + scenario.getName());
        logger.info("🚀 WebDriver setup complete for scenario: {}", scenario.getName());

        // 🔐 Auto-login unless login page
        if (!scenario.getName().toLowerCase().contains("login")) {
            performLogin();
        } else {
            test.log(Status.INFO, "🔍 Skipping login for Login Page validation scenario");
            logger.info("🔍 Skipping login for login-related scenario.");
        }
    }





    /**
     * Tears down the WebDriver and ends reporting after each scenario.
     * - Closes the browser
     * - Logs closure into the report
     * - Removes test from ExtentTestManager
     *
     * @param scenario The Cucumber scenario just executed
     */

    @After
    public void tearDown(Scenario scenario) {

        if (scenario.isFailed()) {
            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "Failure_" + scenario.getName().replace(" ", "_"));
            ExtentTestManager.getTest().fail("❌ Scenario failed: " + scenario.getName(),
                    MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
        }

        if (driver != null) {
            driver.quit();
            ExtentTestManager.getTest().log(Status.INFO, "🧹 Browser closed for scenario: " + scenario.getName());
            logger.info("🧹 Browser closed after scenario: {}", scenario.getName());
        }
        ExtentTestManager.removeTest();
    }

    /**
     * Performs application login before each non-login scenario.
     * Navigates to base URL and logs in using credentials from the config file.
     * Fails the test if login verification fails.
     */

    private void performLogin() {
        driver.get(ConfigReader.get("baseUrl"));
        logger.info("🌐 Navigated to: {}", ConfigReader.get("baseUrl"));

        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterEmail(ConfigReader.get("email"));
        loginPage.clickOtpButton();
        loginPage.enterOtp(ConfigReader.get("otp"));

        HomePage homePage = new HomePage(driver);
        boolean success = homePage.isLoginSuccessful("Vakilsearch");

        if (!success) {
            logger.error("❌ Login failed during setup.");
            throw new IllegalStateException("Login failed during setup");
        }

        ExtentTestManager.getTest().log(Status.INFO, "🔐 User logged in successfully before test begins");
        logger.info("🔐 Login successfully performed via @Before hook.");
    }

    @AfterStep
    public void attachScreenshotOnFailure(Scenario scenario) {
        if (scenario.isFailed()) {
            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, scenario.getName());
            ExtentTestManager.getTest().fail("❌ Step failed: " + scenario.getName())
                    .addScreenCaptureFromPath(screenshotPath);
        }
    }


}



