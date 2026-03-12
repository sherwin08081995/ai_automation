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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
 * ✅ URL capture for ZAP security scanning  (urls-visited.txt)
 * ✅ Cookie capture for ZAP authenticated scanning (zap-cookies.txt)
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

    // ── ZAP: accumulates every unique authenticated URL visited across all scenarios ──
    private static final Set<String> visitedUrls = new LinkedHashSet<>();

    // ── ZAP: flag so we only write cookies once (after the first successful login) ──
    private static volatile boolean cookiesWritten = false;

    static {
        AllureEnvironmentWriter.createEnvironmentFile();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // @Before  — setup driver + auto-login
    // ─────────────────────────────────────────────────────────────────────────
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

        // ---- downloads dir
        Path downloadDirPath = Paths.get(System.getProperty("user.dir"), "downloads");
        try { Files.createDirectories(downloadDirPath); } catch (IOException ignored) {}
        String downloadDir = downloadDirPath.toAbsolutePath().toString();
        System.setProperty("download.dir", downloadDir);
        logger.info("📂 Using download dir: {}", downloadDir);

        // ---- clean downloads before scenario
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
        prefs.put("download.default_directory", downloadDir);
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("profile.default_content_setting_values.automatic_downloads", 1);
        prefs.put("safebrowsing.enabled", true);
        options.setExperimentalOption("prefs", prefs);

        if (Boolean.parseBoolean(headless)) {
            options.addArguments("--headless=new");
            logger.info("🔧 Running in headless mode.");
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
        logger.info("🔧 ChromeOptions set for 1920x1080 run");

        driver = new ChromeDriver(options);

        // ---- DevTools download behavior
        try {
            HasDevTools devToolsDriver = (HasDevTools) driver;
            DevTools devTools = devToolsDriver.getDevTools();
            devTools.createSession();
            devTools.send(Browser.setDownloadBehavior(
                    Browser.SetDownloadBehaviorBehavior.ALLOW,
                    java.util.Optional.empty(),
                    java.util.Optional.of(downloadDir),
                    java.util.Optional.of(true)
            ));
            logger.info("✅ DevTools download behavior set to ALLOW → {}", downloadDir);
        } catch (Throwable t) {
            logger.warn("⚠️ Could not set DevTools download behavior. Using Chrome prefs only. {}", t.toString());
        }

        // ---- window & timeouts
        driver.manage().window().setSize(new Dimension(1920, 1080));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(
                Long.parseLong(ConfigReader.get("pageLoadTimeout"))));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(
                Long.parseLong(ConfigReader.get("implicitWait"))));

        logger.info("🚀 WebDriver setup complete for scenario: {}", scenario.getName());

        // ---- Auto-login for non-login scenarios
        boolean skipAutoLogin =
                scenario.getSourceTagNames().contains("@noAutoLogin") ||
                scenario.getName().toLowerCase().contains("login");

        if (!skipAutoLogin) {
            performLogin();

            // ── ZAP: capture session cookies once after first successful login ──
            if (!cookiesWritten) {
                writeZapCookies();
            }
        } else {
            logger.info("🔍 Skipping pre-scenario login for scenario: {}", scenario.getName());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // @After  — screenshot on fail + ZAP URL capture + browser close
    // ─────────────────────────────────────────────────────────────────────────
    @After
    public void tearDown(Scenario scenario) {

        try {
            if (scenario.isFailed() && driver instanceof TakesScreenshot) {
                try {
                    String screenshotName = "Failure_" + scenario.getName().replace(" ", "_");
                    ScreenshotUtils.takeScreenshot(driver, screenshotName);
                    ScreenshotUtils.attachScreenshotToAllure(driver, screenshotName);
                } catch (WebDriverException e) {
                    logger.warn("Could not capture failure screenshot: {}", e.getMessage());
                }
            }
        } finally {

            // ── ZAP: capture URL before closing browser ───────────────────────
            try {
                if (driver != null) {
                    String currentUrl = driver.getCurrentUrl();
                    if (currentUrl != null
                            && currentUrl.startsWith("https://grc.vakilsearch.com")
                            && !currentUrl.contains("/grc/auth/signin")
                            && !currentUrl.contains("/grc/login")
                            && !currentUrl.contains("about:blank")) {
                        visitedUrls.add(currentUrl);
                        logger.info("🔍 ZAP target captured: {}", currentUrl);
                    }
                }
            } catch (Exception e) {
                logger.warn("⚠️ Could not capture URL for ZAP: {}", e.getMessage());
            }

            // ── ZAP: write accumulated URLs to workspace file ─────────────────
            try {
                Path urlsFile = Paths.get(System.getProperty("user.dir"), "urls-visited.txt");
                Files.write(
                    urlsFile,
                    (String.join("\n", visitedUrls) + "\n").getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
                );
                logger.info("📝 urls-visited.txt updated ({} URL(s)): {}", visitedUrls.size(), visitedUrls);
            } catch (IOException e) {
                logger.warn("⚠️ Could not write urls-visited.txt: {}", e.getMessage());
            }

            // ── Close the browser ─────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────────────────────
    // ZAP Cookie Capture
    // Reads all cookies from the live Selenium session and writes them as a
    // single "name=value; name2=value2" string to zap-cookies.txt.
    // Jenkins reads this file and injects it into every ZAP HTTP request via
    // ZAP's replacer addon, so ZAP scans pages as an authenticated user.
    // ─────────────────────────────────────────────────────────────────────────
    private void writeZapCookies() {
        try {
            Set<Cookie> cookies = driver.manage().getCookies();

            if (cookies == null || cookies.isEmpty()) {
                logger.warn("⚠️ ZAP cookie capture: no cookies found in session.");
                return;
            }

            // Build "name=value; name2=value2" header string
            String cookieHeader = cookies.stream()
                    .map(c -> c.getName() + "=" + c.getValue())
                    .collect(Collectors.joining("; "));

            Path cookiesFile = Paths.get(System.getProperty("user.dir"), "zap-cookies.txt");
            Files.write(
                cookiesFile,
                cookieHeader.getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );

            cookiesWritten = true;
            logger.info("🍪 ZAP cookies captured ({} cookie(s)) → zap-cookies.txt", cookies.size());
            logger.debug("🍪 Cookie header (masked): {}",
                cookieHeader.replaceAll("=[^;]+", "=***"));

        } catch (Exception e) {
            logger.warn("⚠️ Could not write zap-cookies.txt: {}", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // performLogin
    // ─────────────────────────────────────────────────────────────────────────
    public static void performLogin() throws InterruptedException {
        final String ctx = "Pre-Scenario Login";
        final long t0 = System.currentTimeMillis();

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
            logger.warn("{}: Config 'email' is empty.", ctx);
        }

        try {
            driver.get(baseUrl);
            logger.info("🌐 {}: Navigated to {}", ctx, baseUrl);
        } catch (Exception e) {
            logger.error("💥 {}: navigation to '{}' failed: {}", ctx, baseUrl, e.toString(), e);
            throw e;
        }

        LoginPage loginPage = new LoginPage(driver);
        HomePage homePage   = new HomePage(driver);

        // 1) Enter mobile / email
        try {
            logger.info("✍️ {}: entering mobile/email: '{}'", ctx, maskForLogs(mobNum));
            loginPage.enterEmail(mobNum);
            logger.info("✅ {}: entered mobile/email", ctx);
        } catch (Exception e) {
            logger.error("💥 {}: enterEmail failed: {}", ctx, e.toString(), e);
            throw e;
        }

        // 2) Click Get OTP
        try {
            logger.info("🔘 {}: clicking Get OTP…", ctx);
            loginPage.clickGetOtpButton();
            logger.info("✅ {}: Get OTP clicked", ctx);
        } catch (Exception e) {
            logger.error("💥 {}: clickGetOtpButton failed: {}", ctx, e.toString(), e);
            throw e;
        }

        // 3) Enter OTP
        try {
            logger.info("🔐 {}: entering OTP ({} digits)…", ctx, otp.trim().length());
            loginPage.enterOtp(otp);
            logger.info("✅ {}: OTP entered", ctx);
        } catch (RuntimeException re) {
            logger.error("💥 {}: enterOtp failed: {}", ctx, re.getMessage(), re);
            throw re;
        } catch (Exception e) {
            logger.error("💥 {}: enterOtp unexpected failure: {}", ctx, e.toString(), e);
            throw e;
        }

        // 4) Email chooser
        try {
            if (loginPage.isChooserOpen()) {
                if (email == null || email.trim().isEmpty()) {
                    throw new IllegalStateException("Chooser opened but 'email' was not provided");
                }
                logger.info("📮 {}: chooser visible → selecting email '{}'", ctx, maskForLogs(email));
                loginPage.selectEmailInChooser(email);
                logger.info("✅ {}: email selected", ctx);
            } else {
                logger.info("📮 {}: chooser not open.", ctx);
            }
        } catch (Exception e) {
            logger.error("💥 {}: email chooser failed: {}", ctx, e.toString(), e);
            throw e;
        }

        // 5) Close popup if present
        try {
            if (loginPage.hasCloseIcon()) {
                logger.info("🧩 {}: popup detected → closing…", ctx);
                try {
                    loginPage.closePopupIfPresent();
                    logger.info("✅ {}: popup closed", ctx);
                } catch (Exception e) {
                    logger.warn("⚠️ {}: failed to close popup (continuing): {}", ctx, e.getMessage());
                }
            } else {
                logger.info("🧩 {}: no popup detected.", ctx);
            }
        } catch (Exception e) {
            logger.warn("⚠️ {}: popup detection error (continuing): {}", ctx, e.toString());
        }

        // 6) Festive popup
        try {
            if (loginPage.isFestivePopupVisible()) {
                logger.info("🎉 {}: festive popup detected → clicking 'Explore Service Hub'.", ctx);
                loginPage.clickExploreServiceHubFromPopup();
                boolean atHub = loginPage.isOnServiceHubPage();
                if (atHub) {
                    logger.info("✅ {}: Service Hub opened. URL={}", ctx, safeGetUrl(driver));
                } else {
                    logger.error("❌ {}: Service Hub not visible. URL={}", ctx, safeGetUrl(driver));
                    throw new IllegalStateException("Service Hub did not open as expected.");
                }
                driver.navigate().back();
                logger.info("↩️ {}: returned from Service Hub. URL={}", ctx, safeGetUrl(driver));
            } else {
                logger.info("🎉 {}: festive popup not present.", ctx);
            }
        } catch (IllegalStateException ise) {
            throw ise;
        } catch (Exception e) {
            logger.warn("⚠️ {}: festive popup handling error (continuing): {}", ctx, e.toString());
        }

        // 7) Final verification
        try {
            boolean success = homePage.isLoginSuccessful("Vakilsearch");
            if (!success) {
                logger.error("❌ {}: login verification failed.", ctx);
                throw new IllegalStateException("Login failed during setup");
            }
            logger.info("🔐 {}: login successful. Total time: {} ms", ctx, System.currentTimeMillis() - t0);
        } catch (Exception e) {
            logger.error("💥 {}: final login verification failed: {}", ctx, e.toString(), e);
            throw e;
        }
    }

    private static String maskForLogs(String s) {
        if (s == null) return "<null>";
        String v = s.trim();
        int at = v.indexOf('@');
        if (at > 0) {
            return v.substring(0, Math.min(2, at)) + "****" + v.substring(at);
        }
        if (v.length() <= 2) return "**";
        return "****" + v.substring(v.length() - 2);
    }

    private static String safeGetUrl(WebDriver driver) {
        try { return driver.getCurrentUrl(); } catch (Exception e) { return "<unavailable>"; }
    }
}
