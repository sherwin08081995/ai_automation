package utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
            // Ensure folder exists
            new File(SCREENSHOT_PATH).mkdirs();

            // 🔁 Trigger layout & visual refresh
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);"); // Scroll to bottom
            Thread.sleep(500);
            js.executeScript("window.scrollTo(0, 0);"); // Scroll to top
            Thread.sleep(800); // Allow animation/render

            // 📸 Capture screenshot
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, destFile);

            // ⚠️ Warn if likely blank
            if (destFile.length() < 10_000) {
                System.err.println("⚠️ Screenshot might be blank: " + destFile.getAbsolutePath());
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
