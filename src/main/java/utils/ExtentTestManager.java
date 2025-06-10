package utils;

import com.aventstack.extentreports.ExtentTest;

/**
 * @author Sherwin
 * @since 08-06-2025
 */

public class ExtentTestManager {
    // Thread-safe storage for ExtentTest (for parallel test support)
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    /**
     * Set the current thread's ExtentTest instance.
     */
    public static void setTest(ExtentTest test) {
        extentTest.set(test);
    }

    /**
     * Get the current thread's ExtentTest instance.
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }

    /**
     * Clean up the ThreadLocal after the test is complete.
     */
    public static void removeTest() {
        extentTest.remove();
    }
}
