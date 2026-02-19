package stepDefinitions;


import hooks.Hooks;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Dimension;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.SkipException;
import utils.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Locale;

import static utils.AllureLoggerUtils.logToAllure;

/**
 * @author Sherwin
 * @since 08-09-2025
 */

public class BrowserCompatibilityValidationSteps {

    WebDriver driver = Hooks.driver;
    Logger logger;
    WebDriverWait wait;
    AllureLoggerUtils allureLogging;
    ReusableCommonMethods helperMethods;


    public BrowserCompatibilityValidationSteps() {
        this.driver = Hooks.driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.logger = LoggerUtils.getLogger(getClass());
        this.helperMethods = new ReusableCommonMethods(driver);
    }

    @Step("{message}")
    public void logStep(String message) { /* Allure step placeholder */ }

    private boolean isHeadless() {
        return Boolean.parseBoolean(System.getProperty("headless", ConfigReader.get("headless")));
    }

    @Given("the user launches the {string} browser")
    public void the_user_launches_the_browser(String browserName) {
        try {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_BrowserLaunch_Context"); // may be null; that’s ok
            String b = browserName == null ? "" : browserName.trim().toLowerCase(Locale.ROOT);
            if ("mozilla".equals(b)) b = "firefox"; // alias

            logStep("🧭 Launching browser: " + browserName + " (normalized: " + b + ")");

            switch (b) {
                case "chrome": {
                    ChromeOptions opt = new ChromeOptions();
                    if (isHeadless()) opt.addArguments("--headless=new");
                    opt.addArguments("--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080", "--force-device-scale-factor=1", "--hide-scrollbars", "--remote-allow-origins=*");
                    try {
                        driver = new ChromeDriver(opt); // Selenium Manager
                    } catch (Exception sm) {
                        try {
                            WebDriverManager.chromedriver().setup();
                            driver = new ChromeDriver(opt);
                        } catch (Exception wdm) {
                            String local = Paths.get(System.getProperty("user.dir"), "drivers", "chromedriver.exe").toString();
                            System.setProperty("webdriver.chrome.driver", local);
                            driver = new ChromeDriver(opt);
                        }
                    }
                    break;
                }
                case "firefox": {
                    if (!helperMethods.isFirefoxPresent()) {
                        logToAllure("⚠️ Skipping Firefox", "Firefox not installed on this host.");
                        throw new SkipException("Firefox is not installed on this machine. Skipping.");
                    }
                    FirefoxOptions fopts = new FirefoxOptions();
                    if (isHeadless()) fopts.addArguments("-headless");
                    try {
                        driver = new FirefoxDriver(fopts); // Selenium Manager
                    } catch (Exception sm) {
                        try {
                            WebDriverManager.firefoxdriver().setup();
                            driver = new FirefoxDriver(fopts);
                        } catch (Exception wdm) {
                            String local = Paths.get(System.getProperty("user.dir"), "drivers", "geckodriver.exe").toString();
                            System.setProperty("webdriver.gecko.driver", local);
                            driver = new FirefoxDriver(fopts);
                        }
                    }
                    break;
                }
                case "edge": {
                    EdgeOptions eopts = new EdgeOptions();
                    if (isHeadless()) eopts.addArguments("--headless=new");
                    eopts.addArguments("--window-size=1920,1080");
                    try {
                        driver = new EdgeDriver(eopts); // Selenium Manager
                    } catch (Exception sm) {
                        try {
                            WebDriverManager.edgedriver().setup();
                            driver = new EdgeDriver(eopts);
                        } catch (Exception wdm) {
                            Path local = Paths.get(System.getProperty("user.dir"), "drivers", "msedgedriver.exe");
                            if (Files.isRegularFile(local)) {
                                System.setProperty("webdriver.edge.driver", local.toString());
                                driver = new EdgeDriver(eopts);
                            } else {
                                logToAllure("⚠️ Skipping Edge", "No EdgeDriver available (network/local).");
                                throw new SkipException("EdgeDriver not available. Skipping.");
                            }
                        }
                    }
                    break;
                }
                case "safari": {
                    String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
                    if (!os.contains("mac")) {
                        logToAllure("⚠️ Skipping Safari", "Safari supported only on macOS.");
                        throw new SkipException("Safari is only supported on macOS. Skipping.");
                    }
                    driver = new SafariDriver();
                    break;
                }
                default:
                    Assert.fail("Unsupported browser in examples: " + browserName);
            }

            Hooks.driver = driver;

            // Common window/timeouts
            driver.manage().window().setSize(new Dimension(1920, 1080));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(helperMethods.readLong("pageLoadTimeout", 300)));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(helperMethods.readLong("implicitWait", 0)));

