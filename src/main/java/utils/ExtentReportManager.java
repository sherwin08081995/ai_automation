package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {
    private static ExtentReports extent;

    private static ExtentReports createInstance(String fileName) {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(fileName);
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("Automation Report");
        sparkReporter.config().setReportName("Execution Report");

        ExtentReports extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        return extentReports;
    }

    public static ExtentReports getInstance() {
        if (extent == null) {
            String reportPath = ConfigReader.get("report.path");
            extent = createInstance(reportPath);

            extent.setSystemInfo("Framework", "Zolvit Cucumber + TestNG");
            extent.setSystemInfo("Browser", ConfigReader.get("browser"));
            extent.setSystemInfo("Tester", "Anto Sherwin");

            // global flush after everything is done
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                extent.flush(); // Called once JVM is shutting down
            }));
        }
        return extent;
    }
}
