package utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {

    // Folder for screenshots (inside target/extent-report)
    private static final String SCREENSHOT_FOLDER = System.getProperty("user.dir") + "/target/extent-report/screenshots/";

    /**
     * Captures screenshot and returns the relative path to be used in ExtentReports.
     *
     * @param driver         WebDriver instance
     * @param screenshotName Custom name for screenshot
     * @return Relative path (e.g., "screenshots/abc_20250623_224533.png")
     */
    public static String takeScreenshot(WebDriver driver, String screenshotName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = screenshotName + "_" + timestamp + ".png";
        File destFile = new File(SCREENSHOT_FOLDER + fileName);

        try {
            // Ensure screenshot directory exists
            FileUtils.forceMkdirParent(destFile);

            // Take screenshot and save to file
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, destFile);

            System.out.println("📸 Screenshot saved at: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Failed to save screenshot: " + e.getMessage());
        }

        // Return relative path for ExtentReports
        return "screenshots/" + fileName;
    }

    /**
     * Returns Base64 string of screenshot, for inline embedding in ExtentReports.
     */
    public static String getBase64Screenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    /**
     * Builds HTML image tag from Base64 string (for advanced inlining).
     */
    public static String getInlineBase64Img(String base64, int height) {
        return "<br><img src='data:image/png;base64," + base64 + "' height='" + height + "' />";
    }



    public static void attachScreenshotToAllure(WebDriver driver, String screenshotName) {
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(screenshotName, new ByteArrayInputStream(screenshot));
        } catch (Exception e) {
            System.err.println("❌ Failed to attach screenshot to Allure: " + e.getMessage());
        }
    }




    /**
     * Clears or creates the screenshot folder at test start.
     */
    public static void clearScreenshotFolder() {
        File screenshotDir = new File(SCREENSHOT_FOLDER);
        try {
            if (screenshotDir.exists()) {
                FileUtils.cleanDirectory(screenshotDir);
            } else {
                FileUtils.forceMkdir(screenshotDir);
            }
            System.out.println("✅ Screenshot folder ready: " + screenshotDir.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Error preparing screenshot folder: " + e.getMessage());
        }
    }
}
