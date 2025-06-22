package utils;

import org.apache.commons.io.FileUtils;
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

        // Add delay to avoid capturing partially rendered pages
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destFile = new File(SCREENSHOT_PATH + fileName);

        try {
            new File(SCREENSHOT_PATH).mkdirs(); // Ensure folder exists
            FileUtils.copyFile(srcFile, destFile);

            // ✅ Add this check right after copying the screenshot
            if (destFile.length() < 10_000) { // ~10 KB is too small for a valid image
                System.err.println("⚠️ Warning: Screenshot " + destFile.getName() + " might be blank or incomplete.");
            }

            System.out.println("📸 Screenshot saved at: " + destFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
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
