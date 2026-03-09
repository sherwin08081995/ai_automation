package utils;


import io.qameta.allure.Allure;

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
}
