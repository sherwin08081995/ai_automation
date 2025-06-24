package hooks;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import io.cucumber.java.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

public class Hooks {

    public static WebDriver driver;
    private static final Logger logger = LogManager.getLogger(Hooks.class);

    @Before
    public void setup(Scenario scenario) {
        // Clear screenshot folder only once per run
        if (System.getProperty("screenshots.cleared") == null) {
            ScreenshotUtils.clearScreenshotFolder();
            System.setProperty("screenshots.cleared", "true");
            logger.info("✅ Screenshot folder cleaned.");
        }

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        // Detect headless mode
        boolean isHeadless = Boolean.parseBoolean(System.getenv().getOrDefault("CHROME_HEADLESS", ConfigReader.get("headless")));

        if (isHeadless) {
            options.addArguments("--headless"); // ✅ Use classic headless for better CI stability
            options.addArguments("--disable-gpu");
            logger.info("🔧 Headless mode enabled via config/env.");
        }

        // Set consistent rendering size
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");

        driver = new ChromeDriver(options);

        // Maximize only in non-headless (headed) mode
        if (!isHeadless) {
            driver.manage().window().maximize();
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Long.parseLong(ConfigReader.get("pageLoadTimeout"))));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Long.parseLong(ConfigReader.get("implicitWait"))));

        // Extent report setup
        ExtentReports extent = ExtentReportManager.getInstance();
        ExtentTest test = extent.createTest(scenario.getName());
        ExtentTestManager.setTest(test);

        test.log(Status.INFO, "🚀 Browser launched for scenario: " + scenario.getName());
        logger.info("🚀 WebDriver setup complete for scenario: {}", scenario.getName());

        // Auto-login unless scenario name includes "login"
        if (!scenario.getName().toLowerCase().contains("login")) {
            performLogin();
        } else {
            test.log(Status.INFO, "🔍 Skipping login for login-related scenario");
            logger.info("🔍 Skipping login for login-related scenario.");
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        String scenarioName = scenario.getName().replace(" ", "_");

        if (scenario.isFailed()) {
            String screenshotName = "Failure_" + scenarioName;

            // Save for ExtentReports
            ScreenshotUtils.takeScreenshot(driver, screenshotName);

            // Attach to Extent (Base64)
            String base64 = ScreenshotUtils.getBase64Screenshot(driver);
            ExtentTestManager.getTest().fail("❌ Scenario failed: " + scenario.getName(), MediaEntityBuilder.createScreenCaptureFromBase64String(base64, "Failure Screenshot").build());

            // Attach to Allure
            ScreenshotUtils.attachScreenshotToAllure(driver, screenshotName);
        }

        if (driver != null) {
            driver.quit();
            ExtentTestManager.getTest().log(Status.INFO, "🧹 Browser closed after scenario: " + scenario.getName());
            logger.info("🧹 Browser closed after scenario: {}", scenario.getName());
        }

        ExtentTestManager.removeTest();
    }

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
        logger.info("🔐 Login successful.");
    }
}
