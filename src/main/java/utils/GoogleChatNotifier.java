package utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GoogleChatNotifier {

    /**
     * Backward-compatible API — your existing calls will now:
     * - Render the Ultra Premium CI Dashboard style
     * - Show emoji status
     * - Include timestamp
     * - Include Git info from Jenkins env
     * - Show "Triggered by: Sherwin"
     */
    public static void sendNotification(String buildNumber, String jobName, String buildStatus, String allureReportUrl, String jenkinsBuildUrl, int total, int passed, int failed, int skipped, String duration) {
        sendNotification(buildNumber, jobName, buildStatus, allureReportUrl, jenkinsBuildUrl, total, passed, failed, skipped, duration, "Sherwin",   // display name
                true,        // include timestamp
                true,        // include Git info
                true         // emoji status
        );
    }

    /**
     * Preferred overload with explicit options.
     */
    public static void sendNotification(String buildNumber, String jobName, String buildStatus, String allureReportUrl, String jenkinsBuildUrl, int total, int passed, int failed, int skipped, String duration, String triggeredBy, boolean includeTimestamp, boolean includeGitInfo, boolean emojiStatus) {
        try {
            // Status → pretty (with emoji)
            String statusPretty = buildStatusPretty(buildStatus, emojiStatus);

            // Timestamp (local timezone)
            String timestamp = includeTimestamp ? formatNow() : null;

            // Git info from Jenkins env (best-effort)
            String branch = "";
            String commitShort = "";
            if (includeGitInfo) {
                branch = firstNonEmpty(System.getenv("BRANCH_NAME"), System.getenv("GIT_BRANCH"), System.getenv("CHANGE_BRANCH"), System.getenv("GIT_LOCAL_BRANCH"), "");
                commitShort = safeShortHash(System.getenv("GIT_COMMIT"));
            }

            // Environment (optional, default to "Production")
            String environment = firstNonEmpty(System.getenv("TEST_ENV"), System.getenv("ENVIRONMENT"), "", "", "Production");

            // Build ultra-premium JSON payload
            String jsonPayload = buildUltraPremiumCardPayload(jobName, buildNumber, statusPretty, buildStatus, allureReportUrl, jenkinsBuildUrl, total, passed, failed, skipped, duration, branch, commitShort, nullToEmpty(triggeredBy), environment, timestamp);

            URL url = new URL(ConfigReader.get("chatspace"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            OutputStream os = null;
            try {
                os = conn.getOutputStream();
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                os.flush();
            } finally {
                if (os != null) try {
                    os.close();
                } catch (Exception ignore) {
                }
            }

            System.out.println("✅ Google Chat Notification Sent | HTTP " + conn.getResponseCode());
        } catch (Exception e) {
            System.err.println("❌ Failed to send Google Chat notification.");
            e.printStackTrace();
        }
    }

    // ----------------- Ultra-premium card builder -----------------

    private static String buildUltraPremiumCardPayload(String jobName, String buildNumber, String statusPretty, String rawStatus, String allureReportUrl, String jenkinsBuildUrl, int total, int passed, int failed, int skipped, String duration, String gitBranch, String gitCommit, String triggeredBy, String environment, String timestamp) {
        String safeJobName = nullToEmpty(jobName);
        String safeBuildNumber = nullToEmpty(buildNumber);
        String safeStatus = nullToEmpty(statusPretty);
        String safeDuration = nullToEmpty(duration);
        String safeEnv = nullToEmpty(environment);
        String safeBranch = nullToEmpty(gitBranch);
        String safeCommit = nullToEmpty(gitCommit);
        String safeUser = nullToEmpty(triggeredBy);
        String safeAllureUrl = nullToEmpty(allureReportUrl);
        String safeJenkinsUrl = nullToEmpty(jenkinsBuildUrl);
        String safeTimestamp = nullToEmpty(timestamp);

        // --- Status / severity styling ---
        String color = statusColor(rawStatus);
        String statusChip = "<font color='" + color + "'><b>" + escape(safeStatus) + "</b></font>";

        boolean hasFailures = failed > 0;
        String severity;
        String severityColor;
        if (hasFailures) {
            if (failed >= 10) {
                severity = "CRITICAL";
                severityColor = "#D50000";     // hard red
            } else if (failed >= 3) {
                severity = "HIGH";
                severityColor = "#FF6D00";     // orange
            } else {
                severity = "MEDIUM";
                severityColor = "#FFAB00";     // amber
            }
        } else {
            severity = "NORMAL";
            severityColor = "#00C853";         // green
        }
        String severityBadge = "<font color='" + severityColor + "'><b>" + severity + "</b></font>";

        // --- Insights text (simple auto summary) ---
        String insightsHtml;
        if (hasFailures) {
            insightsHtml = "❗ <b>" + failed + "</b> tests failed out of <b>" + total + "</b>.<br>" + "Review failing suites in <b>Allure</b> and <b>Jenkins console logs</b>.";
        } else if (skipped > 0) {
            insightsHtml = "ℹ All tests passed, but <b>" + skipped + "</b> were skipped.<br>" + "Verify if skipped tests are expected (data/env conditions).";
        } else {
            insightsHtml = "✅ All tests passed. No failures or skips detected.<br>" + "Build looks <b>healthy</b>.";
        }

        // --- Header HTML (top section content) ---
        String headerBlock = "<b>🔧 CI Build — " + escape(safeJobName) + "</b><br>" + "Build #" + escape(safeBuildNumber) + " &nbsp;|&nbsp; " + statusChip + " &nbsp;|&nbsp; Severity: " + severityBadge + "<br>" + "Environment: <b>" + escape(safeEnv) + "</b>";

        // --- Metrics: SAME-LINE format ---
        String metricsHtml = "✅ <b>Passed</b> &rarr; " + passed + "<br>" + "❌ <b>Failed</b> &rarr; " + failed + "<br>" + "⏭ <b>Skipped</b> &rarr; " + skipped + "<br>" + "📦 <b>Total</b> &rarr; " + total + "<br>" + "⏱ <b>Duration</b> &rarr; " + escape(safeDuration);

        // --- Git block ---
        String gitHtml;
        if (!isEmpty(safeBranch) || !isEmpty(safeCommit)) {
            gitHtml = "Branch: <code>" + escape(safeBranch) + "</code><br>" + "Commit: <code>" + escape(safeCommit) + "</code>";
        } else {
            gitHtml = "No Git metadata available for this run.";
        }

        // --- Meta block (user + timestamp) ---
        String metaHtml = "🧑‍💻 Triggered By: <b>" + escape(safeUser) + "</b><br>";
        if (!isEmpty(safeTimestamp)) {
            metaHtml += "📅 " + escape(safeTimestamp);
        }

        // Optional logo (Jenkins logo; change if you want your own)
        String logoUrl = "https://www.jenkins.io/images/logos/jenkins/jenkins.png";

        StringBuilder sb = new StringBuilder();
        sb.append("{\n").append("  \"cards\": [\n").append("    {\n").append("      \"header\": {\n").append("        \"title\": ").append(toJsonString("CI Build Report")).append(",\n").append("        \"subtitle\": ").append(toJsonString(safeJobName + " • Build #" + safeBuildNumber)).append(",\n").append("        \"imageUrl\": ").append(toJsonString(logoUrl)).append(",\n").append("        \"imageStyle\": \"AVATAR\"\n").append("      },\n").append("      \"sections\": [\n")

                // Top summary (headerBlock)
                .append("        {\n").append("          \"widgets\": [\n").append("            { \"textParagraph\": { \"text\": ").append(toJsonString(headerBlock)).append(" } }\n").append("          ]\n").append("        },\n")

                // Metrics section (single textParagraph, each metric on one line)
                .append("        {\n").append("          \"header\": \"📊 Metrics\",\n").append("          \"widgets\": [\n").append("            { \"textParagraph\": { \"text\": ").append(toJsonString(metricsHtml)).append(" } }\n").append("          ]\n").append("        },\n")

                // Insights section
                .append("        {\n").append("          \"header\": \"🧠 Insights\",\n").append("          \"widgets\": [\n").append("            { \"textParagraph\": { \"text\": ").append(toJsonString(insightsHtml)).append(" } }\n").append("          ]\n").append("        },\n")

                // Git info section
                .append("        {\n").append("          \"header\": \"🔱 Git Info\",\n").append("          \"widgets\": [\n").append("            { \"textParagraph\": { \"text\": ").append(toJsonString(gitHtml)).append(" } }\n").append("          ]\n").append("        },\n")

                // Meta section
                .append("        {\n").append("          \"header\": \"Details\",\n").append("          \"widgets\": [\n").append("            { \"textParagraph\": { \"text\": ").append(toJsonString(metaHtml)).append(" } }\n").append("          ]\n").append("        },\n")

                // Actions section
                .append("        {\n").append("          \"header\": \"Actions\",\n").append("          \"widgets\": [\n").append("            {\n").append("              \"buttons\": [\n").append(buildButtonJson("📘 ALLURE REPORT", safeAllureUrl)).append(",\n").append(buildButtonJson("🏗 JENKINS BUILD", safeJenkinsUrl)).append(",\n").append(buildButtonJson("📄 CONSOLE OUTPUT", safeJenkinsUrl + "console")).append("\n").append("              ]\n").append("            }\n").append("          ]\n").append("        }\n")

                .append("      ]\n").append("    }\n").append("  ]\n").append("}\n");

        return sb.toString();
    }

    private static String buildButtonJson(String text, String url) {
        return "                {\n" + "                  \"textButton\": {\n" + "                    \"text\": " + toJsonString(text) + ",\n" + "                    \"onClick\": {\n" + "                      \"openLink\": {\n" + "                        \"url\": " + toJsonString(url) + "\n" + "                      }\n" + "                    }\n" + "                  }\n" + "                }";
    }

    private static String statusColor(String rawStatus) {
        String s = nullToEmpty(rawStatus).trim().toUpperCase();
        switch (s) {
            case "SUCCESS":
                return "#00C853"; // green
            case "FAILURE":
                return "#D50000"; // red
            case "UNSTABLE":
                return "#FFAB00"; // amber
            case "ABORTED":
                return "#2962FF"; // blue
            default:
                return "#9E9E9E"; // grey
        }
    }

    // ----------------- Helpers -----------------

    private static String buildStatusPretty(String status, boolean emoji) {
        String s = nullToEmpty(status).trim().toUpperCase();
        if (s.length() == 0) s = "UNKNOWN";

        if (!emoji) return s;

        if ("SUCCESS".equals(s)) return "✅ SUCCESS";
        if ("FAILURE".equals(s)) return "🔴 FAILURE";
        if ("UNSTABLE".equals(s)) return "🟡 UNSTABLE";
        if ("ABORTED".equals(s)) return "🟦 ABORTED";
        if ("NOT_BUILT".equals(s)) return "🟣 NOT_BUILT";
        return "⚪ " + s;
    }

    private static String formatNow() {
        // Example: 2025-10-23 23:17:42 IST
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date());
    }

    private static String safeShortHash(String full) {
        if (isEmpty(full)) return "";
        String f = full.trim();
        return f.length() <= 7 ? f : f.substring(0, 7);
    }

    private static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String firstNonEmpty(String a, String b, String c, String d, String fallback) {
        if (!isEmpty(a)) return a;
        if (!isEmpty(b)) return b;
        if (!isEmpty(c)) return c;
        if (!isEmpty(d)) return d;
        return fallback;
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String toJsonString(String s) {
        String v = s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        return "\"" + v + "\"";
    }
}



