package utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

/**
 * @author Sherwin Anto
 */

public class ScreenshotUtils {
    // Save screenshots inside the same folder as your ExtentReport
    private static final String SCREENSHOT_PATH = System.getProperty("user.dir") + "/target/extent-report/screenshots/";

    public static String takeScreenshot(WebDriver driver, String screenshotName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = screenshotName + "_" + timestamp + ".png";
        File destFile = new File(SCREENSHOT_PATH + fileName);

        try {
            new File(SCREENSHOT_PATH).mkdirs();

            // Force consistent resolution
            driver.manage().window().setSize(new Dimension(1920, 1080));

            // Wait until the visible part of the DOM is ready
            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body")));

            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Trigger scroll rendering
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(800);
            js.executeScript("window.scrollTo(0, 0);");
            Thread.sleep(1000);

            // Optional: Force a redraw
            js.executeScript("document.body.style.outline='1px solid transparent';");

            // Take initial screenshot
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileHandler.copy(srcFile, destFile);

            // Retry if file is suspiciously small
            long fileSize = destFile.length();
            if (fileSize < 10_000) {
                System.err.println("⚠️ Screenshot might be blank (" + fileSize + " bytes), retrying: " + destFile.getAbsolutePath());
                Thread.sleep(1000);
                srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                FileHandler.copy(srcFile, destFile);
            }

            System.out.println("📸 Screenshot saved at: " + destFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("❌ Screenshot failed: " + e.getMessage());
        }

        return "screenshots/" + fileName;
    }





    /**
     * Clears all existing screenshots from the screenshots directory.
     * If the directory doesn't exist, it is created.
     */
    public static void clearScreenshotFolder() {
        File screenshotDir = new File(SCREENSHOT_PATH);

        try {
            if (screenshotDir.exists()) {
                FileUtils.cleanDirectory(screenshotDir);
            } else {
                FileUtils.forceMkdir(screenshotDir);
            }

            // 🔐 Create a dummy file to ensure folder visibility in Jenkins
            File dummy = new File(screenshotDir, ".keep");
            dummy.createNewFile();

            System.out.println("✅ Screenshot folder cleaned and .keep created: " + screenshotDir.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Failed to clean screenshot folder: " + e.getMessage());
        }
    }

}
