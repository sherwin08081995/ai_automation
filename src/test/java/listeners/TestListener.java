package listeners;

import annotations.Author;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.Listeners;
import utils.*;
import org.apache.logging.log4j.Logger;

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
        LOGGER.info(">>> Test Suite Finished: {}", context.getName());
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
