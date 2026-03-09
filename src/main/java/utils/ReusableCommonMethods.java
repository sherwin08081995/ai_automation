package utils;


import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.HomePage;

/**
 * @author Sherwin
 * @since 29-06-2025
 */

public class ReusableCommonMethods {
    private final WebDriver driver;
    private final Logger logger;
    private final WaitUtils wait;

    public ReusableCommonMethods(WebDriver driver) {
        this.driver = driver;
        this.logger = LoggerUtils.getLogger(ReusableCommonMethods.class);
        this.wait = new WaitUtils(driver);
    }


    /**
     * Refreshes the page and runs any reapply logic passed as a lambda.
     *
     * @param actionLabel    A description for logging and debugging
     * @param reapplyAction  The logic to reapply after refresh (e.g., dropdown selection)
     */
    public void refreshAndReapply(String actionLabel, Runnable reapplyAction) {
        try {
            logger.info("🔄 Refreshing the page before reapplying: {}", actionLabel);
            driver.navigate().refresh();
            wait.waitForPageToLoad();

            logger.info("🎯 Reapplying after refresh: {}", actionLabel);
            reapplyAction.run();

            logger.info("✅ Completed reapplying: {}", actionLabel);
        } catch (Exception e) {
            logger.error("❌ Error during '{}': {}", actionLabel, e.getMessage());
            ScreenshotUtils.attachScreenshotToAllure(driver, "Error_Reapply_" + actionLabel.replaceAll("\\s+", "_"));
            throw e;
        }
    }
}

