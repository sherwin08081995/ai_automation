package hooks;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.Status;
import io.cucumber.java.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import pages.HomePage;
import pages.LoginPage;
import utils.*;

import java.time.Duration;

/**
 * Hooks.java
 * <p>
 * Purpose:
 * This class contains Cucumber Hooks for setting up and tearing down the WebDriver
 * before and after each test scenario. It supports:
 * <p>
 * ✅ WebDriver initialization with Chrome (headless or headed)
 * ✅ Screenshot folder cleanup (once per test run)
 * ✅ Page timeouts and window sizing
 * ✅ Auto-login before non-login scenarios
 * ✅ ExtentReports & Allure reporting integration
 * ✅ Screenshot capture and embedding for failed scenarios
 * <p>
 * Configuration-driven: Uses ConfigReader to pull values for:
 * - headless mode
 * - timeouts
 * - base URL
 * - login credentials (email, OTP)
 * <p>
 * Associated Utilities:
 * - ScreenshotUtils: Folder cleanup, capture, Allure attachment
 * - ExtentReportManager & ExtentTestManager: Reporting
 * - ConfigReader: Loads config from properties file
 * - LoginPage & HomePage: Page Object Model (POM) for login automation
 * <p>
 * Usage:
 * - Automatically invoked before and after each Cucumber scenario
 * - Add this class in your Cucumber glue path
 *
 * @author Sherwin
 * @since 17-06-2025
 */


public class Hooks {

    public static WebDriver driver;
    private static final Logger logger = LogManager.getLogger(Hooks.class);

    static {
        // Create Allure environment.properties once before all tests
        AllureEnvironmentWriter.createEnvironmentFile();
    }

    @Before
    public void setup(Scenario scenario) {
        // Run once per test execution
        if (System.getProperty("init.once") == null) {
            ScreenshotUtils.clearScreenshotFolder();
            AllureTrendUtils.preserveTrendHistory();
            AllureEnvironmentWriter.createEnvironmentFile();

            System.setProperty("init.once", "true");
            logger.info("✅ One-time setup done: screenshots, trend, environment file created.");
        }

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        String headless = System.getProperty("headless", ConfigReader.get("headless"));

        if (Boolean.parseBoolean(headless)) {
            options.addArguments("--headless=chrome");
            logger.info("🔧 Running in headless mode (system or config).");
        } else {
            logger.info("🖥️ Running in visible (headed) mode.");
        }

        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--force-device-scale-factor=1");
        options.addArguments("--hide-scrollbars");
        options.addArguments("--remote-allow-origins=*");

        logger.info("🔧 ChromeOptions set for 1920x1080 headless/visual run");

        driver = new ChromeDriver(options);
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Long.parseLong(ConfigReader.get("pageLoadTimeout"))));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Long.parseLong(ConfigReader.get("implicitWait"))));

        logger.info("🚀 WebDriver setup complete for scenario: {}", scenario.getName());

        // Auto-login for non-login scenarios
        if (!scenario.getName().toLowerCase().contains("login")) {
            performLogin();
        } else {
            logger.info("🔍 Skipping login for login-related scenario.");
        }
    }



    @After
    public void tearDown(Scenario scenario) {
        String scenarioName = scenario.getName().replace(" ", "_");

        if (scenario.isFailed()) {
            String screenshotName = "Failure_" + scenarioName;
            ScreenshotUtils.takeScreenshot(driver, screenshotName);

            String base64 = ScreenshotUtils.getBase64Screenshot(driver);

            ScreenshotUtils.attachScreenshotToAllure(driver, screenshotName);
        }

        if (driver != null) {
            driver.quit();
            logger.info("🪚 Browser closed after scenario: {}", scenario.getName());
        }

    }


    /**
     * Performs automated login before executing test scenarios.
     *
     * Notes:
     * - This method is called only for non-login scenarios to avoid redundancy.
     * - Throws an IllegalStateException if login verification fails.
     * - Logs status in both ExtentReports and Log4j.`.
     */


    private void performLogin() {
        driver.get(ConfigReader.get("baseUrl"));
        logger.info("🌐 Navigated to: {}", ConfigReader.get("baseUrl"));

        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterEmail(ConfigReader.get("email"));
//        loginPage.clickLoginWithOtpButton();
        loginPage.clickGetOtpButton();
        loginPage.enterOtp(ConfigReader.get("otp"));

        HomePage homePage = new HomePage(driver);
        boolean success = homePage.isLoginSuccessful("Vakilsearch");

        if (!success) {
            logger.error("❌ Login failed during setup.");
            throw new IllegalStateException("Login failed during setup");
        }

        logger.info("🔐 Login successful.");
    }
}
