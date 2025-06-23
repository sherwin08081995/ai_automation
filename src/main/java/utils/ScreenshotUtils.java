package utils;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {

    private static final String SCREENSHOT_FOLDER = System.getProperty("user.dir") + "/target/extent-report/screenshots/";

    public static String takeScreenshot(WebDriver driver, String screenshotName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = screenshotName + "_" + timestamp + ".png";
        File destFile = new File(SCREENSHOT_FOLDER + fileName);

        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, destFile);
            System.out.println("📸 Screenshot saved at: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Failed to save screenshot: " + e.getMessage());
        }

        return destFile.getAbsolutePath(); // For attaching to Extent using file path
    }

    public static String getBase64Screenshot(WebDriver driver) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        return ts.getScreenshotAs(OutputType.BASE64);
    }

    public static String getInlineBase64Img(String base64, int height) {
        return "<br><img src='data:image/png;base64," + base64 + "' height='" + height + "' />";
    }


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
