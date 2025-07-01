package utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Utility to upload Allure reports to Google Drive and notify Google Chat.
 * For local runs with OAuth desktop client.
 * <p>
 * Author: Sherwin
 * Date: 30-06-2025
 */
public class GoogleChatNotifier {

//    private static final String WEBHOOK_URL = "https://chat.googleapis.com/v1/spaces/AAQAXOiSU7Y/messages?key=AIzaSyDdI0hCZtE6vySjMm-WEfRq3CPzqKqqsHI&token=NMCMdf2ZzBauM_4I1-maUK08QYXSDBURzyFw0O7GmyM";


    public static void sendNotification(String buildNumber, String jobName, String buildStatus, String reportUrl) {
        try {
            String jsonPayload = "{\n" + "  \"cards\": [\n" + "    {\n" + "      \"sections\": [\n" + "        {\n" + "          \"widgets\": [\n" + "            {\n" + "              \"textParagraph\": {\n" + "                \"text\": \"<b>🔔 Jenkins Build Notification</b><br>" + "Job: <b>" + jobName + "</b><br>" + "Build #: <b>" + buildNumber + "</b><br>" + "Status: <b>" + buildStatus + "</b><br>" + "📎 <a href='" + reportUrl + "'>View Allure Report</a>\"\n" + "              }\n" + "            }\n" + "          ]\n" + "        }\n" + "      ]\n" + "    }\n" + "  ]\n" + "}";

            URL url = new URL(ConfigReader.get("chatspace"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            System.out.println("✅ Google Chat Notification Sent | HTTP " + conn.getResponseCode());
        } catch (Exception e) {
            System.err.println("❌ Failed to send Google Chat notification.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        String url = "http://192.168.5.202:8080/job/Zolvit-QE-Tests/allure/";
        sendNotification("144", "Zolvit-QE-Tests", "✅ SUCCESS", ConfigReader.get("reportUrl"));
    }
}
