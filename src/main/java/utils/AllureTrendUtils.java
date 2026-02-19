package utils;


import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

/**
 * @author Sherwin
 * @since 29-06-2025
 */

public class AllureTrendUtils {
    private static final String PROJECT_DIR = System.getProperty("user.dir");
    private static final File HISTORY_SOURCE = new File(PROJECT_DIR + "/allure-report/history");
    private static final File ALLURE_RESULTS_DIR = new File(PROJECT_DIR + "/allure-results");
    private static final File HISTORY_DEST = new File(ALLURE_RESULTS_DIR, "history");

    public static void preserveTrendHistory() {
        try {
            // Step 1: Copy history to allure-results
            if (HISTORY_SOURCE.exists()) {
                FileUtils.copyDirectory(HISTORY_SOURCE, HISTORY_DEST);
                System.out.println("✅ Trend history copied to allure-results.");
            } else {
                System.out.println("⚠️ No previous history to preserve.");
            }

            // Step 2: Clear old allure-results (except history)
            for (File file : ALLURE_RESULTS_DIR.listFiles()) {
                if (!file.getName().equals("history")) {
                    FileUtils.forceDelete(file);
                }
            }

            System.out.println("🧹 allure-results cleaned (except history).");

        } catch (IOException e) {
            System.err.println("❌ Trend history preservation failed: " + e.getMessage());
        }
    }
}
