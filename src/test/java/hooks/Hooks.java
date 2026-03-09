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
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.v139.browser.Browser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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
    public void setup(Scenario scenario) throws InterruptedException {
        // ---- one-time project bootstrapping
        if (System.getProperty("init.once") == null) {
            ScreenshotUtils.clearScreenshotFolder();
            AllureTrendUtils.preserveTrendHistory();
            AllureEnvironmentWriter.createEnvironmentFile();
            System.setProperty("init.once", "true");
            logger.info("✅ One-time setup done: screenshots, trend, environment file created.");
        }

        if (scenario.getSourceTagNames().contains("@compatibility")) {
            logger.info("🔧 Compatibility scenario detected — skipping default Chrome setup & auto-login.");
            return;
        }

        WebDriverManager.chromedriver().setup();

        // ---- downloads dir (native absolute path, Windows-safe)
        Path downloadDirPath = Paths.get(System.getProperty("user.dir"), "downloads");
        try { Files.createDirectories(downloadDirPath); } catch (IOException ignored) {}
        String downloadDir = downloadDirPath.toAbsolutePath().toString();

        System.setProperty("download.dir", downloadDir);
        logger.info("📂 Using download dir: {}", downloadDir);


        // ---- clean downloads BEFORE any scenario runs
        try (Stream<Path> paths = Files.list(downloadDirPath)) {
            paths.filter(Files::isRegularFile).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (IOException ignored) {}
            });
            logger.info("🧹 Download folder cleaned: {}", downloadDir);
        } catch (IOException e) {
            logger.warn("⚠️ Failed to clean download folder: {}", e.getMessage());
        }

        // ---- clean downloads BEFORE any scenario runs
        try (Stream<Path> paths = Files.list(downloadDirPath)) {
            paths.filter(Files::isRegularFile).forEach(p -> {
                try { Files.deleteIfExists(p); } catch (IOException ignored) {}
            });
            logger.info("🧹 Download folder cleaned: {}", downloadDir);
        } catch (IOException e) {
            logger.warn("⚠️ Failed to clean download folder: {}", e.getMessage());
        }

        // ---- Chrome options & prefs
        ChromeOptions options = new ChromeOptions();

        String headless = System.getProperty("headless", ConfigReader.get("headless"));

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadDir);             // absolute native path
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("profile.default_content_setting_values.automatic_downloads", 1);
        prefs.put("safebrowsing.enabled", true);
        options.setExperimentalOption("prefs", prefs);

        if (Boolean.parseBoolean(headless)) {
            // If you don’t add DevTools, prefer old --headless to avoid download issues:
            // options.addArguments("--headless");
            options.addArguments("--headless=new");
            logger.info("🔧 Running in headless mode (system or config).");
        } else {
            logger.info("🖥️ Running in visible (headed) mode.");
        }

        options.addArguments(
                "--disable-gpu",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--window-size=1920,1080",
                "--force-device-scale-factor=1",
                "--hide-scrollbars",
                "--remote-allow-origins=*"
        );

        logger.info("🔧 ChromeOptions set for 1920x1080 headless/visual run");

        // ---- Create driver
        driver = new ChromeDriver(options);

        // ---- Allow downloads via DevTools (works in headless=new; harmless in headed)
        // Requires selenium-devtools-v139; adjust v### if your devtools artifact differs.
        try {
            HasDevTools devToolsDriver = (HasDevTools) driver;
            DevTools devTools = devToolsDriver.getDevTools();
            devTools.createSession();

            devTools.send(Browser.setDownloadBehavior(
                    Browser.SetDownloadBehaviorBehavior.ALLOW,
                    java.util.Optional.empty(),          // BrowserContextID (none)
                    java.util.Optional.of(downloadDir),  // your downloads folder
                    java.util.Optional.of(true)          // eventsEnabled
            ));
            logger.info("✅ DevTools download behavior set to ALLOW → {}", downloadDir);

        } catch (Throwable t) {
            // If the devtools module/version isn’t on classpath, we still proceed with prefs.
            logger.warn("⚠️ Could not set DevTools download behavior. Using Chrome prefs only. {}", t.toString());
        }

        // ---- window & timeouts
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(
                Long.parseLong(ConfigReader.get("pageLoadTimeout"))));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(
                Long.parseLong(ConfigReader.get("implicitWait"))));

        logger.info("🚀 WebDriver setup complete for scenario: {}", scenario.getName());

