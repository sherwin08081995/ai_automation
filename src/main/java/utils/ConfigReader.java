package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    static {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("config/config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in classpath under config/");
            }
            properties = new Properties();
            System.out.println("Loading properties file...");
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }


    public static String get(String key) {

        return properties.getProperty(key);
    }
}
