package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Test Execution Environment Details
 *
 * @author Sherwin
 * @since 29-06-2025
 */

public class AllureEnvironmentWriter {

    public static void createEnvironmentFile() {
        Properties props = new Properties();
        props.setProperty("Browser", "Chrome");
        props.setProperty("Environment", "Prod");
        props.setProperty("BaseURL", ConfigReader.get("baseUrl"));
        props.setProperty("Tester", "Anto Sherwin");

        try (FileOutputStream output = new FileOutputStream("allure-results/environment.properties")) {
            props.store(output, "--------- Test Execution Environment Details ---------");
            System.out.println("✅ environment.properties created successfully with header.");
        } catch (IOException e) {
            System.err.println("❌ Failed to create environment.properties: " + e.getMessage());
        }
    }
}