//        // ---- Auto-login for non-login scenarios
//        if (!scenario.getName().toLowerCase().contains("login")) {
//            performLogin();
//        } else {
//            logger.info("🔍 Skipping login for login-related scenario.");
//        }

        // ---- Auto-login for non-login scenarios
        boolean skipAutoLogin =
                scenario.getSourceTagNames().contains("@noAutoLogin") ||   // 👈 NEW
                        scenario.getName().toLowerCase().contains("login");

        if (!skipAutoLogin) {
            performLogin();
        } else {
            logger.info("🔍 Skipping pre-scenario login for scenario: {}", scenario.getName());
        }

    }


    @After
    public void tearDown(Scenario scenario) {
        String scenarioName = scenario.getName().replace(" ", "_");

        try {
            if (scenario.isFailed() && driver instanceof TakesScreenshot) {
                try {
                    String screenshotName = "Failure_" + scenarioName;
                    ScreenshotUtils.takeScreenshot(driver, screenshotName);
                    ScreenshotUtils.attachScreenshotToAllure(driver, screenshotName);
                } catch (WebDriverException e) {
                    logger.warn("Could not capture failure screenshot: {}", e.getMessage());
                }
            }
        } finally {
            try {
                if (driver != null) {
                    driver.quit();
                    logger.info("🪚 Browser closed after scenario: {}", scenario.getName());
                }
            } catch (Exception e) {
                logger.warn("Error during driver.quit(): {}", e.getMessage());
            }
        }
    }

    /**
     * Performs automated login before executing test scenarios.
     *
     * Notes:
     * - Called only for non-login scenarios to avoid redundancy.
     * - Throws IllegalStateException if login verification fails.
     * - Uses Log4j for detailed logs.
     */
    public static void performLogin() throws InterruptedException {
        final String ctx = "Pre-Scenario Login";
        final long t0 = System.currentTimeMillis();

        // --------- Read & validate config upfront ----------
        final String baseUrl = ConfigReader.get("baseUrl");
        final String mobNum  = ConfigReader.get("mobNum");
        final String otp     = ConfigReader.get("otp");
        final String email   = ConfigReader.get("email");

        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            logger.error("{}: missing Config 'baseUrl'.", ctx);
            throw new IllegalStateException("Config 'baseUrl' is required");
        }
        if (mobNum == null || mobNum.trim().isEmpty()) {
            logger.error("{}: missing Config 'mobNum'.", ctx);
            throw new IllegalStateException("Config 'mobNum' is required");
        }
        if (otp == null || otp.trim().isEmpty()) {
            logger.error("{}: missing Config 'otp'.", ctx);
            throw new IllegalStateException("Config 'otp' is required");
        }
        if (email == null || email.trim().isEmpty()) {
            logger.warn("{}: Config 'email' is empty. Chooser selection will be skipped if required.", ctx);
        }

        // --------- Navigate ----------
        try {
            driver.get(baseUrl);
            logger.info("🌐 {}: Navigated to {}", ctx, baseUrl);
        } catch (Exception e) {
            logger.error("💥 {}: navigation to '{}' failed: {}", ctx, baseUrl, e.toString(), e);
            throw e;
        }

        // Page objects
        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage   = new HomePage(driver);

        // --------- 1) Login steps ----------
        try {
            final long tEnterStart = System.currentTimeMillis();
            logger.info("✍️ {}: entering mobile/email: '{}'", ctx, maskForLogs(mobNum));
            loginPage.enterEmail(mobNum);
            logger.info("✅ {}: entered mobile/email in {} ms", ctx, (System.currentTimeMillis() - tEnterStart));
        } catch (Exception e) {
            logger.error("💥 {}: enterEmail failed: {}", ctx, e.toString(), e);
            throw e;
        }

        try {
            final long tOtpClickStart = System.currentTimeMillis();
            logger.info("🔘 {}: clicking Get OTP…", ctx);
            loginPage.clickGetOtpButton();
            logger.info("✅ {}: Get OTP clicked in {} ms", ctx, (System.currentTimeMillis() - tOtpClickStart));
        } catch (Exception e) {
            logger.error("💥 {}: clickGetOtpButton failed: {}", ctx, e.toString(), e);
            throw e;
        }

        try {
            final long tOtpEnterStart = System.currentTimeMillis();
            logger.info("🔐 {}: entering OTP ({} digits)…", ctx, otp.trim().length());
            loginPage.enterOtp(otp);
            logger.info("✅ {}: OTP entered in {} ms", ctx, (System.currentTimeMillis() - tOtpEnterStart));
        } catch (RuntimeException re) {
            logger.error("💥 {}: enterOtp failed: {}", ctx, re.getMessage(), re);
            throw re;
        } catch (Exception e) {
            logger.error("💥 {}: enterOtp unexpected failure: {}", ctx, e.toString(), e);
            throw e;
        }

        // --------- 2) Choose email only if chooser is open ----------
        try {
            if (loginPage.isChooserOpen()) {
                if (email == null || email.trim().isEmpty()) {
                    logger.error("⚠️ {}: chooser is open but Config 'email' is empty.", ctx);
                    throw new IllegalStateException("Chooser opened but 'email' was not provided");
                }
                final long tChooseStart = System.currentTimeMillis();
                logger.info("📮 {}: chooser visible → selecting email '{}'", ctx, maskForLogs(email));
                loginPage.selectEmailInChooser(email);
                logger.info("✅ {}: email selected in {} ms", ctx, (System.currentTimeMillis() - tChooseStart));
            } else {
                logger.info("📮 {}: chooser not open; likely already logged in.", ctx);
            }
        } catch (Exception e) {
            logger.error("💥 {}: email selection in chooser failed: {}", ctx, e.toString(), e);
            throw e;
        }

        // --------- 3) Close popup only if present (non-blocking) ----------
        try {
            if (loginPage.hasCloseIcon()) {
                logger.info("🧩 {}: popup detected after login → attempting to close…", ctx);
                long tClose = System.currentTimeMillis();
                try {
                    loginPage.closePopupIfPresent();
                    logger.info("✅ {}: popup closed in {} ms", ctx, (System.currentTimeMillis() - tClose));
                } catch (Exception e) {
                    logger.warn("⚠️ {}: failed to close popup (continuing): {}", ctx, e.getMessage());
                }
            } else {
                logger.info("🧩 {}: no popup detected after login.", ctx);
            }
        } catch (Exception e) {
            logger.warn("⚠️ {}: popup detection error (continuing): {}", ctx, e.toString());
        }

        // --------- 3a) Festive popup → Explore Service Hub (AFTER hasCloseIcon) ----------
        try {
            if (loginPage.isFestivePopupVisible()) {
                logger.info("🎉 {}: festive popup detected → clicking 'Explore Service Hub'.", ctx);

                long tHubStart = System.currentTimeMillis();
                loginPage.clickExploreServiceHubFromPopup();

                boolean atHub = loginPage.isOnServiceHubPage();
                long tHubMs = System.currentTimeMillis() - tHubStart;

                if (atHub) {
                    logger.info("✅ {}: Service Hub opened in {} ms. URL={}", ctx, tHubMs, safeGetUrl(driver));
                } else {
                    logger.error("❌ {}: Service Hub marker not visible after {} ms. URL={}", ctx, tHubMs, safeGetUrl(driver));
                    throw new IllegalStateException("Service Hub did not open as expected.");
                }

                // Return to previous page to continue setup flow
                driver.navigate().back();
                logger.info("↩️ {}: returned from Service Hub to previous page. URL={}", ctx, safeGetUrl(driver));
            } else {
                logger.info("🎉 {}: festive popup not present; continuing.", ctx);
            }
        } catch (IllegalStateException ise) {
            // Make this fail fast (per your step logic)
            throw ise;
        } catch (Exception e) {
            // Non-fatal to overall login unless you want to fail here
            logger.warn("⚠️ {}: festive popup handling error (continuing): {}", ctx, e.toString());
        }

        // --------- 4) Final verification ----------
        try {
            boolean success = homePage.isLoginSuccessful("Vakilsearch");
            if (!success) {
                logger.error("❌ {}: login verification failed on HomePage.", ctx);
                throw new IllegalStateException("Login failed during setup");
            }
            final long elapsed = System.currentTimeMillis() - t0;
            logger.info("🔐 {}: login successful. Total time: {} ms", ctx, elapsed);
        } catch (Exception e) {
            logger.error("💥 {}: final login verification failed: {}", ctx, e.toString(), e);
            throw e;
        }
    }

    /** Mask emails/mobiles in logs: first 2 chars + **** + domain or last 2 digits. */
    private static String maskForLogs(String s) {
        if (s == null) return "<null>";
        String v = s.trim();
        int at = v.indexOf('@');
        if (at > 0) {
            String prefix = v.substring(0, Math.min(2, at));
            String domain = v.substring(at); // includes '@'
            return prefix + "****" + domain;
        }
        if (v.length() <= 2) return "**";
        String tail = v.substring(v.length() - 2);
        return "****" + tail;
    }

    /** Safe current URL for logs. */
    private static String safeGetUrl(WebDriver driver) {
        try { return driver.getCurrentUrl(); } catch (Exception e) { return "<unavailable>"; }
    }




}
