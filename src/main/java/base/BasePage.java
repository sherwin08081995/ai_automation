package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import utils.*;
import org.apache.logging.log4j.Logger;

/**
 * BasePage is an abstract foundation for all page objects.
 * It initializes commonly used utilities like WebDriver, wait handling, helper methods, and logging.
 * All other page classes should extend this class to inherit shared behavior.
 *
 * @author Sherwin
 * @since 09-06-2025
 */

public class BasePage {
    protected WebDriver driver;
    protected WaitUtils wait;
    protected SeleniumHelperMethods helpers;
    protected Logger logger;
    protected ReusableCommonMethods commonMethods;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WaitUtils(driver);
        helpers = new SeleniumHelperMethods();
        logger = LoggerUtils.getLogger(getClass());
        commonMethods = new ReusableCommonMethods(driver);
    }

    /**
     * Handles any validation-related failure (either Exception or AssertionError) by:
     * - Logging the error with context
     * - Capturing and attaching a screenshot to Allure
     * - Throwing an AssertionError to fail the test
     *
     *
     * @param context A brief description of where the failure occurred (e.g., "OTP entry", "Compliance page confirmation")
     * @param t       The caught Throwable (Exception or AssertionError)
     */
    public void handleValidationException(String context, Throwable t) {
        logger.error("❌ Exception during {}: {}", context, t.getMessage());
        try {
            ScreenshotUtils.attachScreenshotToAllure(driver, "Error_" + context.replaceAll("\\s+", "_"));
        } catch (Exception screenshotEx) {
            logger.warn("⚠️ Failed to capture screenshot for context '{}': {}", context, screenshotEx.getMessage());
        }
        throw new AssertionError("❌ Validation error during: " + context, t);
    }

}

