package utils;

import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {

    private static final String SCREENSHOT_FOLDER = System.getProperty("user.dir") + "/target/extent-report/screenshots/";

    /**
     * Captures screenshot and returns the relative path to be used in ExtentReports.
     */
    public static String takeScreenshot(WebDriver driver, String screenshotName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = screenshotName + "_" + timestamp + ".png";
        File destFile = new File(SCREENSHOT_FOLDER + fileName);

        try {
            FileUtils.forceMkdirParent(destFile);
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, destFile);
            System.out.println("📸 Screenshot saved at: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Failed to save screenshot: " + e.getMessage());
        }

        return "screenshots/" + fileName;
    }

    /**
     * Returns Base64 string of screenshot, for inline embedding in ExtentReports.
     */
    public static String getBase64Screenshot(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    /**
     * Builds HTML image tag from Base64 string (for Extent).
     */
    public static String getInlineBase64Img(String base64, int height) {
        return "<br><img src='data:image/png;base64," + base64 + "' height='" + height + "' />";
    }

    /**
     * Attaches screenshot to Allure Report using ByteArrayInputStream (CI safe, reliable).
     */
    public static void attachScreenshotToAllure(WebDriver driver, String screenshotName) {
        try {
            // Wait a bit to ensure page is fully rendered
            Thread.sleep(200);

            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            System.out.println("🧾 Screenshot size: " + screenshot.length + " bytes");

            // Attach screenshot to Allure
            Allure.addAttachment(screenshotName, "image/png", new ByteArrayInputStream(screenshot), ".png");

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
