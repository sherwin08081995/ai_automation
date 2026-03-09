package utils;


import io.qameta.allure.Allure;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import java.util.List;

/**
 * @author Sherwin
 * @since 27-06-2025
 */

public class AllureLoggerUtils {
    public static void logToAllure(String title, List<String> messages) {
        StringBuilder sb = new StringBuilder();
        for (String msg : messages) {
            sb.append("→ ").append(msg).append("\n");
        }
        Allure.addAttachment(title, sb.toString());
    }

    public static void logToAllure(String title, String message) {
        Allure.addAttachment(title, message);
    }

    /** Capture the entire desktop and attach to Allure as PNG. */
    public static void attachDesktopScreenshot(String attachmentName) {
        try {
            Robot robot = new Robot();
            Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage img = robot.createScreenCapture(rect);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            byte[] bytes = baos.toByteArray();

            Allure.addAttachment(attachmentName, "image/png",
                    new ByteArrayInputStream(bytes), ".png");
        } catch (Exception e) {
            Allure.addAttachment(attachmentName + " (failed)", e.toString());
        }
    }

    /** Attach an existing image file to Allure (useful if you already saved it). */
    public static void attachFile(String attachmentName, Path file) {
        try {
            byte[] bytes = Files.readAllBytes(file);
            Allure.addAttachment(attachmentName, "image/png",
                    new ByteArrayInputStream(bytes), ".png");
        } catch (Exception e) {
            Allure.addAttachment(attachmentName + " (failed)", e.toString());
        }
    }

}
