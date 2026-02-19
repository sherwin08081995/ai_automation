package listeners;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.*;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class TestListener implements ITestListener {

    private static final Logger LOGGER = LoggerUtils.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        if ("runScenario".equals(result.getMethod().getMethodName())) {
            LOGGER.warn("Skipping test start logic for Cucumber scenario.");
            return;
        }

        LOGGER.info("Test Started: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if ("runScenario".equals(result.getMethod().getMethodName())) {
            LOGGER.info("Skipping onTestSuccess for Cucumber scenario.");
            return;
        }

        LOGGER.info("Test Passed: {}", result.getMethod().getMethodName());

        if (Boolean.parseBoolean(ConfigReader.get("screenshot.on.pass"))) {
            takeAndAttachScreenshot(result, "_PASS");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if ("runScenario".equals(result.getMethod().getMethodName())) {
            LOGGER.info("Skipping onTestFailure for Cucumber scenario.");
            return;
        }

        LOGGER.error("Test Failed: {}", result.getMethod().getMethodName(), result.getThrowable());
        takeAndAttachScreenshot(result, "_FAIL");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOGGER.warn("Test Skipped: {}", result.getMethod().getMethodName());

        if (Boolean.parseBoolean(ConfigReader.get("screenshot.on.skip"))) {
            takeAndAttachScreenshot(result, "_SKIPPED");
        }
    }

    @Override
    public void onStart(ITestContext context) {
        LOGGER.info(">>> Test Suite Started: {}", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        int total = passed + failed + skipped;

        String buildStatus = failed > 0 ? "FAILURE" : "SUCCESS";

        String defaultBase = "http://192.168.4.74:8080/";
        String jenkinsBaseUrl = System.getenv().getOrDefault("JENKINS_URL", defaultBase);
        jenkinsBaseUrl = ensureTrailingSlash(jenkinsBaseUrl);

        String jobName = System.getenv().getOrDefault("JOB_NAME", "Zolvit360-Production");
        String buildNumber = System.getenv().getOrDefault("BUILD_NUMBER", "local");

        String buildUrl = System.getenv("BUILD_URL");
        if (buildUrl == null || buildUrl.isEmpty()) {
            String numPart = buildNumber.equals("local") ? "" : (buildNumber + "/");
            buildUrl = jenkinsBaseUrl + "job/" + jobName + "/" + numPart;
        }
        buildUrl = ensureTrailingSlash(buildUrl);

        String allureReportUrl = buildUrl + "allure/";

        long ms = context.getEndDate().getTime() - context.getStartDate().getTime();
        String duration = String.format("%d min %d sec", (ms / 60000), (ms / 1000) % 60);

        try {
            GoogleChatNotifier.sendNotification(
                    buildNumber, jobName, buildStatus,
                    allureReportUrl, buildUrl,
                    total, passed, failed, skipped,
                    duration
            );
            System.out.println("✅ Google Chat notification sent.");
        } catch (Throwable t) {
            System.err.println("❌ Failed to send Google Chat notification.");
            t.printStackTrace();
        }
    }


    /** Helper you already use elsewhere */
    private static String ensureTrailingSlash(String s) {
        if (s == null || s.isEmpty()) return "/";
        return s.endsWith("/") ? s : (s + "/");
    }


    private void takeAndAttachScreenshot(ITestResult result, String suffix) {
        WebDriver driver = getDriverFromTestInstance(result.getInstance());

        if (driver != null) {
            String methodName = result.getMethod().getMethodName() + suffix;
            ScreenshotUtils.takeScreenshot(driver, methodName);
            ScreenshotUtils.attachScreenshotToAllure(driver, methodName);

            LOGGER.info("Screenshot captured for: {}", methodName);
        } else {
            LOGGER.warn("WebDriver is null. Screenshot not taken for: {}", result.getMethod().getMethodName());
        }
    }

    private WebDriver getDriverFromTestInstance(Object testInstance) {
        try {
            return (WebDriver) testInstance.getClass().getMethod("getDriver").invoke(testInstance);
        } catch (Exception e) {
            LOGGER.error("Could not get WebDriver from test instance: {}", e.getMessage());
            return null;
        }
    }
}
