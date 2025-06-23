package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * Utility class to manage a single instance of {@link ExtentReports}.
 * Configures and returns the report object used across test execution.
 * Initializes the Spark HTML reporter with custom theme, title, and system info.
 * The report is auto-flushed when the JVM shuts down.</p>
 *
 * @author Sherwin
 * @since 17-06-2025
 */

public class ExtentReportManager {
    private static ExtentReports extent;

    /**
     * Creates and configures a new instance of {@link ExtentReports} with the provided report file path.
     *
     * @param fileName Full path to the HTML report file.
     * @return Configured ExtentReports instance.
     */

    private static ExtentReports createInstance(String fileName) {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(fileName);

        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setEncoding("utf-8"); // ✅ Prevent Base64 image issues
        sparkReporter.config().setDocumentTitle("Automation Report");
        sparkReporter.config().setReportName("Execution Report");

        ExtentReports extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        return extentReports;
    }


    /**
     * Returns a singleton instance of {@link ExtentReports}.
     * If not already initialized, it will:
     * <ul>
     *     <li>Create a new report instance using the path from config</li>
     *     <li>Set system-level information like Project, Framework, Browser, Tester</li>
     *     <li>Register a shutdown hook to auto-flush the report</li>
     * </ul>
     *
     * @return The ExtentReports instance to be used across the test suite.
     */

    public static ExtentReports getInstance() {
        if (extent == null) {
            String reportPath = ConfigReader.get("report.path");
            extent = createInstance(reportPath);

            extent.setSystemInfo("Project", "Zolvit360");
            extent.setSystemInfo("Framework", "Cucumber + TestNG + Selenium");
            extent.setSystemInfo("Browser", ConfigReader.get("browser"));
            extent.setSystemInfo("Tester", "Anto Sherwin");

            Runtime.getRuntime().addShutdownHook(new Thread(() -> extent.flush()));
        }
        return extent;
    }
}