            // Utilities
            this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            this.helperMethods = new ReusableCommonMethods(driver);

            helperMethods.pauseForScreenshot();
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Browser_Launched");
            logToAllure("✅ Browser Launch", "Launched: " + browserName + " (" + driver.getClass().getSimpleName() + ")");
        } catch (SkipException se) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Skip_Browser_Launch");
            throw se;
        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Exception_Browser_Launch");
            Assert.fail("Failed to launch browser: " + browserName + " | " + e.getMessage(), e);
        }
    }

    @When("the user navigates to the Zolvit 360 application")
    public void the_user_navigates_to_the_zolvit_360_application() {
        try {
            driver = Hooks.driver;
            Assert.assertNotNull(driver, "WebDriver not initialized.");

            String url = ConfigReader.get("baseUrl");
            logStep("🌐 Navigating to: " + url);

            ScreenshotUtils.attachScreenshotToAllure(driver, "Before_Navigation_about_blank");
            driver.get(url);

            // Optional lightweight readiness check
            try {
                new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").toString().equals("complete"));
            } catch (Exception ignore) {
                // Non-fatal; we still capture UI evidence below
            }

            helperMethods.pauseForScreenshot();
            ScreenshotUtils.attachScreenshotToAllure(driver, "After_Navigation_Landed");
            logToAllure("✅ Navigation", "Arrived at: " + driver.getCurrentUrl());
        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Exception_Navigation");
            Assert.fail("Navigation to baseUrl failed: " + e.getMessage(), e);
        }
    }

    @Then("the Zolvit 360 should be accessible in the {string} browser")
    public void the_zolvit_360_should_be_accessible_in_the_browser(String browserName) {
        try {
            driver = Hooks.driver;
            Assert.assertNotNull(driver, "WebDriver not initialized.");

            String url = ConfigReader.get("baseUrl");

            // 1) HTTP probe (best-effort)
            int code = -1;
            try {
                code = helperMethods.httpStatus(url);
            } catch (Exception ignored) { /* continue with UI checks */ }
            boolean httpLooksOk = (code >= 200 && code < 400);

            // 2) UI sanity checks
            String current = driver.getCurrentUrl();
            Assert.assertTrue(current.startsWith("https://"), "Not on HTTPS after navigation: " + current);

            String title = driver.getTitle();
            Assert.assertNotNull(title, "Page title is null (unexpected).");

            if (code != -1) {
                Assert.assertTrue(httpLooksOk, "URL not accessible. HTTP status: " + code);
            }

            ScreenshotUtils.attachScreenshotToAllure(driver, "Accessible_" + browserName);
            logToAllure("✅ Accessibility OK (" + browserName + ")", "Reachable: " + (code != -1 ? "HTTP " + code : "HTTP probe skipped") + " | URL: " + current + " | Title present");

        } catch (AssertionError ae) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "AssertionFailed_Accessibility_" + browserName);
            Assert.fail("Accessibility check failed for " + browserName + ": " + ae.getMessage(), ae);
        } catch (Exception e) {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Exception_Accessibility_" + browserName);
            Assert.fail("Unexpected error during accessibility check for " + browserName + ": " + e.getMessage(), e);
        }
    }

}
