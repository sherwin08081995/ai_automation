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
import utils.*;
import org.apache.logging.log4j.Logger;

public class TestListener implements ITestListener {

    private static final Logger LOGGER = LoggerUtils.getLogger(TestListener.class);
    private static final ExtentReports extent = ExtentReportManager.getInstance();
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        // Skip logging for Cucumber scenario method — Cucumber uses Hooks.java for reporting
        if ("runScenario".equals(result.getMethod().getMethodName())) {
            LOGGER.warn("Skipping ExtentTest creation for method: runScenario (handled via Hooks)");
            return;
        }

        // ✅ Create ExtentTest for this TestNG test method
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());

        // ✅ Assign @Author if annotated
        Author authorAnnotation = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Author.class);
        if (authorAnnotation == null) {
            authorAnnotation = result.getTestClass().getRealClass().getAnnotation(Author.class);
        }

        if (authorAnnotation != null) {
            extentTest.assignAuthor(authorAnnotation.value());
        }

        // ✅ Register this test in ThreadLocal storage
        ExtentTestManager.setTest(extentTest);

        // ✅ Safely log start of test
        if (extentTest != null) {
            extentTest.log(Status.INFO, "Test Started: " + result.getMethod().getMethodName());
        }

        LOGGER.info("Test Started: {}", result.getMethod().getMethodName());
    }


    @Override
    public void onTestSuccess(ITestResult result) {
        // Skip Cucumber's "runScenario" since Hooks already handles reporting
        if ("runScenario".equals(result.getMethod().getMethodName())) {
            LOGGER.info("Skipping onTestSuccess for Cucumber scenario: runScenario");
            return;
        }

        ExtentTest test = ExtentTestManager.getTest();

        if (test != null) {
            test.log(Status.PASS, "Test Passed: " + result.getMethod().getMethodName());
        } else {
            LOGGER.warn("ExtentTest is null in onTestSuccess for method: {}", result.getMethod().getMethodName());
        }

        LOGGER.info("Test Passed: {}", result.getMethod().getMethodName());

        if (Boolean.parseBoolean(ConfigReader.get("screenshot.on.pass"))) {
            takeAndAttachScreenshot(result, "_PASS");
        }
    }


    @Override
    public void onTestFailure(ITestResult result) {
        // Skip Cucumber's "runScenario" since Hooks already handles reporting
        if ("runScenario".equals(result.getMethod().getMethodName())) {
            LOGGER.info("Skipping onTestFailure for Cucumber scenario: runScenario");
            return;
        }

        ExtentTest test = ExtentTestManager.getTest();

        if (test != null) {
            test.log(Status.FAIL, "Test Failed: " + result.getMethod().getMethodName());
            test.log(Status.FAIL, result.getThrowable());
        } else {
            LOGGER.warn("ExtentTest is null in onTestFailure for method: {}", result.getMethod().getMethodName());
        }

        LOGGER.error("Test Failed: {}", result.getMethod().getMethodName(), result.getThrowable());

        takeAndAttachScreenshot(result, "_FAIL");
    }


    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ExtentTestManager.getTest();

        if (test != null) {
            test.log(Status.SKIP, "Test Skipped: " + result.getMethod().getMethodName());
        } else {
            LOGGER.warn("ExtentTest is null in onTestSkipped for method: {}", result.getMethod().getMethodName());
        }

        LOGGER.warn("Test Skipped: {}", result.getMethod().getMethodName());

        if (Boolean.parseBoolean(ConfigReader.get("screenshot.on.skip"))) {
            takeAndAttachScreenshot(result, "_SKIPPED");
        }
    }


    @Override
    public void onStart(ITestContext context) {
        LOGGER.info(">>> Test Suite Started: {}", context.getName());
        System.out.println(">>> Test Suite Started: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LOGGER.info(">>> Test Suite Finished: {}", context.getName());
        System.out.println(">>> Test Suite Finished: " + context.getName());
    }

    private void takeAndAttachScreenshot(ITestResult result, String suffix) {
        WebDriver driver = getDriverFromTestInstance(result.getInstance());

        if (driver != null) {
            String methodName = result.getMethod().getMethodName() + suffix;
            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, methodName);

            ExtentTest test = ExtentTestManager.getTest();
            if (test != null) {
                test.log(Status.INFO, "Screenshot attached",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build()
                );
            } else {
                LOGGER.warn("ExtentTest is null. Cannot attach screenshot for: {}", methodName);
            }

            LOGGER.info("Screenshot captured for: {}", methodName);
        } else {
            ExtentTest test = ExtentTestManager.getTest();
            if (test != null) {
                test.log(Status.WARNING, "WebDriver is null. Screenshot not taken.");
            } else {
                LOGGER.warn("WebDriver is null and ExtentTest is null for: {}", result.getMethod().getMethodName());
            }
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
