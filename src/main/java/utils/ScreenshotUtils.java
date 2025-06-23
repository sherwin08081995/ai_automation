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
            // Create screenshot folder if not exists
            new File(SCREENSHOT_PATH).mkdirs();

            // Maximize or set fixed size for headless environments
            driver.manage().window().setSize(new Dimension(1920, 1080));

            // Wait for page to visually load
            new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body")));


            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Scroll to force full render
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(800);
            js.executeScript("window.scrollTo(0, 0);");
            Thread.sleep(1000);

            // Force a redraw
            js.executeScript("document.body.style.outline='1px solid transparent';");

            // Take screenshot
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileHandler.copy(srcFile, destFile);

            // Check if screenshot is too small (likely blank) – Retry once
            if (destFile.length() < 10_000) {
                System.err.println("⚠️ Screenshot might be blank, retrying once: " + destFile.getAbsolutePath());
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
