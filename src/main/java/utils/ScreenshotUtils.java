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

    // 🛠 Matches the path where extent-report.html is located
    private static final String SCREENSHOT_PATH = System.getProperty("user.dir") + "/target/extent-report/screenshots/";

    public static String takeScreenshot(WebDriver driver, String screenshotName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = screenshotName + "_" + timestamp + ".png";

        File destFile = new File(SCREENSHOT_PATH + fileName);

        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, destFile);
            System.out.println("📸 Screenshot saved: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Screenshot capture failed: " + e.getMessage());
        }

        return destFile.getAbsolutePath(); // ✅ Use absolute path to avoid image loading issues
    }

    public static String getBase64Screenshot(WebDriver driver) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        return ts.getScreenshotAs(OutputType.BASE64);
    }


    public static void clearScreenshotFolder() {
        File screenshotDir = new File(SCREENSHOT_PATH);

        try {
            if (screenshotDir.exists()) {
                FileUtils.cleanDirectory(screenshotDir);
            } else {
                FileUtils.forceMkdir(screenshotDir);
            }
            System.out.println("✅ Screenshot folder cleaned: " + screenshotDir.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Failed to clean screenshot folder: " + e.getMessage());
        }
    }
}
