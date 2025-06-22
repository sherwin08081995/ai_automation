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
    private static final String SCREENSHOT_PATH = System.getProperty("user.dir") + "/target/screenshots/";

    public static String takeScreenshot(WebDriver driver, String screenshotName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = screenshotName + "_" + timestamp + ".png";

        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destFile = new File(SCREENSHOT_PATH + fileName);

        try {
            FileUtils.copyFile(srcFile, destFile);
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
            System.out.println("✅ Screenshot folder cleaned: " + screenshotDir.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Failed to clean screenshot folder: " + e.getMessage());
        }
    }

}
