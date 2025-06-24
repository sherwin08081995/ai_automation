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

public class Hooks {

    public static WebDriver driver;
    private static final Logger logger = LogManager.getLogger(Hooks.class);

    @Before
    public void setup(Scenario scenario) {
        if (System.getProperty("screenshots.cleared") == null) {
            ScreenshotUtils.clearScreenshotFolder();
            System.setProperty("screenshots.cleared", "true");
            logger.info("✅ Screenshot folder cleaned.");
        }

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        // 🔒 Force headless in CI environments for consistent rendering
        options.addArguments("--headless=chrome"); // ✅ Stable headless rendering
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080"); // ✅ Critical for full DOM
        options.addArguments("--force-device-scale-factor=1"); // ✅ Optional: clean render
        options.addArguments("--hide-scrollbars");
        options.addArguments("--remote-allow-origins=*");

        logger.info("🔧 Running in forced headless mode with resolution 1920x1080");

        driver = new ChromeDriver(options);

        // ✅ Ensure Chrome respects the correct viewport size
        driver.manage().window().setSize(new Dimension(1920, 1080));

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(Long.parseLong(ConfigReader.get("pageLoadTimeout"))));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Long.parseLong(ConfigReader.get("implicitWait"))));

        ExtentReports extent = ExtentReportManager.getInstance();
        ExtentTest test = extent.createTest(scenario.getName());
        ExtentTestManager.setTest(test);

        test.log(Status.INFO, "🚀 Browser launched for scenario: " + scenario.getName());
        logger.info("🚀 WebDriver setup complete for scenario: {}", scenario.getName());

        // Auto-login
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
            ScreenshotUtils.takeScreenshot(driver, screenshotName);

            String base64 = ScreenshotUtils.getBase64Screenshot(driver);
            ExtentTestManager.getTest().fail("❌ Scenario failed: " + scenario.getName(), MediaEntityBuilder.createScreenCaptureFromBase64String(base64, "Failure Screenshot").build());

            ScreenshotUtils.attachScreenshotToAllure(driver, screenshotName);
        }

        if (driver != null) {
            driver.quit();
            ExtentTestManager.getTest().log(Status.INFO, "🪚 Browser closed after scenario: " + scenario.getName());
            logger.info("🪚 Browser closed after scenario: {}", scenario.getName());
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
